package com.lib.imagepicker.utils;

import android.content.Context;
import android.support.v4.content.FileProvider;

/**
 * TODO 自定义FileProvider
 */

public class ImagePickerProvider extends FileProvider
{
    public static String getAuthorities(Context context)
    {
        return context.getPackageName() + ".provider";
    }
}
