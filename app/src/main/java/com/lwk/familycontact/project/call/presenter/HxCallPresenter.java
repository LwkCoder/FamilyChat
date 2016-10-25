package com.lwk.familycontact.project.call.presenter;

import android.os.Handler;

import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.lib.base.log.KLog;
import com.lwk.familycontact.R;
import com.lwk.familycontact.im.helper.HxCallHelper;
import com.lwk.familycontact.im.listener.HxCallView;

/**
 * Created by LWK
 * TODO 实时通话Presenter基类
 * 2016/10/25
 */
public abstract class HxCallPresenter
{
    protected HxCallView mViewImpl;

    protected Handler mMainHandler;

    public HxCallPresenter(HxCallView viewImpl, Handler handler)
    {
        this.mViewImpl = viewImpl;
        this.mMainHandler = handler;
    }

    /**
     * 拨打实时语音通话
     */
    public void startVoiceCall(String phone)
    {
        try
        {
            HxCallHelper.getInstance().startVoiceCall(phone);
        } catch (EMServiceNotReadyException e)
        {
            KLog.e("HxCallPresenter can not startVoiceCall:" + e.toString());
            mViewImpl.showError(R.string.call_state_unknow_error);
        }
    }

    /**
     * 拨打实时视频通话
     */
    public void startVideoCall(String phone)
    {
        try
        {
            HxCallHelper.getInstance().startVideoCall(phone);
        } catch (EMServiceNotReadyException e)
        {
            KLog.e("HxCallPresenter can not startVideoCall:" + e.toString());
        }
    }

    /**
     * 接听通话
     */
    public void answerCall()
    {
        try
        {
            HxCallHelper.getInstance().answerCall();
        } catch (EMNoActiveCallException e)
        {
            KLog.e("HxCallPresenter can not answerCall:" + e.toString());
            mViewImpl.showError(R.string.call_state_cannot_answer);
        }
    }

    /**
     * 结束通话
     */
    public void endCall()
    {
        try
        {
            HxCallHelper.getInstance().endCall();
        } catch (EMNoActiveCallException e)
        {
            KLog.e("HxCallPresenter can not endCall:" + e.toString());
            mViewImpl.showError(0);
        }
    }

    /**
     * 拒接通话
     */
    public void rejectCall()
    {
        try
        {
            HxCallHelper.getInstance().rejectCall();
        } catch (EMNoActiveCallException e)
        {
            KLog.e("HxCallPresenter can not rejectCall:" + e.toString());
            mViewImpl.showError(0);
        }
    }
}
