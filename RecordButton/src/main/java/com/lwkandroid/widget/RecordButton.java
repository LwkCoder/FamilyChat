package com.lwkandroid.widget;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;


/**
 * 自定义录音按钮【适用于IM】
 * Created by LuoWK on 15/10/4.
 */
public class RecordButton extends Button implements RecordAudioManager.PrepareListener
{
    //正常状态
    private static final int STATE_NORMAL = 1;
    //正在录音状态
    private static final int STATE_RECORDING = 2;
    //打算取消录音状态
    private static final int STATE_WANT_CANCEL = 3;
    //最大音量等级
    private static final int MAX_VOICE_LEVEL = 7;
    //最短录音时间
    private static final float MIN_RECORD_TIME = 1f;

    //当前状态
    private int mCurState;
    //是否正在录音
    private boolean mIsRecoring;
    //向上滑动取消录音的距离
    private static int DISTANCE_Y_CANCEL;
    //dialog控制器
    private RecordDialogManager mDialogMgr;
    //录音控制器
    private RecordAudioManager mAudioMgr;
    //录音时长
    private float mTime;
    //录音完成回调
    private RecorderListener mRecorderListener;
    //屏幕常亮
    private PowerManager.WakeLock mWakeLock;
    //震动管理器
    private Vibrator mVibratorMgr;
    //是否执行了action_up，防止快速点击的判断条件
    private boolean mHasUp;
    //SD卡是否存在
    private boolean mIsSdExist = true;

    public RecordButton(Context context)
    {
        super(context, null);
    }

    public RecordButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    //录音正常完成的回调
    public interface RecorderListener
    {
        void startRecord();

        void recordFinish(float seconds, String filePath);
    }

    public void setOnRecorderListener(RecorderListener listener)
    {
        this.mRecorderListener = listener;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context)
    {
        mIsRecoring = false;
        DISTANCE_Y_CANCEL = (int) getResources().getDimension(R.dimen.recordbtn_distance_y_cancel);
        //获取屏幕常量唤醒对象
        mWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "RecordVoice");
        //获取震动管理器
        mVibratorMgr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //初始化dialog管理类
        mDialogMgr = new RecordDialogManager(context);
        changeState(STATE_NORMAL);
        //初始化录音管理类
        mAudioMgr = RecordAudioManager.getInstance();
        String defVoiceFile = Environment.getExternalStorageDirectory() + "/Android/data/VoiceCache";
        mAudioMgr.setDirPath(defVoiceFile);//设置默认输出文件夹
        mAudioMgr.setOnPreparedListener(this);
    }

    //自定义输出文件保存路径
    public void setAudioDirPath(String path)
    {
        if (mAudioMgr != null)
            mAudioMgr.setDirPath(path);
    }

    private static final int MSG_AUDIO_PREPARED = 0X001;//录音器准备完成的msg标识
    private static final int MSG_VOICE_LEVEL_CHANGED = 0X002;//音量等级发生变化的msg标识
    private static final int MSG_DIALOG_DISMISS = 0X003;//dialog消失后的msg标识


    private android.os.Handler mHandler = new android.os.Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_AUDIO_PREPARED:
                    //                    Log.e("", "prepared!!!");
                    if (mHasUp)
                    {
                        mAudioMgr.cancel();
                        mDialogMgr.showRecordingDialog();
                        mDialogMgr.tooShort();
                        mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1500);
                        return;
                    }
                    mIsRecoring = true;
                    vibrate();
                    mAudioMgr.start();
                    mDialogMgr.showRecordingDialog();
                    //开启线程实时获取音量等级并更新UI
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_LEVEL_CHANGED:
                    mDialogMgr.updateVoiceLevel(mAudioMgr.getVoiceLevel(MAX_VOICE_LEVEL));
                    break;
                case MSG_DIALOG_DISMISS:
                    mDialogMgr.dismissDialog();
                    break;
            }
        }
    };

    //获取音量大小的runnable，顺便计算录音时长
    private Runnable mGetVoiceLevelRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            while (mIsRecoring)
            {
                try
                {
                    Thread.sleep(100);
                    mTime += 0.1f;//计算时长
                    mHandler.sendEmptyMessage(MSG_VOICE_LEVEL_CHANGED);
                } catch (InterruptedException e)
                {
                    Log.e("RecordButton", "GetVoiceLevel Thread Sleep Be Interrupt");
                }
            }
        }
    };

    @Override
    public synchronized boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                //                Log.e("", "ACTION_DOWN");
                mHasUp = false;
                mWakeLock.acquire();
                changeState(STATE_RECORDING);
                //检查sd卡
                if (!isSdcardExits())
                {
                    mIsSdExist = false;
                    mDialogMgr.showRecordingDialog();
                    mDialogMgr.noSdcard();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1500);//延迟关闭dialog
                    //这里也回调主要是考虑到录音前有正在播放的语音也需要告诉Act停止播放
                    if (mRecorderListener != null)
                        mRecorderListener.startRecord();
                    return super.onTouchEvent(event);
                }
                //正常执行
                mAudioMgr.prepareAudio();
                if (mRecorderListener != null)
                    mRecorderListener.startRecord();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsRecoring)
                {
                    //根据x，y坐标判断是否打算取消录音
                    if (wantCancel(x, y))
                    {
                        mDialogMgr.wantToCancel();
                        changeState(STATE_WANT_CANCEL);
                    } else
                    {
                        mDialogMgr.recording();
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //                Log.e("", "ACTION_UP");
                mHasUp = true;
                if (mWakeLock != null && mWakeLock.isHeld())
                    mWakeLock.release();
                //录音状态下取消录音【之所以把它放在前面判断是因为用户执行取消手势后无需显示时间太短的dialog】
                if (mIsRecoring && mCurState == STATE_WANT_CANCEL)
                {
                    mDialogMgr.dismissDialog();
                    //cancelRecord
                    mAudioMgr.cancel();
                }
                //如果录音器没有初始化完成或者录音时间太短，则关闭录音器
                else if ((!mIsRecoring || mTime < MIN_RECORD_TIME) && mIsSdExist)
                {
                    Log.e("RecordButton", "Recorder not prepared or time too short !!!");
                    mDialogMgr.tooShort();
                    mAudioMgr.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1500);//延迟关闭dialog
                }
                //正常录音
                else if (mIsRecoring && mCurState == STATE_RECORDING)
                {
                    mDialogMgr.dismissDialog();
                    //releaseRecord
                    mAudioMgr.release();
                    //callBackToActivity
                    if (mRecorderListener != null)
                        mRecorderListener.recordFinish(mTime, mAudioMgr.getCurrentPath());
                }
                //恢复状态和标识
                reset();
                break;
            //监听ACTION_CANCEL，防止某些rom在第一次使用录音时弹出权限dialog，导致不触发ACTION_UP
            case MotionEvent.ACTION_CANCEL:
                mHasUp = true;
                if (mWakeLock != null && mWakeLock.isHeld())
                    mWakeLock.release();
                if (mIsRecoring)
                {
                    mDialogMgr.dismissDialog();
                    //cancelRecord
                    mAudioMgr.cancel();
                }
                //恢复状态和标识
                reset();
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void prepared()
    {
        //        Log.e("RecordButton", "HasUp=" + mHasUp);
        if (mHasUp)
            mAudioMgr.cancel();
        else
            mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    /**
     * 切换状态
     */
    private void changeState(int state)
    {
        if (mCurState == state)
            return;

        switch (state)
        {
            case STATE_NORMAL:
                setBackgroundResource(R.drawable.recordbtn_state_normal_bg);
                setText(R.string.str_recordbtn_state_normal);
                break;
            case STATE_RECORDING:
                setBackgroundResource(R.drawable.recordbtn_state_recording_bg);
                setText(R.string.str_recordbtn_state_recording);
                break;
            case STATE_WANT_CANCEL:
                setBackgroundResource(R.drawable.recordbtn_state_recording_bg);
                setText(R.string.str_recordbtn_state_want_cancel);
                break;
        }
        mCurState = state;
    }

    /**
     * 判断是否打算取消录音
     * 【在xy坐标轴上判断手指触摸点：x轴上只要超出按钮区域就视为打算取消
     * ，y轴上超出按钮区域外自定义距离就视为打算取消】
     */
    private boolean wantCancel(int x, int y)
    {
        if (x < 0 || x > getWidth())
            return true;
        else if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL)
            return true;
        return false;
    }

    /**
     * 恢复标志位和状态
     */
    private void reset()
    {
        mIsRecoring = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    //准备完成后开始录音时震动一下
    private void vibrate()
    {
        if (mVibratorMgr != null)
        {
            long[] l = new long[]{0, 50};
            mVibratorMgr.vibrate(l, -1);
        }
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    private boolean isSdcardExits()
    {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
