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

    private String mVideoCachePath;

    /**
     * 获取用户头像缓存地址
     */
    public String getUserHeadCachePath()
    {
        if (mUserHeadCachePath == null)
        {
            mUserHeadCachePath = new StringBuffer().append(ROOT_PATH).append("userhead_cache/").toString();
            File file = new File(mUserHeadCachePath);
            if (!file.exists())
            {
                if (file.mkdirs())
                    return mUserHeadCachePath;
                else
                    return mUserHeadCachePath = ROOT_PATH;
            }
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
            mVoiceMsgCachePath = new StringBuffer().append(ROOT_PATH).append("voice_cache/").toString();
            File file = new File(mVoiceMsgCachePath);
            if (!file.exists())
            {
                if (file.mkdirs())
                    return mVoiceMsgCachePath;
                else
                    return mVoiceMsgCachePath = ROOT_PATH;
            }
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
            mImageCachePath = new StringBuffer().append(ROOT_PATH).append("img_cache/").toString();
            File file = new File(mImageCachePath);
            if (!file.exists())
            {
                if (file.mkdirs())
                    return mImageCachePath;
                else
                    return mImageCachePath = ROOT_PATH;
            }
        }
        return mImageCachePath;
    }

    /**
     * 获取短视频消息缓存地址
     */
    public String getVideoCachePath()
    {
        if (mVideoCachePath == null)
        {
            mVideoCachePath = new StringBuffer().append(ROOT_PATH).append("video_cache/").toString();
            File file = new File(mVideoCachePath);
            if (!file.exists())
            {
                if (file.mkdirs())
                    return mVideoCachePath;
                else
                    return mVideoCachePath = ROOT_PATH;
            }
        }
        return mVideoCachePath;
    }
}
