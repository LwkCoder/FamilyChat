package com.lwk.familycontact.project.chat.presenter;

import android.os.Handler;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.im.helper.HxChatHelper;
import com.lwk.familycontact.project.chat.model.HxChatModel;
import com.lwk.familycontact.project.chat.utils.VoicePlayListener;
import com.lwk.familycontact.project.chat.utils.VoicePlayUtils;
import com.lwk.familycontact.project.chat.view.HxChatImpl;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
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
    private HxChatModel mModel;
    private HxChatImpl mViewImpl;
    private Handler mMainHandler;
    private int mCurPlayVoicePosition = -1;
    private VoicePlayUtils mVoicePlayUtils;

    public HxChatPresenter(HxChatImpl viewImpl, Handler handler)
    {
        this.mViewImpl = viewImpl;
        this.mMainHandler = handler;
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

    /**
     * 加载一页消息记录
     *
     * @param conId       会话id
     * @param chatType    聊天类型
     * @param isFirstLoad 是否为第一次加载
     */
    public void loadOnePageData(EMMessage.ChatType chatType, String conId, final boolean isFirstLoad)
    {
        String startMsgId = null;
        EMConversation.EMConversationType conType = getConTypeFromChatType(chatType);
        EMConversation conversation = HxChatHelper.getInstance().getConversation(conId, conType);

        //会话不存在时
        if (conversation == null)
        {
            if (isFirstLoad)
            {
                mViewImpl.loadOnePageMessagesSuccess(null, isFirstLoad);
            }
            else
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
     * 根据ChatType获取EMConversationType
     */
    private EMConversation.EMConversationType getConTypeFromChatType(EMMessage.ChatType chatType)
    {
        if (chatType == EMMessage.ChatType.Chat)
            return EMConversation.EMConversationType.Chat;
        else if (chatType == EMMessage.ChatType.GroupChat)
            return EMConversation.EMConversationType.GroupChat;
        else if (chatType == EMMessage.ChatType.ChatRoom)
            return EMConversation.EMConversationType.ChatRoom;
        else
            return EMConversation.EMConversationType.Chat;
    }

    /**
     * 发送文本消息
     */
    public void sendTextMessage(EMMessage.ChatType chatType, String conId, String message)
    {
        EMMessage emMessage = HxChatHelper.getInstance().sendTextMessage(chatType, conId, message);
        addNewMessage(emMessage);
    }

    /**
     * 发送语音消息
     */
    public void sendVoiceMessage(EMMessage.ChatType chatType, String conId, String filePath, int seconds)
    {
        EMMessage emMessage = HxChatHelper.getInstance().sendVoiceMessage(chatType, conId, filePath, seconds);
        addNewMessage(emMessage);
    }

    //将新发送的消息加到RecyclerView中
    private void addNewMessage(final EMMessage message)
    {
        mViewImpl.addNewMessage(message);
        message.setMessageStatusCallback(new EMCallBack()
        {
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
        });
    }

    public void showImageDetail(EMMessage message, int position)
    {
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
        mVoicePlayUtils.playVoice(filePath, new VoicePlayListener()
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
        mVoicePlayUtils.stopVoice();
        mCurPlayVoicePosition = -1;
    }


    public void clickVideoMessage(EMMessage message, final int position)
    {
    }

    public void resendMessage(EMMessage message, int position)
    {

    }
}
