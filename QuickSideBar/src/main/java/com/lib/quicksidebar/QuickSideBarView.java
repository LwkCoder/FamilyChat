package com.lib.quicksidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lib.quicksidebar.listener.OnQuickSideBarTouchListener;

import java.util.Arrays;
import java.util.List;

/**
 * 快速选择侧边栏
 */
public class QuickSideBarView extends View
{
    //默认的侧边栏数据
    private final String[] mDefLetters = new String[]{
            "↑", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z", "#", "↓"};
    private List<String> mLetters;
    private OnQuickSideBarTouchListener listener;
    private int mChoose = -1;
    private Paint mPaint = new Paint();
    private float mTextSize;
    private float mTextSizeChoose;
    private int mTextColor;
    private int mTextColorChoose;
    private int mWidth;
    private int mHeight;
    private float mItemHeight;

    public QuickSideBarView(Context context)
    {
        this(context, null);
    }

    public QuickSideBarView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public QuickSideBarView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        mLetters = Arrays.asList(mDefLetters);

        mTextColor = context.getResources().getColor(android.R.color.black);
        mTextColorChoose = context.getResources().getColor(android.R.color.black);
        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.textSize_quicksidebar);
        mTextSizeChoose = context.getResources().getDimensionPixelSize(R.dimen.textSize_quicksidebar_choose);
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.QuickSideBarView);

            mTextColor = a.getColor(R.styleable.QuickSideBarView_sidebarTextColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.QuickSideBarView_sidebarTextColorChoose, mTextColorChoose);
            mTextSize = a.getDimension(R.styleable.QuickSideBarView_sidebarTextSize, mTextSize);
            mTextSizeChoose = a.getDimension(R.styleable.QuickSideBarView_sidebarTextSizeChoose, mTextSizeChoose);
            a.recycle();
        }
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        for (int i = 0; i < mLetters.size(); i++)
        {
            String letter = mLetters.get(i);
            mPaint.setColor(mTextColor);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(mTextSize);
            if (i == mChoose)
            {
                mPaint.setColor(mTextColorChoose);
                mPaint.setFakeBoldText(true);
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                mPaint.setTextSize(mTextSizeChoose);
            }

            //计算位置
            // x坐标等于中间-字符串宽度的一半.
            float xPos = mWidth / 2 - mPaint.measureText(letter) / 2;
            float yPos = mItemHeight * i + mItemHeight;

            canvas.drawText(letter, xPos, yPos, mPaint);
            mPaint.reset();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        //每个item的高度由item的个数决定
        mItemHeight = mHeight / mLetters.size();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mChoose;
        final int newChoose = (int) (y / mItemHeight);
        switch (action)
        {
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(true);
                mChoose = -1;
                if (listener != null)
                    listener.onLetterTouching(false);
                invalidate();
                break;
            default:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (oldChoose != newChoose)
                {
                    if (newChoose >= 0 && newChoose < mLetters.size())
                    {
                        mChoose = newChoose;
                        if (listener != null)
                        {
                            //计算位置
                            float yPos = mItemHeight * mChoose + mItemHeight;
                            listener.onLetterChanged(mLetters.get(newChoose), mChoose, yPos);
                        }
                    }
                    invalidate();
                }
                //如果是cancel也要调用onLetterUpListener 通知
                if (event.getAction() == MotionEvent.ACTION_CANCEL)
                {
                    if (listener != null)
                        listener.onLetterTouching(false);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    //按下调用 onLetterDownListener
                    if (listener != null)
                        listener.onLetterTouching(true);
                }

                break;
        }
        return true;
    }

    public OnQuickSideBarTouchListener getListener()
    {
        return listener;
    }

    public void setOnQuickSideBarTouchListener(OnQuickSideBarTouchListener listener)
    {
        this.listener = listener;
    }

    public List<String> getLetters()
    {
        return mLetters;
    }

    public String getFirstLetters()
    {
        return mLetters != null ? mLetters.get(0) : null;
    }

    public String getLastLetters()
    {
        return mLetters != null ? mLetters.get(mLetters.size() - 1) : null;
    }

    /**
     * 设置字母表
     */
    public void setLetters(List<String> letters)
    {
        this.mLetters = letters;
        invalidate();
    }
}

