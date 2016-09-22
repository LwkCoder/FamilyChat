package com.lwk.familycontact.project.chat.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.lib.base.log.KLog;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.common.FCError;
import com.lwk.familycontact.storage.sp.SpSetting;

import java.io.IOException;

/**
 * Created by LWK
 * TODO 语音消息播放帮助类
 * 2016/9/22
 */
public class VoicePlayUtils
{
    private Context mContext;
    //语音播放player
    private MediaPlayer mMediaPlayer;

    public VoicePlayUtils(Context context)
    {
        this.mContext = context.getApplicationContext();
    }

    private MediaPlayer initPlayer()
    {
        if (mMediaPlayer == null)
        {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            mMediaPlayer = new MediaPlayer();
            //开启扬声器播放语音
            if (SpSetting.isVoiceMsgHandFreeEnable(mContext))
            {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            } else
            {
                audioManager.setSpeakerphoneOn(false);// 关闭扬声器
                // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
        }
        return mMediaPlayer;
    }

    /**
     * 播放语音
     *
     * @param filePath 语音地址
     * @param listener 监听
     */
    public void playVoice(String filePath, final VoicePlayListener listener)
    {
        if (StringUtil.isEmpty(filePath))
        {
            if (listener != null)
                listener.error(FCError.VOICE_PLAY_ERROR, R.string.error_play_voice_message);
            return;
        }

        stopVoice();
        initPlayer();

        try
        {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mp.start();
                    if (listener != null)
                        listener.startPlay();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    if (listener != null)
                        listener.endPlay();
                }
            });
        } catch (IOException e)
        {
            KLog.e("VoicePlayUtils playVoice() error:" + e.toString());
            if (listener != null)
                listener.error(FCError.VOICE_PLAY_ERROR, R.string.error_play_voice_message);
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
            {
                mMediaPlayer.stop();
                mMediaPlayer.reset();//4.4需要【mediaplayer went away with unhandled events】
                mMediaPlayer.release();
            }
            mMediaPlayer = null;
        }
    }
}
