package com.lwk.familycontact.project.chat.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.lib.base.log.KLog;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.common.FCError;
import com.lwk.familycontact.storage.sp.SpSetting;

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
    private AudioManager mAudioManager;
    private HxChatPresenter mPresenter;

    public VoicePlayUtils(Context context, HxChatPresenter presenter)
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
    public synchronized void playVoice(String filePath, final VoicePlayListener listener)
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
            //开启扬声器播放语音
            final boolean isHandFree = SpSetting.isVoiceMsgHandFreeEnable(mContext);
            if (isHandFree)
            {
                mAudioManager.setSpeakerphoneOn(true);
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            } else
            {
                mAudioManager.setSpeakerphoneOn(false);// 关闭扬声器
                // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
                mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mPresenter.showVoicePlayInCallWarning();//提示用户
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
                    if (listener != null)
                        listener.endPlay(isHandFree);
                }
            });
        } catch (Exception e)
        {
            KLog.e("VoicePlayUtils playVoice() error:" + e.toString());
            if (listener != null)
                listener.error(FCError.VOICE_PLAY_ERROR, R.string.error_play_voice_message);
            mPresenter.closeVoicePlayInCallWarning();
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
}
