package com.lwk.familycontact.project.chat.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.lib.base.widget.CommonActionBar;
import com.lib.ptrview.CommonPtrLayout;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.chat.adapter.HxChatAdapter;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ChatActEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

import java.util.List;

/**
 * 聊天界面
 */
public class HxChatActivity extends FCBaseActivity implements HxChatImpl, CommonPtrLayout.OnRefreshListener
{
    private static final String INTENT_KEY_USERBEAN = "userbean";
    private static final String INTENT_KEY_PHONE = "phone";
    private HxChatPresenter mPresenter;
    private String mConversationId;
    private UserBean mUserBean;
    private CommonActionBar mActionBar;
    private CommonPtrLayout mPtrView;
    private RecyclerView mRecyclerView;
    private HxChatAdapter mAdapter;

    /**
     * 跳转到聊天界面的公共方法
     *
     * @param activity 发起跳转的Activity
     * @param phone    手机号
     * @param userBean 对方资料
     */
    public static void start(Activity activity, String phone, UserBean userBean)
    {
        Intent intent = new Intent(activity, HxChatActivity.class);
        intent.putExtra(INTENT_KEY_PHONE, phone);
        intent.putExtra(INTENT_KEY_USERBEAN, userBean);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        Intent intent = getIntent();
        mConversationId = intent.getStringExtra(INTENT_KEY_PHONE);
        mUserBean = intent.getParcelableExtra(INTENT_KEY_USERBEAN);
        //发送进入聊天界面的通知
        EventBusHelper.getInstance().post(new ChatActEventBean(true, mConversationId));
    }

    @Override
    protected int setContentViewId()
    {
        mPresenter = new HxChatPresenter(this);
        return R.layout.activity_hx_chat;
    }

    @Override
    protected void initUI()
    {
        mActionBar = findView(R.id.cab_hx_chat);
        mActionBar.setLeftLayoutAsBack(this);

        mPtrView = findView(R.id.prt_chat);
        mPtrView.setOnRefreshListener(this);
        mRecyclerView = findView(R.id.common_ptrview_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new HxChatAdapter(this, null, mPresenter, mUserBean);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.setActionBarTitle(mConversationId, mUserBean);
        List<EMMessage> messageList = EMClient.getInstance().chatManager().getConversation(mConversationId).getAllMessages();
        mAdapter.refreshDatas(messageList);
    }

    @Override
    public void onRefresh()
    {
        mMainHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mPtrView.notifyRefreshSuccess();
            }
        }, 1000);
    }

    @Override
    public void onRefreshActionBarTitle(String title)
    {
        mActionBar.setTitleText(title);
    }

    @Override
    public void refershAdapterStatus()
    {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(int errorCode, int errMsgResId)
    {
        showShortToast(errMsgResId);
    }

    @Override
    protected void onClick(int id, View v)
    {

    }

    @Override
    protected void onDestroy()
    {
        mPresenter.stopPlayVoiceMessage();
        mPresenter.clearConversationUnreadCount(mConversationId);
        //发送离开聊天界面的通知
        EventBusHelper.getInstance().post(new ChatActEventBean(false, mConversationId));
        super.onDestroy();
    }
}
