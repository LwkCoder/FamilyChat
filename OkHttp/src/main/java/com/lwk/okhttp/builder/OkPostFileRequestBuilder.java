package com.lwk.okhttp.builder;


import com.lwk.okhttp.request.OkPostFileRequest;
import com.lwk.okhttp.request.OkRequestCall;

import java.io.File;

import okhttp3.MediaType;

/**
 * PostFile请求的Request的构造器
 */
public class OkPostFileRequestBuilder extends OkHttpRequestBuilder<OkPostFileRequestBuilder>
{
    private File file;
    private MediaType mediaType;


    public OkHttpRequestBuilder file(File file)
    {
        this.file = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public OkRequestCall build()
    {
        return new OkPostFileRequest(url, tag, params, headers, file, mediaType, responseOnMainThread, id).build();
    }
}
