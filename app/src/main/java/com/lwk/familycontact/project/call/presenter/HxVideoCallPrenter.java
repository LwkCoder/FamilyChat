package com.lwk.familycontact.project.call.presenter;

import android.os.Handler;

import com.hyphenate.chat.EMCallManager;
import com.hyphenate.media.EMCallSurfaceView;
import com.lib.base.log.KLog;
import com.lwk.familycontact.im.helper.HxCallHelper;
import com.lwk.familycontact.project.call.view.HxCallView;

/**
 * Created by LWK
 * TODO 实时视频通话Presenter
 * 2016/10/27
 */
public class HxVideoCallPrenter extends HxCallPresenter
{
    private EMCallManager.EMVideoCallHelper mVideoCallHelper;

    public HxVideoCallPrenter(HxCallView viewImpl, Handler handler)
    {
        super(viewImpl, handler);
        mVideoCallHelper = HxCallHelper.getInstance().getVideoCallHelper();
        HxCallHelper.getInstance().enableFixedVideoResolution(true);
        HxCallHelper.getInstance().setVideoCallResolution(540, 960);
    }

    /**
     * 设置双方SurfaceView
     */
    public void setSurfaceView(EMCallSurfaceView localSurfaceView, EMCallSurfaceView opSurfaceView)
    {
        HxCallHelper.getInstance().setSurfaceView(localSurfaceView, opSurfaceView);
    }

    /**
     * 停止录像
     */
    public void stopVideoRecord()
    {
        if (mVideoCallHelper != null)
        {
            try
            {
                mVideoCallHelper.stopVideoRecord();
            } catch (Exception e)
            {
                KLog.e("HxVideoCallPresenter", "Can not stop video record:" + e);
            }
        }
    }
}
