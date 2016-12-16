package com.lwk.familycontact.utils.event;

import com.hyphenate.chat.EMMessage;

/**
 * Created by LWK
 * TODO 实时通话结束后发送的通知，用于通知聊天界面添加聊天记录
 * 2016/12/16
 */

public class NewCallRecordEventBean
{
    private String conId;
    private EMMessage message;

    public NewCallRecordEventBean(EMMessage message)
    {
        this.message = message;
        if (message.direct() == EMMessage.Direct.SEND)
            this.conId = message.getTo();
        else
            this.conId = message.getFrom();
    }

    public String getConId()
    {
        return conId;
    }

    public void setConId(String conId)
    {
        this.conId = conId;
    }

    public EMMessage getMessage()
    {
        return message;
    }

    public void setMessage(EMMessage message)
    {
        this.message = message;
    }
}
