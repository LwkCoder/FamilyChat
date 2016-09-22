package com.lwk.familycontact.project.chat.presenter;

import com.hyphenate.chat.EMMessage;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.chat.model.HxChatModel;
import com.lwk.familycontact.project.chat.utils.VoicePlayListener;
import com.lwk.familycontact.project.chat.utils.VoicePlayUtils;
import com.lwk.familycontact.project.chat.view.HxChatImpl;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.other.ThreadManager;

/**
 * Created by LWK
 * TODO 聊天界面Presenter
 * 2016/9/20
 */
public class HxChatPresenter
{
    private HxChatModel mModel;
    private HxChatImpl mViewImpl;
    private int mCurPlayVoicePosition = -1;
    private VoicePlayUtils mVoicePlayUtils;

    public HxChatPresenter(HxChatImpl viewImpl)
    {
        this.mViewImpl = viewImpl;
        mModel = new HxChatModel();
        mVoicePlayUtils = new VoicePlayUtils(FCApplication.getInstance());
    }

    /**
     * 设置ActionBar的title
     */
    public void setActionBarTitle(String conversationId, UserBean userBean)
    {
        if (userBean != null)
            mViewImpl.onRefreshActionBarTitle(userBean.getDisplayName());
        else
            mViewImpl.onRefreshActionBarTitle(conversationId);
    }

    /**
     * 将会话未读消息数清零
     */
    public void clearConversationUnreadCount(final String phone)
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                mModel.clearConversationUnreadCount(phone);
                //发送通知刷新未读消息数
                EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_UNREAD_MSG));
            }
        });
    }

    public int getCurPlayVoicePosition()
    {
        return mCurPlayVoicePosition;
    }

    public void getPageSizeMessage(String conId)
    {
    }

    public void showImageDetail(EMMessage message, int position)
    {
    }

    public void clickVoiceMessage(EMMessage message, final String filePath, final int position)
    {
        if (mCurPlayVoicePosition == position)
        {
            stopPlayVoiceMessage();
            mViewImpl.refershAdapterStatus();
            return;
        }

        stopPlayVoiceMessage();
        message.setListened(true);
        mViewImpl.refershAdapterStatus();
        mVoicePlayUtils.playVoice(filePath, new VoicePlayListener()
        {
            @Override
            public void startPlay()
            {
                mCurPlayVoicePosition = position;
                mViewImpl.refershAdapterStatus();
            }

            @Override
            public void endPlay()
            {
                stopPlayVoiceMessage();
                mViewImpl.refershAdapterStatus();
            }

            @Override
            public void error(int errorCode, int errMsgResId)
            {
                mViewImpl.showError(errorCode, errMsgResId);
            }
        });
    }

    //停止播放语音消息
    public void stopPlayVoiceMessage()
    {
        mVoicePlayUtils.stopVoice();
        mCurPlayVoicePosition = -1;
    }

    public void resendMessage(EMMessage message, int position)
    {

    }
}
