package com.lib.imrecordbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by LWK
 * 带录音功能的自定义Button
 * 使用此控件前，请在业务代码中确认获取RECORD_AUDIO、WRITE_EXTERNAL_STORAGE权限
 * 2016/9/18
 */
public class IMRecordButton extends Button implements IMRecordAudioManager.onRecorderPreparedListener
{
    private IMRecordViewImpl mViewImpl;
    private IMRecordAudioManager mAudioManager;
    //震动管理器
    private Vibrator mVibratorMgr;
    //屏幕常亮
    private PowerManager.WakeLock mWakeLock;
    //录音监听
    private IMRecordListener mRecordListener;
    //是否执行了action_up，防止快速点击的判断条件
    private boolean mHasUp;
    //录音时长
    private float mTime;
    //当前状态
    private int mCurState;
    //取消录音的Y轴距离
    private final int DISTANCE_CANCEL_Y = getResources().getDimensionPixelSize(R.dimen.distance_cancel_y);
    //AudioManager是否准备失败
    private boolean mIsAudioPreparedFail;
    //正常状态下文案
    private String mStrStateNormal;
    //录音状态下文案
    private String mStrStateRecording;
    //欲取消状态下文案
    private String mStrStateWantCancel;
    //正常状态下背景图
    private int mBgResIdNormal;
    //按下状态下背景图
    private int mBgResIdPressed;
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
    //录音器准备成功的msg标识
    private static final int MSG_AUDIO_PREPARED_SUCCESS = 0X100;
    //录音器准备失败的msg标识
    private static final int MSG_AUDIO_PREPARED_FAIL = 0x101;
    //sd卡不存在的msg标识
    private static final int MSG_SDCARD_NOT_EXIST = 0X102;
    //正在录音的msg标识
    private static final int MSG_RECORDING = 0X103;
    //音量等级发生变化的msg标识
    private static final int MSG_VOICE_LEVEL_CHANGED = 0X104;
    //与取消的msg标识
    private static final int MSG_WANT_CANCEL = 0X105;
    //录音时间太短的msg标识
    private static final int MSG_VOICE_TOO_SHORT = 0x106;
    //录音完成的msg标识
    private static final int MSG_RECORD_FINISH = 0X107;

    public IMRecordButton(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public IMRecordButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IMRecordButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        //默认资源
        mStrStateNormal = getResources().getString(R.string.im_record_button_status_normal);
        mStrStateRecording = getResources().getString(R.string.im_record_button_status_recording);
        mStrStateWantCancel = getResources().getString(R.string.im_record_button_status_want_cancel);
        mBgResIdNormal = R.drawable.shape_rect_im_record_button_normal;
        mBgResIdPressed = R.drawable.shape_rect_im_record_button_pressed;
        //获取自定义属性
        final TypedArray ty = context.obtainStyledAttributes(attrs, R.styleable.IMRecordButton);
        if (ty != null)
        {
            final int N = ty.getIndexCount();
            for (int i = 0; i < N; i++)
            {
                int attr = ty.getIndex(i);
                if (attr == R.styleable.IMRecordButton_normal_text)
                    mStrStateNormal = ty.getString(attr);
                else if (attr == R.styleable.IMRecordButton_recording_text)
                    mStrStateRecording = ty.getString(attr);
                else if (attr == R.styleable.IMRecordButton_cancel_text)
                    mStrStateWantCancel = ty.getString(attr);
                else if (attr == R.styleable.IMRecordButton_backgrount_resId_normal)
                    mBgResIdNormal = ty.getResourceId(attr, R.drawable.shape_rect_im_record_button_normal);
                else if (attr == R.styleable.IMRecordButton_backgrount_resId_pressed)
                    mBgResIdPressed = ty.getResourceId(attr, R.drawable.shape_rect_im_record_button_pressed);
            }
            ty.recycle();
        }

        //获取屏幕常量唤醒对象
        mWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.SCREEN_DIM_WAKE_LOCK, "IMRecordButtonScreenLock");
        //获取震动管理器
        mVibratorMgr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        mViewImpl = new IMRecordDialog(context);
        mAudioManager = new IMRecordAudioManager();
        mAudioManager.setOnRercorderPreparedListener(this);
        //默认状态
        changeState(STATE_NORMAL);
        setBackgroundResource(mBgResIdNormal);
    }

    /**
     * 设置录音监听
     */
    public void setOnRecordListener(IMRecordListener l)
    {
        this.mRecordListener = l;
    }

    /**
     * 设置正常状态下文案
     *
     * @param resId 文案资源id
     */
    public void setStateNormalTextResId(int resId)
    {
        this.mStrStateNormal = getResources().getString(resId);
    }

    /**
     * 设置录音状态下文案
     *
     * @param resId 文案资源id
     */
    public void setStateRecordingTextResId(int resId)
    {
        this.mStrStateRecording = getResources().getString(resId);
    }

    /**
     * 设置欲取消状态下文案
     *
     * @param resId 文案资源id
     */
    public void setStateCancelTextResId(int resId)
    {
        this.mStrStateWantCancel = getResources().getString(resId);
    }

    /**
     * 设置普通状态下背景图资源id
     *
     * @param resId 资源id
     */
    public void setBackgroundResIdNormal(int resId)
    {
        this.mBgResIdNormal = resId;
    }

    /**
     * 设置按下状态时背景图资源id
     *
     * @param resId 资源id
     */
    public void setBackgroundResIdPressed(int resId)
    {
        this.mBgResIdPressed = resId;
    }

    /**
     * 设置缓存文件夹路径
     *
     * @param floderPath 缓存路径
     */
    public void setCachePath(String floderPath)
    {
        mAudioManager.setCachePath(floderPath);
    }

    /**
     * 设置按钮各状态View
     */
    public void setRecordView(IMRecordViewImpl view)
    {
        this.mViewImpl = view;
    }

    /**
     * 切换状态
     */
    private void changeState(int state)
    {
        if (mCurState == state)
            return;

        if (state == STATE_NORMAL)
        {
            setText(mStrStateNormal);
        } else if (state == STATE_RECORDING)
        {
            setText(mStrStateRecording);
        } else if (state == STATE_WANT_CANCEL)
        {
            setText(mStrStateWantCancel);
        }
        mCurState = state;
    }

    @Override
    public synchronized boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                setBackgroundResource(mBgResIdPressed);
                mHasUp = false;
                if (mWakeLock != null)
                    mWakeLock.acquire();

                changeState(STATE_RECORDING);
                //之所以先触发回调通知，是因为按下按钮时可能外部正在播放语音，可先利用回调停止播放
                triggerRecordStartListener();
                //sd卡不存在的时候提示用户
                if (!isSdcardExits())
                {
                    mHandler.sendEmptyMessage(MSG_SDCARD_NOT_EXIST);
                    return super.onTouchEvent(event);
                }
                mAudioManager.prepareAudio();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mAudioManager.isRecording())
                {
                    //根据x，y坐标判断是否打算取消录音
                    if (wantCancel(x, y))
                    {
                        mHandler.sendEmptyMessage(MSG_WANT_CANCEL);
                        changeState(STATE_WANT_CANCEL);
                    } else
                    {
                        mHandler.sendEmptyMessage(MSG_RECORDING);
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundResource(mBgResIdNormal);
                mHasUp = true;
                if (mWakeLock != null && mWakeLock.isHeld())
                    mWakeLock.release();
                //录音状态下取消录音【之所以把它放在前面判断是因为用户执行取消手势后无需显示时间太短的dialog】
                if (mAudioManager.isRecording() && mCurState == STATE_WANT_CANCEL)
                {
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessage(MSG_RECORD_FINISH);
                }
                //如果录音器没有初始化完成或者录音时间太短，则关闭录音器
                else if ((!mIsAudioPreparedFail && !mAudioManager.isRecording())
                        || (mAudioManager.isRecording() && mTime < MIN_RECORD_TIME))
                {
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessage(MSG_VOICE_TOO_SHORT);
                }
                //正常录音
                else if (mAudioManager.isRecording() && mCurState == STATE_RECORDING)
                {
                    mHandler.sendEmptyMessage(MSG_RECORD_FINISH);
                    mAudioManager.reset();
                    triggerRecordFinishListener();
                }
                //恢复状态和标识
                reset();
                break;
            //监听ACTION_CANCEL，防止某些rom在第一次使用录音时弹出权限dialog，导致不触发ACTION_UP
            case MotionEvent.ACTION_CANCEL:
                setBackgroundResource(mBgResIdNormal);
                mHasUp = true;
                if (mWakeLock != null && mWakeLock.isHeld())
                    mWakeLock.release();
                if (mAudioManager.isRecording())
                {
                    mHandler.sendEmptyMessage(MSG_RECORD_FINISH);
                    mAudioManager.cancel();
                }
                //恢复状态和标识
                reset();
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onPreparedSuccess()
    {
        if (mHasUp)
            mAudioManager.cancel();
        else
            mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED_SUCCESS);
    }

    @Override
    public void onPreparedFail(int errorCode)
    {
        mIsAudioPreparedFail = true;
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED_FAIL);
    }

    //更新View状态的handler
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_AUDIO_PREPARED_SUCCESS:
                    //如果准备完成之前手指就抬起来了就提示用户录音时间太短
                    if (mHasUp)
                    {
                        mAudioManager.cancel();
                        mHandler.sendEmptyMessage(MSG_VOICE_TOO_SHORT);
                        return;
                    }

                    vibrate();
                    mAudioManager.start();
                    if (mViewImpl != null)
                    {
                        mViewImpl.showView(IMRecordButton.this);
                        //开启线程实时获取音量等级并更新UI
                        new Thread(mGetVoiceLevelRunnable).start();
                    }
                    break;
                case MSG_AUDIO_PREPARED_FAIL:
                    if (mViewImpl != null)
                    {
                        mViewImpl.showView(IMRecordButton.this);
                        mViewImpl.onError(IMRecordButton.this, IMRecordError.RECORD_PREPARE_FAIL);
                        mHandler.sendEmptyMessageDelayed(MSG_RECORD_FINISH, 2000);
                    }
                    break;
                case MSG_RECORDING:
                    if (mViewImpl != null)
                        mViewImpl.onRecording(IMRecordButton.this);
                    break;
                case MSG_SDCARD_NOT_EXIST:
                    if (mViewImpl != null)
                    {
                        mViewImpl.showView(IMRecordButton.this);
                        mViewImpl.onError(IMRecordButton.this, IMRecordError.NO_SDCARD);
                        mHandler.sendEmptyMessageDelayed(MSG_RECORD_FINISH, 1500);
                    }
                    break;
                case MSG_VOICE_LEVEL_CHANGED:
                    if (mViewImpl != null)
                        mViewImpl.onUpdateVoiceLevel(IMRecordButton.this, mAudioManager.getVoiceLevel(MAX_VOICE_LEVEL));
                    break;
                case MSG_WANT_CANCEL:
                    if (mViewImpl != null)
                        mViewImpl.onWantCancel(IMRecordButton.this);
                    break;
                case MSG_VOICE_TOO_SHORT:
                    if (mViewImpl != null)
                    {
                        mViewImpl.showView(IMRecordButton.this);
                        mViewImpl.onRecordTooShort(IMRecordButton.this);
                        mHandler.sendEmptyMessageDelayed(MSG_RECORD_FINISH, 1500);
                    }
                    break;
                case MSG_RECORD_FINISH:
                    if (mViewImpl != null)
                        mViewImpl.onFinish(IMRecordButton.this);
                    break;
            }
        }
    };

    /**
     * 判断是否打算取消录音
     * 【在xy坐标轴上判断手指触摸点：x轴上只要超出按钮区域就视为打算取消
     * ，y轴上超出按钮区域外自定义距离就视为打算取消】
     */
    private boolean wantCancel(int x, int y)
    {
        if (x < 0 || x > getWidth())
            return true;
        else if (y < -DISTANCE_CANCEL_Y || y > getHeight() + DISTANCE_CANCEL_Y)
            return true;
        return false;
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

    //获取音量大小的runnable，顺便计算录音时长
    private Runnable mGetVoiceLevelRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            while (mAudioManager.isRecording())
            {
                try
                {
                    Thread.sleep(100);
                    mTime += 0.1f;//计算时长
                    mHandler.sendEmptyMessage(MSG_VOICE_LEVEL_CHANGED);
                } catch (InterruptedException e)
                {
                    Log.e("IMRecordButton", "GetVoiceLevel Thread Sleep Be Interrupt");
                }
            }
        }
    };

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

    //触发录音开始的监听
    private void triggerRecordStartListener()
    {
        if (mRecordListener != null)
            mRecordListener.startRecord();
    }

    //触发录音结束的监听
    private void triggerRecordFinishListener()
    {
        if (mRecordListener != null)
            mRecordListener.recordFinish(mTime, mAudioManager.getCurrentPath());
    }

    /**
     * 恢复标志位和状态
     */
    private void reset()
    {
        mTime = 0;
        mIsAudioPreparedFail = false;
        mAudioManager.clearFilePath();
        changeState(STATE_NORMAL);
    }
}
