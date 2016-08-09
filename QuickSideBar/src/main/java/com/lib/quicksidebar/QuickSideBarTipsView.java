package com.lib.quicksidebar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lib.quicksidebar.tipsview.QuickSideBarTipsItemView;


/**
 * QuickSideBar触摸浮动提示
 */
public class QuickSideBarTipsView extends RelativeLayout
{
    private QuickSideBarTipsItemView mTipsView;

    public QuickSideBarTipsView(Context context)
    {
        this(context, null);
    }

    public QuickSideBarTipsView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public QuickSideBarTipsView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        mTipsView = new QuickSideBarTipsItemView(context, attrs);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mTipsView, layoutParams);
    }

    public void setText(String text, int poistion, float y)
    {
        mTipsView.setText(text);
        LayoutParams layoutParams = (LayoutParams) mTipsView.getLayoutParams();
        int topMargin = (int) (y - getWidth());
        layoutParams.topMargin = topMargin > 0 ? topMargin : 0;
        mTipsView.setLayoutParams(layoutParams);
    }


}
