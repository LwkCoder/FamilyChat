package com.lwk.familycontact.widget.shortvideo;

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lib.base.utils.ScreenUtils;
import com.lwk.familycontact.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LWK
 * TODO 短视频录制控件
 * 2016/10/10
 */
public class ShortVideoRecorder extends LinearLayout implements MediaRecorder.OnErrorListener
{
    private final String TAG = this.getClass().getSimpleName();
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mProgressBar;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器

    private int mWidth;// 视频分辨率宽度,默认为屏幕宽度
    private int mHeight;// 视频分辨率高度，默认为屏幕高度
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private String mCacheDir;//缓存文件夹
    private File mVecordFile = null;// 文件
    private OnShortVideoRecordFinishListener mListener;

    public ShortVideoRecorder(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public ShortVideoRecorder(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ShortVideoRecorder(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShortVideoRecorder, defStyleAttr, 0);
        mWidth = a.getInteger(R.styleable.ShortVideoRecorder_video_width, ScreenUtils.getScreenWidth(context));// 默认为屏幕宽度
        mHeight = a.getInteger(R.styleable.ShortVideoRecorder_video_height, ScreenUtils.getScreenHeight(context));// 默认为屏幕高度
        mRecordMaxTime = a.getInteger(R.styleable.ShortVideoRecorder_video_max_time, 10);// 默认为10

        mCacheDir = getDefCacheDir();

        LayoutInflater.from(context).inflate(R.layout.layout_short_video_recorder, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        a.recycle();
    }

    private class CustomCallBack implements SurfaceHolder.Callback
    {
        @Override
        public void surfaceCreated(SurfaceHolder holder)
        {
            try
            {
                initCamera();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            releaseCamera();
        }
    }

    /**
     * 初始化摄像头
     */
    private void initCamera() throws IOException
    {
        if (mCamera != null)
            releaseCamera();

        try
        {
            mCamera = Camera.open();
        } catch (Exception e)
        {
            Log.e(TAG, "initCamera fail:" + e.toString());
            releaseCamera();
        }

        if (mCamera == null)
            return;

        setCameraParams();
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.autoFocus(null);
        mCamera.unlock();
    }

    /**
     * 设置摄像头为竖屏
     */
    private void setCameraParams()
    {
        if (mCamera != null)
        {
            Camera.Parameters params = mCamera.getParameters();
            params.set("orientation", Camera.Parameters.SCENE_MODE_PORTRAIT);
            mCamera.setParameters(params);
        }
    }

    /**
     * 释放摄像头资源
     */
    private synchronized void releaseCamera()
    {
        if (mCamera != null)
        {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private void createRecordFile()
    {
        //录制的视频保存文件夹
        File cacheDir = new File(mCacheDir);//录制视频的保存地址
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        // 创建文件
        try
        {
            mVecordFile = File.createTempFile("short_video", ".mp4", cacheDir);// mp4格式的录制的视频文件
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    private void initRecorder()
    {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();

        try
        {
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setOnErrorListener(this);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频格式
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式
            mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
            mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：
            //            mMediaRecorder.setVideoFrameRate(16);// 设置帧率【需要硬件支持，如果不支持就会报错：start fail:-19】
            mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024 * 100);// 设置帧频率，然后就清晰了
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (Exception e)
        {
            Log.e(TAG, "initRecorder fail:" + e.toString());
        }
    }

    /**
     * 开始录制视频
     */
    public void startRecord()
    {
        createRecordFile();
        try
        {
            initRecorder();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    mTimeCount++;
                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    // 达到指定时间，停止拍摄
                    if (mTimeCount == mRecordMaxTime)
                        stopRecord();
                }
            }, 0, 1000);
        } catch (Exception e)
        {
            Log.e(TAG, "startRecord fail:" + e.toString());
        }
    }

    /**
     * 停止拍摄
     */
    public void stopRecord()
    {
        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        resetRecorder();
        if (mListener != null)
            mListener.onShortVideoRecordFinish(mVecordFile, mTimeCount);
    }

    /**
     * 重置Recorder
     */
    private void resetRecorder()
    {
        if (mMediaRecorder != null)
        {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try
            {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            } catch (Exception e)
            {
                Log.e(TAG, "resetRecorder fail:" + e.toString());
            }
        }
        mMediaRecorder = null;
    }

    /**
     * 释放资源
     */
    public void relesaseResource()
    {
        if (mMediaRecorder != null)
            mMediaRecorder.release();
        if (mCamera != null)
            mCamera.release();
    }

    /**
     * 获取录制时间
     */
    public int getTimeCount()
    {
        return mTimeCount;
    }

    /**
     * 返回录制的视频文件
     */
    public File getShortVideoFile()
    {
        return mVecordFile;
    }

    /**
     * 设置缓存文件夹
     */
    public void setCachePath(String cachePath)
    {
        this.mCacheDir = cachePath;
    }

    //获取默认缓存文件夹
    private String getDefCacheDir()
    {
        return new StringBuffer()
                .append(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append("/ShortVideoCacahe/")
                .toString();
    }

    /**
     * 设置录制完成监听
     */
    public void setOnShortVideoRecordFinishListener(OnShortVideoRecordFinishListener l)
    {
        this.mListener = l;
    }

    /**
     * 录制完成回调接口
     */
    public interface OnShortVideoRecordFinishListener
    {
        void onShortVideoRecordFinish(File videoFile, int seconds);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra)
    {
        try
        {
            if (mr != null)
                mr.reset();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
