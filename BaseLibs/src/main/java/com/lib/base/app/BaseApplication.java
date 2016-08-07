package com.lib.base.app;

import android.app.Application;

import com.lib.base.log.KLog;

/**
 * Function:通用基类application
 */
public class BaseApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        KLog.init(true);
    }
}
