package com.lwk.familycontact.project.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by LWK
 * TODO Glide实现的加载图片
 * 2016/9/23
 */
public class GlideImageDisplayer implements ImageDisplayer
{
    @Override
    public void display(Context context, ImageView imageView, String url, int maxWidth, int maxHeight)
    {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .override(maxWidth, maxHeight)
                .into(imageView);
    }

    @Override
    public void display(Context context, ImageView imageView, String url, int maxWidth, int maxHeight, int holderImgResId, int errorImgResId)
    {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .placeholder(holderImgResId)
                .error(errorImgResId)
                .override(maxWidth, maxHeight)
                .into(imageView);
    }

    @Override
    public void downloadBitmap(Context context, String url, final OnBitmapDownloadListener listener)
    {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
                    {
                        if (listener != null)
                            listener.onDownload(resource);
                    }
                });
    }
}
