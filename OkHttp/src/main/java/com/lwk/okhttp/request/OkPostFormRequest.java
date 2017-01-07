package com.lwk.okhttp.request;


import com.lwk.okhttp.OkHttpUtils;
import com.lwk.okhttp.builder.OkPostFormRequestBuilder;
import com.lwk.okhttp.callback.OkCallback;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Post表单提交的请求体
 */
public class OkPostFormRequest extends OkHttpRequest
{
    private List<OkPostFormRequestBuilder.FileInput> files;

    public OkPostFormRequest(String url, Object tag, Map<String, String> params
            , Map<String, String> headers, List<OkPostFormRequestBuilder.FileInput> files
            , boolean responseOnMainThread, int id)
    {
        super(url, tag, params, headers, responseOnMainThread, id);
        this.files = files;
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        if (files == null || files.isEmpty())
        {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            return builder.build();
        } else
        {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            addParams(builder);

            for (int i = 0; i < files.size(); i++)
            {
                OkPostFormRequestBuilder.FileInput fileInput = files.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }
            return builder.build();
        }
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

    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try
        {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder)
    {
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    private void addParams(FormBody.Builder builder)
    {
        if (params != null)
        {
            for (String key : params.keySet())
            {
                builder.add(key, params.get(key));
            }
        }
    }

}
