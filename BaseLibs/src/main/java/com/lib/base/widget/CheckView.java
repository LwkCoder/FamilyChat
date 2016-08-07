package com.lib.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lib.base.utils.ScreenUtils;

/**
 * Function:仿CheckBox的控件
 */
public class CheckView extends LinearLayout implements Checkable, View.OnClickListener
{
    /**
     * ImageView在左边的排列方式
     */
    public static final int HORIZONTAL_IMAGE_LEFT = 1;

    /**
     * ImageView在右边的排列方式
     */
    public static final int HORIZONTAL_IMAGE_RIGHT = 2;

    /**
     * TextView和ImageView的间距
     * 默认10dp
     */
    private int TEXTVIEW_MARGIN;

    /**
     * ImageView和TextView的排列方式
     */
    private int mArrangement;

    /**
     * 具有checked属性的ImageView
     */
    private CheckableImageView mImageView;

    /**
     * 文案TextView
     */
    private TextView mTextView;

    /**
     * 是否可选
     */
    private boolean mIsCheckable;

    /**
     * 当前是否选中状态
     */
    private boolean mIsChecked;

    /**
     * 选中状态改变监听
     */
    private OnCheckChangeListener mOnCheckedChangeListener;

    /**
     * 点击监听
     */
    private CheckView.OnClickListener mOnClickListener;

    public CheckView(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public CheckView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CheckView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr)
    {
        //初始化父布局参数
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        //初始化ImageView
        mImageView = new CheckableImageView(context, attrs);
        LinearLayout.LayoutParams imageParams = new LayoutParams(context, attrs);
        imageParams.width = ScreenUtils.dp2px(context, 22);
        imageParams.height = ScreenUtils.dp2px(context, 22);
        mImageView.setLayoutParams(imageParams);

        //初始化TextView
        mTextView = new TextView(context);
        LinearLayout.LayoutParams textParmas = new LayoutParams(context, attrs);
        mTextView.setLayoutParams(textParmas);
        mTextView.setGravity(Gravity.CENTER);
        setTextSizeBySp(15);
        setTextColor(Color.BLACK);
        TEXTVIEW_MARGIN = ScreenUtils.dp2px(context, 10);

        //设置排列方式 默认图片在左边
        setArrangement(HORIZONTAL_IMAGE_LEFT);
        //默认TextView不可见
        mTextView.setVisibility(GONE);
        //默认可check
        setCheckable(true);
        setOnClickListener(this);
    }

    public interface OnClickListener
    {
        void onClick(View v);
    }

    public interface OnCheckChangeListener
    {
        void onChecked(CheckView checkView, boolean isChecked);
    }

    /**
     * 设置选中状态改变监听
     */
    public void setOnCheckChangeListener(OnCheckChangeListener listener)
    {
        this.mOnCheckedChangeListener = listener;
    }

    /**
     * 设置点击监听
     */
    public void setOnClickListener(CheckView.OnClickListener listener)
    {
        this.mOnClickListener = listener;
    }

    /**
     * 设置TextView 和 ImageView的margin值
     *
     * @param marginDp 间距 单位dp
     */
    public void setTextViewMarginByDp(int marginDp)
    {
        this.TEXTVIEW_MARGIN = ScreenUtils.dp2px(getContext(), marginDp);
        setArrangement(mArrangement);
    }

    /**
     * 设置排列方式
     *
     * @param arrangement 该类下HORIZONTAL_IMAGE_LEFT或HORIZONTAL_IMAGE_RIGHT
     */
    public void setArrangement(int arrangement)
    {
        if (mArrangement == arrangement)
            return;
        this.mArrangement = arrangement;
        LinearLayout.LayoutParams layoutParams = (LayoutParams) mTextView.getLayoutParams();
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        if (mArrangement == HORIZONTAL_IMAGE_LEFT)
        {
            layoutParams.leftMargin = TEXTVIEW_MARGIN;
            mTextView.setLayoutParams(layoutParams);
            removeAllViews();
            addView(mImageView, 0);
            addView(mTextView, 1);
        } else
        {
            layoutParams.rightMargin = TEXTVIEW_MARGIN;
            mTextView.setLayoutParams(layoutParams);
            removeAllViews();
            addView(mTextView, 0);
            addView(mImageView, 1);
        }
    }

    /**
     * 设置是否可选
     */
    public void setCheckable(boolean checkable)
    {
        this.mIsCheckable = checkable;
        if (mIsCheckable)
            setAlpha(1.0f);
        else
            setAlpha(0.4f);
    }

    /**
     * 获取当前是否可选状态
     */
    public boolean isCheckable()
    {
        return mIsCheckable;
    }

    /**
     * 设置ImageView的selector
     */
    public void setImageSelector(int resId)
    {
        mImageView.setImageResource(resId);
    }

    /**
     * 设置ImageView的大小
     *
     * @param width  宽度 单位dp
     * @param height 高度 单位dp
     */
    public void setImageSizeByDp(int width, int height)
    {
        LinearLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
        layoutParams.width = ScreenUtils.dp2px(getContext(), width);
        layoutParams.height = ScreenUtils.dp2px(getContext(), height);
        mImageView.setLayoutParams(layoutParams);
    }

    /**
     * 设置文字大小
     *
     * @param spSize 文字大小 sp值
     */
    public void setTextSizeBySp(int spSize)
    {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, spSize);
    }

    /**
     * 设置TextView的color
     */
    public void setTextColor(int resId)
    {
        mTextView.setTextColor(resId);
    }

    /**
     * 设置文案
     */
    public void setText(int resId)
    {
        mTextView.setVisibility(VISIBLE);
        mTextView.setText(getResources().getString(resId));
    }

    /**
     * 设置文案
     */
    public void setText(String s)
    {
        mTextView.setVisibility(VISIBLE);
        mTextView.setText(s);
    }

    @Override
    public void setChecked(boolean checked)
    {
        if (!mIsCheckable)
            return;

        if (mIsChecked != checked)
        {
            mIsChecked = checked;
            mImageView.setChecked(mIsChecked);
            if (mOnCheckedChangeListener != null)
                mOnCheckedChangeListener.onChecked(this, mIsChecked);
        }
    }

    @Override
    public boolean isChecked()
    {
        return mIsChecked;
    }

    @Override
    public void toggle()
    {
        setChecked(!mIsChecked);
    }

    @Override
    public void onClick(View v)
    {
        toggle();
        if (mOnClickListener != null)
            mOnClickListener.onClick(v);
    }
}
