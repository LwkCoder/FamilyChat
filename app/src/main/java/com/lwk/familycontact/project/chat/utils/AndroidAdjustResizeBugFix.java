package com.lwk.familycontact.project.chat.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

/**
 * Created by LWK
 * TODO 修复沉浸式状态栏导致Activity的AdjustResize属性失效的工具类
 * 2016/9/27
 */
public class AndroidAdjustResizeBugFix
{
    private View mChildOfContent;
    private int preContentViewHeight;
    private int statusBarHeight;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Activity mActivity;

    public static void assistActivity(Activity activity)
    {
        new AndroidAdjustResizeBugFix(activity);
    }

    private AndroidAdjustResizeBugFix(Activity activity)
    {
        mActivity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        statusBarHeight = getStatusBarHeight();
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            public void onGlobalLayout()
            {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent()
    {
        int curContentViewHeight = getContentViewHeight();
        if (curContentViewHeight != preContentViewHeight)
        {
            int rootViewHeight = mChildOfContent.getRootView().getHeight();
            int heightDiff = rootViewHeight - curContentViewHeight;
            //            KLog.e("curContentViewHeight = " + curContentViewHeight);
            //            KLog.e("preContentViewHeight = " + preContentViewHeight);
            //            KLog.e("rootViewHeight = " + rootViewHeight);
            //            KLog.e("heightDiff = " + heightDiff);
            if (heightDiff > (rootViewHeight / 4))
            {
                // keyboard probably just became visible
                // 如果有高度变化，mChildOfContent.requestLayout()之后界面才会重新测量
                frameLayoutParams.height = rootViewHeight - 1;//Activity能监听到布局发生了1px的高度变化
            } else
            {
                // keyboard probably just became hidden
                frameLayoutParams.height = rootViewHeight;
            }
            mChildOfContent.requestLayout();
            preContentViewHeight = curContentViewHeight;
        }
    }

    private int getContentViewHeight()
    {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return r.bottom - r.top + statusBarHeight;
    }

    private int getStatusBarHeight()
    {
        try
        {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            int dimensionPixelSize = mActivity.getResources().getDimensionPixelSize(x);
            return dimensionPixelSize;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
