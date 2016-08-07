package com.lib.base.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.lib.base.log.KLog;

import java.util.List;

/**
 * App帮助类
 */
public class AppUtil
{
    /**
     * 获取包名
     */
    public static String getMyPackageName(Context context)
    {
        return context.getPackageName();
    }

    /**
     * * 获取应用版本号(用于系统识别)
     * * @return 当前应用的版本号
     */
    public static int getAppVersionCode(Context context)
    {
        try
        {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * * 获取应用版本名称(用于用户识别)
     * * @return 当前应用的版本名称
     */
    public static String getAppVersionName(Context context)
    {
        try
        {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取系统版本号
     */
    public static int getAndroidSDKVersion()
    {
        int version = 0;
        try
        {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e)
        {
            KLog.e(e.toString());
        }
        return version;
    }

    /**
     * 获取应用程序最大可用内存
     *
     * @return
     */
    public static int getMaxMemory()
    {
        return (int) Runtime.getRuntime().maxMemory();
    }

    /**
     * 获取设备唯一识别码
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context)
    {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 判断当前程序是否正在运行
     */
    public static boolean isProcessRunning(Context context)
    {
        if (context == null)
            return false;

        String processName = getMyPackageName(context);
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
        if (processInfoList == null || processInfoList.size() == 0)
        {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList)
        {
            if (processInfo != null && processInfo.pid == pid
                    && ObjectUtils.isEquals(processName, processInfo.processName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前程序是否处于后台，需要权限android.permission.GET_TASKS
     */
    public static boolean isApplicationInBackground(Context context)
    {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty())
        {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    public static boolean isForeground(Context context, String className)
    {
        if (context == null || TextUtils.isEmpty(className))
        {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0)
        {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
            {
                return true;
            }
        }
        return false;
    }
}
