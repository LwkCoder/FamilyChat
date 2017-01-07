package com.lwk.okhttp.builder;

import android.net.Uri;

import com.lwk.okhttp.request.OkGetRequest;
import com.lwk.okhttp.request.OkRequestCall;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Get请求的requst的构造器
 */
public class OkGetRequestBuilder extends OkHttpRequestBuilder<OkGetRequestBuilder> implements OkParamsImpl
{
    @Override
    public OkRequestCall build()
    {
        if (params != null)
            url = appendParams(url, params);

        return new OkGetRequest(url, tag, params, headers, responseOnMainThread, id).build();
    }

    protected String appendParams(String url, Map<String, String> params)
    {
        if (url == null || params == null || params.isEmpty())
            return url;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    @Override
    public OkGetRequestBuilder setParams(Map<String, String> params)
    {
        this.params = params;
        return this;
    }

    @Override
    public OkGetRequestBuilder addParams(String key, String val)
    {
        if (this.params == null)
            params = new LinkedHashMap<>();
        params.put(key, val);
        return this;
    }
}
