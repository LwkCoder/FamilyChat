package com.lwk.okhttp.callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * String形式的回调
 */
public abstract class OkStringCallback extends OkCallback<String>
{
    @Override
    public String transResponse(Response response, int id) throws IOException
    {
        return response.body().string();
    }
}
