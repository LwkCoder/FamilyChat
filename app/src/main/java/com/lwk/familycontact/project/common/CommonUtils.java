package com.lwk.familycontact.project.common;

/**
 * Created by LWK
 * TODO 公共方法集合
 * 2016/9/23
 */
public class CommonUtils
{
    private CommonUtils()
    {
    }

    private static final class CommonUtilsHolder
    {
        private static CommonUtils instance = new CommonUtils();
    }

    public static CommonUtils getInstance()
    {
        return CommonUtilsHolder.instance;
    }

    private ImageDisplayer mImageDisplayer = new GlideImageDisplayer();

    public ImageDisplayer getImageDisplayer()
    {
        return mImageDisplayer;
    }
}
