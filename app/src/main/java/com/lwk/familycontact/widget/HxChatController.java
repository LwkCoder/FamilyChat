package com.lwk.familycontact.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lib.base.utils.KeyboradUtils;
import com.lib.base.utils.StringUtil;
import com.lib.imrecordbutton.IMRecordButton;
import com.lib.imrecordbutton.IMRecordListener;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 聊天输入控制器
 * 2016/9/26
 */
public class HxChatController extends LinearLayout
{
    //文字输入模式标识
    public static final int INPUT_MODE_TEXT = 1;
    //语音输入模式标识
    public static final int INPUT_MODE_VOICE = 2;
    //当前输入模式
    private int mCurInputMode = -1;
    //输入模式切换器开关
    private ImageView mImgInputModeSwitch;
    //文字输入EditText
    private EditText mEdTextInput;
    //语音输入IMRecordButton
    private IMRecordButton mIMRecordButton;
    //发送按钮
    private Button mBtnSend;
    //发送监听
    private onTextSendListener mSendListener;
    //切换到语音输入模式的监听
    private onCheckModeToVoiceInputListener mVoiceInputModeListener;

    public HxChatController(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public HxChatController(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public HxChatController(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        inflate(context, R.layout.layout_chat_controller, this);
        mImgInputModeSwitch = (ImageView) findViewById(R.id.img_chat_controller_input_mode);
        mEdTextInput = (EditText) findViewById(R.id.ed_chat_controller_text_input);
        mIMRecordButton = (IMRecordButton) findViewById(R.id.imbtn_chat_controller_voice_input);
        mBtnSend = (Button) findViewById(R.id.btn_chat_controller_send);

        mImgInputModeSwitch.setOnClickListener(mInputModeSwitchListener);
        mBtnSend.setOnClickListener(mBtnSendClickListener);
        //初始化输入模式
        switchInputMode(false);
    }

    //切换输入模式
    private void switchInputMode(boolean requestFocus)
    {
        mEdTextInput.clearFocus();
        if (mCurInputMode == INPUT_MODE_TEXT)
        {
            //切换到语音输入模式下要检查当前系统版本是不是在6.0以上
            //是的话要让Activity去申请权限,申请成功后由Activity那边调用切换模式的方法
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (mVoiceInputModeListener != null)
                    mVoiceInputModeListener.onCheckToVoiceInputMode();
            } else
            {
                checkModeToVoiceInput();
            }
        } else
        {
            checkModeToTextInput(requestFocus);
        }
    }

    //切换为语音输入模式
    public void checkModeToVoiceInput()
    {
        mImgInputModeSwitch.setImageResource(R.drawable.img_chat_contorller_voice_selector);
        mEdTextInput.setVisibility(GONE);
        mBtnSend.setVisibility(GONE);
        KeyboradUtils.HideKeyboard(mEdTextInput);
        mIMRecordButton.setVisibility(VISIBLE);
        mCurInputMode = INPUT_MODE_VOICE;
    }

    //切换为文字输入模式
    public void checkModeToTextInput(boolean requestFocus)
    {
        mImgInputModeSwitch.setImageResource(R.drawable.img_chat_contorller_text_selector);
        mEdTextInput.setVisibility(VISIBLE);
        mBtnSend.setVisibility(VISIBLE);
        mIMRecordButton.setVisibility(GONE);
        if (requestFocus)
        {
            mEdTextInput.requestFocus();
            KeyboradUtils.ShowKeyboard(mEdTextInput);
        }
        mCurInputMode = INPUT_MODE_TEXT;
    }

    //发送按钮监听
    private OnClickListener mBtnSendClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String content = mEdTextInput.getText().toString();
            if (StringUtil.isEmpty(content))
                return;

            if (mSendListener != null)
                mSendListener.onClickSend(content);
            mEdTextInput.setText(null);
        }
    };

    /**
     * 获取当前InputMode
     */
    public int getCurInputMode()
    {
        return mCurInputMode;
    }

    //输入模式切换监听
    private OnClickListener mInputModeSwitchListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switchInputMode(true);
        }
    };

    /**
     * 设置录音监听
     */
    public void setOnRecordListener(IMRecordListener listener)
    {
        if (mIMRecordButton != null)
            mIMRecordButton.setOnRecordListener(listener);
    }

    /**
     * 设置文本消息发送监听
     */
    public void setOnTextSendListener(onTextSendListener listener)
    {
        this.mSendListener = listener;
    }

    public interface onTextSendListener
    {
        void onClickSend(String content);
    }

    /**
     * 设置切换到语音输入模式的监听
     * [麻痹！要让Activity申请6.0的权限]
     */
    public void setOnCheckModeToVoiceInputListener(onCheckModeToVoiceInputListener listener)
    {
        this.mVoiceInputModeListener = listener;
    }

    public interface onCheckModeToVoiceInputListener
    {
        void onCheckToVoiceInputMode();
    }
}
