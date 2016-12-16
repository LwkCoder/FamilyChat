package com.lwk.familycontact.base;

import android.app.Application;

import com.cengalabs.flatui.FlatUI;
import com.lib.base.log.KLog;
import com.lib.base.utils.AppUtil;
import com.lwk.familycontact.im.helper.HxSdkHelper;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

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

        //获取gradle.properties里映射到BuildConfig的参数
        try
        {
            String packageName = AppUtil.getMyPackageName(this);
            //根据BuildConfig路径获取实例对象
            Class buildConfig = Class.forName(packageName + ".BuildConfig");
            BuildParams.IS_DEBUG = getConfigField(buildConfig, "IS_DEBUG");
            BuildParams.UMENG_APPKEY = getConfigField(buildConfig, "UMENG_APPKEY");
            BuildParams.UMENG_CHANNEL = getConfigField(buildConfig, "UMENG_CHANNEL");
            BuildParams.EASEMOB_APPKEY = getConfigField(buildConfig, "EASEMOB_APPKEY");
            BuildParams.MIPUSH_APPID = getConfigField(buildConfig, "MIPUSH_APPID");
            BuildParams.MIPUSH_APPKEY = getConfigField(buildConfig, "MIPUSH_APPKEY");
            BuildParams.HWPUSH_APPID = getConfigField(buildConfig, "HWPUSH_APPID");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }

        //初始化Klog
        KLog.init(BuildParams.IS_DEBUG);
        //初始化FlatUI
        FlatUI.initDefaultValues(this);
        FlatUI.setDefaultTheme(FlatUI.DEEP);
        //初始化环信sdk
        HxSdkHelper.getInstance().initSdkOptions(this, BuildParams.IS_DEBUG);
        //初始化友盟
        MobclickAgent.UMAnalyticsConfig umAnalyticsConfig
                = new MobclickAgent.UMAnalyticsConfig(this, BuildParams.UMENG_APPKEY, BuildParams.UMENG_CHANNEL, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.startWithConfigure(umAnalyticsConfig);
        MobclickAgent.setDebugMode(BuildParams.IS_DEBUG);
    }

    public static FCApplication getInstance()
    {
        return mInstance;
    }

    private <T> T getConfigField(Class buildConfig, String field) throws NoSuchFieldException, IllegalAccessException
    {
        Field field1 = buildConfig.getField(field);
        boolean access = field1.isAccessible();
        if (!access)
            field1.setAccessible(true);
        T value = (T) field1.get(buildConfig);
        if (!access)
            field1.setAccessible(false);
        return value;
    }
}
