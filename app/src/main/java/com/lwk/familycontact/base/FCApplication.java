package com.lwk.familycontact.base;

import android.app.Application;

import com.cengalabs.flatui.FlatUI;
import com.lib.base.log.KLog;
import com.lwk.familycontact.im.HxSdkHelper;

/**
 * Created by LWK
 * TODO app的Application入口
 * 2016/8/2
 */
public class FCApplication extends Application
{
    private static FCApplication mInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
        //初始化Klog
        KLog.init(BuildParams.DEBUG);
        //初始化FlatUI
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        //初始化环信sdk
        HxSdkHelper.getInstance().initSdkOptions(this);
    }

    public static FCApplication getIntance()
    {
        return mInstance;
    }
}
