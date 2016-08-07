package com.lib.base.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.base.R;


/**
 * Function:通用自定义actionbar
 */
public class CommonActionBar extends RelativeLayout
{
    private View mLayoutLeftContainer;
    private ImageView mImgLeft;
    private TextView mTvLeft;
    private View mLayoutBack;
    private TextView mTvBack;
    private View mLayoutLeft;
    private TextView mTvTitle;
    private View mLayoutRight;
    private ImageView mImgRight;
    private TextView mTvRight;

    public CommonActionBar(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public CommonActionBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        inflate(context, R.layout.layout_common_actionbar, this);
        //        setBackgroundColor(context.getResources().getColor(R.color.blue_3F51B5));
        mLayoutLeftContainer = findViewById(R.id.ll_common_actionbar_left_container);
        mLayoutBack = findViewById(R.id.ll_common_actionbar_left_back);
        mTvBack = (TextView) findViewById(R.id.tv_common_actionbar_back);
        mLayoutLeft = findViewById(R.id.fl_common_actionbar_left_other);
        mImgLeft = (ImageView) findViewById(R.id.img_common_actionbar_left_other);
        mTvLeft = (TextView) findViewById(R.id.tv_common_actionbar_left_other);
        mTvTitle = (TextView) findViewById(R.id.tv_common_actionbar_title);
        mLayoutRight = findViewById(R.id.fl_common_actionbar_right);
        mTvRight = (TextView) findViewById(R.id.tv_common_actionbar_right);
        mImgRight = (ImageView) findViewById(R.id.img_common_actionbar_right);
        setWillNotDraw(false);
    }

    /**
     * 设置左侧布局为返回功能
     *
     * @param activity
     */
    public void setLeftLayoutAsBack(final Activity activity)
    {
        mLayoutBack.setVisibility(VISIBLE);
        mLayoutLeft.setVisibility(GONE);
        mLayoutLeftContainer.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (activity != null)
                    activity.finish();
            }
        });
    }

    /**
     * 设置左侧布局为返回功能但是没有文字
     *
     * @param activity
     */
    public void setLeftLayoutAsBackWithoutText(final Activity activity)
    {
        mLayoutBack.setVisibility(VISIBLE);
        mLayoutLeft.setVisibility(GONE);
        mTvBack.setVisibility(GONE);
        mLayoutLeftContainer.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (activity != null)
                    activity.finish();
            }
        });
    }

    /**
     * 设置左侧部分文字
     *
     * @param resId 资源id
     */
    public void setLeftTvText(int resId)
    {
        mLayoutBack.setVisibility(GONE);
        mLayoutLeft.setVisibility(VISIBLE);
        mTvLeft.setVisibility(VISIBLE);
        mTvLeft.setText(getResources().getText(resId));
    }

    /**
     * 设置左侧部分文字
     *
     * @param s 字符串
     */
    public void setLeftTvText(String s)
    {
        mLayoutBack.setVisibility(GONE);
        mLayoutLeft.setVisibility(VISIBLE);
        mTvLeft.setVisibility(VISIBLE);
        mTvLeft.setText(s);
    }

    /**
     * 设置左侧部分图片
     *
     * @param resId 资源id
     */
    public void setLeftImgResource(int resId)
    {
        mLayoutBack.setVisibility(GONE);
        mLayoutLeft.setVisibility(VISIBLE);
        mImgLeft.setVisibility(VISIBLE);
        mImgLeft.setImageResource(resId);
    }

    /**
     * 设置标题
     *
     * @param resId 资源id
     */
    public void setTitleText(int resId)
    {
        mTvTitle.setText(getResources().getText(resId));
    }

    /**
     * 设置标题
     *
     * @param s 字符串
     */
    public void setTitleText(String s)
    {
        mTvTitle.setText(s);
    }

    /**
     * 设置是否显示右边部分【默认不可见】
     *
     * @param visibility
     */
    public void setRightLayoutVisibility(int visibility)
    {
        mLayoutRight.setVisibility(visibility);
    }

    /**
     * 设置右边布局点击事件
     *
     * @param listener
     */
    public void setRightLayoutClickListener(OnClickListener listener)
    {
        mLayoutRight.setOnClickListener(listener);
    }

    /**
     * 设置右边部分图片
     *
     * @param resId 资源id
     */
    public void setRightImgResource(int resId)
    {
        setRightLayoutVisibility(VISIBLE);
        mImgRight.setVisibility(VISIBLE);
        mImgRight.setImageResource(resId);
    }

    /**
     * 设置右边部分文字
     *
     * @param resId 资源id
     */
    public void setRightTvText(int resId)
    {
        setRightLayoutVisibility(VISIBLE);
        mTvRight.setVisibility(VISIBLE);
        mTvRight.setText(getResources().getText(resId));
    }

    /**
     * 设置右边部分文字
     *
     * @param s 字符串
     */
    public void setRightTvText(String s)
    {
        setRightLayoutVisibility(VISIBLE);
        mTvRight.setVisibility(VISIBLE);
        mTvRight.setText(s);
    }
}
