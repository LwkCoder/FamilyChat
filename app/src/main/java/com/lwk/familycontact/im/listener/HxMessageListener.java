package com.lwk.familycontact.im.listener;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.lib.base.log.KLog;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.HxMessageEventBean;
import com.lwk.familycontact.utils.notify.FCNotifyUtils;

import java.util.List;

/**
 * Created by LWK
 * TODO 环信消息监听
 * 2016/9/20
 */
public class HxMessageListener implements EMMessageListener
{
    //收到消息
    @Override
    public void onMessageReceived(List<EMMessage> list)
    {
        KLog.i("HxMessageListener onMessageReceived : " + list);
        //铃声、震动通知
        FCNotifyUtils.getInstance().startNotify();
        //TODO 后台发送通知栏
        //通知刷新未读消息数
        EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_UNREAD_MSG));
        //通知收到新消息
        EventBusHelper.getInstance().post(new HxMessageEventBean(HxMessageEventBean.NEW_MESSAGE_RECEIVED, list));
    }

    //收到透传消息
    @Override
    public void onCmdMessageReceived(List<EMMessage> list)
    {
        KLog.i("HxMessageListener onCmdMessageReceived : " + list);
    }

    //收到已读回执
    @Override
    public void onMessageReadAckReceived(List<EMMessage> list)
    {
        KLog.i("HxMessageListener onMessageReadAckReceived : " + list);
    }

    //收到已送达回执
    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> list)
    {
        KLog.i("HxMessageListener onMessageDeliveryAckReceived : " + list);
    }

    //消息状态变动
    @Override
    public void onMessageChanged(EMMessage emMessage, Object o)
    {
        KLog.i("HxMessageListener onMessageChanged : message = " + emMessage);
    }
}
