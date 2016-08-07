package com.lib.base.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 能直接播放帧动画的imageview
 * 【不是直接读取AnimDrawable，而是利用定时器一张一张切换图片实现，节省内存】
 */
public class FrameAnimImageView extends ImageView
{
    private static final int MSG_START = 0xf1;
    private static final int MSG_STOP = 0xf2;
    private static final int STATE_STOP = 0xf3;
    private static final int STATE_RUNNING = 0xf4;

    /*正常状态下图片资源id*/
    private int mNormalResId = -1;
    /* 运行状态*/
    private int mState = STATE_RUNNING;
    /* 图片资源ID列表*/
    private List<Integer> mResourceIdList = null;
    /* 定时任务*/
    private Timer mTimer = null;
    private AnimTimerTask mTimeTask = null;
    /* 记录播放位置*/
    private int mFrameIndex = 0;
    /* 播放形式*/
    private boolean isLooping = false;

    public FrameAnimImageView(Context context)
    {
        super(context);
        init();
    }

    public FrameAnimImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FrameAnimImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        mTimer = new Timer();
    }

    /**
     * 设置正常状态下显示图片的id
     */
    public void setNormalResId(int normalResId)
    {
        this.mNormalResId = normalResId;
    }

    /**
     * 开始播放动画
     *
     * @param loop     时候循环播放
     * @param duration 每帧动画播放时间间隔
     */
    public void start(Integer[] animResourceArray, boolean loop, int duration)
    {
        ArrayList<Integer> animResourceList = new ArrayList<>(Arrays.asList(animResourceArray));
        start(animResourceList, loop, duration);
    }

    /**
     * 开始播放动画
     *
     * @param loop     时候循环播放
     * @param duration 每帧动画播放时间间隔
     */
    public void start(List<Integer> animResourceList, boolean loop, int duration)
    {
        this.mResourceIdList = animResourceList;
        stop();
        isLooping = loop;
        mFrameIndex = 0;
        mState = STATE_RUNNING;
        mTimeTask = new AnimTimerTask();
        mTimer.schedule(mTimeTask, 0, duration);
    }

    /**
     * 停止动画播放
     * 【如果没有设置NormalResId，则直接将动画第一帧的图片当NormalResId】
     */
    public void stop()
    {
        if (mTimeTask != null)
        {
            mFrameIndex = 0;
            mState = STATE_STOP;
            mTimer.purge();
            mTimeTask.cancel();
            mTimeTask = null;
        }
        if (mNormalResId != -1)
            setImageResource(mNormalResId);
        else if (mResourceIdList != null && mResourceIdList.size() > 0)
            setImageResource(mResourceIdList.get(0));
    }

    /**
     * 定时器任务
     */
    private class AnimTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            if (mFrameIndex < 0 || mState == STATE_STOP)
            {
                return;
            }

            if (mFrameIndex < mResourceIdList.size())
            {
                Message msg = AnimHanlder.obtainMessage(MSG_START, 0, 0, null);
                msg.sendToTarget();
            } else
            {
                mFrameIndex = 0;
                if (!isLooping)
                {
                    Message msg = AnimHanlder.obtainMessage(MSG_STOP, 0, 0, null);
                    msg.sendToTarget();
                }
            }
        }
    }

    private Handler AnimHanlder = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_START:
                    if (mFrameIndex >= 0 && mFrameIndex < mResourceIdList.size() && mState == STATE_RUNNING)
                    {
                        setImageResource(mResourceIdList.get(mFrameIndex));
                        mFrameIndex++;
                    }
                    break;
                case MSG_STOP:
                    stop();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDetachedFromWindow()
    {
        if (mTimeTask != null)
        {
            mFrameIndex = 0;
            mState = STATE_STOP;
            mTimer.purge();
            mTimeTask.cancel();
            mTimer = null;
            mTimeTask = null;
        }
        if (mResourceIdList != null)
        {
            mResourceIdList.clear();
            mResourceIdList = null;
        }
        super.onDetachedFromWindow();
    }
}
