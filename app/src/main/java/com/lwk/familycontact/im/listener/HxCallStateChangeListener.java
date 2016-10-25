package com.lwk.familycontact.im.listener;

import android.os.Handler;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.lib.base.log.KLog;

/**
 * Created by LWK
 * TODO 环信实时通话监听
 * 2016/10/21
 */
public class HxCallStateChangeListener implements EMCallStateChangeListener
{
    private final String TAG = "HxCallStateChangeListener";
    private Handler mMainHandler;
    private HxCallView mViewImpl;

    public HxCallStateChangeListener(Handler handler, HxCallView viewImpl)
    {
        this.mMainHandler = handler;
        this.mViewImpl = viewImpl;
    }

    @Override
    public void onCallStateChanged(CallState callState, final CallError callError)
    {
        switch (callState)
        {
            case IDLE:
                KLog.i(TAG + ":Idle");
                break;
            case RINGING:
                KLog.i(TAG + ":Ringing");
                break;
            case CONNECTING: // 正在连接对方
                KLog.i(TAG + ":Connecting");
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mViewImpl != null)
                            mViewImpl.connecting();
                    }
                });
                break;
            case CONNECTED: // 双方已经建立连接
                KLog.i(TAG + ":Connected");
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mViewImpl != null)
                            mViewImpl.connected();
                    }
                });
                break;
            case ANSWERING:
                KLog.i(TAG + ":Answering");
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mViewImpl != null)
                            mViewImpl.answering();
                    }
                });
                break;
            case ACCEPTED: // 电话接通成功
                KLog.i(TAG + ":Accpet");
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mViewImpl != null)
                            mViewImpl.accepted();
                    }
                });
                break;
            case DISCONNECTED: // 电话断了
                KLog.i(TAG + ":Disconnectd callError=" + callError);
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mViewImpl != null)
                        {
                            if (callError == CallError.REJECTED)
                                mViewImpl.beRejected();
                            else if (callError == CallError.ERROR_BUSY)
                                mViewImpl.busy();
                            else if (callError == CallError.ERROR_NORESPONSE)
                                mViewImpl.noResponse();
                            else if (callError == CallError.ERROR_UNAVAILABLE)
                                mViewImpl.offline();
                            else
                                mViewImpl.onDisconnect(callError);
                        }
                    }
                });
                break;
            case NETWORK_UNSTABLE: //网络不稳定
                KLog.i(TAG + ":Netword Unstable callError=" + callError);
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mViewImpl != null)
                            mViewImpl.onNetworkUnstable(callError);
                    }
                });
                break;
            case NETWORK_NORMAL: //网络恢复正常
                KLog.i(TAG + ":Network resume");
                mMainHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mViewImpl != null)
                            mViewImpl.onNetworkResumed();
                    }
                });
                break;
            case NETWORK_DISCONNECTED:
                KLog.i(TAG + ":Network disconnected");
                break;
            case VOICE_PAUSE: //暂停语音传输【静音】
                KLog.i(TAG + ":Voice pause");
                break;
            case VOICE_RESUME://恢复语音传输【取消静音】
                KLog.i(TAG + ":Voice resume");
                break;
            case VIDEO_PAUSE:
                KLog.i(TAG + ":Video pause");
                break;
            case VIDEO_RESUME:
                KLog.i(TAG + ":Video resume");
                break;
            default:
                break;
        }
    }
}
