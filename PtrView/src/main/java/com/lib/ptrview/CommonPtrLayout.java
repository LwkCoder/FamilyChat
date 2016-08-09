package com.lib.ptrview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * 通用下拉刷新上拉加载控件
 */
public class CommonPtrLayout extends FrameLayout
{
    //默认刷新/加载后留存时间
    private static final int DEFAULT_DURATION_DELAY = 300;
    //自动刷新时间
    private static final int DEFAULT_AUTO_DURATION = 100;
    //默认摩擦系数
    private static final float DEFAULT_FRICTION = 0.5f;

    //通过触摸判断滑动方向
    private static byte NO_SCROLL = 0;
    private static byte NO_SCROLL_UP = 1;
    private static byte NO_SCROLL_DOWN = 2;

    //  头部下拉刷新View
    protected View mRefreshView;
    //  底部上拉加载View
    protected View mLoadMoreView;
    //  展示内容的View
    protected View mContentView;
    //列表View 当上面的内容View为CoordinatorLayout时有用（mIsCoo=true时有效）
    protected View mScrollContentView;
    //下拉时背景:默认为#333333
    private int mRefreshBgResId = R.color.black_ptr_view_bg;
    //上拉时背景:默认为#333333
    private int mLoadMoreBgResId = R.color.black_ptr_view_bg;

    protected AppBarLayout mAppBar;

    //头部高度
    protected int mRefreshViewHeight;
    //底部高度
    protected int mLoadMoreViewHeight;

    //是否在刷新中
    private boolean isRefreshing;
    //是否正在加载更多中
    private boolean isLoadMoring;

    //摩擦系数
    private float mFriction = DEFAULT_FRICTION;
    //是否可下拉
    private boolean mRefreshEnabled = true;
    //是否可上拉
    private boolean mLoadMoreEnabled = true;
    //下拉监听
    protected OnRefreshListener mOnRefreshListener;
    //上拉监听
    protected OnLoadMoreListener mOnLoadMoreListener;

    //刷新/加载完成后留存时间
    private int mDurationOfDelay = DEFAULT_DURATION_DELAY;
    //不可滑动view的滑动方向
    private int isUpOrDown = NO_SCROLL;
    //判断y轴方向的存储值
    float directionX;
    //判断x轴方向存储值
    float directionY;
    //最后一次触摸的位置
    private float lastY;
    //偏移
    private int currentOffSetY;
    //触摸移动的位置
    private int offsetSum;
    //触摸移动的位置之和
    private int scrollSum;
    //内容视图是否是CoordinatorLayout
    private boolean mIsCoo;
    //是否展开  mIsCoo=true时有效
    private boolean isDependentOpen = true;

    private Scroller mScroller = new Scroller(getContext());

    public CommonPtrLayout(Context context)
    {
        this(context, null);
    }

    public CommonPtrLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CommonPtrLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, com.lib.ptrview.R.styleable.CommonPtrLayout, defStyleAttr, 0);

        try
        {
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++)
            {
                int attr = a.getIndex(i);
                if (attr == com.lib.ptrview.R.styleable.CommonPtrLayout_ptr_enable_refresh)
                    setRefreshEnabled(a.getBoolean(attr, true));
                else if (attr == com.lib.ptrview.R.styleable.CommonPtrLayout_ptr_enable_loadmore)
                    setLoadMoreEnabled(a.getBoolean(attr, true));
                else if (attr == com.lib.ptrview.R.styleable.CommonPtrLayout_ptr_friction)
                    setFriction(a.getFloat(attr, DEFAULT_FRICTION));
                else if (attr == com.lib.ptrview.R.styleable.CommonPtrLayout_ptr_delay_duration)
                    mDurationOfDelay = a.getInt(attr, DEFAULT_DURATION_DELAY);
                else if (attr == R.styleable.CommonPtrLayout_ptr_bg_refresh)
                    mRefreshBgResId = a.getResourceId(attr, R.color.black_ptr_view_bg);
                else if (attr == R.styleable.CommonPtrLayout_ptr_bg_loadmore)
                    mLoadMoreBgResId = a.getResourceId(attr, R.color.black_ptr_view_bg);
                else if (attr == com.lib.ptrview.R.styleable.CommonPtrLayout_ptr_is_coordinatorLayout)
                    mIsCoo = a.getBoolean(attr, false);
            }
        } finally
        {
            a.recycle();
        }
    }

    private void setAppBarListener()
    {
        if (mAppBar != null)
        {
            mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
            {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
                {
                    int miniH = mAppBar.getMeasuredHeight() / 2;

                    if (verticalOffset == 0)
                    {
                        isDependentOpen = true;
                    } else if (Math.abs(verticalOffset) >= miniH)
                    {
                        isDependentOpen = false;
                    }
                }
            });
        }
    }

    /**
     * 设置是否可下拉刷新
     */
    public void setRefreshEnabled(boolean enable)
    {
        this.mRefreshEnabled = enable;
    }

    /**
     * 下拉刷新是否可用
     */
    public boolean isRefreshEnable()
    {
        return mRefreshEnabled ? mRefreshView != null ? true : false : false;
    }

    /**
     * 设置是否可上拉加载
     */
    public void setLoadMoreEnabled(boolean enable)
    {
        this.mLoadMoreEnabled = enable;
    }

    /**
     * 上拉加载是否可用
     */
    public boolean isLoadMoreEnable()
    {
        return mLoadMoreEnabled ? mLoadMoreView != null ? true : false : false;
    }

    /**
     * 设置摩擦系数
     *
     * @param mFriction
     */
    public void setFriction(@FloatRange(from = 0.0, to = 1.0) float mFriction)
    {
        this.mFriction = mFriction;
    }

    /**
     * 设置默认刷新时间
     *
     * @param mDuration
     */
    public void setDuration(int mDuration)
    {
        this.mDurationOfDelay = mDuration;
    }

    /**
     * 设置下拉刷新时背景
     */
    public void setRefreshBg(int colorResId)
    {
        this.mRefreshBgResId = colorResId;
        if (isRefreshEnable())
            mRefreshView.setBackgroundResource(mRefreshBgResId);
    }

    /**
     * 设置加载更多时背景
     */
    public void setLoadMoreBg(int colorResId)
    {
        this.mLoadMoreBgResId = colorResId;
        if (isLoadMoreEnable())
            mLoadMoreView.setBackgroundResource(colorResId);
    }

    /**
     * 通过id得到相应的view
     */
    @Override
    protected void onFinishInflate()
    {
        final int childCount = getChildCount();

        if (childCount > 0)
        {
            mRefreshView = findViewById(com.lib.ptrview.R.id.common_ptrview_refresh_view);
            mContentView = findViewById(com.lib.ptrview.R.id.common_ptrview_content);
            mLoadMoreView = findViewById(com.lib.ptrview.R.id.common_ptrview_loadmore_view);
            mScrollContentView = findViewById(com.lib.ptrview.R.id.common_ptrview_content_scroll);
        }

        if (mContentView == null)
        {
            throw new IllegalStateException("CommonPtrLayout: mContentView is null");
        }

        if (mIsCoo)
        {
            if (mContentView instanceof CoordinatorLayout)
            {
                CoordinatorLayout coo = (CoordinatorLayout) mContentView;
                mAppBar = (AppBarLayout) coo.getChildAt(0);
                setAppBarListener();
            } else
            {
                throw new IllegalStateException("CommonPtrLayout: mContentView is not CoordinatorLayout");
            }

            if (mScrollContentView == null)
            {
                throw new IllegalStateException("CommonPtrLayout: mScrollContentView is null");
            }

            if (!(mScrollContentView instanceof NestedScrollingChild))
            {
                throw new IllegalStateException("CommonPtrLayout: mScrollContentView is not NestedScrollingChild");
            }
        }

        if (mRefreshEnabled)
        {
            if (mRefreshView == null)
                throw new IllegalStateException("CommonPrtLayout : You must set a refreshView when the refreshEnabled is true.");
            if (!(mRefreshView instanceof CommonPtrViewImpl))
                throw new IllegalStateException("CommonPrtLayout : The refreshView must instanceof CommonPtrViewImpl.");

            mRefreshView.setBackgroundResource(mRefreshBgResId);
            getRefreshView().setIsPullDownMode(true);
        }

        if (mLoadMoreEnabled)
        {
            if (mLoadMoreView == null)
                throw new IllegalStateException("CommonPrtLayout : You must set a loadMoreView when the refreshEnabled is true.");
            if (!(mLoadMoreView instanceof CommonPtrViewImpl))
                throw new IllegalStateException("CommonPrtLayout : The loadMoreView must instanceof CommonPtrViewImpl.");

            mLoadMoreView.setBackgroundResource(mLoadMoreBgResId);
            getLoadMoreView().setIsPullDownMode(false);
        }

        super.onFinishInflate();

        for (int i = 0; i < childCount; i++)
        {
            View v = getChildAt(i);
            if (v != mContentView)
                bringChildToFront(v);
        }
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3)
    {
        childLayout();
    }

    /**
     * 设置上拉下拉中间view的位置
     */
    private void childLayout()
    {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mRefreshView != null)
        {
            MarginLayoutParams lp = (MarginLayoutParams) mRefreshView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin - mRefreshViewHeight;
            final int right = left + mRefreshView.getMeasuredWidth();
            final int bottom = top + mRefreshView.getMeasuredHeight();

            mRefreshView.layout(left, top, right, bottom);
        }

        if (mLoadMoreView != null)
        {
            MarginLayoutParams lp = (MarginLayoutParams) mLoadMoreView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = getMeasuredHeight() + paddingTop + lp.topMargin;
            final int right = left + mLoadMoreView.getMeasuredWidth();
            final int bottom = top + mLoadMoreView.getMeasuredHeight();
            mLoadMoreView.layout(left, top, right, bottom);
        }

        if (mContentView != null)
        {
            MarginLayoutParams lp = (MarginLayoutParams) mContentView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin;
            final int right = left + mContentView.getMeasuredWidth();
            final int bottom = top + mContentView.getMeasuredHeight();

            mContentView.layout(left, top, right, bottom);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {

            View v = getChildAt(i);

            if (v != mRefreshView && v != mLoadMoreView && v != mContentView)
            {
                MarginLayoutParams lp = (MarginLayoutParams) v.getLayoutParams();
                int left = paddingLeft + lp.leftMargin;
                int top = paddingTop + lp.topMargin;
                int right = left + v.getMeasuredWidth();
                int bottom = top + v.getMeasuredHeight();

                v.layout(left, top, right, bottom);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mRefreshView != null)
        {
            measureChildWithMargins(mRefreshView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mRefreshView.getLayoutParams();

            mRefreshViewHeight = mRefreshView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }

        if (mLoadMoreView != null)
        {
            measureChildWithMargins(mLoadMoreView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mLoadMoreView.getLayoutParams();

            mLoadMoreViewHeight = mLoadMoreView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }
        if (mContentView != null)
        {
            measureChildWithMargins(mContentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            View v = getChildAt(i);
            if (v != mRefreshView && v != mLoadMoreView && v != mContentView)
            {
                measureChildWithMargins(v, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }
    }


    /**
     * 能否刷新
     *
     * @return
     */
    private boolean canRefresh()
    {
        return !isRefreshing && mRefreshEnabled && mRefreshView != null && !canChildScrollUp();
    }

    /**
     * 能否加载更多
     *
     * @return
     */
    private boolean canLoadMore()
    {
        return !isLoadMoring && mLoadMoreEnabled && mLoadMoreView != null && !canChildScrollDown();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        return super.dispatchTouchEvent(e);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                directionY = e.getY();
                directionX = e.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                if (directionY <= 0 || directionX <= 0)
                    break;

                float eventY = e.getY();
                float eventX = e.getX();

                float offY = eventY - directionY;
                float offX = eventX - directionX;

                directionY = eventY;
                directionX = eventX;

                boolean moved = Math.abs(offY) > Math.abs(offX);

                if (offY > 0 && moved && canRefresh())
                {
                    isUpOrDown = NO_SCROLL_UP;
                } else if (offY < 0 && moved && canLoadMore())
                {
                    isUpOrDown = NO_SCROLL_DOWN;
                } else
                {
                    isUpOrDown = NO_SCROLL;
                }

                if (isUpOrDown == NO_SCROLL_DOWN || isUpOrDown == NO_SCROLL_UP)
                {
                    return true;
                }

                break;
        }

        return super.onInterceptTouchEvent(e);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        //     当是不可滑动的view里进入
        if (!canChildScrollDown() && !canChildScrollUp())
        {
            if (isUpOrDown == NO_SCROLL_UP)
            {
                if (canRefresh())
                {
                    return touch(e, true);
                }
            } else if (isUpOrDown == NO_SCROLL_DOWN)
            {
                if (canLoadMore())
                {
                    return touch(e, false);
                }
            } else
            {
                switch (e.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        directionY = e.getY();
                        directionX = e.getX();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (directionY <= 0 || directionX <= 0)
                            break;

                        float eventY = e.getY();
                        float eventX = e.getX();

                        float offY = eventY - directionY;
                        float offX = eventX - directionX;

                        directionY = eventY;
                        directionX = eventX;

                        boolean moved = Math.abs(offY) > Math.abs(offX);


                        if (offY > 0 && moved && canRefresh())
                        {
                            isUpOrDown = NO_SCROLL_UP;
                        } else if (offY < 0 && moved && canLoadMore())
                        {
                            isUpOrDown = NO_SCROLL_DOWN;
                        } else
                        {
                            isUpOrDown = NO_SCROLL;
                        }

                        break;
                }

                return true;
            }
        } else
        {

            if (canRefresh())
            {
                return touch(e, true);
            } else if (canLoadMore())
            {
                return touch(e, false);
            }
        }
        return super.onTouchEvent(e);
    }


    /**
     * 触摸滑动处理
     *
     * @param e
     * @param isHead
     * @return
     */
    private boolean touch(MotionEvent e, boolean isHead)
    {
        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                lastY = e.getY();
                return true;

            case MotionEvent.ACTION_MOVE:

                if (lastY > 0)
                {
                    currentOffSetY = (int) (e.getY() - lastY);
                    offsetSum += currentOffSetY;
                }
                lastY = e.getY();

                boolean isCanMove;
                if (isHead)
                {
                    isCanMove = offsetSum > 0;
                } else
                {
                    isCanMove = offsetSum < 0;
                }


                if (isCanMove)
                {
                    float ratio = getRatio();

                    if (ratio < 0)
                        ratio = 0;

                    int scrollNum = -((int) (currentOffSetY * ratio));

                    scrollSum += scrollNum;

                    if (isHead)
                    {
                        setBackgroundResource(mRefreshBgResId);
                        smoothMove(true, true, scrollNum, scrollSum);

                        if (Math.abs(scrollSum) > mRefreshViewHeight)
                            getRefreshView().onPrepare();

                        getRefreshView().onPulling(Math.abs(scrollSum) / (float) mRefreshViewHeight);
                    } else
                    {
                        setBackgroundResource(mLoadMoreBgResId);
                        smoothMove(false, true, scrollNum, scrollSum);

                        if (Math.abs(scrollSum) > mLoadMoreViewHeight)
                            getLoadMoreView().onPrepare();

                        getLoadMoreView().onPulling(Math.abs(scrollSum) / (float) mLoadMoreViewHeight);
                    }
                }

                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (Math.abs(scrollSum) > 3)
                {
                    if (isHead)
                    {
                        if (Math.abs(scrollSum) > mRefreshViewHeight)
                        {
                            smoothMove(true, false, -mRefreshViewHeight, mRefreshViewHeight);
                            getRefreshView().onRelease();
                            refreshing();
                        } else
                        {
                            smoothMove(true, false, 0, 0);
                        }

                    } else
                    {
                        if (Math.abs(scrollSum) > mLoadMoreViewHeight)
                        {
                            smoothMove(false, false, mContentView.getMeasuredHeight() - getMeasuredHeight() + mLoadMoreViewHeight, mLoadMoreViewHeight);
                            getLoadMoreView().onRelease();
                            loadingMore();
                        } else
                        {
                            smoothMove(false, false, mContentView.getMeasuredHeight() - getMeasuredHeight(), 0);
                        }
                    }
                }

                resetParameter();

                break;
        }

        return super.onTouchEvent(e);
    }


    /**
     * 滑动距离越大比率越小，越难拖动
     *
     * @return
     */
    private float getRatio()
    {
        return 1 - (Math.abs(offsetSum) / (float) getMeasuredHeight()) - 0.3f * mFriction;
    }


    /**
     * 重置参数
     */
    private void resetParameter()
    {
        directionX = 0;
        directionY = 0;
        isUpOrDown = NO_SCROLL;
        lastY = 0;
        offsetSum = 0;
        scrollSum = 0;
    }

    /**
     * * 滚动布局的方法
     *
     * @param isHeader
     * @param isMove      手指在移动还是已经抬起
     * @param moveScrollY
     * @param moveY
     */
    private void smoothMove(boolean isHeader, boolean isMove, int moveScrollY, int moveY)
    {
        if (isHeader)
        {
            if (isMove)
                smoothScrollBy(0, moveScrollY);
            else
                smoothScrollTo(0, moveScrollY);
        } else
        {
            if (isMove)
                smoothScrollBy(0, moveScrollY);
            else
                smoothScrollTo(0, moveScrollY);
        }
    }

    /**
     * 调用此方法滚动到目标位置
     *
     * @param fx
     * @param fy
     */
    public void smoothScrollTo(int fx, int fy)
    {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    /**
     * 调用此方法设置滚动的相对偏移
     *
     * @param dx
     * @param dy
     */
    public void smoothScrollBy(int dx, int dy)
    {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();
    }

    /**
     * 刷新成功
     */
    public void notifyRefreshSuccess()
    {
        if (!isRefreshing)
            return;

        getRefreshView().onSuccess();
        postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                smoothMove(true, false, 0, 0);
                isRefreshing = false;
                getRefreshView().onReset();
            }
        }, mDurationOfDelay);
    }

    /**
     * 刷新失败
     */
    public void notifyRefreshFail()
    {
        if (!isRefreshing)
            return;

        getRefreshView().onFail();
        postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                smoothMove(true, false, 0, 0);
                isRefreshing = false;
                getRefreshView().onReset();
            }
        }, mDurationOfDelay);
    }

    /**
     * 加载更多成功
     */
    public void notifyLoadMoreSuccess()
    {
        if (!isLoadMoring)
            return;

        getLoadMoreView().onSuccess();
        postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                smoothMove(false, false, mContentView.getMeasuredHeight() - getMeasuredHeight(), 0);
                isLoadMoring = false;
                getLoadMoreView().onReset();
            }
        }, mDurationOfDelay);
    }

    /**
     * 加载更多成功
     */
    public void notifyLoadMoreFail()
    {
        if (!isLoadMoring)
            return;

        getLoadMoreView().onFail();
        postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                smoothMove(false, false, mContentView.getMeasuredHeight() - getMeasuredHeight(), 0);
                isLoadMoring = false;
                getLoadMoreView().onReset();
            }
        }, mDurationOfDelay);
    }

    /**
     * 自动刷新
     */
    public void autoRefresh()
    {
        if (mRefreshView != null)
        {
            postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    setBackgroundResource(mRefreshBgResId);
                    smoothMove(true, false, -mRefreshViewHeight, -mRefreshViewHeight);
                    getRefreshView().onRelease();
                    refreshing();
                }
            }, DEFAULT_AUTO_DURATION);
        }
    }

    private void refreshing()
    {
        isRefreshing = true;
        if (mOnRefreshListener != null)
            mOnRefreshListener.onRefresh();
    }

    private void loadingMore()
    {
        isLoadMoring = true;
        if (mOnLoadMoreListener != null)
            mOnLoadMoreListener.onLoadMore();
    }


    @Override
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }

        super.computeScroll();
    }

    private CommonPtrViewImpl getRefreshView()
    {
        return (CommonPtrViewImpl) mRefreshView;
    }

    private CommonPtrViewImpl getLoadMoreView()
    {
        return (CommonPtrViewImpl) mLoadMoreView;
    }

    /**
     * 被包裹的子view是否能下拉
     */
    protected boolean canChildScrollUp()
    {
        if (mIsCoo)
            return !isDependentOpen;
        return canScrollUp(mContentView);
    }

    private boolean canScrollUp(View view)
    {
        if (android.os.Build.VERSION.SDK_INT < 14)
        {
            if (view instanceof AbsListView)
            {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else
            {
                return ViewCompat.canScrollVertically(view, -1) || view.getScrollY() > 0;
            }
        } else
        {
            return ViewCompat.canScrollVertically(view, -1);
        }
    }


    /**
     * 是否能上拉
     */
    protected boolean canChildScrollDown()
    {
        if (mIsCoo)
            return isDependentOpen || canScrollDown(mScrollContentView);

        return canScrollDown(mContentView);
    }

    private boolean canScrollDown(View view)
    {
        if (android.os.Build.VERSION.SDK_INT < 14)
        {
            if (view instanceof AbsListView)
            {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else
            {
                return ViewCompat.canScrollVertically(view, 1) || view.getScrollY() < 0;
            }
        } else
        {
            return ViewCompat.canScrollVertically(view, 1);
        }
    }

    /****************************** 设置监听 *****************************************************************/
    /**
     * 设置刷新监听
     */
    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener)
    {
        this.mOnRefreshListener = mOnRefreshListener;
    }

    /**
     * 设置加载更多监听
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public interface OnLoadMoreListener
    {
        void onLoadMore();
    }

    public interface OnRefreshListener
    {
        void onRefresh();
    }
}
