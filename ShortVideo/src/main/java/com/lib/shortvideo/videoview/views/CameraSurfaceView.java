package com.lib.shortvideo.videoview.views;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lib.shortvideo.R;
import com.lib.shortvideo.videoview.camera.CameraMgr;

import java.lang.ref.WeakReference;

/**
 * Created by LWK
 * TODO 录像预览SurfaceView，支持手动对焦和缩放
 * 2016/11/2
 */
public class CameraSurfaceView extends FrameLayout implements Camera.AutoFocusCallback
{
    private final String TAG = "CameraSurfaceView";

    // 用于判断双击事件的两次按下事件的间隔
    private static final long DOUBLE_CLICK_INTERVAL = 200;
    //延迟展示画面的时间
    private final int DELAY_SHOW_VIEW_DURATION = 100;
    private long mLastTouchDownTime;
    private CameraMgr mCameraMgr;
    private ZoomRunnable mZoomRunnable;
    // 对焦动画视图
    private ImageView mFocusAnimationView;
    private Animation mFocusAnimation;
    // 相机指示图片
    private ImageView mIndicatorView;
    private Animation mIndicatorAnimation;
    private boolean mIsIndicatorAnimFinished;

    private SurfaceView mSurfaceView;

    public CameraSurfaceView(Context context)
    {
        super(context);
        initUI(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs)
    {
        inflate(context, R.layout.layout_camera_surface_view, this);
        setWillNotDraw(false);

        mSurfaceView = (SurfaceView) findViewById(R.id.sfv_shortvideo_record_camera_perview);

        // 添加一个占位视图，解决下面添加的对焦动画视图，若layout调整到他的上面，视图会被切掉的bug
        addView(new View(getContext()));

        // 添加相机画面指示视图
        mIndicatorView = new ImageView(context);
        mIndicatorView.setImageResource(R.drawable.ic_shortvideo_indicator);
        addView(mIndicatorView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        // 指示图动画
        mIndicatorAnimation = AnimationUtils.loadAnimation(context, R.anim.indicator_animation);
        mIndicatorAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mIndicatorView.setVisibility(INVISIBLE);
                mIsIndicatorAnimFinished = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        // 添加对焦动画视图
        mFocusAnimationView = new ImageView(context);
        mFocusAnimationView.setVisibility(INVISIBLE);
        mFocusAnimationView.setImageResource(R.drawable.ic_shortvideo_focus);
        addView(mFocusAnimationView, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        // 定义对焦动画
        mFocusAnimation = AnimationUtils.loadAnimation(context, R.anim.focus_animation);
        mFocusAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mFocusAnimationView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        mIndicatorView.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mSurfaceView.setVisibility(VISIBLE);
                mIndicatorView.startAnimation(mIndicatorAnimation);
            }
        }, DELAY_SHOW_VIEW_DURATION);
    }

    //关联相机管理对象
    public void setCameraManager(CameraMgr cameraMgr)
    {
        this.mCameraMgr = cameraMgr;
    }

    private Camera getCamera()
    {
        return mCameraMgr != null ? mCameraMgr.getCamera() : null;
    }

    public SurfaceView getSurfaceView()
    {
        return mSurfaceView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                long downTime = System.currentTimeMillis();
                if (getCamera().getParameters().isZoomSupported() && downTime - mLastTouchDownTime <= DOUBLE_CLICK_INTERVAL)
                {
                    zoomPreview();
                }
                mLastTouchDownTime = downTime;
                focusOnTouch(event.getX(), event.getY());
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {
        // 设置对焦方式为视频连续对焦
        mCameraMgr.setCameraFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }

    /**
     * 放大预览视图
     */
    private void zoomPreview()
    {
        if (getCamera() == null)
            return;

        Camera.Parameters parameters = getCamera().getParameters();
        int currentZoom = parameters.getZoom();
        int maxZoom = (int) (parameters.getMaxZoom() / 2f + 0.5);
        int destZoom = 0 == currentZoom ? maxZoom : 0;
        if (parameters.isSmoothZoomSupported())
        {
            getCamera().stopSmoothZoom();
            getCamera().startSmoothZoom(destZoom);
        } else
        {
            Handler handler = getHandler();
            if (null == handler)
                return;
            handler.removeCallbacks(mZoomRunnable);
            handler.post(mZoomRunnable = new ZoomRunnable(destZoom, currentZoom, getCamera()));
        }
    }

    public void focusOnTouch(final float x, final float y)
    {
        getCamera().cancelAutoFocus();
        // 设置对焦方式为自动对焦
        mCameraMgr.setCameraFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        getCamera().autoFocus(this);

        mFocusAnimation.cancel();
        mFocusAnimationView.clearAnimation();
        if (mIsIndicatorAnimFinished)
        {
            int left = (int) (x - mFocusAnimationView.getWidth() / 2f);
            int top = (int) (y - mFocusAnimationView.getHeight() / 2f);
            FrameLayout.LayoutParams layoutParams = (LayoutParams) mFocusAnimationView.getLayoutParams();
            layoutParams.gravity = Gravity.NO_GRAVITY;
            layoutParams.topMargin = top;
            layoutParams.leftMargin = left;
            mFocusAnimationView.setLayoutParams(layoutParams);
        }
        mFocusAnimationView.setVisibility(VISIBLE);
        mFocusAnimationView.startAnimation(mFocusAnimation);
    }

    /**
     * 放大预览视图任务
     */
    private static class ZoomRunnable implements Runnable
    {
        int destZoom, currentZoom;
        WeakReference<Camera> cameraWeakRef;

        public ZoomRunnable(int destZoom, int currentZoom, Camera camera)
        {
            this.destZoom = destZoom;
            this.currentZoom = currentZoom;
            cameraWeakRef = new WeakReference<>(camera);
        }

        @Override
        public void run()
        {
            Camera camera = cameraWeakRef.get();
            if (null == camera)
                return;

            boolean zoomUp = destZoom > currentZoom;
            for (int i = currentZoom; zoomUp ? i <= destZoom : i >= destZoom; i = (zoomUp ? ++i : --i))
            {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setZoom(i);
                camera.setParameters(parameters);
            }
        }
    }
}
