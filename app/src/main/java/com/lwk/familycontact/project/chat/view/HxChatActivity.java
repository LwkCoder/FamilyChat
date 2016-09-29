package com.lwk.familycontact.project.chat.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lib.base.utils.KeyboradUtils;
import com.lib.base.widget.CommonActionBar;
import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imrecordbutton.IMRecordListener;
import com.lib.ptrview.CommonPtrLayout;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.chat.adapter.HxChatAdapter;
import com.lwk.familycontact.project.chat.dialog.VoicePlayInCallWarning;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.chat.utils.AndroidAdjustResizeBugFix;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ChatActEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.HxMessageEventBean;
import com.lwk.familycontact.widget.HxChatController;
import com.lwk.familycontact.widget.ResizeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 聊天界面
 */
public class HxChatActivity extends FCBaseActivity implements HxChatImpl
        , CommonPtrLayout.OnRefreshListener
        , ResizeLayout.OnResizeListener
        , HxChatController.onTextSendListener
        , IMRecordListener
{
    private static final String INTENT_KEY_USERBEAN = "userbean";
    private static final String INTENT_KEY_CONID = "conId";
    private static final String INTENT_KEY_CONTYPE = "conType";
    private HxChatPresenter mPresenter;
    private EMConversation.EMConversationType mConType = EMConversation.EMConversationType.Chat;//目前都作为单聊
    private String mConversationId;
    private UserBean mUserBean;
    private CommonActionBar mActionBar;
    private CommonPtrLayout mPtrView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HxChatAdapter mAdapter;
    private HxChatController mChatController;
    private ResizeLayout mResizeLayout;
    private VoicePlayInCallWarning mVoicePlayInCallWarning = new VoicePlayInCallWarning(this);

    /**
     * 跳转到聊天界面的公共方法
     *
     * @param activity 发起跳转的Activity
     * @param conId    会话id
     * @param userBean 对方资料
     */
    public static void start(Activity activity, String conId, UserBean userBean)
    {
        Intent intent = new Intent(activity, HxChatActivity.class);
        intent.putExtra(INTENT_KEY_CONID, conId);
        intent.putExtra(INTENT_KEY_USERBEAN, userBean);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        Intent intent = getIntent();
        mConversationId = intent.getStringExtra(INTENT_KEY_CONID);
        mUserBean = intent.getParcelableExtra(INTENT_KEY_USERBEAN);
        //发送进入聊天界面的通知
        EventBusHelper.getInstance().post(new ChatActEventBean(true, mConversationId));
    }

    @Override
    protected int setContentViewId()
    {
        mPresenter = new HxChatPresenter(this, mMainHandler);
        EventBusHelper.getInstance().regist(this);
        return R.layout.activity_hx_chat;
    }

    @Override
    protected void initUI()
    {
        mActionBar = findView(R.id.cab_hx_chat);
        mActionBar.setLeftLayoutAsBack(this);
        mActionBar.setRightImgResource(R.drawable.ic_cab_plus_menu);
        mActionBar.setRightLayoutClickListener(this);

        mResizeLayout = findView(R.id.rel_hx_chat);
        mResizeLayout.setOnResizeListener(this);

        mPtrView = findView(R.id.prt_chat);
        mPtrView.setDuration(1000);
        mPtrView.setOnRefreshListener(this);
        mRecyclerView = findView(R.id.common_ptrview_content);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HxChatAdapter(this, null, mPresenter, mUserBean);
        mRecyclerView.setAdapter(mAdapter);

        mChatController = findView(R.id.hcc_hx_chat);
        mChatController.setOnTextSendListener(this);
        mChatController.setOnRecordListener(this);
        AndroidAdjustResizeBugFix.assistActivity(this);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.setActionBarTitle(mConversationId, mUserBean);
        mPresenter.loadOnePageData(mConType, mConversationId, true);
    }

    @Override
    public void onRefresh()
    {
        mPresenter.loadOnePageData(mConType, mConversationId, false);
    }

    @Override
    public void onRefreshActionBarTitle(String title)
    {
        mActionBar.setTitleText(title);
    }

    @Override
    public EMMessage getAdapterFirstMsg()
    {
        return mAdapter.getDatas() != null && mAdapter.getDatas().size() > 0 ?
                mAdapter.getDatas().get(0) : null;
    }

    @Override
    public int getAdapterMsgCount()
    {
        return mAdapter.getDatas() != null ? mAdapter.getDatas().size() : 0;
    }

    @Override
    public String getConversationId()
    {
        return mConversationId;
    }

    @Override
    public EMConversation.EMConversationType getConversationType()
    {
        return mConType;
    }

    @Override
    public void loadOnePageMessagesSuccess(List<EMMessage> messages, boolean isFirstLoad)
    {
        if (messages == null)
            return;

        mAdapter.getDatas().addAll(0, messages);
        mAdapter.notifyDataSetChanged();
        if (messages != null && !isFirstLoad)
            mRecyclerView.scrollToPosition(messages.size());
    }

    @Override
    public void onPtrSuccess()
    {
        mPtrView.notifyRefreshSuccess();
    }

    @Override
    public void onPtrFail()
    {
        mPtrView.notifyRefreshFail();
    }

    @Override
    public void showNoMoreMessageWarning()
    {
        showShortToast(R.string.warning_no_more_message_history);
    }

    @Override
    public void scrollToBottom()
    {
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void refershAdapterStatus()
    {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addNewMessage(EMMessage message)
    {
        if (mAdapter != null)
        {
            //判断当前添加消息前最后一条可见消息的位置是不是为最底部的消息，是就在添加新消息后将会话拉到底部
            int curLastVisiablePosition = mLayoutManager.findLastVisibleItemPosition();
            boolean needScrollToBottom = curLastVisiablePosition == mAdapter.getDatas().size() - 1;
            mAdapter.addData(message);
            if (needScrollToBottom)
                scrollToBottom();
        }
    }

    @Override
    public void onMessageStatusChanged(EMMessage message)
    {
        if (mAdapter != null)
        {
            int position = mAdapter.getDatas().indexOf(message);
            mAdapter.notifyItemChanged(position, message);
        }
    }

    @Override
    public void removeMessage(EMMessage message, int position)
    {
        mAdapter.getDatas().remove(message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void startToImageDetailAct(String firstVisiableMsgId)
    {
        HxImageDetailActivity.start(this, mConType, mConversationId, firstVisiableMsgId);
    }

    @Override
    public void showVoicePlayInCall()
    {
        mVoicePlayInCallWarning.showAsDropDown(mActionBar, 0, 0);
    }

    @Override
    public void closeVoicePlayInCall()
    {
        mVoicePlayInCallWarning.dismiss();
    }

    @Override
    public void showError(int errorCode, int errMsgResId)
    {
        showShortToast(errMsgResId);
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.fl_common_actionbar_right:
                KeyboradUtils.HideKeyboard(mActionBar);
                ImagePicker.getInstance().pickMutilImage(this, 9, new ImagePicker.OnSelectedListener()
                {
                    @Override
                    public void onSelected(List<ImageBean> list)
                    {
                        mPresenter.sendImageMessages(mConType, mConversationId, list);
                    }
                });
                break;
        }
    }

    @Override
    public void onClickSend(String content)
    {
        mPresenter.sendTextMessage(mConType, mConversationId, content);
    }

    @Override
    public void startRecord()
    {
        mPresenter.stopPlayVoiceMessage();
    }

    @Override
    public void recordFinish(float seconds, String filePath)
    {
        mPresenter.sendVoiceMessage(mConType, mConversationId, filePath, (int) seconds);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageReceived(HxMessageEventBean eventBean)
    {
        switch (eventBean.getFlag())
        {
            case HxMessageEventBean.NEW_MESSAGE_RECEIVED:
                mPresenter.addNewReceivedMessages(eventBean);
                break;
        }
    }

    @Override
    public void OnResize(int w, int h, int oldw, int oldh)
    {
        if (h != 0 && oldh != 0 && h < oldh)
            scrollToBottom();
    }

    //该界面不需要点击非edittext区域关闭软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return getWindow().superDispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy()
    {
        mPresenter.stopPlayVoiceMessage();
        mPresenter.clearConversationUnreadCount(mConversationId);
        //发送离开聊天界面的通知
        EventBusHelper.getInstance().post(new ChatActEventBean(false, mConversationId));
        EventBusHelper.getInstance().unregist(this);
        super.onDestroy();
    }
}
