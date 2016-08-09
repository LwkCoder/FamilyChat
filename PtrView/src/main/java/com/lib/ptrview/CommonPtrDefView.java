package com.lib.ptrview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 默认下拉刷新View/上拉加载View
 */
public class CommonPtrDefView extends RelativeLayout implements CommonPtrViewImpl
{
    //文字颜色：默认白色
    protected int mTvColor = Color.WHITE;
    //pulldown箭头Icon
    protected int mIconPullDownArrow = R.drawable.icon_pull_down_arrow;
    //pullup箭头Icon
    protected int mIconPullUpArrow = R.drawable.icon_pull_up_arrow;
    //成功状态icon
    protected int mIconStatusSuccess = R.drawable.icon_pull_status_success;
    //失败状态icon
    protected int mIconStatusFail = R.drawable.icon_pull_status_fail;
    //reset状态下pulldown的文案
    protected String mStrPullDownReset = getResources().getString(R.string.tv_pull_down_status_reset);
    //pulling状态下pulldown的文案
    protected String mStrPullDownPulling = getResources().getString(R.string.tv_pull_down_status_pulling);
    //released状态下pulldown的文案
    protected String mStrPullDownReleased = getResources().getString(R.string.tv_pull_down_status_released);
    //success状态下pulldown的文案
    protected String mStrPullDownSuccess = getResources().getString(R.string.tv_pull_down_status_success);
    //fail状态下pulldown的文案
    protected String mStrPullDownFail = getResources().getString(R.string.tv_pull_down_status_fail);
    //reset状态下pullup的文案
    protected String mStrPullUpReset = getResources().getString(R.string.tv_pull_up_status_reset);
    //pulling状态下pullup的文案
    protected String mStrPullUpPulling = getResources().getString(R.string.tv_pull_up_status_pulling);
    //released状态下pullup的文案
    protected String mStrPullUpReleased = getResources().getString(R.string.tv_pull_up_status_released);
    //success状态下pullup的文案
    protected String mStrPullUpSuccess = getResources().getString(R.string.tv_pull_up_status_success);
    //fail状态下pullup的文案
    protected String mStrPullUpFail = getResources().getString(R.string.tv_pull_up_status_fail);
    //pgb圈圈
    protected int mPgbDrawable = R.drawable.pgb_ptrdefview;

    protected boolean mIsPullDownMode;
    private TextView mTvStatus;
    private ImageView mImgStatus;
    private ProgressBar mPgbLoading;
    private ImageView mImgArrow;
    private Animation mAnimRotateDown;
    private Animation mAnimRotateUp;
    private boolean mIsRotated = false;

    public CommonPtrDefView(Context context)
    {
        this(context, null);
    }

    public CommonPtrDefView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CommonPtrDefView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonPtrDefView, defStyleAttr, 0);

        try
        {
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++)
            {
                int attr = a.getIndex(i);
                if (attr == R.styleable.CommonPtrDefView_ptr_def_view_tv_color)
                    mTvColor = a.getColor(attr, Color.WHITE);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_icon_pull_down_arrow)
                    mIconPullDownArrow = a.getResourceId(attr, R.drawable.icon_pull_down_arrow);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_icon_pull_up_arrow)
                    mIconPullUpArrow = a.getResourceId(attr, R.drawable.icon_pull_up_arrow);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_icon_status_success)
                    mIconStatusSuccess = a.getResourceId(attr, R.drawable.icon_pull_status_success);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_icon_status_fail)
                    mIconStatusFail = a.getResourceId(attr, R.drawable.icon_pull_status_fail);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pulldown_reset)
                    mStrPullDownReset = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pulldown_pulling)
                    mStrPullDownPulling = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pulldown_released)
                    mStrPullDownReleased = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pulldown_success)
                    mStrPullDownSuccess = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pulldown_fail)
                    mStrPullDownFail = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pullup_reset)
                    mStrPullUpReset = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pullup_pulling)
                    mStrPullUpPulling = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pullup_released)
                    mStrPullUpReleased = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pullup_success)
                    mStrPullUpSuccess = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_label_pullup_fail)
                    mStrPullUpFail = a.getString(attr);
                else if (attr == R.styleable.CommonPtrDefView_ptr_def_view_pgb_drawable)
                    mPgbDrawable = a.getResourceId(attr, R.drawable.pgb_ptrdefview);
            }
        } finally
        {
            a.recycle();
        }

        initUI(context);
    }

    private void initUI(Context context)
    {
        inflate(context, R.layout.layout_ptrview_def_view, this);
        setWillNotDraw(false);

        mAnimRotateDown = AnimationUtils.loadAnimation(context, R.anim.arrow_rotate_down);
        mAnimRotateUp = AnimationUtils.loadAnimation(context, R.anim.arrow_rotate_up);

        mTvStatus = (TextView) findViewById(R.id.tv_ptrview_def_view_status);
        mImgStatus = (ImageView) findViewById(R.id.img_ptrview_def_view_status);
        mPgbLoading = (ProgressBar) findViewById(R.id.pgb_ptrview_def_view_loading);
        mImgArrow = (ImageView) findViewById(R.id.img_ptrview_def_view_arrow);

        mTvStatus.setTextColor(mTvColor);
        mPgbLoading.setIndeterminateDrawable(getResources().getDrawable(mPgbDrawable));
    }


    @Override
    public void onReset()
    {
        mIsRotated = false;

        if (mIsPullDownMode)
        {
            mTvStatus.setText(mStrPullDownReset);
            mImgArrow.setVisibility(VISIBLE);
            mImgArrow.setImageResource(mIconPullDownArrow);
        } else
        {
            mTvStatus.setText(mStrPullUpReset);
            mImgArrow.setVisibility(VISIBLE);
            mImgArrow.setImageResource(mIconPullUpArrow);
        }

        mImgStatus.setVisibility(GONE);
        mPgbLoading.setVisibility(GONE);
    }

    @Override
    public void onPrepare()
    {
    }

    @Override
    public void onPulling(float currentPercent)
    {
        mImgStatus.setVisibility(GONE);
        mPgbLoading.setVisibility(GONE);
        mImgArrow.setVisibility(VISIBLE);

        if (currentPercent > 1)
        {
            if (mIsPullDownMode)
                mTvStatus.setText(mStrPullDownPulling);
            else
                mTvStatus.setText(mStrPullUpPulling);

            if (!mIsRotated)
            {
                mImgArrow.clearAnimation();
                mImgArrow.startAnimation(mAnimRotateUp);
                mIsRotated = true;
            }
        } else
        {
            if (mIsPullDownMode)
                mTvStatus.setText(mStrPullDownReset);
            else
                mTvStatus.setText(mStrPullUpReset);

            if (mIsRotated)
            {
                mImgArrow.clearAnimation();
                mImgArrow.startAnimation(mAnimRotateDown);
                mIsRotated = false;
            }
        }
    }

    @Override
    public void onRelease()
    {
        if (mIsPullDownMode)
            mTvStatus.setText(mStrPullDownReleased);
        else
            mTvStatus.setText(mStrPullUpReleased);

        mPgbLoading.setVisibility(VISIBLE);
        mImgArrow.clearAnimation();
        mImgArrow.setVisibility(GONE);
        mImgStatus.setVisibility(GONE);
    }

    @Override
    public void onSuccess()
    {
        mIsRotated = false;

        if (mIsPullDownMode)
            mTvStatus.setText(mStrPullDownSuccess);
        else
            mTvStatus.setText(mStrPullUpSuccess);

        mImgStatus.setVisibility(VISIBLE);
        mImgStatus.setImageResource(mIconStatusSuccess);
        mImgArrow.setVisibility(GONE);
        mPgbLoading.setVisibility(GONE);
    }


    @Override
    public void onFail()
    {
        mIsRotated = false;

        if (mIsPullDownMode)
            mTvStatus.setText(mStrPullDownFail);
        else
            mTvStatus.setText(mStrPullUpFail);

        mImgStatus.setVisibility(VISIBLE);
        mImgStatus.setImageResource(mIconStatusFail);
        mImgArrow.setVisibility(GONE);
        mPgbLoading.setVisibility(GONE);
    }

    @Override
    public void setIsPullDownMode(boolean isPullDown)
    {
        this.mIsPullDownMode = isPullDown;
        if (isPullDown)
            mImgArrow.setImageResource(mIconPullDownArrow);
        else
            mImgArrow.setImageResource(mIconPullUpArrow);
    }
}
