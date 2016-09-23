package com.lwk.familycontact.im.helper;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lwk.familycontact.im.listener.HxMessageListener;

import java.util.List;
import java.util.Map;

/**
 * Created by LWK
 * 环信聊天帮助类
 * [和聊天相关的所有方法都在这里]
 * 2016/9/18
 */
public class HxChatHelper
{
    private HxChatHelper()
    {
    }

    private static final class HxChatHelperHolder
    {
        private static HxChatHelper instance = new HxChatHelper();
    }

    public static HxChatHelper getInstance()
    {
        return HxChatHelperHolder.instance;
    }

    /**
     * 添加新消息监听
     */
    public void addMessageListener(HxMessageListener listener)
    {
        if (listener != null)
            EMClient.getInstance().chatManager().addMessageListener(listener);
    }

    /**
     * 移除新消息监听
     */
    public void removeMessageListener(HxMessageListener listener)
    {
        if (listener != null)
            EMClient.getInstance().chatManager().removeMessageListener(listener);
    }

    /**
     * 获取所有会话
     */
    public Map<String, EMConversation> getAllConversations()
    {
        return EMClient.getInstance().chatManager().getAllConversations();
    }

    /**
     * 获取所有未读消息总数
     */
    public int getAllUnreadMsgCount()
    {
        int count = 0;
        Map<String, EMConversation> conversationMap = getAllConversations();
        if (conversationMap != null && conversationMap.size() > 0)
        {
            for (EMConversation conversation : conversationMap.values())
            {
                count += conversation.getUnreadMsgCount();
            }
        }
        return count;
    }

    /**
     * 将某条会话的未读消息数清零
     *
     * @param conversationId 会话id
     */
    public void clearConversationUnreadCount(String conversationId)
    {
        EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(conversationId);
        if (emConversation != null)
            emConversation.markAllMessagesAsRead();
    }

    /**
     * 删除某条会话
     *
     * @param conId        会话id
     * @param clearHistory 是否清空聊天记录
     */
    public void delConversation(String conId, boolean clearHistory)
    {
        EMClient.getInstance().chatManager().deleteConversation(conId, clearHistory);
    }

    /**
     * 获取某条会话
     *
     * @param conId 会话id
     * @return 会话对象
     */
    public EMConversation getConversation(String conId)
    {
        return EMClient.getInstance().chatManager().getConversation(conId);
    }

    /**
     * 从数据库中加载若干条历史消息
     *
     * @param conId      会话id
     * @param startMsgId 起始消息id
     * @param size       消息数量
     */
    public List<EMMessage> loadMessageFormDB(String conId, String startMsgId, int size)
    {
        EMConversation conversation = getConversation(conId);
        return conversation.loadMoreMsgFromDB(startMsgId, size);
    }

    /**
     * 发送文本消息
     *
     * @param chatType 聊天类型
     * @param content  内容
     * @param conId    会话id
     * @return 文本消息
     */
    public EMMessage sendTextMessage(EMMessage.ChatType chatType, String content, String conId)
    {
        EMMessage message = EMMessage.createTxtSendMessage(content, conId);
        message.setChatType(chatType);
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }

    /**
     * 发送语音消息
     *
     * @param chatType 聊天类型
     * @param filePath 语音文件地址
     * @param seconds  语音时长【秒】
     * @param conId    会话id
     * @return 语音消息
     */
    public EMMessage sendVoiceMessage(EMMessage.ChatType chatType, String filePath, int seconds, String conId)
    {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, seconds, conId);
        message.setChatType(chatType);
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }

    /**
     * 发送图片消息
     *
     * @param chatType      聊天类型
     * @param filePath      图片文件地址
     * @param sendOriginPic 是否发送原图【false为不发送，超过100K会被压缩发送】
     * @param conId         会话id
     * @return 图片消息
     */
    public EMMessage sendImageMessage(EMMessage.ChatType chatType, String filePath, boolean sendOriginPic, String conId)
    {
        EMMessage message = EMMessage.createImageSendMessage(filePath, sendOriginPic, conId);
        message.setChatType(chatType);
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }

    /**
     * 发送视频消息
     *
     * @param chatType    聊天类型
     * @param filePath    视频文件地址
     * @param thumbPath   预览图文件地址
     * @param videoLength 视频时长【秒1】
     * @param conId       会话id
     * @return 视频消息
     */
    public EMMessage sendVideoMessage(EMMessage.ChatType chatType, String filePath, String thumbPath, int videoLength, String conId)
    {
        EMMessage message = EMMessage.createVideoSendMessage(filePath, thumbPath, videoLength, conId);
        message.setChatType(chatType);
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }

    /**
     * 发送位置消息
     *
     * @param chatType        聊天类型
     * @param latitude        纬度
     * @param longitude       经度
     * @param locationAddress 地址
     * @param conId           会话id
     * @return 位置消息
     */
    public EMMessage sendLocationMessage(EMMessage.ChatType chatType, long latitude, long longitude, String locationAddress, String conId)
    {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, conId);
        message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }

    /**
     * 发送文件消息
     *
     * @param chatType 聊天类型
     * @param filePath 文件地址
     * @param conId    会话id
     * @return 文件消息
     */
    public EMMessage sendFileMessage(EMMessage.ChatType chatType, String filePath, String conId)
    {
        EMMessage message = EMMessage.createFileSendMessage(filePath, conId);
        message.setChatType(chatType);
        EMClient.getInstance().chatManager().sendMessage(message);
        return message;
    }

}
