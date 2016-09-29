package com.lwk.familycontact.project.chat.dialog;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

import com.lwk.familycontact.R;
import com.lwk.familycontact.widget.BasePop;

/**
 * Created by LWK
 * TODO 语音消息在听筒播放的提示
 * 2016/9/29
 */
public class VoicePlayInCallWarning extends BasePop
{

    public VoicePlayInCallWarning(Activity context)
    {
        super(context);
    }

    @Override
    public boolean setFocusable()
    {
        return false;
    }

    @Override
    public boolean setOutsideTouchable()
    {
        return false;
    }

    @Override
    public int setLayoutWidthParams()
    {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    public int setLayoutHeightParams()
    {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int setContentViewId()
    {
        return R.layout.pop_voice_play_in_call;
    }

    @Override
    protected int setAnimStyle()
    {
        return 0;
    }

    @Override
    protected void initUI(View contentView)
    {

    }
}
