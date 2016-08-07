package com.lib.base.utils;

import android.content.Context;

/**
 * Function:本地资源获取帮助类
 */
public class ResUtils
{
    /**
     * 从本地资源文件中获取字符串
     *
     * @param c     上下文
     * @param resId 资源文件id
     */
    public static String getString(Context c, int resId)
    {
        return c.getApplicationContext().getResources().getString(resId);
    }

    /**
     * 从本地资源文件中获取浮点长度
     *
     * @param c     上下文
     * @param resId 资源文件id
     */
    public static float getDimen(Context c, int resId)
    {
        return c.getApplicationContext().getResources().getDimension(resId);
    }

    /**
     * 从本地资源文件中获取颜色
     *
     * @param c     上下文
     * @param resId 资源文件id
     */
    public static int getColor(Context c, int resId)
    {
        return c.getApplicationContext().getResources().getColor(resId);
    }
}
