package com.lwk.familycontact.widget;

import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by LWK
 * TODO PopupWindow基类
 * 2016/8/17
 */
public abstract class BasePop
{
    protected Activity mContext;
    protected PopupWindow mPopupWindow;
    protected View mContentView;

    public BasePop(Activity context)
    {
        this.mContext = context;
    }

    private void initPop()
    {
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setFocusable(setFocusable());
        boolean outsideTouchacle = setOutsideTouchable();
        mPopupWindow.setOutsideTouchable(outsideTouchacle);
        mPopupWindow.setBackgroundDrawable(new ShapeDrawable());
        mPopupWindow.setWidth(setLayoutWidthParams());
        mPopupWindow.setHeight(setLayoutHeightParams());
        int animStyle = setAnimStyle();
        if (animStyle != 0)
            mPopupWindow.setAnimationStyle(animStyle);
        //设置内容布局
        mContentView = mContext.getLayoutInflater().inflate(setContentViewId()
                , (ViewGroup) mContext.findViewById(android.R.id.content), false);
        mContentView.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(mContentView);
        //设置点击外部关闭pop
        if (outsideTouchacle)
            mPopupWindow.setTouchInterceptor(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                    {
                        mPopupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });
    }

    /**
     * 子类实现是否获取焦点
     */
    public abstract boolean setFocusable();

    /**
     * 子类实现外部点击是否关闭PopupWindow
     */
    public abstract boolean setOutsideTouchable();

    /**
     * 子类实现指定布局宽度
     */
    public abstract int setLayoutWidthParams();

    /**
     * 子类实现指定布局高度
     */
    public abstract int setLayoutHeightParams();

    /**
     * 子类实现此方法来指定内容布局的id
     */
    protected abstract int setContentViewId();

    /**
     * 子类实现此方法来指定显示/消失动画
     */
    protected abstract int setAnimStyle();

    /**
     * 子类实现此方法来初始化ui
     */
    protected abstract void initUI(View contentView);

    /**
     * 在某位置弹出
     *
     * @param parent  popupwindow所在父布局
     * @param gravity 权重位置
     * @param xOffset x偏移量
     * @param yOffset y偏移量
     */
    public void showAtLocation(View parent, int gravity, int xOffset, int yOffset)
    {
        if (mPopupWindow == null)
        {
            initPop();
            initUI(mContentView);
        }
        mPopupWindow.showAtLocation(parent, gravity, xOffset, yOffset);
    }

    /**
     * 作为某个参照物view的下拉控件
     *
     * @param anchor  参照物view
     * @param xOffset x偏移量
     * @param yOffset y偏移量
     */
    public void showAsDropDown(View anchor, int xOffset, int yOffset)
    {
        if (mPopupWindow == null)
        {
            initPop();
            initUI(mContentView);
        }
        mPopupWindow.showAsDropDown(anchor, xOffset, yOffset);
    }

    /**
     * 判断当前popupwindow是否显示
     */
    public boolean isShowing()
    {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    /**
     * 关闭pop
     */
    public void dismiss()
    {
        if (isShowing())
        {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    /**
     * 设置关闭监听
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener listener)
    {
        if (mPopupWindow != null)
            mPopupWindow.setOnDismissListener(listener);
    }

    public PopupWindow getPopupWindow()
    {
        return mPopupWindow;
    }

    public View getContentView()
    {
        return mContentView;
    }
}
