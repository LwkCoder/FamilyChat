package com.lib.imrecordbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by LWK
 * TODO 带录音功能的自定义Button
 * 2016/9/18
 */
public class IMRecordButton extends Button
{

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
    }
}
