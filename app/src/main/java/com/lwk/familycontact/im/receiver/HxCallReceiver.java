package com.lwk.familycontact.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.hyphenate.chat.EMClient;
import com.lib.base.log.KLog;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.im.helper.HxSdkHelper;
import com.lwk.familycontact.project.call.view.HxVideoCallActivity;
import com.lwk.familycontact.project.call.view.HxVoiceCallActivity;

/**
 * Created by LWK
 * TODO 环信实时通话广播监听
 * 2016/10/20
 */
public class HxCallReceiver extends BroadcastReceiver
{
    private final String FROM = "from";
    private final String TO = "to";
    private final String TYPE = "type";
    //视频通话
    private final String VIDEO_CALL = "video";
    //语音通话
    private final String VOICE_CALL = "voice";

    /**
     * 绑定广播监听的公共方法
     */
    public static HxCallReceiver regist(Context context)
    {
        HxCallReceiver receiver = new HxCallReceiver();
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        context.registerReceiver(receiver, callFilter);
        return receiver;
    }

    /**
     * 解绑广播监听的公共方法
     */
    public static void unregist(Context context, HxCallReceiver receiver)
    {
        if (context != null && receiver != null)
            context.unregisterReceiver(receiver);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String from = intent.getStringExtra(FROM);
        String to = intent.getStringExtra(TO);
        String type = intent.getStringExtra(TYPE);
        KLog.i("收到实时通话请求：from=" + from + ",to=" + to + ",type=" + type);

        /**
         * 手机里如果有多个app同时使用了环信的sdk，在接收到实时通话的广播时，会唤醒多个app的通话界面
         * 这种解决方案是环信方面建议的，但不是最优办法，因为如果多个app当前登录账号是一样的还是无法避免上述情况【比如都是手机号注册登录的】
         * 环信方面还没有最优解决方案，先这么着吧
         */
        if (!StringUtil.isEquals(to, HxSdkHelper.getInstance().getCurLoginUser()))
            return;

        if (VOICE_CALL.equals(type))
        {
            //跳转到语音通话
            HxVoiceCallActivity.start(context, from, true);
        } else if (VIDEO_CALL.equals(type))
        {
            //跳转到视频通话
            HxVideoCallActivity.start(context, from, true);
        }
    }
}
