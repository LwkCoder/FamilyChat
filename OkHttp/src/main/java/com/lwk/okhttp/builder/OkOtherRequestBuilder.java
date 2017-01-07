package com.lwk.okhttp.builder;


import com.lwk.okhttp.request.OkOtherRequest;
import com.lwk.okhttp.request.OkRequestCall;

import okhttp3.RequestBody;

/**
 * DELETE、PUT、PATCH等其他方法
 */
public class OkOtherRequestBuilder extends OkHttpRequestBuilder<OkOtherRequestBuilder>
{
    private RequestBody requestBody;
    private String method;
    private String content;

    public OkOtherRequestBuilder(String method)
    {
        this.method = method;
    }

    @Override
    public OkRequestCall build()
    {
        return new OkOtherRequest(requestBody, content, method, url, tag, params, headers, responseOnMainThread, id).build();
    }

    public OkOtherRequestBuilder requestBody(RequestBody requestBody)
    {
        this.requestBody = requestBody;
        return this;
    }

    public OkOtherRequestBuilder requestBody(String content)
    {
        this.content = content;
        return this;
    }


}
