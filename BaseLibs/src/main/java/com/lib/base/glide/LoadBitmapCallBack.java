package com.lib.base.glide;

import android.graphics.Bitmap;

/**
 * glide异步获取bitmapd回调
 */
public interface LoadBitmapCallBack
{
    void onLoadFinish(Bitmap bitmap);
}
