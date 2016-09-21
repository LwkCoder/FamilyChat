package com.lwk.familycontact.im.listener;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EasyUtils;
import com.lib.base.log.KLog;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.HxMessageEventBean;
import com.lwk.familycontact.utils.notify.FCNotifyUtils;

import java.util.HashSet;
import java.util.List;

/**
 * Created by LWK
 * TODO 环信消息监听
 * 2016/9/20
 */
public class HxMessageListener implements EMMessageListener
{
    //存放无需铃声提醒的会话id
    private HashSet<String> conversationSet = new HashSet<>();

    //添加无需铃声提醒的会话id
    public void addConId(String conId)
    {
        if (StringUtil.isNotEmpty(conId))
            conversationSet.add(conId);
    }

    //移除无需铃声提醒的会话id
    public void removeConId(String conId)
    {
        if (StringUtil.isNotEmpty(conId))
            conversationSet.remove(conId);
    }

    //收到消息
    @Override
    public void onMessageReceived(List<EMMessage> list)
    {
        KLog.i("HxMessageListener onMessageReceived : " + list);
        //应用处于后台就发送通知栏提醒
        if (!EasyUtils.isAppRunningForeground(FCApplication.getInstance()))
        {
            FCNotifyUtils.getInstance().sendMessageNotifivation(list);
        } else
        {
            //应用处于前台就发送铃声、震动通知
            //震动通知不受限制
            //铃声通知需要检查单条消息所属会话是否无需提醒,多条消息不受限制
            FCNotifyUtils.getInstance().vibratorNotify();
            if (list.size() == 1 && !conversationSet.contains(list.get(0).getFrom()))
                FCNotifyUtils.getInstance().ringtongNotify();
            else if (list.size() > 1)
                FCNotifyUtils.getInstance().ringtongNotify();
        }
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
