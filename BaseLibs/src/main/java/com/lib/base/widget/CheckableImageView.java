package com.lib.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

/**
 * Function:实现Checkable属性的ImageView,不能直接在xml中使用!!!
 */
public class CheckableImageView extends ImageView implements Checkable
{
    public CheckableImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private boolean mChecked = false;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked)
    {
        if (mChecked != checked)
        {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked()
    {
        return mChecked;
    }

    @Override
    public void toggle()
    {
        setChecked(!mChecked);
    }
}