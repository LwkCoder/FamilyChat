package com.lwk.okhttp.request;


import com.lwk.okhttp.callback.OkCallback;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 请求体基类
 */
public abstract class OkHttpRequest
{
    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    protected int id;
    protected boolean responseOnMain = true;

    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag
            , Map<String, String> params
            , Map<String, String> headers
            , boolean responseOnMain
            , int id)
    {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.responseOnMain = responseOnMain;
        this.id = id;

        if (url == null)
            throw new IllegalArgumentException("OkHttpRequest: url can not be null !");

        initBuilder();
    }

    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initBuilder()
    {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, final OkCallback okCallback)
    {
        return requestBody;
    }

    protected abstract Request buildRequest(RequestBody requestBody);

    public OkRequestCall build()
    {
        return new OkRequestCall(this);
    }


    public Request generateRequest(OkCallback okCallback)
    {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, okCallback);
        Request request = buildRequest(wrappedRequestBody);
        return request;
    }


    protected void appendHeaders()
    {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty())
            return;

        for (String key : headers.keySet())
        {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    public boolean isResponseOnMain()
    {
        return responseOnMain;
    }

    public int getId()
    {
        return id;
    }
}
