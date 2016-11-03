package com.lib.shortvideo.videoview.recorder;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.shortvideo.R;
import com.lib.shortvideo.utils.ShortVideoCommonUtil;
import com.lib.shortvideo.videoview.camera.CameraMgr;
import com.lib.shortvideo.videoview.views.CameraSurfaceView;
import com.lib.shortvideo.videoview.views.RecorderProgressBar;

/**
 * Created by LWK
 * TODO 短视频录制控件
 * 2016/11/1
 */
public class ShortVideoRecorder extends RelativeLayout implements SurfaceHolder.Callback, RecorderProgressBar.onTimeEndListener, View.OnClickListener
{
    private final String TAG = "ShortVideoRecorder";
    //默认最大录制时间
    private final int DEF_MAX_DURATION = 10000;
    //默认最小录制时间
    private final int DEF_MIN_DURATION = 1000;
    //默认高宽比
    private final float DEF_HW_RATE = 1.0f;
    //录音最小内存空间
    private final long MIN_MEMORY_SPACE = 10 * 1024 * 1024;
    //取消录像的滑动距离
    private float mCancelRange;
    private int mViewWidth;
    private int mViewHeight;

    //缓存文件夹绝对路径
    private String mCacheFolder;
    private long mMaxDuration;
    private long mMinDuration;
    private float mHWRate;
    private String mOutputFilePath;

    private CameraSurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private TextView mTvBottomDesc;
    private RecorderProgressBar mPgbRecording;
    private ImageView mImgController;
    private Button mBtnLight;
    private TextView mTvCancelHint;

    private CameraMgr mCameraMgr;
    private RecorderInterface mRecorder;
    private boolean mIsOpenCamera;
    private boolean mIsCancel;
    private float mDownY;
    private OnRecordListener mRecordListener;

    public ShortVideoRecorder(Context context)
    {
        super(context);
        initParams(context, null);
        initUI(context);
    }

    public ShortVideoRecorder(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initParams(context, attrs);
        initUI(context);
    }

    private void initParams(Context context, AttributeSet attrs)
    {
        TypedArray ty = context.obtainStyledAttributes(attrs, R.styleable.ShortVideoRecorder);
        if (ty != null)
        {
            mMaxDuration = ty.getInteger(R.styleable.ShortVideoRecorder_max_duration, DEF_MAX_DURATION);
            mMinDuration = ty.getInteger(R.styleable.ShortVideoRecorder_min_duration, DEF_MIN_DURATION);
            mHWRate = ty.getFloat(R.styleable.ShortVideoRecorder_video_height_width_rate, DEF_HW_RATE);
            ty.recycle();
        }
        //默认缓存路径为Sd卡
        mCacheFolder = ShortVideoCommonUtil.getSdPath() + "/";
        mCancelRange = -getResources().getDimensionPixelSize(R.dimen.short_video_cancel_distance);
        mCameraMgr = new CameraMgr();
        mRecorder = new MediaRecorderSystemImpl();
    }

    private void initUI(Context context)
    {
        inflate(context, R.layout.layout_short_video_recorder, this);
        setWillNotDraw(false);

        mSurfaceView = (CameraSurfaceView) findViewById(R.id.sfv_shortvideo_record);
        mSurfaceView.setCameraManager(mCameraMgr);
        mTvBottomDesc = (TextView) findViewById(R.id.tv_short_video_record_hint);
        mPgbRecording = (RecorderProgressBar) findViewById(R.id.rpgb_short_video_record);
        mPgbRecording.setMaxDuration(mMaxDuration);
        mImgController = (ImageView) findViewById(R.id.img_short_video_record_controller);
        mBtnLight = (Button) findViewById(R.id.btn_short_video_record_light);
        mTvCancelHint = (TextView) findViewById(R.id.tv_shortvideo_cancel_hint);

        mSurfaceHolder = mSurfaceView.getSurfaceView().getHolder();
        mSurfaceHolder.addCallback(this);
        setBottomDesc();
        setPgbMaxDuration();
        mImgController.setOnTouchListener(mControllerTouchListener);
        mPgbRecording.setOnTimeEndListener(this);
        mBtnLight.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //根据传入的参数，分别获取测量模式和测量值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int hieghtMode = MeasureSpec.getMode(heightMeasureSpec);

        //如果测量模式为非精确值
        if (widthMode != MeasureSpec.EXACTLY || hieghtMode != MeasureSpec.EXACTLY)
        {
            // 设置为背景图的宽度
            mViewWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽
            mViewWidth = mViewWidth == 0 ? ShortVideoCommonUtil.getScreenWidth(getContext()) : mViewWidth;

            //设置为背景图高度
            mViewHeight = getSuggestedMinimumHeight();
            //如果未设置背景图，则设置为屏幕高度
            mViewHeight = mViewHeight == 0 ? ShortVideoCommonUtil.getScreenHeight(getContext()) : mViewHeight;
        } else
        {
            mViewWidth = width;
            mViewHeight = height;
        }

        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
                adjustSurfaceView();
    }

    /**
     * 获取缓存文件夹路径
     */
    public String getCacheFolder()
    {
        return mCacheFolder;
    }

    /**
     * 设置缓存文件夹路径
     */
    public void setCacheFolder(String cacheFolder)
    {
        this.mCacheFolder = cacheFolder;
    }

    /**
     * 获取最大录制时间，单位ms
     */
    public long getMaxDuration()
    {
        return mMaxDuration;
    }

    /**
     * 设置最大录制时间，单位ms
     */
    public void setMaxDuration(long maxDuration)
    {
        this.mMaxDuration = maxDuration;
        setBottomDesc();
        setPgbMaxDuration();
    }

    /**
     * 获取最小录制时间，单位ms
     */
    public long getMinDuration()
    {
        return mMinDuration;
    }

    /**
     * 设置最小录制时间，单位ms
     */
    public void setMinDuration(long minDuration)
    {
        this.mMinDuration = minDuration;
        setBottomDesc();
    }

    /**
     * 获取视频高宽比
     */
    public float getHWRate()
    {
        return mHWRate;
    }

    /**
     * 设置视频高宽比
     */
    public void setHWRate(float mHWRate)
    {
        this.mHWRate = mHWRate;
    }

    //设置底部说明
    private void setBottomDesc()
    {
        if (mTvBottomDesc != null)
        {
            String descEX = getResources().getString(R.string.tv_shortvideo_record_hint);
            String desc = descEX.replaceFirst("%%1", String.valueOf(mMinDuration / 1000)).
                    replaceFirst("%%2", String.valueOf(mMaxDuration / 1000));
            mTvBottomDesc.setText(desc);
        }
    }

    //设置滚动条最大时间
    private void setPgbMaxDuration()
    {
        if (mPgbRecording != null)
            mPgbRecording.setMaxDuration(mMaxDuration);
    }

    //调整SurfaceView的大小
    private void adjustSurfaceView()
    {
        if (mSurfaceView != null)
        {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) mSurfaceView.getLayoutParams();
            layoutParams.width = mViewWidth;
            layoutParams.height = (int) (layoutParams.width * mHWRate);
            mSurfaceView.setLayoutParams(layoutParams);
        }
    }

    //重置某些参数
    private void resetParams()
    {
        mOutputFilePath = null;
        mIsCancel = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        initCamera(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        //设置最优尺寸预览
        Camera.Size size = mCameraMgr.getOptimalPreviewSize(Math.max(width, height));
        if (size != null)
        {
            mCameraMgr.adjustPreviewSize(size, holder);
            mSurfaceView.requestLayout();
            mSurfaceView.focusOnTouch(mSurfaceView.getWidth() / 2f, mSurfaceView.getHeight() / 2f);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        releaseCamera();
    }

    @Override
    public void onTimeEnd(long maxDuration)
    {
        Log.i(TAG, "ShortVideoRecorder stopRecord due to run out of time");
        stopRecord();
    }

    /**
     * 开始录制
     */
    public synchronized void startRecord()
    {
        if (mRecorder.isRecording())
            return;

        //检查SD卡是否存在
        if (!ShortVideoCommonUtil.isSDCardMounted())
        {
            if (mRecordListener != null)
                mRecordListener.onRecordError(R.string.error_sdcard_not_exist);
            return;
        }
        //检查SD卡剩余空间是否够10M
        if (ShortVideoCommonUtil.getAvailableExternalMemorySize() < MIN_MEMORY_SPACE)
        {
            if (mRecordListener != null)
                mRecordListener.onRecordError(R.string.error_sdcard_not_enough);
            return;
        }

        mCameraMgr.unlock();
        initCamera(mSurfaceHolder);
        mOutputFilePath = ShortVideoCommonUtil.createNewFileName(mCacheFolder);
        mRecorder.initRecorder(mCameraMgr.getCamera()
                , mCameraMgr.getCameraId()
                , mSurfaceHolder.getSurface()
                , mOutputFilePath);

        if (mRecorder.startRecord())
        {
            mPgbRecording.start();
            mTvCancelHint.setVisibility(VISIBLE);
            setCancelHintNormal();
        } else
        {
            if (mRecordListener != null)
                mRecordListener.onRecordError(R.string.error_camera_start_record_fail);
        }
    }

    //初始化相机
    public void initCamera(SurfaceHolder holder)
    {
        if (mIsOpenCamera)
            return;

        try
        {
            mIsOpenCamera = mCameraMgr.initCamera(getContext(), holder);
        } catch (Exception e)
        {
            Log.e(TAG, "ShortVideoRecorder init camera fail:" + e.toString());
            if (mRecordListener != null)
                mRecordListener.onRecordError(R.string.error_camera_can_not_access);
            releaseCamera();
        }
    }

    //释放相机
    private void releaseCamera()
    {
        mCameraMgr.lock();
        //如果闪光灯打开了要关闭
        if (mCameraMgr.isLightOn())
        {
            if (mCameraMgr.closeFlashLight())
                mBtnLight.setBackgroundResource(R.drawable.ic_shortvideo_light_normal);
        }
        mCameraMgr.releaseCameraResource();
        mIsOpenCamera = false;
    }

    //设置取消录制的正常提醒
    private void setCancelHintNormal()
    {
        if (mTvCancelHint != null)
        {
            mTvCancelHint.setText(R.string.tv_shortvideo_cancel_hint_normal);
            mTvCancelHint.setTextColor(Color.WHITE);
        }
    }

    //设置取消录制的松手提醒
    private void setCancelHintRelease()
    {
        if (mTvCancelHint != null)
        {
            mTvCancelHint.setText(R.string.tv_shortvideo_cancel_hint_release);
            mTvCancelHint.setTextColor(getResources().getColor(R.color.red_wx_progress_bar));
        }
    }

    /**
     * 停止录制
     */
    public synchronized void stopRecord()
    {
        mTvCancelHint.setVisibility(INVISIBLE);
        long duration = mPgbRecording.getPastTime();
        mPgbRecording.reset();

        mRecorder.stopRecord();
        mRecorder.releaseRecorder();
        releaseCamera();

        if (TextUtils.isEmpty(mOutputFilePath))
            return;
        Log.i(TAG, "ShortVideo path = " + mOutputFilePath + "\nShortVideo duration = " + duration);
        boolean isTooShort = duration < mMinDuration;
        if (mIsCancel || isTooShort)
        {
            //重新打开相机
            mPgbRecording.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    initCamera(mSurfaceHolder);
                }
            }, 300);
            //删除文件
            ShortVideoCommonUtil.deleteFile(mOutputFilePath);
            if (isTooShort)
            {
                if (mRecordListener != null)
                    mRecordListener.onRecordError(R.string.warning_shortvideo_time_too_short);
            }
        } else
        {
            if (mRecordListener != null)
                mRecordListener.onRecordFinish(mOutputFilePath, duration);
        }

        resetParams();
    }

    private OnTouchListener mControllerTouchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mImgController.setImageResource(R.drawable.img_shortvideo_controller_pressed);
                    mIsCancel = false;
                    mDownY = event.getY();
                    startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mRecorder.isRecording())
                        return false;

                    float y = event.getY();
                    if (y - mDownY < mCancelRange)
                    {
                        if (!mIsCancel)
                        {
                            mIsCancel = true;
                            mPgbRecording.setState(RecorderProgressBar.State.CANCEL);
                            setCancelHintRelease();
                        }
                    } else
                    {
                        mIsCancel = false;
                        mPgbRecording.setState(RecorderProgressBar.State.RUNNING);
                        setCancelHintNormal();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mImgController.setImageResource(R.drawable.img_shortvideo_controller_normal);
                    stopRecord();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mImgController.setImageResource(R.drawable.img_shortvideo_controller_normal);
                    mIsCancel = true;
                    stopRecord();
                    break;
            }
            return true;
        }
    };

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_short_video_record_light)
        {
            //正在录制就不让切换
            if (mRecorder.isRecording())
                return;

            if (mCameraMgr.isLightOn())
            {
                if (mCameraMgr.closeFlashLight())
                    mBtnLight.setBackgroundResource(R.drawable.ic_shortvideo_light_normal);
            } else
            {
                if (mCameraMgr.openFlashLight())
                    mBtnLight.setBackgroundResource(R.drawable.ic_shortvideo_light_pressed);
                else if (mRecordListener != null)
                    mRecordListener.onRecordError(R.string.error_camera_can_not_open_light);
            }
        }
    }

    public void setOnRecordListener(OnRecordListener listener)
    {
        this.mRecordListener = listener;
    }

    public interface OnRecordListener
    {
        void onRecordFinish(String filePath, long duration);

        void onRecordError(int errResId);
    }
}
