package com.lwkandroid.widget;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 录音帮助类
 * Created by LuoWK on 15/10/5.
 */
public class RecordAudioManager
{
    private MediaRecorder mRecorder;
    //保存文件的文件夹路径
    private String mDir;
    //保存文件的绝对路径
    private String mFilePath;
    //准备结束后的回调
    private PrepareListener mListener;
    //标识recorder是否已经开始录音
    private boolean mIsRecording;

    private RecordAudioManager()
    {
    }

    private static RecordAudioManager mInstance;

    public static RecordAudioManager getInstance()
    {

        if (mInstance == null)
        {
            synchronized (RecordAudioManager.class)
            {
                if (mInstance == null)
                    mInstance = new RecordAudioManager();
            }
        }
        return mInstance;
    }

    //准备完成的回调
    public interface PrepareListener
    {
        void prepared();
    }

    public void setOnPreparedListener(PrepareListener listener)
    {
        this.mListener = listener;
    }

    protected void setDirPath(String path)
    {
        this.mDir = path;
    }

    /**
     * 初始化recorder
     */
    public synchronized void prepareAudio()
    {
        //还原标识
        mIsRecording = false;
        if (mDir == null && mDir.equals(""))
        {
            Log.e("RecordAudioManager", "prepareAudio fail because dirPath is null !!!");
            return;
        }

        if (mRecorder != null)
            release();

        try
        {
            //检查路径是否存在，不存在就创建
            File dir = new File(mDir);
            if (!dir.exists())
                dir.mkdirs();
            //生成文件名
            String fileName = createFileName();
            //生成保存文件的file
            File file = new File(dir, fileName);
            mFilePath = file.getAbsolutePath();
            //初始化mediaRecord
            mRecorder = new MediaRecorder();
            mRecorder.setOutputFile(mFilePath);//设置输出路径
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置音频输入源为麦克风
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//设置音频格式为amr
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码为amr
            mRecorder.prepare();
            //回调
            if (mListener != null)
                mListener.prepared();

        } catch (IOException e)
        {
            Log.e("RecordAudioManager", "prepareAudio fail because IOException");
        }


    }

    //生成文件名的方法
    private String createFileName()
    {
        return "VIC_" + System.currentTimeMillis() + ".amr";
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
            mRecorder.start();
            mIsRecording = true;
        }
    }

    /**
     * 释放recorder
     */
    public synchronized void release()
    {
        if (mRecorder != null)
        {
//            Log.e("", "RecordMgr--->release");
            if (mIsRecording)
            {
                mRecorder.stop();
                mRecorder.release();
            }
            mIsRecording = false;
            mRecorder = null;
        }
    }

    /**
     * 取消录音
     */
    public void cancel()
    {
//        Log.e("", "RecordMgr--->cancel");
        release();
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
}
