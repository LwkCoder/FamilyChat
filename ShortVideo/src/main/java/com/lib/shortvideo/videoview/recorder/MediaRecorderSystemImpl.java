package com.lib.shortvideo.videoview.recorder;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;

/**
 * Created by LWK
 * TODO 系统MediaRecorder实现类
 * 2016/11/1
 */
public class MediaRecorderSystemImpl implements RecorderInterface, MediaRecorder.OnErrorListener
{
    private final String TAG = "MediaRecorderSystemImpl";
    private MediaRecorder mMediaRecorder;
    private boolean mIsRecording;

    @Override
    public void initRecorder(Camera camera, int cameraId, Surface surface, String filePath)
    {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (camera != null)
            mMediaRecorder.setCamera(camera);
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//音频源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//视频输出格式 也可设为3gp等其他格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//音频格式
        mMediaRecorder.setVideoSize(640, 480);//设置分辨率，市面上大多数都支持640*480
        //        mediaRecorder.setVideoFrameRate(25);//设置每秒帧数 这个设置有可能会出问题，有的手机不支持这种帧率就会录制失败，这里使用默认的帧率，当然视频的大小肯定会受影响
        //这里设置可以调整清晰度
        mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 512);

        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
            mMediaRecorder.setOrientationHint(90);
        else
            mMediaRecorder.setOrientationHint(270);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//视频录制格式
        mMediaRecorder.setOutputFile(filePath);
    }

    @Override
    public boolean startRecord()
    {
        if (mMediaRecorder != null)
        {
            try
            {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                return mIsRecording = true;
            } catch (Exception e)
            {
                Log.e(TAG, "MediaRecorder start record fail:" + e.toString());
            }
        }
        return mIsRecording = false;
    }

    @Override
    public void stopRecord()
    {
        if (mMediaRecorder != null && mIsRecording)
        {
            try
            {
                mMediaRecorder.setOnErrorListener(null);//设置后防止崩溃
                mMediaRecorder.setPreviewDisplay(null);
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.stop();
            } catch (Exception e)
            {
                Log.e(TAG, "MediaRecorder stop record fail:" + e.toString());
            }
        }
        mIsRecording = false;
    }

    @Override
    public void releaseRecorder()
    {
        if (mMediaRecorder != null)
        {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.release();
        }
        mMediaRecorder = null;
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
            mIsRecording = false;
        }
    }

    @Override
    public boolean isRecording()
    {
        return mIsRecording;
    }
}
