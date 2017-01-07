package com.lwk.okhttp.request;

import android.text.TextUtils;

import com.lwk.okhttp.OkHttpUtils;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * 其他种类的请求体
 */
public class OkOtherRequest extends OkHttpRequest
{
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    private RequestBody requestBody;
    private String method;
    private String content;

    public OkOtherRequest(RequestBody requestBody, String content, String method, String url, Object tag
            , Map<String, String> params, Map<String, String> headers, boolean responseOnMainThread, int id)
    {
        super(url, tag, params, headers, responseOnMainThread, id);
        this.requestBody = requestBody;
        this.method = method;
        this.content = content;

    }

    @Override
    protected RequestBody buildRequestBody()
    {
        if (requestBody == null && TextUtils.isEmpty(content) && HttpMethod.requiresRequestBody(method))
            throw new IllegalArgumentException("requestBody and content can not be null in method:" + method);

        if (requestBody == null && !TextUtils.isEmpty(content))
            requestBody = RequestBody.create(MEDIA_TYPE_PLAIN, content);

        return requestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody)
    {
        if (method.equals(OkHttpUtils.METHOD.PUT))
        {
            builder.put(requestBody);
        } else if (method.equals(OkHttpUtils.METHOD.DELETE))
        {
            if (requestBody == null)
                builder.delete();
            else
                builder.delete(requestBody);
        } else if (method.equals(OkHttpUtils.METHOD.HEAD))
        {
            builder.head();
        } else if (method.equals(OkHttpUtils.METHOD.PATCH))
        {
            builder.patch(requestBody);
        }

        return builder.build();
    }

}
