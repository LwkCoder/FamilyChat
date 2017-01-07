package com.lwk.okhttp.request;


import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * PostString的请求体
 */
public class OkPostStringRequest extends OkHttpRequest
{
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    private String content;
    private MediaType mediaType;


    public OkPostStringRequest(String url, Object tag, Map<String, String> params
            , Map<String, String> headers, String content
            , MediaType mediaType, boolean responseOnMainThread, int id)
    {
        super(url, tag, params, headers, responseOnMainThread, id);
        this.content = content;
        this.mediaType = mediaType;

        if (this.content == null)
            throw new IllegalArgumentException("OkPostStringRequest: the content can not be null !");
        if (this.mediaType == null)
            this.mediaType = MEDIA_TYPE_PLAIN;
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody)
    {
        return builder.post(requestBody).build();
    }
}
