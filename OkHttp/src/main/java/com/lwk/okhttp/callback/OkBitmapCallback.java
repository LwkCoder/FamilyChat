package com.lwk.okhttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * Bitmap形式的回调
 */
public abstract class OkBitmapCallback extends OkCallback<Bitmap>
{
    @Override
    public Bitmap transResponse(Response response , int id)
    {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }
}
