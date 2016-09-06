package com.lwk.familycontact.im;

import com.hyphenate.EMContactListener;
import com.lib.base.log.KLog;
import com.lwk.familycontact.storage.db.invite.InviteBean;
import com.lwk.familycontact.storage.db.invite.InviteDao;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.notify.FCNotifyUtils;

/**
 * Created by LWK
 * TODO 环信好友事件监听
 * 2016/8/26
 */
public class HxContactListener implements EMContactListener
{
    @Override
    public void onContactAdded(String phone)
    {
        //增加了联系人时回调此方法
        KLog.i("HxContactListener onContactAdded: phone=" + phone);
    }

    @Override
    public void onContactDeleted(String phone)
    {
        //被删除时回调此方法
        KLog.i("HxContactListener onContactDeleted: phone=" + phone);
    }

    @Override
    public void onContactInvited(String phone, String reason)
    {
        //收到好友邀请
        KLog.i("HxContactListener onContactInvited: phone=" + phone);
        //存储到数据库中
        InviteBean inviteBean = new InviteBean(phone, System.currentTimeMillis());
        if (InviteDao.getInstance().saveIfNotHandled(inviteBean))
        {
            //铃声、震动通知
            FCNotifyUtils.getInstance().startNotify();
            //通知相关界面刷新
            EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_USER_INVITE));
        }
    }

    @Override
    public void onContactAgreed(String phone)
    {
        //好友请求被同意
        KLog.i("HxContactListener onContactAgreed: phone=" + phone);
    }

    @Override
    public void onContactRefused(String phone)
    {
        //好友请求被拒绝
        KLog.i("HxContactListener onContactRefused: phone=" + phone);
    }
}
