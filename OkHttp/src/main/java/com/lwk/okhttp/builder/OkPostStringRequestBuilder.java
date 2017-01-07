package com.lwk.okhttp.builder;


import com.lwk.okhttp.request.OkPostStringRequest;
import com.lwk.okhttp.request.OkRequestCall;

import okhttp3.MediaType;

/**
 * PostString请求的requst的构造器
 */
public class OkPostStringRequestBuilder extends OkHttpRequestBuilder<OkPostStringRequestBuilder>
{
    private String content;
    private MediaType mediaType;


    public OkPostStringRequestBuilder content(String content)
    {
        this.content = content;
        return this;
    }

    public OkPostStringRequestBuilder mediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public OkRequestCall build()
    {
        return new OkPostStringRequest(url, tag, params, headers, content, mediaType, responseOnMainThread, id).build();
    }
}
