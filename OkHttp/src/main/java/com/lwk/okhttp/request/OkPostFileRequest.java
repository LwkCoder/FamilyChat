package com.lwk.okhttp.request;


import com.lwk.okhttp.OkHttpUtils;
import com.lwk.okhttp.callback.OkCallback;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 文件提交PostFile的请求体
 */
public class OkPostFileRequest extends OkHttpRequest
{
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    private File file;
    private MediaType mediaType;

    public OkPostFileRequest(String url, Object tag, Map<String, String> params
            , Map<String, String> headers, File file
            , MediaType mediaType, boolean responseOnMainThread, int id)
    {
        super(url, tag, params, headers, responseOnMainThread, id);
        this.file = file;
        this.mediaType = mediaType;

        if (this.file == null)
            throw new IllegalArgumentException("OkPostFileRequest: the file can not be null !");
        if (this.mediaType == null)
            this.mediaType = MEDIA_TYPE_STREAM;
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return RequestBody.create(mediaType, file);
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final OkCallback okCallback)
    {
        if (okCallback == null)
            return requestBody;
        OkCountingRequestBody okCountingRequestBody = new OkCountingRequestBody(requestBody, new OkCountingRequestBody.Listener()
        {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength)
            {

                OkHttpUtils.getInstance().getDelivery().execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        okCallback.inProgress(bytesWritten * 1.0f / contentLength, contentLength, id);
                    }
                });

            }
        });
        return okCountingRequestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody)
    {
        return builder.post(requestBody).build();
    }


}
