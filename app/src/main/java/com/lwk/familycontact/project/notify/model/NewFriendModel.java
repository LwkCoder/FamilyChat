package com.lwk.familycontact.project.notify.model;

import com.lwk.familycontact.storage.db.invite.InviteBean;
import com.lwk.familycontact.storage.db.invite.InviteDao;
import com.lwk.familycontact.storage.db.user.UserDao;

import java.util.List;

/**
 * Created by LWK
 * TODO 新的好友通知界面数据层
 * 2016/9/6
 */
public class NewFriendModel
{
    //查询所有通知
    public List<InviteBean> getAllInviteNotify()
    {
        return InviteDao.getInstance().queryAllSortByStamp();
    }

    //更新某条通知
    public void updateNotify(InviteBean inviteBean)
    {
        InviteDao.getInstance().update(inviteBean);
    }

    //将所有通知设置为已读
    public void martAllInviteAsRead()
    {
        InviteDao.getInstance().setAllNotifyRead();
    }

    //将某手机号用户标注为注册用户，或者添加新好友数据
    public void addOrUpdateNewUserData(String phone)
    {
        UserDao.getInstance().addOrUpdateUser(phone);
    }
}
