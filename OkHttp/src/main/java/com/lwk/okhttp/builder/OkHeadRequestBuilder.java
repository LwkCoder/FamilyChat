package com.lwk.okhttp.builder;


import com.lwk.okhttp.OkHttpUtils;
import com.lwk.okhttp.request.OkOtherRequest;
import com.lwk.okhttp.request.OkRequestCall;

/**
 * HEAD方式请求的Request构造器
 */
public class OkHeadRequestBuilder extends OkGetRequestBuilder
{
    @Override
    public OkRequestCall build()
    {
        return new OkOtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers, responseOnMainThread, id).build();
    }
}
