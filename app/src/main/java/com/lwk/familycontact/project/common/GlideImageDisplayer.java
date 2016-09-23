package com.lwk.familycontact.project.common;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
                .override(maxWidth, maxHeight)
                .into(imageView);
    }

    @Override
    public void display(Context context, ImageView imageView, String url, int maxWidth, int maxHeight, int holderImgResId, int errorImgResId)
    {
        Glide.with(context)
                .load(url)
                .placeholder(holderImgResId)
                .error(errorImgResId)
                .override(maxWidth, maxHeight)
                .into(imageView);
    }
}
