package com.lwk.familycontact.base;

import android.os.Bundle;

import com.lib.base.app.BaseActivity;
import com.lib.base.utils.ScreenUtils;
import com.lwk.familycontact.R;
import com.lwk.familycontact.utils.notify.FCNotifyUtils;
import com.umeng.analytics.MobclickAgent;

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
        //修改状态栏和导航栏颜色
        ScreenUtils.changeStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));
        ScreenUtils.changeNavigationBarColor(this, getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FCNotifyUtils.getInstance().resetNotification();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
