package com.lib.shortvideo.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 文件帮助类
 */
public class FileUtil
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
}
