package com.lwk.okhttp.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * get请求体
 */
public class OkGetRequest extends OkHttpRequest
{
    public OkGetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, boolean responseOnMainThread, int id)
    {
        super(url, tag, params, headers, responseOnMainThread, id);
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody)
    {
        return builder.get().build();
    }
}
