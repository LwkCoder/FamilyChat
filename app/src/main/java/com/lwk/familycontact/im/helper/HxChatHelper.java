package com.lwk.familycontact.im.helper;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.lwk.familycontact.im.listener.HxMessageListener;

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
}
