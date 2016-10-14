package com.lib.shortvideo.utils;

import android.os.Environment;

import com.lib.shortvideo.videoview.recorder.Constants;

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
        File file = new File(filePath);
        if (file.exists())
            return file.delete();
        return true;
    }

    /**
     * 创建文件名
     *
     * @param folder    父文件夹
     * @param subfolder 子文件加
     * @param uniqueId  文件名唯一识别码
     */
    public static String createFilePath(String folder, String subfolder, String uniqueId)
    {
        File dir = new File(Environment.getExternalStorageDirectory(), folder);
        if (subfolder != null)
            dir = new File(dir, subfolder);
        dir.mkdirs();
        String fileName = Constants.FILE_START_NAME + uniqueId + Constants.VIDEO_EXTENSION;
        return new File(dir, fileName).getAbsolutePath();
    }
}
