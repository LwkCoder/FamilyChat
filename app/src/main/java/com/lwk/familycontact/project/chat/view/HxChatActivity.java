package com.lwk.familycontact.project.chat.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ChatActEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

/**
 * 聊天界面
 */
public class HxChatActivity extends FCBaseActivity implements HxChatImpl
{
    private static final String INTENT_KEY_USERBEAN = "userbean";
    private static final String INTENT_KEY_PHONE = "phone";
    private HxChatPresenter mPresenter;
    private String mConversationId;
    private UserBean mUserBean;
    private CommonActionBar mActionBar;

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
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.setActionBarTitle(mConversationId, mUserBean);
    }

    @Override
    public void onRefreshActionBarTitle(String title)
    {
        mActionBar.setTitleText(title);
    }

    @Override
    protected void onClick(int id, View v)
    {

    }

    @Override
    protected void onDestroy()
    {
        mPresenter.clearConversationUnreadCount(mConversationId);
        //发送离开聊天界面的通知
        EventBusHelper.getInstance().post(new ChatActEventBean(false, mConversationId));
        super.onDestroy();
    }
}
