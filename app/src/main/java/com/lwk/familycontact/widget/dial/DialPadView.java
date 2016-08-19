package com.lwk.familycontact.widget.dial;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 拨号盘
 * 2016/8/18
 */
public class DialPadView extends RelativeLayout implements View.OnClickListener
        , View.OnKeyListener
        , View.OnLongClickListener
        , TextWatcher
{
    private DigitsEditText mEdInput;
    private ImageButton mBtnDelete;
    private boolean mIsFeedBackEnable = true;
    private final Object mToneGeneratorLock = new Object();
    private ToneGenerator mToneGenerator;
    //按键发音时的音量
    private static final int TONE_RELATIVE_VOLUME = 80;
    //按键声音类型
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;
    //按键声音时长
    private static final int TONE_LENGTH_INFINITE = 100;
    //震动管理器
    private Vibrator mVibratorMgr;
    //拨打电话监听
    private onCallListener mOnCallListener;
    //文本输入监听
    private onTextChangedListener mTextChangedListener;

    public void setOnCallListener(onCallListener listener)
    {
        this.mOnCallListener = listener;
    }

    public interface onCallListener
    {
        void onCall(String phone);
    }

    public void setOnTextChangedListener(onTextChangedListener listener)
    {
        this.mTextChangedListener = listener;
    }

    public interface onTextChangedListener
    {
        void onTextChanged(String s);
    }

    public DialPadView(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public DialPadView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DialPadView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        inflate(context, R.layout.layout_dial_pad, this);
        mEdInput = (DigitsEditText) findViewById(R.id.ed_dial_input);
        mEdInput.setKeyListener(UnicodeDialerKeyListener.INSTANCE);
        mEdInput.setOnClickListener(this);
        mEdInput.setOnKeyListener(this);
        mEdInput.setOnLongClickListener(this);
        mEdInput.addTextChangedListener(this);

        mBtnDelete = (ImageButton) findViewById(R.id.btn_dial_input_delete);
        mBtnDelete.setOnClickListener(this);
        mBtnDelete.setOnLongClickListener(this);
        findViewById(R.id.btn_dial_digist_0).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_1).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_2).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_3).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_4).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_5).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_6).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_7).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_8).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_9).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_star).setOnClickListener(this);
        findViewById(R.id.btn_dial_digist_pound).setOnClickListener(this);
        findViewById(R.id.btn_dial_input_plus).setOnClickListener(this);
        findViewById(R.id.btn_dial_pad_call).setOnClickListener(this);

        //获取震动管理器对象
        mVibratorMgr = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ed_dial_input:
                if (!isInputEmpty())
                    mEdInput.setCursorVisible(true);
                break;
            case R.id.btn_dial_digist_0:
                keyPressed(KeyEvent.KEYCODE_0);
                break;
            case R.id.btn_dial_digist_1:
                keyPressed(KeyEvent.KEYCODE_1);
                break;
            case R.id.btn_dial_digist_2:
                keyPressed(KeyEvent.KEYCODE_2);
                break;
            case R.id.btn_dial_digist_3:
                keyPressed(KeyEvent.KEYCODE_3);
                break;
            case R.id.btn_dial_digist_4:
                keyPressed(KeyEvent.KEYCODE_4);
                break;
            case R.id.btn_dial_digist_5:
                keyPressed(KeyEvent.KEYCODE_5);
                break;
            case R.id.btn_dial_digist_6:
                keyPressed(KeyEvent.KEYCODE_6);
                break;
            case R.id.btn_dial_digist_7:
                keyPressed(KeyEvent.KEYCODE_7);
                break;
            case R.id.btn_dial_digist_8:
                keyPressed(KeyEvent.KEYCODE_8);
                break;
            case R.id.btn_dial_digist_9:
                keyPressed(KeyEvent.KEYCODE_9);
                break;
            case R.id.btn_dial_digist_star:
                keyPressed(KeyEvent.KEYCODE_STAR);
                break;
            case R.id.btn_dial_digist_pound:
                keyPressed(KeyEvent.KEYCODE_POUND);
                break;
            case R.id.btn_dial_input_plus:
                keyPressed(KeyEvent.KEYCODE_PLUS);
                break;
            case R.id.btn_dial_input_delete:
                keyPressed(KeyEvent.KEYCODE_DEL);
                break;
            case R.id.btn_dial_pad_call:
                triggerCall();
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        switch (v.getId())
        {
            case R.id.ed_dial_input:
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    triggerCall();
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ed_dial_input:
                if (mEdInput != null)
                    mEdInput.setCursorVisible(true);
                break;
            case R.id.btn_dial_input_delete:
                clearInput();
                break;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        //更新光标
        if (isInputEmpty())
            mEdInput.setCursorVisible(false);
        //更新删除按钮状态
        updateDeleteButtonEnabledState();
        //触发文本监听
        if (mTextChangedListener != null)
            mTextChangedListener.onTextChanged(s.toString().trim());
    }

    //判断输入的文本是否为空
    private boolean isInputEmpty()
    {
        return mEdInput.length() == 0;
    }

    /**
     * 设置是否允许按键反馈
     * 【开启后按键时有声音和震动】
     */
    public void setFeedBackEnable(boolean enable)
    {
        this.mIsFeedBackEnable = enable;
    }

    private void keyPressed(int keyCode)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_1:
                triggerFeedBack(ToneGenerator.TONE_DTMF_1, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_2:
                triggerFeedBack(ToneGenerator.TONE_DTMF_2, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_3:
                triggerFeedBack(ToneGenerator.TONE_DTMF_3, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_4:
                triggerFeedBack(ToneGenerator.TONE_DTMF_4, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_5:
                triggerFeedBack(ToneGenerator.TONE_DTMF_5, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_6:
                triggerFeedBack(ToneGenerator.TONE_DTMF_6, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_7:
                triggerFeedBack(ToneGenerator.TONE_DTMF_7, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_8:
                triggerFeedBack(ToneGenerator.TONE_DTMF_8, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_9:
                triggerFeedBack(ToneGenerator.TONE_DTMF_9, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_0:
                triggerFeedBack(ToneGenerator.TONE_DTMF_0, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_POUND:
                triggerFeedBack(ToneGenerator.TONE_DTMF_P, TONE_LENGTH_INFINITE);
                break;
            case KeyEvent.KEYCODE_STAR:
                triggerFeedBack(ToneGenerator.TONE_DTMF_S, TONE_LENGTH_INFINITE);
                break;
            default:
                break;
        }

        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mEdInput.onKeyDown(keyCode, event);

        // 如果光标在文本末尾就隐藏光标
        final int length = mEdInput.length();
        if (length == mEdInput.getSelectionStart() && length == mEdInput.getSelectionEnd())
            mEdInput.setCursorVisible(false);
    }

    //触发按键反馈
    private void triggerFeedBack(int tone, int durationMs)
    {
        if (!mIsFeedBackEnable)
            return;

        // 判断系统设置
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT))
        {
            if (ringerMode == AudioManager.RINGER_MODE_VIBRATE)
                vibrate();
            return;
        }
        //震动
        vibrate();
        //发音
        synchronized (mToneGeneratorLock)
        {
            if (mToneGenerator == null)
                return;
            mToneGenerator.startTone(tone, durationMs);
        }
    }

    //震动一下
    private void vibrate()
    {
        if (mVibratorMgr != null)
        {
            long[] l = new long[]{0, 50};
            mVibratorMgr.vibrate(l, -1);
        }
    }

    /**
     * 停止发音
     */
    private void stopTone()
    {
        if (!mIsFeedBackEnable)
            return;
        synchronized (mToneGeneratorLock)
        {
            if (mToneGenerator == null)
                return;
            mToneGenerator.stopTone();
        }
    }

    //触发拨打电话的箭筒
    private void triggerCall()
    {
        if (mOnCallListener != null)
            mOnCallListener.onCall(mEdInput.getText().toString().trim());
    }

    /**
     * 更新删除按钮状态
     */
    private void updateDeleteButtonEnabledState()
    {
        if (mBtnDelete != null)
        {
            final boolean digitsNotEmpty = !isInputEmpty();
            mBtnDelete.setEnabled(digitsNotEmpty);
        }
    }

    /**
     * 清空输入
     */
    public void clearInput()
    {
        if (mEdInput != null)
            mEdInput.getText().clear();
    }

    /**
     * 在Aactivity/Fragment的onStart()调用
     */
    public void onStart()
    {
        updateDeleteButtonEnabledState();

        synchronized (mToneGeneratorLock)
        {
            if (mToneGenerator == null)
            {
                try
                {
                    mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE, TONE_RELATIVE_VOLUME);
                } catch (RuntimeException e)
                {
                    mToneGenerator = null;
                }
            }
        }
    }

    /**
     * 在Activity/Fragment的onPause()调用
     */
    public void onPause()
    {
        stopTone();
    }

    /**
     * 在Activity/Fragment的onStop()调用
     */
    public void onStop()
    {
        synchronized (mToneGeneratorLock)
        {
            if (mToneGenerator != null)
            {
                mToneGenerator.release();
                mToneGenerator = null;
            }
        }
    }
}
