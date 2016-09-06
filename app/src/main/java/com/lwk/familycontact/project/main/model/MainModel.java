package com.lwk.familycontact.project.main.model;

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
}
