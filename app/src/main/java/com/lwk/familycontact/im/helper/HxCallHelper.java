package com.lwk.familycontact.im.helper;

import com.hyphenate.chat.EMCallManager;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;

/**
 * Created by LWK
 * TODO 环信实时通话帮助类
 * 2016/10/21
 */
public class HxCallHelper
{
    private HxCallHelper()
    {
    }

    private static final class HxCallHelperHolder
    {
        private static HxCallHelper instance = new HxCallHelper();
    }

    public static HxCallHelper getInstance()
    {
        return HxCallHelperHolder.instance;
    }

    /**
     * 拨打实时语音通话
     *
     * @param phone 对方号码
     * @throws EMServiceNotReadyException
     */
    public void startVoiceCall(String phone) throws EMServiceNotReadyException
    {
        EMClient.getInstance().callManager().makeVoiceCall(phone);
    }

    /**
     * 拨打实时视频通话
     *
     * @param phone 对方号码
     * @throws EMServiceNotReadyException
     */
    public void startVideoCall(String phone) throws EMServiceNotReadyException
    {
        EMClient.getInstance().callManager().makeVideoCall(phone);
    }

    /**
     * 接听实时通话【语音/视频通用】
     *
     * @throws EMNoActiveCallException
     */
    public void answerCall() throws EMNoActiveCallException
    {
        EMClient.getInstance().callManager().answerCall();
    }

    /**
     * 拒绝接听实时通话【语音/视频通用】
     *
     * @throws EMNoActiveCallException
     */
    public void rejectCall() throws EMNoActiveCallException
    {
        EMClient.getInstance().callManager().rejectCall();
    }

    /**
     * 挂断通话【语音/视频通用】
     *
     * @throws EMNoActiveCallException
     */
    public void endCall() throws EMNoActiveCallException
    {
        EMClient.getInstance().callManager().endCall();
    }

    /**
     * 添加实时通话状态监听
     *
     * @param listener
     */
    public void addCallStateChangeListener(EMCallStateChangeListener listener)
    {
        EMClient.getInstance().callManager().addCallStateChangeListener(listener);
    }

    /**
     * 移除实时通话状态监听
     *
     * @param listener
     */
    public void removeCallStateChangeListener(EMCallStateChangeListener listener)
    {
        if (listener != null)
            EMClient.getInstance().callManager().removeCallStateChangeListener(listener);
    }

    /**
     * 暂停语音传输
     * 【用于静音】
     */
    public void pauseVoiceTransfer() throws HyphenateException
    {
        EMClient.getInstance().callManager().pauseVoiceTransfer();
    }

    /**
     * 恢复语音传输
     * 【取消静音】
     */
    public void resumeVoiceTransfer() throws HyphenateException
    {
        EMClient.getInstance().callManager().resumeVoiceTransfer();
    }

    /**
     * 获取EMVideoCallHelper对象
     */
    public EMCallManager.EMVideoCallHelper getVideoCallHelper()
    {
        return EMClient.getInstance().callManager().getVideoCallHelper();
    }

    /**
     * 设置双方SurfaceView
     *
     * @param localSurfaceView 本方
     * @param opSurfaceView    对方
     */
    public void setSurfaceView(EMCallSurfaceView localSurfaceView, EMCallSurfaceView opSurfaceView)
    {
        EMClient.getInstance().callManager().setSurfaceView(localSurfaceView, opSurfaceView);
    }

    /**
     * 暂停视频传输
     *
     * @throws HyphenateException
     */
    public void pauseVideoTransfer() throws HyphenateException
    {
        EMClient.getInstance().callManager().pauseVideoTransfer();
    }

    /**
     * 恢复视频传输
     *
     * @throws HyphenateException
     */
    public void resumeSurfaceView() throws HyphenateException
    {
        EMClient.getInstance().callManager().resumeVideoTransfer();
    }

    /**
     * 设置视频通话是否自动调节分辨率
     *
     * @param b
     */
    public void enableFixedVideoResolution(boolean b)
    {
        EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(b);
    }

    /**
     * 设置视频通话默认分辨率
     */
    public void setVideoCallResolution(int w, int h)
    {
        EMClient.getInstance().callManager().getCallOptions().setVideoResolution(w, h);
    }
}
