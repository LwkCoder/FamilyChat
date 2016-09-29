package com.lwk.familycontact.project.chat.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by LWK
 * TODO 耳机拔插广播监听
 * 2016/9/29
 */
public class HeadSetReceiver extends BroadcastReceiver
{
    private onHeadSetStateChangeListener mListener;

    public void setOnHeadSetStateChangeListener(onHeadSetStateChangeListener listener)
    {
        this.mListener = listener;
    }

    public interface onHeadSetStateChangeListener
    {
        void onHeadSetStateChanged(boolean headSetIn);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        switch (action)
        {
            //插入和拔出耳机会触发此广播
            case Intent.ACTION_HEADSET_PLUG:
                int state = intent.getIntExtra("state", 0);
                if (state == 1)
                {
                    //耳机插入
                    if (mListener != null)
                        mListener.onHeadSetStateChanged(true);
                } else if (state == 0)
                {
                    //耳机拔出
                    if (mListener != null)
                        mListener.onHeadSetStateChanged(false);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 注册广播的公共方法
     */
    public static HeadSetReceiver registInActivity(Activity activity, onHeadSetStateChangeListener l)
    {
        HeadSetReceiver receiver = new HeadSetReceiver();
        receiver.setOnHeadSetStateChangeListener(l);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        activity.registerReceiver(receiver, filter);
        return receiver;
    }

    /**
     * 解绑广播的公共方法
     */
    public static void unregistFromActivity(Activity activity, HeadSetReceiver receiver)
    {
        activity.unregisterReceiver(receiver);
    }
}
