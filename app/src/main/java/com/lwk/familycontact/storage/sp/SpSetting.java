package com.lwk.familycontact.storage.sp;

import android.content.Context;

import com.lib.base.sp.Sp;

/**
 * Created by LWK
 * TODO 通用sp设置
 * 2016/8/19
 */
public class SpSetting
{
    /**
     * 获取最近登录的手机号
     */
    public static String getLastLoginPhone(Context context)
    {
        return Sp.getString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PHONE);
    }

    /**
     * 设置最近登录的手机号
     */
    public static void setLastLoginPhone(Context context, String phone)
    {
        Sp.putString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PHONE, phone);
    }

    /**
     * 获取最近登录的密码
     */
    public static String getLastLoginPwd(Context context)
    {
        return Sp.getString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PWD);
    }

    /**
     * 设置最近登录的密码
     */
    public static void setLastLoginPwd(Context context, String pwd)
    {
        Sp.putString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PWD, pwd);
    }

    /**
     * 是否允许拨号按键触摸反馈
     */
    public static boolean isDialFeedBackEnable(Context context)
    {
        return Sp.getBoolean(context.getApplicationContext(), SpKeys.DIAL_FEEDBACK, true);
    }

    /**
     * 设置是否允许拨号按键触摸反馈
     */
    public static void setDialFeendBackEnable(Context context, boolean enable)
    {
        Sp.putBoolean(context, SpKeys.DIAL_FEEDBACK, enable);
    }
}
