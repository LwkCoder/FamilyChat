package com.lwk.familycontact.utils.event;

/**
 * Created by LWK
 * TODO 进入\离开聊天界面后发送的通知
 * 2016/9/21
 */
public class ChatActEventBean
{
    private boolean isEnterIn;

    private String conversatinoId;

    public ChatActEventBean()
    {
    }

    public ChatActEventBean(boolean isEnterIn, String conversatinoId)
    {
        this.isEnterIn = isEnterIn;
        this.conversatinoId = conversatinoId;
    }

    public String getConversatinoId()
    {
        return conversatinoId;
    }

    public void setConversatinoId(String conversatinoId)
    {
        this.conversatinoId = conversatinoId;
    }

    public boolean isEnterIn()
    {
        return isEnterIn;
    }

    public void setEnterIn(boolean enterIn)
    {
        isEnterIn = enterIn;
    }

    @Override
    public String toString()
    {
        return "ChatActEventBean{" +
                "isEnterIn=" + isEnterIn +
                ", conversatinoId='" + conversatinoId + '\'' +
                '}';
    }
}
