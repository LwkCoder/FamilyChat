package com.lib.base.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.lib.base.log.KLog;
import com.lib.base.toast.ToastUtils;
import com.lib.base.utils.KeyboradUtils;
import com.lib.base.utils.ViewFinder;

/**
 * Function:通用基类Activity
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener
{
    protected String LOG_TAG = this.getClass().getSimpleName();
    protected View mRootLayout;
    protected Handler mMainHandler;
    protected ViewFinder mVFinder;

    protected void beforeOnCreate(Bundle savedInstanceState)
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        beforeOnCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        KLog.d(LOG_TAG + "--->onCreate()");
        ActivityManager.getInstance().addActivity(this);

        mMainHandler = new Handler(getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                onHandlerMessage(msg);
            }
        };
        if (mRootLayout == null)
            mRootLayout = getLayoutInflater().inflate(setContentViewId(), null);
        setContentView(mRootLayout);
        mVFinder = new ViewFinder(mRootLayout);//查找控件的帮助类
        beforeInitUI(savedInstanceState);
        initUI();
        initData();
    }

    protected abstract int setContentViewId();

    protected void beforeInitUI(Bundle savedInstanceState)
    {
    }

    protected abstract void initUI();

    protected void initData()
    {
    }

    protected void onHandlerMessage(Message msg)
    {
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        KLog.d(LOG_TAG + "--->onStart()");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        KLog.d(LOG_TAG + "--->onResume()");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        KLog.d(LOG_TAG + "--->onPause()");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        KLog.d(LOG_TAG + "--->onStop()");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        KLog.d(LOG_TAG + "--->onRestart()");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        KLog.d(LOG_TAG + "--->onDestroy()");
        ActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 查找View
     */
    protected <T extends View> T findView(int resId)
    {
        return mVFinder.findView(resId);
    }

    /**
     * 添加点击监听到onClick()中
     */
    protected void addClick(View view)
    {
        if (view != null)
            view.setOnClickListener(this);
    }

    /**
     * 添加点击监听到onClick()中
     */
    protected void addClick(int id)
    {
        View view = mVFinder.findView(id);
        addClick(view);
    }

    /**
     * 弹出Toast
     */
    protected void showShortToast(final int resId)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showShortMsg(BaseActivity.this, resId);
            }
        });
    }

    /**
     * 弹出Toast
     */
    protected void showShortToast(final String s)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showShortMsg(BaseActivity.this, s);
            }
        });
    }

    /**
     * 弹出Toast
     */
    protected void showLongToast(final int resId)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showLongMsg(BaseActivity.this, resId);
            }
        });
    }

    /**
     * 弹出Toast
     */
    protected void showLongToast(final String s)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                ToastUtils.showLongMsg(BaseActivity.this, s);
            }
        });
    }

    // 所有子acticity中，如果手指触碰的区域不是edittext，则关闭软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev))
            {
                // 关闭软键盘
                KeyboradUtils.HideKeyboard(v);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev))
        {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event)
    {
        if (v != null && (v instanceof EditText))
        {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom)
            {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v)
    {
        onClick(v.getId(), v);
    }

    protected abstract void onClick(int id, View v);
}
