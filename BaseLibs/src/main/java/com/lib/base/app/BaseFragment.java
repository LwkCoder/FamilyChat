package com.lib.base.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lib.base.log.KLog;
import com.lib.base.utils.ViewFinder;
import com.lib.base.toast.ToastUtils;

/**
 * Function:通用基类fragment
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener
{
    protected String LOG_TAG = this.getClass().getSimpleName();
    protected View mRootLayout;
    protected Handler mMainHandler;
    protected ViewFinder mVFinder;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        KLog.d(LOG_TAG + "--->onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        KLog.d(LOG_TAG + "--->onCreate()");
        mMainHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                onHandlerMessage(msg);
            }
        };
    }

    protected void onHandlerMessage(Message msg)
    {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        KLog.d(LOG_TAG + "--->onCreateView()");
        if (mRootLayout == null)
            mRootLayout = inflater.inflate(setRootLayoutId(), container, false);
        mVFinder = new ViewFinder(mRootLayout);
        initUI();
        initData();
        return mRootLayout;
    }

    protected abstract int setRootLayoutId();

    protected abstract void initUI();

    protected void initData()
    {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        KLog.d(LOG_TAG + "--->onActivityCreated()");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        KLog.d(LOG_TAG + "--->onStart()");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        KLog.d(LOG_TAG + "--->onResume()");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        KLog.d(LOG_TAG + "--->onPause()");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        KLog.d(LOG_TAG + "--->onStop()");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        KLog.d(LOG_TAG + "--->onDestroyView()");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        KLog.d(LOG_TAG + "--->onDestroy()");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        KLog.d(LOG_TAG + "--->onDetach()");
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        KLog.d(LOG_TAG + "--->onHiddenChanged() = " + hidden);
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
                ToastUtils.showShortMsg(getActivity(), resId);
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
                ToastUtils.showShortMsg(getActivity(), s);
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
                ToastUtils.showLongMsg(getActivity(), resId);
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
                ToastUtils.showLongMsg(getActivity(), s);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        onClick(v.getId(), v);
    }

    protected abstract void onClick(int id, View v);
}
