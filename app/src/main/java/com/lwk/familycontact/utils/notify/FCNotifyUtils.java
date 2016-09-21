package com.lwk.familycontact.utils.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.hyphenate.chat.EMMessage;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.main.view.MainActivity;
import com.lwk.familycontact.storage.sp.SpSetting;

import java.util.List;

/**
 * Created by LWK
 * TODO 新消息/通知 铃声震动提醒帮助类
 * 2016/9/5
 */
public class FCNotifyUtils
{
    private FCNotifyUtils(Context context)
    {
        //获取铃声管理器
        mAudioMgr = (AudioManager) FCApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        //获取震动管理器
        mVibratorMgr = (Vibrator) FCApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        //创建通知栏管理对象
        mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static final class FCNotifyUtilsHolder
    {
        private static FCNotifyUtils instance = new FCNotifyUtils(FCApplication.getInstance());
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
        return mAudioMgr.getRingerMode() != AudioManager.RINGER_MODE_SILENT;
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
    public void ringtongAndVibratorNotify()
    {
        vibratorNotify();
        ringtongNotify();
    }

    /*******************************************
     * 通知栏相关
     ************************************************************************/

    //通知管理器
    private NotificationManager mNotifyMgr;
    //后台通知id【不显示消息详情时】
    protected int mBackNotifyId = 0x123;

    //取消通知栏提醒
    public void resetNotification()
    {
        if (mNotifyMgr != null)
            mNotifyMgr.cancelAll();
    }

    /**
     * 发送新消息通知栏提醒
     *
     * @param list 新消息
     */
    public void sendMessageNotifivation(List<EMMessage> list)
    {
        PackageManager packageManager = FCApplication.getInstance().getPackageManager();
        //将应用名设置为通知栏标题
        String title = (String) packageManager
                .getApplicationLabel(FCApplication.getInstance().getApplicationInfo());
        String message = FCApplication.getInstance().getResources().getString(R.string.notification_message);
        //创建通知栏点击意图
        Intent msgIntent = new Intent(FCApplication.getInstance(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(FCApplication.getInstance(),
                mBackNotifyId, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建notification对象
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(FCApplication.getInstance())
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_logo)//TODO 改为只使用alpha图层的Icon
                .setLargeIcon(BitmapFactory.decodeResource(FCApplication.getInstance().getResources(), R.mipmap.ic_logo))
                .setLights(Color.BLUE, 2000, 2000)//三色灯提醒,其中ledARGB 表示灯光颜色、 ledOnMS 亮持续时间、ledOffMS 暗的时间
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_SHOW_LIGHTS;//要支持三色灯，这个flag绝对不能少

        //发送通知
        mNotifyMgr.notify(mBackNotifyId, notification);
        //铃声、震动
        ringtongAndVibratorNotify();
    }
}
