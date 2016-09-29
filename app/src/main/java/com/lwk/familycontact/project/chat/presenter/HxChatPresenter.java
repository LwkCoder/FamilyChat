package com.lwk.familycontact.project.chat.presenter;

import android.os.Handler;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.lib.base.utils.StringUtil;
import com.lib.imagepicker.bean.ImageBean;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.im.helper.HxChatHelper;
import com.lwk.familycontact.project.chat.utils.VoiceMessagePlayUtils;
import com.lwk.familycontact.project.chat.utils.VoiceMessagePlayListener;
import com.lwk.familycontact.project.chat.view.HxChatImpl;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.HxMessageEventBean;
import com.lwk.familycontact.utils.other.ThreadManager;

import java.util.List;

/**
 * Created by LWK
 * TODO 聊天界面Presenter
 * 2016/9/20
 */
public class HxChatPresenter
{
    //每页消息数量
    private final int EACH_PAGE_SIZE = 20;
    private HxChatImpl mViewImpl;
    private Handler mMainHandler;
    private int mCurPlayVoicePosition = -1;
    private VoiceMessagePlayUtils mVoiceMessagePlayUtils;

    public HxChatPresenter(HxChatImpl viewImpl, Handler handler)
    {
        this.mViewImpl = viewImpl;
        this.mMainHandler = handler;
        mVoiceMessagePlayUtils = new VoiceMessagePlayUtils(FCApplication.getInstance(), this);
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
                HxChatHelper.getInstance().clearConversationUnreadCount(phone);
                //发送通知刷新未读消息数
                EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_UNREAD_MSG));
            }
        });
    }

    /**
     * 加载一页消息记录
     *
     * @param conId       会话id
     * @param conType     会话类型
     * @param isFirstLoad 是否为第一次加载
     */
    public void loadOnePageData(EMConversation.EMConversationType conType, String conId, final boolean isFirstLoad)
    {
        String startMsgId = null;
        EMConversation conversation = HxChatHelper.getInstance().getConversation(conId, conType);

        //会话不存在时
        if (conversation == null)
        {
            if (isFirstLoad)
            {
                mViewImpl.loadOnePageMessagesSuccess(null, isFirstLoad);
            } else
            {
                mViewImpl.showNoMoreMessageWarning();
                mViewImpl.onPtrFail();
            }
            return;
        }

        //获取适配器里第一条消息，以便获取起始msgId
        EMMessage firstMessage = mViewImpl.getAdapterFirstMsg();
        if (firstMessage != null)
            startMsgId = firstMessage.getMsgId();

        int cacheMsgCount = mViewImpl.getAdapterMsgCount();//适配器里所有消息的数量
        //当适配器消息数量小于该会话所有消息数量就去数据库拉取
        if (cacheMsgCount < conversation.getAllMsgCount())
        {
            final List<EMMessage> messages = HxChatHelper.getInstance().loadMessageFormDB(conType, conId, startMsgId, EACH_PAGE_SIZE);
            //第一次加载直接拉到底部
            if (isFirstLoad)
            {
                mViewImpl.loadOnePageMessagesSuccess(messages, isFirstLoad);
                mViewImpl.scrollToBottom();
            }
            //下拉刷新加载
            else
            {
                mViewImpl.onPtrSuccess();
                //延迟一会儿再将数据传过去，不然太快了
                mMainHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mViewImpl.loadOnePageMessagesSuccess(messages, isFirstLoad);
                    }
                }, 500);
            }
        } else if (!isFirstLoad)
        {
            mViewImpl.showNoMoreMessageWarning();
            mViewImpl.onPtrFail();
        }
    }

    /**
     * 根据EMConversationType获取ChatType
     */
    private EMMessage.ChatType getChatTypeFromConType(EMConversation.EMConversationType conType)
    {
        if (conType == EMConversation.EMConversationType.Chat)
            return EMMessage.ChatType.Chat;
        else if (conType == EMConversation.EMConversationType.GroupChat)
            return EMMessage.ChatType.GroupChat;
        else if (conType == EMConversation.EMConversationType.ChatRoom)
            return EMMessage.ChatType.ChatRoom;
        else
            return EMMessage.ChatType.Chat;
    }

    /**
     * 发送文本消息
     */
    public void sendTextMessage(EMConversation.EMConversationType conType, String conId, String message)
    {
        EMMessage emMessage = HxChatHelper.getInstance().createTextMessage(getChatTypeFromConType(conType), conId, message);
        emMessage.setMessageStatusCallback(new MessageStatusCallBack(emMessage));
        mViewImpl.addNewMessage(emMessage);
        HxChatHelper.getInstance().sendMessage(emMessage);
    }

    /**
     * 发送语音消息
     */
    public void sendVoiceMessage(EMConversation.EMConversationType conType, String conId, String filePath, int seconds)
    {
        EMMessage emMessage = HxChatHelper.getInstance().createVoiceMessage(getChatTypeFromConType(conType), conId, filePath, seconds);
        emMessage.setMessageStatusCallback(new MessageStatusCallBack(emMessage));
        mViewImpl.addNewMessage(emMessage);
        HxChatHelper.getInstance().sendMessage(emMessage);
    }

    /**
     * 发送若干张图片消息
     */
    public void sendImageMessages(final EMConversation.EMConversationType conType, final String conId, List<ImageBean> list)
    {
        int delay = 0;
        for (final ImageBean imageBean : list)
        {
            mMainHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    sendImageMessage(conType, conId, imageBean.getImagePath(), false);
                }
            }, delay);
            delay += 400;
        }
    }

    /**
     * 发送图片消息
     */
    public void sendImageMessage(EMConversation.EMConversationType conType, String conId, String filePath, boolean sendOriginFile)
    {
        EMMessage emMessage = HxChatHelper.getInstance().createImageMessage(getChatTypeFromConType(conType), conId, filePath, sendOriginFile);
        emMessage.setMessageStatusCallback(new MessageStatusCallBack(emMessage));
        mViewImpl.addNewMessage(emMessage);
        HxChatHelper.getInstance().sendMessage(emMessage);
    }

    //消息发送回调
    private class MessageStatusCallBack implements EMCallBack
    {
        private EMMessage message;

        public MessageStatusCallBack(EMMessage message)
        {
            this.message = message;
        }

        @Override
        public void onSuccess()
        {
            mMainHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mViewImpl.onMessageStatusChanged(message);
                }
            });
        }

        @Override
        public void onError(int code, String error)
        {
            mMainHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mViewImpl.onMessageStatusChanged(message);
                }
            });
        }

        @Override
        public void onProgress(int progress, String status)
        {
            //在这里可以刷新消息发送进度
        }
    }

    /**
     * 添加新接收的消息
     */
    public void addNewReceivedMessages(HxMessageEventBean eventBean)
    {
        List<EMMessage> messageList = eventBean.getMsgList();
        String conId = mViewImpl.getConversationId();
        for (EMMessage message : messageList)
        {
            if (StringUtil.isEquals(message.getFrom(), conId))
                mViewImpl.addNewMessage(message);
        }
    }

    /**
     * 获取当前播放语音消息的位置
     */
    public int getCurPlayVoicePosition()
    {
        return mCurPlayVoicePosition;
    }

    /**
     * 点击语音消息
     *
     * @param message  被点击的语音消息
     * @param filePath 语音消息播放地址
     * @param position 消息位置
     */
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
        mVoiceMessagePlayUtils.playVoice(filePath, new VoiceMessagePlayListener()
        {
            @Override
            public void startPlay(boolean isHandFree)
            {
                mCurPlayVoicePosition = position;
                mViewImpl.refershAdapterStatus();
            }

            @Override
            public void endPlay(boolean isHandFree)
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
        mVoiceMessagePlayUtils.stopVoice();
        mCurPlayVoicePosition = -1;
    }

    /**
     * 提示语音播放在听筒里
     */
    public void showVoicePlayInCallWarning()
    {
        mViewImpl.showVoicePlayInCall();
    }

    /**
     * 关闭语音播放在听筒的提示
     */
    public void closeVoicePlayInCallWarning()
    {
        mViewImpl.closeVoicePlayInCall();
    }

    /**
     * 通知耳机插入
     */
    public void notifyHeadSetIn()
    {
        mVoiceMessagePlayUtils.notifyHeadSetIn();
    }

    /**
     * 通知耳机拔出
     */
    public void notifyHeadSetOut()
    {
        mVoiceMessagePlayUtils.notifyHeadSetOut();
    }

    /**
     * 点击查看图片消息的大图
     *
     * @param message
     * @param position
     */
    public void clickImageMessage(EMMessage message, int position)
    {
        mViewImpl.startToImageDetailAct(message.getMsgId());
    }


    public void clickVideoMessage(EMMessage message, final int position)
    {
    }

    /**
     * 重新发送某条消息
     */
    public void resendMessage(EMMessage message, int position)
    {
        EMConversation.EMConversationType conType = mViewImpl.getConversationType();
        String conId = mViewImpl.getConversationId();
        //移除已有消息
        HxChatHelper.getInstance().deleteMessage(conType, conId, message);
        mViewImpl.removeMessage(message, position);
        //重发消息
        switch (message.getType())
        {
            case TXT:
                EMTextMessageBody textMessageBody = (EMTextMessageBody) message.getBody();
                sendTextMessage(conType, conId, textMessageBody.getMessage());
                break;
            case VOICE:
                EMVoiceMessageBody voiceMessageBody = (EMVoiceMessageBody) message.getBody();
                sendVoiceMessage(conType, conId, voiceMessageBody.getLocalUrl(), voiceMessageBody.getLength());
                break;
            case IMAGE:
                EMImageMessageBody imageMessageBody = (EMImageMessageBody) message.getBody();
                sendImageMessage(conType, conId, imageMessageBody.getLocalUrl(), imageMessageBody.isSendOriginalImage());
                break;
        }
    }
}
