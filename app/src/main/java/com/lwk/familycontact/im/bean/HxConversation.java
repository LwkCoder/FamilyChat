package com.lwk.familycontact.im.bean;

import com.hyphenate.chat.EMConversation;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * 环信会话对象封装
 * 【结合环信会话对象和本地用户资料】
 * 2016/9/20
 */
public class HxConversation
{
    private EMConversation emConversation;

    private UserBean userBean;

    public HxConversation()
    {
    }

    public HxConversation(EMConversation emConversation, UserBean userBean)
    {
        this.emConversation = emConversation;
        this.userBean = userBean;
    }

    public EMConversation getEmConversation()
    {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation)
    {
        this.emConversation = emConversation;
    }

    public UserBean getUserBean()
    {
        return userBean;
    }

    public void setUserBean(UserBean userBean)
    {
        this.userBean = userBean;
    }

    @Override
    public String toString()
    {
        return "HxConversation{" +
                "emConversation=" + emConversation +
                ", userBean=" + userBean +
                '}';
    }
}
