package com.lwk.familycontact.project.main.model;

import com.lwk.familycontact.im.helper.HxChatHelper;
import com.lwk.familycontact.storage.db.invite.InviteDao;

/**
 * Created by LWK
 * TODO MainActivity的数据操作层
 * 2016/9/6
 */
public class MainModel
{

    public MainModel()
    {
    }

    //获取未读的好友通知数量
    public int getUnreadFriendNotifyNum()
    {
        return InviteDao.getInstance().getUnreadNotifyNum();
    }

    //获取所有未读消息总数
    public int getAllUnreadMsgCount()
    {
        return HxChatHelper.getInstance().getAllUnreadMsgCount();
    }
}
