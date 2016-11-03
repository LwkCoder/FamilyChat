package com.lib.shortvideo.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;

/**
 * 通用帮助类
 */
public class ShortVideoCommonUtil
{

    /**
     * 判断SD卡是否挂载
     *
     * @return
     */
    public static boolean isSDCardMounted()
    {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取sd卡绝对路径
     */
    public static String getSdPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * SD卡剩余空间【单位为byte】
     */
    public static long getAvailableExternalMemorySize()
    {
        if (isSDCardMounted())
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else
        {
            return -1;
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    public static boolean deleteFile(String filePath)
    {
        if (TextUtils.isEmpty(filePath))
            return false;

        File file = new File(filePath);
        if (file.exists())
            return file.delete();
        return true;
    }

    /**
     * 创建指定文件夹下新文件绝对路径
     */
    public static String createNewFileName(String folderPath)
    {
        File file = new File(folderPath);
        if (!file.exists())
            file.exists();
        return new StringBuffer().append(folderPath)
                .append("VID_")
                .append(String.valueOf(System.currentTimeMillis()))
                .append(".mp4").toString();
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context)
    {
        int width;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        return width;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context)
    {
        int height;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        height = outMetrics.heightPixels;
        return height;
    }
}
