package com.lwk.familycontact.project.chat.model;

import com.lwk.familycontact.im.helper.HxChatHelper;

/**
 * Created by LWK
 * TODO 聊天界面数据层
 * 2016/9/20
 */
public class HxChatModel
{
    public HxChatModel()
    {
    }

    //将会话未读消息数清零
    public void clearConversationUnreadCount(String phone)
    {
        HxChatHelper.getInstance().clearConversationUnreadCount(phone);
    }
}
