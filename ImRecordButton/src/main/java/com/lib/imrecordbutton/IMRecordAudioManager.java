package com.lib.imrecordbutton;

import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by LWK
 * TODO 录音按钮录音器控制类
 * 2016/9/18
 */
public class IMRecordAudioManager
{
    //录音器
    private MediaRecorder mRecorder;
    //保存文件的文件夹路径
    private String mCachePath;
    //保存文件的绝对路径
    private String mFilePath;
    //准备结束后的回调
    private onRecorderPreparedListener mListener;
    //标识recorder是否已经开始录音
    private boolean mIsRecording;

    public IMRecordAudioManager()
    {
        //Init
        initRecorder();
    }

    //初始化
    private void initRecorder()
    {
        if (mRecorder == null)
            mRecorder = new MediaRecorder();
        //设置默认缓存路径
        mCachePath = getDefCachePath();
    }

    public interface onRecorderPreparedListener
    {
        void onPreparedSuccess();

        void onPreparedFail(int errorCode);
    }

    public void setOnRercorderPreparedListener(onRecorderPreparedListener l)
    {
        this.mListener = l;
    }

    //获取默认缓存路径
    private String getDefCachePath()
    {
        return new StringBuffer()
                .append(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append("/IMRecordCache/")
                .toString();
    }

    /**
     * 设置录音文件缓存地址
     */
    public void setCachePath(String cachePath)
    {
        this.mCachePath = cachePath;
        //检查路径是否存在，不存在就创建
        File dir = new File(mCachePath);
        if (!dir.exists())
            dir.mkdirs();
    }

    /**
     * 初始化recorder
     */
    public synchronized void prepareAudio()
    {
        //还原标识
        mIsRecording = false;
        if (TextUtils.isEmpty(mCachePath))
        {
            Log.e("RecordAudioManager", "Cache folder can not be empty !!!");
            if (mListener != null)
                mListener.onPreparedFail(IMRecordError.CACHE_PATH_EMPTY);
            return;
        }

        try
        {
            //检查路径是否存在，不存在就创建
            File dir = new File(mCachePath);
            if (!dir.exists())
                dir.mkdirs();
            //生成文件名
            String fileName = createFileName();
            //生成保存文件的file
            File file = new File(dir, fileName);
            mFilePath = file.getAbsolutePath();
            //设置mediaRecord各种参数并开始准备
            mRecorder.setOutputFile(mFilePath);//设置输出路径
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置音频输入源为麦克风
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//设置音频格式为amr
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码为amr
            mRecorder.prepare();
            //回调
            if (mListener != null)
                mListener.onPreparedSuccess();

        } catch (Exception e)
        {
            Log.e("IMRecordAudioManager", "prepareAudio fail : " + e.toString());
            if (mListener != null)
                mListener.onPreparedFail(IMRecordError.RECORD_PREPARE_FAIL);
        }
    }

    //创建录音文件的名字
    private String createFileName()
    {
        return new StringBuffer()
                .append("VIC_")
                .append(System.currentTimeMillis())
                .append(".amr")
                .toString();
    }

    /**
     * 获取实时音量级别
     *
     * @param maxLevel 预期最大的音量等级【7】
     */
    public int getVoiceLevel(int maxLevel)
    {
        if (mIsRecording)
        {
            //mRecorder.getMaxAmplitude()值的范围为1～32767
            //maxLevel * mRecorder.getMaxAmplitude() / 32768 的结果始终为0～6【整数】
            return maxLevel * mRecorder.getMaxAmplitude() / 32768 + 1;
        }

        return 1;
    }

    /**
     * 开始录音
     */
    public void start()
    {
        if (mRecorder != null)
        {
            mIsRecording = true;
            mRecorder.start();
        }
    }

    /**
     * 复位recorder
     */
    public synchronized void reset()
    {
        if (mRecorder != null)
        {
            if (mIsRecording)
                mRecorder.stop();
            mIsRecording = false;
            mRecorder.reset();
        }
    }

    /**
     * 取消录音
     */
    public void cancel()
    {
        reset();
        if (mFilePath != null)
        {
            File file = new File(mFilePath);
            file.delete();
            mFilePath = null;
        }
    }

    /**
     * 获取最近录音文件地址
     */
    public String getCurrentPath()
    {
        return mFilePath;
    }

    /**
     * 是否正在录音
     */
    public boolean isRecording()
    {
        return mIsRecording;
    }

    /**
     * 清除录音地址
     */
    public void clearFilePath()
    {
        mFilePath = null;
    }
}
