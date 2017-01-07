package com.lwk.okhttp.builder;


import com.lwk.okhttp.request.OkRequestCall;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * okhttp请求的构造器基类
 */
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder>
{
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, String> params;
    protected int id;
    protected boolean responseOnMainThread = true;

    public T id(int id)
    {
        this.id = id;
        return (T) this;
    }

    public T url(String url)
    {
        this.url = url;
        return (T) this;
    }


    public T tag(Object tag)
    {
        this.tag = tag;
        return (T) this;
    }

    public T headers(Map<String, String> headers)
    {
        this.headers = headers;
        return (T) this;
    }

    public T addHeader(String key, String val)
    {
        if (this.headers == null)
        {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return (T) this;
    }

    /**
     * 设置网络请求的response回调是否在主线程
     */
    public T responseOnMainThread(boolean b)
    {
        this.responseOnMainThread = b;
        return (T) this;
    }

    public abstract OkRequestCall build();
}
