package com.lwk.familycontact.utils.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by LWK
 * TODO Dialog基类
 * 2016/8/5
 */
public abstract class FCBaseDialog extends DialogFragment
{
    private final String TAG = this.getClass().getSimpleName();
    protected View mRootLayout;
    protected boolean mNeedTitle = true;

    public FCBaseDialog needTitle(boolean b)
    {
        this.mNeedTitle = b;
        return this;
    }

    public FCBaseDialog cancelable(boolean b)
    {
        setCancelable(b);
        return this;
    }

    public FCBaseDialog showDialog(FragmentActivity activity)
    {
        show(activity.getSupportFragmentManager(), TAG);
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (!mNeedTitle)
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRootLayout = inflater.inflate(getLayoutId(), container, false);
        initUI(mRootLayout);
        return mRootLayout;
    }

    public abstract int getLayoutId();

    public abstract void initUI(View layout);
}
