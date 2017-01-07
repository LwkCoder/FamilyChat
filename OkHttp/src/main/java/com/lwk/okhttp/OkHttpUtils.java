package com.lwk.okhttp;


import com.lwk.okhttp.builder.OkGetRequestBuilder;
import com.lwk.okhttp.builder.OkHeadRequestBuilder;
import com.lwk.okhttp.builder.OkOtherRequestBuilder;
import com.lwk.okhttp.builder.OkPostFileRequestBuilder;
import com.lwk.okhttp.builder.OkPostFormRequestBuilder;
import com.lwk.okhttp.builder.OkPostStringRequestBuilder;
import com.lwk.okhttp.callback.OkCallback;
import com.lwk.okhttp.request.OkRequestCall;
import com.lwk.okhttp.utils.Platform;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * OkHttp工具类
 */
public class OkHttpUtils
{
    public static final long DEFAULT_MILLISECONDS = 30_000L;
    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;

    private OkHttpUtils(OkHttpClient okHttpClient)
    {
        if (okHttpClient == null)
        {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
        } else
        {
            mOkHttpClient = okHttpClient;
        }

        mPlatform = Platform.get();
    }

    /**
     * 指定OkHttpCilint的方法，可在Application的onCreate()中传入自定义参数的Client
     */
    public static OkHttpUtils initClient(OkHttpClient okHttpClient)
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                    mInstance = new OkHttpUtils(okHttpClient);
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance()
    {
        return initClient(null);
    }

    public Executor getDelivery()
    {
        return mPlatform.defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }

    public static OkGetRequestBuilder get()
    {
        return new OkGetRequestBuilder();
    }

    public static OkPostStringRequestBuilder postString()
    {
        return new OkPostStringRequestBuilder();
    }

    public static OkPostFileRequestBuilder postFile()
    {
        return new OkPostFileRequestBuilder();
    }

    public static OkPostFormRequestBuilder post()
    {
        return new OkPostFormRequestBuilder();
    }

    public static OkOtherRequestBuilder put()
    {
        return new OkOtherRequestBuilder(METHOD.PUT);
    }

    public static OkHeadRequestBuilder head()
    {
        return new OkHeadRequestBuilder();
    }

    public static OkOtherRequestBuilder delete()
    {
        return new OkOtherRequestBuilder(METHOD.DELETE);
    }

    public static OkOtherRequestBuilder patch()
    {
        return new OkOtherRequestBuilder(METHOD.PATCH);
    }

    public void execute(final OkRequestCall okRequestCall, OkCallback okCallback)
    {
        if (okCallback == null)
            okCallback = OkCallback.CALLBACK_DEFAULT;
        final OkCallback finalOkCallback = okCallback;
        final int id = okRequestCall.getOkHttpRequest().getId();

        okRequestCall.getCall().enqueue(new okhttp3.Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
                sendFailResultCallback(call, e, finalOkCallback, id);
            }

            @Override
            public void onResponse(final Call call, final Response response)
            {
                if (call.isCanceled())
                {
                    sendCancelCallBack(call, id, finalOkCallback);
                    return;
                }

                if (!finalOkCallback.validateReponse(response, id))
                {
                    sendFailResultCallback(call, new IOException("Http Request Failed , Reponse's code is : " + response.code()), finalOkCallback, id);
                    return;
                }

                Object o = null;
                boolean hasException = false;
                try
                {
                    o = finalOkCallback.transResponse(response, id);
                } catch (Exception e)
                {
                    hasException = true;
                    sendFailResultCallback(call, e, finalOkCallback, id);
                }
                if (!hasException)
                    sendSuccessResultCallback(o, finalOkCallback, okRequestCall.getOkHttpRequest().isResponseOnMain(), id);
            }
        });
    }

    private void sendCancelCallBack(final Call call, final int id, final OkCallback okCallback)
    {
        if (okCallback == null)
            return;

        mPlatform.execute(new Runnable()
        {
            @Override
            public void run()
            {
                okCallback.onCancle(call, id);
                okCallback.onAfter(id);
            }
        });
    }

    private void sendFailResultCallback(final Call call, final Exception e, final OkCallback okCallback, final int id)
    {
        if (okCallback == null)
            return;

        mPlatform.execute(new Runnable()
        {
            @Override
            public void run()
            {
                if (call.isCanceled())
                    okCallback.onCancle(call, id);
                else
                    okCallback.onResponseError(call, e, id);
                okCallback.onAfter(id);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final OkCallback okCallback, final boolean responseOnMainThread, final int id)
    {
        if (okCallback == null)
            return;

        if (responseOnMainThread)
        {
            mPlatform.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    okCallback.onResponseSuccess(object, id);
                    okCallback.onAfter(id);
                }
            });
        } else
        {
            okCallback.onResponseSuccess(object, id);
            mPlatform.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    okCallback.onAfter(id);
                }
            });
        }
    }

    public void cancelTag(Object tag)
    {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
    }

    public static class METHOD
    {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}

