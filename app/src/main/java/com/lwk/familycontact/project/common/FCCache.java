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
        private static FCCache instance = new FCCache(FCApplication.getInstance());
    }

    public static FCCache getInstance()
    {
        return FCCacheHolder.instance;
    }

    private String ROOT_PATH;

    private String mUserHeadCachePath;

    private String mVoiceMsgCachePath;

    private String mImageCachePath;

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

    /**
     * 获取语音消息缓存地址
     */
    public String getVoiceCachePath()
    {
        if (mVoiceMsgCachePath == null)
        {
            mVoiceMsgCachePath = new StringBuffer().append(ROOT_PATH).append("voicemsg/").toString();
            File file = new File(mVoiceMsgCachePath);
            if (!file.exists())
                file.mkdirs();
        }
        return mVoiceMsgCachePath;
    }

    /**
     * 获取语音消息缓存地址
     */
    public String getImageCachePath()
    {
        if (mImageCachePath == null)
        {
            mImageCachePath = new StringBuffer().append(ROOT_PATH).append("imgcache/").toString();
            File file = new File(mImageCachePath);
            if (!file.exists())
                file.mkdirs();
        }
        return mImageCachePath;
    }
}
