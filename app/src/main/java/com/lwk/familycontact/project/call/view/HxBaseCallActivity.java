package com.lwk.familycontact.project.call.view;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.exceptions.HyphenateException;
import com.lib.base.log.KLog;
import com.lib.base.utils.ScreenUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.im.helper.HxCallHelper;
import com.lwk.familycontact.im.listener.HxCallStateChangeListener;
import com.lwk.familycontact.project.chat.utils.HeadSetReceiver;

/**
 * Created by LWK
 * TODO 实时通话界面基类
 * 2016/10/21
 */
public abstract class HxBaseCallActivity extends FCBaseActivity implements HeadSetReceiver.onHeadSetStateChangeListener
{

    protected static final String INTENT_KEY_PHONE = "opPhone";
    protected static final String INTENT_KEY_IS_COMING_CALL = "isComingCall";
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
    //实时通话状态监听
    protected HxCallStateChangeListener mStateChangeListener;
    //头像ImageView
    protected ImageView mImgHead;
    //名字TextView
    protected TextView mTvName;
    //状态TextView
    protected TextView mTvDesc;
    //网络差提示TextView
    protected TextView mTvNetworkUnstable;
    //对方手机号
    protected String mOpPhone;
    //是否为来电
    protected boolean mIsComingCall;
    //接收方待操作区域
    protected View mViewReceiverPanel;
    //接通后控制板
    protected View mViewCallingPanel;
    //静音CheckBox
    protected CheckBox mCkMute;
    //免提CheckBox
    protected CheckBox mCkHandsFree;
    //是否主动接听电话
    protected boolean mHasAnswer = false;
    //主动去电是否被接听
    protected boolean mHasAccept = false;
    //挂断后结束界面的延迟时长
    protected static final long sDELAY_TIME = 1500L;
    //耳机监听
    protected HeadSetReceiver mHeadSetReceiver;

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        ScreenUtils.changStatusbarTransparent(this);

        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getIntentData();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        getIntentData();
    }

    private void getIntentData()
    {
        Intent intent = getIntent();
        mOpPhone = intent.getStringExtra(INTENT_KEY_PHONE);
        mIsComingCall = intent.getBooleanExtra(INTENT_KEY_IS_COMING_CALL, false);
        if (StringUtil.isEmpty(mOpPhone))
            finish();
    }

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

    @Override
    protected void initUI()
    {
        mImgHead = findView(R.id.img_call_avatar);
        mTvName = findView(R.id.tv_call_name);
        mTvDesc = findView(R.id.tv_call_desc);
        mTvNetworkUnstable = findView(R.id.tv_call_network_unstable);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mHeadSetReceiver = HeadSetReceiver.registInActivity(this, this);
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
        try
        {
            if (isMute)
                HxCallHelper.getInstance().pauseVoiceTransfer();
            else
                HxCallHelper.getInstance().resumeVoiceTransfer();
        } catch (HyphenateException e)
        {
            KLog.e("HxBaseCallActivity switchMuteMode fail:" + e.toString());
        }
    }

    //静音开关切换
    private CompoundButton.OnCheckedChangeListener mMuteListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            switchMuteMode(isChecked);
        }
    };

    //免提开关切换
    private CompoundButton.OnCheckedChangeListener mHandsFreeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            switchHandsFreeMode(isChecked);
        }
    };

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

    //展示接收到来电panel
    protected void showComingCallPanel()
    {
        ViewStub vs = findView(R.id.vs_voicecall_receiver_panel);
        mViewReceiverPanel = vs.inflate();
        addClick(R.id.btn_call_receiver_panel_rejectcall);
        addClick(R.id.btn_call_receiver_panel_answercall);
    }

    //展示通话中/去电等待panel
    protected void showCallingPanel()
    {
        ViewStub vs = findView(R.id.vs_voicecall_calling_panel);
        mViewCallingPanel = vs.inflate();
        mCkHandsFree = findView(R.id.ck_call_calling_panel_handsfree);
        mCkMute = findView(R.id.ck_call_calling_panel_mute);
        mCkHandsFree.setOnCheckedChangeListener(mHandsFreeListener);
        mCkMute.setOnCheckedChangeListener(mMuteListener);
        addClick(R.id.btn_call_calling_panel_endcall);
    }

    @Override
    public void onHeadSetStateChanged(boolean headSetIn)
    {
        if (headSetIn)
        {
            //插入耳机后免提关闭且设置为不可更改
            if (mCkHandsFree != null)
            {
                mCkHandsFree.setChecked(false);
                mCkHandsFree.setEnabled(false);
            }
            if (mAudioMgr != null)
                mAudioMgr.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else
        {
            if (mCkHandsFree != null)
                mCkHandsFree.setEnabled(true);
            //耳机拔出后，还未接通通话时设置Mode为Ringtong，否则设置为Communication
            if (mAudioMgr != null)
            {
                if (!mHasAccept && !mHasAnswer)
                    mAudioMgr.setMode(AudioManager.MODE_RINGTONE);
                else
                    mAudioMgr.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
        }
    }

    //设置静音是否可用
    protected void setMuteEnable(boolean enable)
    {
        if (mCkMute != null)
            mCkMute.setEnabled(enable);
    }

    //延迟关闭界面
    protected void finishWithDelay()
    {
        mMainHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                finish();
            }
        }, sDELAY_TIME);
    }

    @Override
    protected void onDestroy()
    {
        HeadSetReceiver.unregistFromActivity(this, mHeadSetReceiver);
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
