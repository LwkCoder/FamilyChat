package com.lwk.familycontact.project.common;

import android.content.Context;

import com.lib.base.utils.SdUtils;
import com.lwk.familycontact.base.FCApplication;

import java.io.File;

/**
 * Created by LWK
 * TODO 各类缓存文件地址
 * 2016/8/15
 */
public class FCCache
{
    private FCCache(Context context)
    {
        ROOT_PATH = SdUtils.getDefaultCachePath(context);
    }

    private static final class FCCacheHolder
    {
        private static FCCache instance = new FCCache(FCApplication.getIntance());
    }

    public static FCCache getInstance()
    {
        return FCCacheHolder.instance;
    }

    private String ROOT_PATH;

    private String mUserHeadCachePath;

    /**
     * 获取用户头像缓存地址
     */
    public String getUserHeadCachePath()
    {
        if (mUserHeadCachePath == null)
        {
            mUserHeadCachePath = new StringBuffer().append(ROOT_PATH).append("userhead/").toString();
            File file = new File(mUserHeadCachePath);
            if (!file.exists())
                file.mkdirs();
        }
        return mUserHeadCachePath;
    }

}
