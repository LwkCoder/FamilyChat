package com.lwk.familycontact.project.call.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;

import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.im.helper.HxCallHelper;

/**
 * Created by LWK
 * TODO 实时通话界面基类
 * 2016/10/21
 */
public abstract class HxBaseCallActivity extends FCBaseActivity
{
    //震动管理器
    protected Vibrator mVibratorMgr;
    //音频AudioManager
    protected AudioManager mAudioMgr;
    //音频
    protected SoundPool mSoundPool;
    //音频播放器
    protected MediaPlayer mMediaPlayer;
    //忙音流
    protected int mWaitStreamId;

    @Override
    protected void beforeInitUI(Bundle savedInstanceState)
    {
        super.beforeInitUI(savedInstanceState);
        //获取音频管理器
        mAudioMgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mAudioMgr.setSpeakerphoneOn(false);
        if (mAudioMgr.isWiredHeadsetOn())//耳机模式下设置Mode为Communication，否则设置为Ringtong
            mAudioMgr.setMode(AudioManager.MODE_IN_COMMUNICATION);
        else
            mAudioMgr.setMode(AudioManager.MODE_RINGTONE);
        //获取震动管理器
        mVibratorMgr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 播放忙音
     */
    protected void playWaittingRingtong(int rawResId)
    {
        if (mSoundPool == null)
        {
            mSoundPool = new SoundPool(1, AudioManager.MODE_RINGTONE, 0);
            final int waitSoundId = mSoundPool.load(this, rawResId, 1);
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
            {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
                {
                    mWaitStreamId = mSoundPool.play(waitSoundId, 0.99f, 0.99f, 1, -1, 1);
                }
            });
        }
    }

    /**
     * 停止播放忙音
     */
    protected void stopWaittingRingtong()
    {
        if (mSoundPool != null)
        {
            if (mWaitStreamId != 0)
                mSoundPool.stop(mWaitStreamId);
            mSoundPool.release();
            mSoundPool = null;
            mWaitStreamId = 0;
        }
    }

    //切换免提开关
    protected void switchHandsFreeMode(boolean isHandsFree)
    {
        if (mAudioMgr == null)
            return;

        mAudioMgr.setSpeakerphoneOn(isHandsFree);
    }

    //切换静音开关
    protected void switchMuteMode(boolean isMute)
    {
        if (isMute)
            HxCallHelper.getInstance().pauseVoiceTransfer();
        else
            HxCallHelper.getInstance().resumeVoiceTransfer();
    }

    /**
     * 电话被接听后震动一次
     */
    protected void vibrateByPickUpPhone()
    {
        long[] pattern = new long[]{0, 500};
        mVibratorMgr.vibrate(pattern, -1);
    }

    /**
     * 收到来电还未接起前震动
     */
    protected void vibrateWithRingtong()
    {
        long[] pattern = new long[]{1500, 1200, 1500, 1200};
        mVibratorMgr.vibrate(pattern, 0);
    }

    /**
     * 播放来电铃声
     */
    protected void playInComingRingtong(int rawResId)
    {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, rawResId);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    /**
     * 来电铃声是否在播放
     */
    protected boolean isInComingCallRingtongPlaying()
    {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    /**
     * 停止来电铃声
     */
    protected void stopInComingRingtong()
    {
        if (isInComingCallRingtongPlaying())
        {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        stopWaittingRingtong();
        stopInComingRingtong();
        if (mAudioMgr != null)
        {
            mAudioMgr.setMode(AudioManager.MODE_NORMAL);
            mAudioMgr.setMicrophoneMute(false);
            mAudioMgr = null;
        }
        if (mVibratorMgr != null)
        {
            mVibratorMgr.cancel();
            mVibratorMgr = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        //按下back无法结束会话
    }
}
