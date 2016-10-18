package com.lib.shortvideo.videoview.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lib.shortvideo.R;

/**
 * Created by LWK
 * TODO
 * 2016/10/18
 */
public class WXProgressBar extends View
{

    //默认常规状态的颜色
    private static final int DEFAULT_NORMAL_COLOR = Color.TRANSPARENT;
    // 默认运行中颜色
    private static final int DEFAULT_RUNNING_COLOR = Color.GREEN;
    // 默认取消状态颜色
    private static final int DEFAULT_CANCEL_COLOR = Color.RED;
    private State mState;
    private long mMaxDuration = 6000;
    private long mStartTime;
    private ValueAnimator mAnimator;
    private onTimeEndListener mListener;
    private int mNormalColor;
    private int mRunningColor;
    private int mCancelColor;

    public WXProgressBar(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public WXProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WXProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WXProgressBar);
        mNormalColor = ta.getColor(R.styleable.WXProgressBar_wx_pgb_normal_color, DEFAULT_NORMAL_COLOR);
        mRunningColor = ta.getColor(R.styleable.WXProgressBar_wx_pgb_running_color, DEFAULT_RUNNING_COLOR);
        mCancelColor = ta.getColor(R.styleable.WXProgressBar_wx_pgb_cancel_color, DEFAULT_CANCEL_COLOR);
        ta.recycle();
        setState(State.NORMAL);
    }

    public void setOnTimeEndListener(onTimeEndListener listener)
    {
        this.mListener = listener;
    }

    public interface onTimeEndListener
    {
        void onTimeEnd(long maxDuration);
    }

    public void setMaxDuration(long maxDuration)
    {
        this.mMaxDuration = maxDuration;
    }

    public void start()
    {
        cancelAnim();
        mStartTime = System.currentTimeMillis();

        mAnimator = startAnimation(this, mMaxDuration, new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setState(State.RUNNING);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mListener != null)
                    mListener.onTimeEnd(mMaxDuration);
                setState(State.NORMAL);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                setState(State.NORMAL);
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
    }

    /**
     * 重置
     */
    public void reset()
    {
        cancelAnim();
        mStartTime = 0;
    }

    /**
     * 获取开始到现在的时间
     */
    public long getPastTime()
    {
        return System.currentTimeMillis() - mStartTime;
    }

    //取消动画
    private void cancelAnim()
    {
        if (mAnimator != null && mAnimator.isRunning())
            mAnimator.cancel();
        mAnimator = null;
    }

    //将View长度逐渐变为0的动画
    private ValueAnimator startAnimation(final View view, final long duration
            , final Animator.AnimatorListener animatorListener)
    {
        ValueAnimator va = ObjectAnimator.ofInt(view.getWidth(), 0);
        va.setDuration(duration);
        va.addListener(animatorListener);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = value;
                view.setLayoutParams(params);
                view.requestLayout();
            }
        });
        //结束时恢复宽高
        final int width = view.getWidth();
        final int height = view.getHeight();
        va.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {
            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                setViewLayoutParams(view, width, height);
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {
                setViewLayoutParams(view, width, height);
            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {
            }
        });
        va.start();
        return va;
    }

    //设置view的宽高
    private void setViewLayoutParams(View view, int width, int height)
    {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
        view.requestLayout();
    }

    /**
     * 设置状态
     */
    public void setState(State state)
    {
        this.mState = state;
        if (mState == State.NORMAL)
        {
            setVisibility(INVISIBLE);
            setBackgroundColor(mNormalColor);
        } else if (mState == State.RUNNING)
        {
            setVisibility(VISIBLE);
            setBackgroundColor(mRunningColor);
        } else if (mState == State.CANCEL)
        {
            setVisibility(VISIBLE);
            setBackgroundColor(mCancelColor);
        }
    }

    public enum State
    {
        NORMAL, RUNNING, CANCEL
    }
}
