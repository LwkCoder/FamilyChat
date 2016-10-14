package com.lwk.familycontact.project.chat.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lib.base.utils.KeyboradUtils;
import com.lib.base.utils.ScreenUtils;
import com.lib.base.widget.CommonActionBar;
import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imrecordbutton.IMRecordListener;
import com.lib.ptrview.CommonPtrLayout;
import com.lib.shortvideo.ShortVideoRecordActivity;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.chat.adapter.HxChatAdapter;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.chat.utils.AndroidAdjustResizeBugFix;
import com.lwk.familycontact.project.chat.utils.HeadSetReceiver;
import com.lwk.familycontact.project.chat.utils.VoiceMessagePlayInCallWarning;
import com.lwk.familycontact.project.common.FCCache;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ChatActEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.HxMessageEventBean;
import com.lwk.familycontact.widget.HxChatController;
import com.lwk.familycontact.widget.ResizeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 聊天界面
 */
@RuntimePermissions
public class HxChatActivity extends FCBaseActivity implements HxChatView
        , CommonPtrLayout.OnRefreshListener
        , ResizeLayout.OnResizeListener
        , HxChatController.onTextSendListener
        , HxChatController.onCheckModeToVoiceInputListener
        , IMRecordListener, HeadSetReceiver.onHeadSetStateChangeListener
        , HxChatPlusDialog.onChatPlusItemSelectedListener
{
    //跳转到该界面Intent键值：userbean(用户资料：单聊时有用)
    private static final String INTENT_KEY_USERBEAN = "userbean";
    //跳转到该界面Intent键值：conId（会话id）
    private static final String INTENT_KEY_CONID = "conId";
    //跳转到该界面Intent键值：conType（会话类型）
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
    //语音消息在播放时如果是听筒模式的提醒
    private VoiceMessagePlayInCallWarning mVoiceMessagePlayInCallWarning = new VoiceMessagePlayInCallWarning(this);
    //耳机插入监听
    private HeadSetReceiver mHeadSetReceiver;

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
        mChatController.setOnCheckModeToVoiceInputListener(this);
        AndroidAdjustResizeBugFix.assistActivity(this);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.setActionBarTitle(mConversationId, mUserBean);
        mPresenter.loadOnePageData(mConType, mConversationId, true);
        //如果优先展示语音输入模式，就切换输入模式
        if (!mPresenter.isTextInputModeFirst(this))
            HxChatActivityPermissionsDispatcher.checkVoiceInputModeWithCheck(this);
        //注册耳机监听
        mHeadSetReceiver = HeadSetReceiver.registInActivity(this, this);
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
        mVoiceMessagePlayInCallWarning.showAsDropDown(mActionBar, 0, 0);
    }

    @Override
    public void closeVoicePlayInCall()
    {
        mVoiceMessagePlayInCallWarning.dismiss();
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
                //关闭软键盘
                KeyboradUtils.HideKeyboard(mChatController);
                //弹出Dialog
                HxChatPlusDialog dialog = new HxChatPlusDialog(this);
                dialog.setOnChatPlusItemSelectedListener(this);
                dialog.show();
                break;
        }
    }

    @Override
    public void onPlusItemSelected(int position)
    {
        switch (position)
        {
            case HxChatPlusDialog.ITEM_PHOTO:
                ImagePicker.getInstance().pickMutilImage(this, 9, new ImagePicker.OnSelectedListener()
                {
                    @Override
                    public void onSelected(List<ImageBean> list)
                    {
                        if (list != null && list.size() > 0)
                            mPresenter.sendImageMessages(mConType, mConversationId, list);
                    }
                });
                break;
            case HxChatPlusDialog.ITEM_VIDEO:
                //                startActivity(new Intent(HxChatActivity.this,HxShortVideoRecordActivity.class));
                ShortVideoRecordActivity.start(this, ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this), FCCache.getInstance().getVideoCachePath(),
                        1, 10);
                break;
            case HxChatPlusDialog.ITEM_VOICE_CALL:
                break;
            case HxChatPlusDialog.ITEM_VIDEO_CALL:
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
    public void onHeadSetStateChanged(boolean headSetIn)
    {
        if (headSetIn)
            mPresenter.notifyHeadSetIn();
        else
            mPresenter.notifyHeadSetOut();
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
    protected void onStop()
    {
        super.onStop();
        //停止播放语音消息
        mPresenter.stopPlayVoiceMessage();
        //关闭软键盘
        KeyboradUtils.HideKeyboard(mChatController);
    }

    @Override
    protected void onDestroy()
    {
        mPresenter.clearConversationUnreadCount(mConversationId);
        //解绑耳机广播监听
        HeadSetReceiver.unregistFromActivity(this, mHeadSetReceiver);
        //发送离开聊天界面的通知
        EventBusHelper.getInstance().post(new ChatActEventBean(false, mConversationId));
        EventBusHelper.getInstance().unregist(this);
        super.onDestroy();
    }

    @Override
    public void onCheckToVoiceInputMode()
    {
        HxChatActivityPermissionsDispatcher.checkVoiceInputModeWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    public void checkVoiceInputMode()
    {
        if (mChatController != null)
            mChatController.checkModeToVoiceInput();
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO)
    public void showRationaleForRecordAudio(final PermissionRequest request)
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_record_audio_message)
                .setPositiveButton(R.string.dialog_permission_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                })
                .create().show();
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    public void onRecordAudioPermissionDenied()
    {
        showLongToast(R.string.warning_permission_record_audio_denied);
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    public void onNeverAskRecordAudio()
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_record_audio_nerver_ask_message)
                .setNegativeButton(R.string.dialog_permission_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_permission_nerver_ask_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HxChatActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
