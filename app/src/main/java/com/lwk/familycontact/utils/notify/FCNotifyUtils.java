package com.lwk.familycontact.utils.notify;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.storage.sp.SpSetting;

/**
 * Created by LWK
 * TODO 新消息/通知 铃声震动提醒帮助类
 * 2016/9/5
 */
public class FCNotifyUtils
{
    private FCNotifyUtils()
    {
        //获取铃声管理器
        mAudioMgr = (AudioManager) FCApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        //获取震动管理器
        mVibratorMgr = (Vibrator) FCApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
    }

    private static final class FCNotifyUtilsHolder
    {
        private static FCNotifyUtils instance = new FCNotifyUtils();
    }

    public static FCNotifyUtils getInstance()
    {
        return FCNotifyUtilsHolder.instance;
    }

    //铃声管理器
    private AudioManager mAudioMgr;
    //震动管理器
    private Vibrator mVibratorMgr;
    //上次震动时间
    private long mLastVibratorTime;
    //上次铃声时间
    private long mLastRingtongTime;
    //铃声
    private Ringtone mRingtone;
    //震动频率
    private final long[] VIBRATOR_FREQUENCY = new long[]{0, 180, 80, 120};

    //判断系统环境
    private boolean canNotify()
    {
        // 判断手机系统是否处于静音模式
        return mAudioMgr.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
    }

    //判断震动条件
    private boolean canVibratorNotify()
    {
        return canNotify()
                && SpSetting.isNewMsgNoticeVibrate(FCApplication.getInstance())
                && System.currentTimeMillis() - mLastVibratorTime > 1000;
    }

    //判断铃声条件
    private boolean canRingtongNotify()
    {
        return canNotify()
                && SpSetting.isNewMsgNoticeVoice(FCApplication.getInstance())
                && System.currentTimeMillis() - mLastRingtongTime > 1000;
    }

    /**
     * 震动
     */
    public void vibratorNotify()
    {
        if (canVibratorNotify())
        {
            mVibratorMgr.vibrate(VIBRATOR_FREQUENCY, -1);//-1代表震动一个周期,0代表持续循环
            mLastVibratorTime = System.currentTimeMillis();
        }
    }

    /**
     * 播放铃声
     */
    public void ringtongNotify()
    {
        if (canRingtongNotify())
        {
            //响铃操作
            if (mRingtone == null)
            {
                //设置铃声为系统默认通知栏的铃声
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                mRingtone = RingtoneManager.getRingtone(FCApplication.getInstance(), notificationUri);
                if (mRingtone == null)
                    return;
            }

            //解决三星S3手机铃声不停止的问题
            if (!mRingtone.isPlaying())
            {
                String vendor = Build.MANUFACTURER;

                mRingtone.play();
                // for samsung S3, we meet a bug that the phone will
                // continue ringtone without stop
                // so add below special handler to stop it after 3s if
                // needed
                if (vendor != null && vendor.toLowerCase().contains("samsung"))
                {
                    Thread ctlThread = new Thread()
                    {
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(3000);
                                if (mRingtone.isPlaying())
                                {
                                    mRingtone.stop();
                                }
                            } catch (Exception e)
                            {
                            }
                        }
                    };
                    ctlThread.run();
                }
            }
            mLastRingtongTime = System.currentTimeMillis();
        }
    }

    /**
     * 播放铃声+震动
     */
    public void startNotify()
    {
        vibratorNotify();
        ringtongNotify();
    }
}
