package com.lwk.okhttp.builder;

import java.util.Map;

/**
 * 设置网络请求参数的接口
 */
public interface OkParamsImpl
{
    OkHttpRequestBuilder setParams(Map<String, String> params);

    OkHttpRequestBuilder addParams(String key, String val);
}
