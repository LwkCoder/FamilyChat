package com.lwk.familycontact.project.common.image;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by LWK
 * TODO 加载图片的接口
 * 2016/9/23
 */
public interface ImageDisplayer
{
    /**
     * 加载图片
     *
     * @param context   上下文环境
     * @param imageView 待加载图片的ImageView
     * @param url       图片地址
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     */
    void display(Context context, ImageView imageView, String url, int maxWidth, int maxHeight);

    /**
     * 加载图片
     *
     * @param context        上下文环境
     * @param imageView      待加载图片的ImageView
     * @param url            图片地址
     * @param maxWidth       最大宽度
     * @param maxHeight      最大高度
     * @param holderImgResId 占位图资源id
     * @param errorImgResId  错误图资源id
     */
    void display(Context context, ImageView imageView, String url, int maxWidth, int maxHeight, int holderImgResId, int errorImgResId);

    /**
     * 加载毛玻璃图片
     *
     * @param context   上下文环境
     * @param imageView 待加载图片的ImageView
     * @param url       图片地址
     * @param radius    半径？【最大取值25】
     * @param sampling  取样率
     */
    void displayBlurImage(Context context, ImageView imageView, String url, int radius, int sampling);

    /**
     * 下载位图
     *
     * @param context  上下文环境
     * @param url      图片地址
     * @param listener 下载完成回调监听
     */
    void downloadBitmap(Context context, String url, OnBitmapDownloadListener listener);
}
