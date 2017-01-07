package com.lwk.okhttp.request;


import com.lwk.okhttp.OkHttpUtils;
import com.lwk.okhttp.callback.OkCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 对OkHttpRequest的封装，对外提供更多的接口：cancel(),readTimeOut()...
 */
public class OkRequestCall
{
    private OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;

    private OkHttpClient cloneClient;

    public OkRequestCall(OkHttpRequest request)
    {
        this.okHttpRequest = request;
    }

    public OkRequestCall readTimeOut(long readTimeOut)
    {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public OkRequestCall writeTimeOut(long writeTimeOut)
    {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public OkRequestCall connTimeOut(long connTimeOut)
    {
        this.connTimeOut = connTimeOut;
        return this;
    }

    public Call buildCall(OkCallback okCallback)
    {
        request = generateRequest(okCallback);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0)
        {
            readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;

            cloneClient = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build();

            call = cloneClient.newCall(request);
        } else
        {
            call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        }
        return call;
    }

    private Request generateRequest(OkCallback okCallback)
    {
        return okHttpRequest.generateRequest(okCallback);
    }

    public void execute(OkCallback okCallback)
    {
        buildCall(okCallback);

        if (okCallback != null)
        {
            okCallback.onBefore(request, getOkHttpRequest().getId());
        }

        OkHttpUtils.getInstance().execute(this, okCallback);
    }

    public Call getCall()
    {
        return call;
    }

    public Request getRequest()
    {
        return request;
    }

    public OkHttpRequest getOkHttpRequest()
    {
        return okHttpRequest;
    }

    public Response execute() throws IOException
    {
        buildCall(null);
        return call.execute();
    }

    public void cancel()
    {
        if (call != null)
        {
            call.cancel();
        }
    }
}
