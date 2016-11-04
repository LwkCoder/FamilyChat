package com.lib.base.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * Function:快速查找控件的帮助类
 */
public class ViewFinder
{
    private final String ERROR = "ViewFinder error: must set up layout !";
    private View mLayout;
    private SparseArray<View> mViews;

    public ViewFinder(View layout)
    {
        this.mLayout = layout;
        if (mLayout == null)
            throw new IllegalArgumentException(ERROR);
        this.mViews = new SparseArray<>();
    }

    /**
     * 获取布局
     */
    public View getLayout()
    {
        return mLayout;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     */
    public <T extends View> T findView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mLayout.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 通过控件Id设置其对应的点击事件
     *
     * @param viewId   控件id
     * @param listener 点击事件
     * @return 该控件
     */
    public View addClick(int viewId, View.OnClickListener listener)
    {
        View view = findView(viewId);
        if (view != null)
            view.setOnClickListener(listener);
        return view;
    }

    /**
     * 设置控件点击事件
     *
     * @param view     控件对象
     * @param listener 点击事件
     * @return 该控件
     */
    public View addClick(View view, View.OnClickListener listener)
    {
        if (view != null)
            view.setOnClickListener(listener);
        return view;
    }
}
