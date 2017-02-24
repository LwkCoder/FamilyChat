package com.lwk.familycontact.project.main.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lib.base.log.KLog;
import com.lwk.familycontact.im.helper.HxChatHelper;
import com.lwk.familycontact.im.helper.HxSdkHelper;
import com.lwk.familycontact.im.listener.HxConnectListener;
import com.lwk.familycontact.im.listener.HxContactListener;
import com.lwk.familycontact.im.listener.HxMessageListener;
import com.lwk.familycontact.im.receiver.HxCallReceiver;
import com.lwk.familycontact.utils.event.ChatActEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 绑定各种环信监听的Service
 * [提高进程优先级]
 */
public class MainService extends Service
{
    private static final int MAIN_SERVICE_ID = 100;
    private MainServiceBinder mBinder = new MainServiceBinder();
    private HxConnectListener mHxConnectListener;
    private HxContactListener mHxContactListener;
    private HxMessageListener mHxMessageListener;
    private HxCallReceiver mCallReceiver;

    public MainService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        registHxListener();
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        KLog.i("MainService--->OnCreate()");
        super.onCreate();
        EventBusHelper.getInstance().regist(this);
    }

    @Override
    public void onDestroy()
    {
        KLog.i("MainService--->onDestory()");
        super.onDestroy();
        EventBusHelper.getInstance().unregist(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        KLog.i("MainService--->onStartCommand()");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(MAIN_SERVICE_ID, new Notification());
        } else
        {
            Intent innerIntent = new Intent(this, InnerService.class);
            startService(innerIntent);
            startForeground(MAIN_SERVICE_ID, new Notification());
        }
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        KLog.i("MainService--->onUnbind()");
        unRegistHxListener();
        return super.onUnbind(intent);
    }

    /**
     * 注册环信监听
     */
    public void registHxListener()
    {
        KLog.i("MainService--->registHxListener()");
        mHxConnectListener = new HxConnectListener();
        HxSdkHelper.getInstance().addConnectListener(mHxConnectListener);
        mHxContactListener = new HxContactListener();
        HxSdkHelper.getInstance().addContactListener(mHxContactListener);
        mHxMessageListener = new HxMessageListener();
        HxChatHelper.getInstance().addMessageListener(mHxMessageListener);
        mCallReceiver = HxCallReceiver.regist(this);
    }

    /**
     * 解绑环信监听
     */
    public void unRegistHxListener()
    {
        KLog.i("MainService--->unRegistHxListener()");
        if (mHxConnectListener != null)
            HxSdkHelper.getInstance().removeConnectListener(mHxConnectListener);
        if (mHxContactListener != null)
            HxSdkHelper.getInstance().removeContactListener(mHxContactListener);
        if (mHxMessageListener != null)
            HxChatHelper.getInstance().removeMessageListener(mHxMessageListener);
        if (mCallReceiver != null)
            HxCallReceiver.unregist(this, mCallReceiver);
    }

    public class MainServiceBinder extends Binder
    {
        public MainService getService()
        {
            return MainService.this;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onChatActEventBeanReceived(ChatActEventBean eventBean)
    {
        if (eventBean.isEnterIn())
        {
            if (mHxMessageListener != null)
                mHxMessageListener.addConId(eventBean.getConversatinoId());
        } else
        {
            if (mHxMessageListener != null)
                mHxMessageListener.removeConId(eventBean.getConversatinoId());
        }
    }

    public static class InnerService extends Service
    {

        @Nullable
        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public void onCreate()
        {
            KLog.i("InnerService--->onCreate()");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
        {
            KLog.i("InnerService--->onStartCommand()");
            startForeground(MAIN_SERVICE_ID, new Notification());
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy()
        {
            KLog.i("InnerService--->onDestory()");
            super.onDestroy();
        }
    }
}
