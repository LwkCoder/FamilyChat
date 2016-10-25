package com.lwk.familycontact.project.chat.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.lib.base.log.KLog;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.common.FCError;
import com.lwk.familycontact.storage.sp.SpSetting;

/**
 * Created by LWK
 * TODO 语音消息播放帮助类
 * 2016/9/22
 */
public class VoiceMessagePlayUtils
{
    private Context mContext;
    //语音播放player
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private HxChatPresenter mPresenter;
    //是否插入耳机
    private boolean mIsHeadSetMode;

    public VoiceMessagePlayUtils(Context context, HxChatPresenter presenter)
    {
        this.mContext = context.getApplicationContext();
        this.mPresenter = presenter;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
    }

    /**
     * 播放语音
     *
     * @param filePath 语音地址
     * @param listener 监听
     */
    public synchronized void playVoice(String filePath, final VoiceMessagePlayListener listener)
    {
        if (StringUtil.isEmpty(filePath))
        {
            if (listener != null)
                listener.error(FCError.VOICE_PLAY_ERROR, R.string.error_play_voice_message);
            return;
        }

        try
        {
            stopVoice();
            //获取当前是否为耳机模式
            mIsHeadSetMode = mAudioManager.isWiredHeadsetOn();
            final boolean isHandFree = SpSetting.isVoiceMsgHandFreeEnable(mContext);
            //耳机插入的时候直接用听筒模式播放
            if (mIsHeadSetMode)
            {
                setAudioModeOfInCall();
            } else
            {
                //开启扬声器播放语音
                if (isHandFree)
                {
                    setAudioModeOfHandFree();
                } else
                {
                    //提示用户当前是听筒模式
                    mPresenter.showVoicePlayInCallWarning();
                    setAudioModeOfInCall();
                }
            }

            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mp.start();
                    if (listener != null)
                        listener.startPlay(isHandFree);
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    playEndSound(isHandFree, listener);
                }
            });
        } catch (Exception e)
        {
            KLog.e("VoiceMessagePlayUtils playVoice() error:" + e.toString());
            if (listener != null)
                listener.error(FCError.VOICE_PLAY_ERROR, R.string.error_play_voice_message);
            mPresenter.closeVoicePlayInCallWarning();
        }
    }

    //播放语音消息结束后的“滴”
    private void playEndSound(final boolean isHandFree, final VoiceMessagePlayListener listener)
    {
        if (mMediaPlayer != null)
        {
            mMediaPlayer.reset();
            mMediaPlayer = MediaPlayer.create(FCApplication.getInstance(), R.raw.play_end);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    if (listener != null)
                        listener.endPlay(isHandFree);
                }
            });
            mMediaPlayer.start();
        }
    }

    /**
     * 停止播放语音
     */
    public void stopVoice()
    {
        if (mMediaPlayer != null)
        {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.reset();//4.4需要【mediaplayer went away with unhandled events】
            mPresenter.closeVoicePlayInCallWarning();
        }
    }

    /**
     * 设置为扬声器模式
     */
    private void setAudioModeOfHandFree()
    {
        if (mAudioManager != null)
        {
            mAudioManager.setSpeakerphoneOn(true);
            mAudioManager.setMode(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    /**
     * 设置为听筒模式
     */
    private void setAudioModeOfInCall()
    {
        if (mAudioManager != null)
        {
            mAudioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mMediaPlayer.setAudioStreamType(AudioManager.MODE_IN_COMMUNICATION);
        }
    }

    /**
     * 播放语音的过程中监听到耳机插入
     */
    public void notifyHeadSetIn()
    {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            mAudioManager.setSpeakerphoneOn(false);// 关闭扬声器
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        }
        mIsHeadSetMode = true;
    }

    /**
     * 播放语音的过程中监听到耳机拔出
     */
    public void notifyHeadSetOut()
    {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
        {
            //开启扬声器播放语音
            final boolean isHandFree = SpSetting.isVoiceMsgHandFreeEnable(mContext);
            if (isHandFree)
            {
                mAudioManager.setSpeakerphoneOn(true);
                mAudioManager.setMode(AudioManager.STREAM_MUSIC);
            } else
            {
                mAudioManager.setSpeakerphoneOn(false);
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
        }
        mIsHeadSetMode = false;
    }

    /**
     * 释放资源
     */
    public void release()
    {
        if (mMediaPlayer != null)
        {
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioManager != null)
        {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setSpeakerphoneOn(false);
            mAudioManager = null;
        }
    }
}
