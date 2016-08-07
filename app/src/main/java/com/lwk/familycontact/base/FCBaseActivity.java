package com.lwk.familycontact.base;

import android.os.Bundle;

import com.lib.base.app.BaseActivity;
import com.lib.base.utils.ScreenUtils;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO app的Activity基类
 * 2016/8/2
 */
public abstract class FCBaseActivity extends BaseActivity
{
    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        ScreenUtils.changeStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));
    }
}
