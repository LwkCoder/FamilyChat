package com.lwk.okhttp.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求回调
 */
public abstract class OkCallback<T>
{
    /**
     * 主线程回调：请求开始前的回调
     * 可在此开启dialog
     *
     * @param request 请求体
     * @param id      请求id
     */
    public void onBefore(Request request, int id)
    {
    }

    /**
     * 主线程回调：请求完成后的回调
     * 可在此关闭dialog
     *
     * @param id 请求id
     */
    public void onAfter(int id)
    {
    }

    /**
     * 主线程回调：请求过程中的回调
     * 常用于下载文件的过程监听
     *
     * @param progress 0~1
     * @param total    请求响应体的大小
     * @param id       请求id
     */
    public void inProgress(float progress, long total, int id)
    {

    }

    /**
     * 判断网络请求的Response是否成功
     * 注意：transResponse()内已经判断过成功或失败，这里应该返回为true
     *
     * @param response 请求响应体
     * @param id       请求id
     * @return 网络请求成功或失败
     */
    public boolean validateReponse(Response response, int id)
    {
        return response.isSuccessful();
    }

    /**
     * 子线程回调：转换网络请求数据的回调，一般可在此进行网络请求status的判断
     * 一般在此进行结果处理，再分发给不同回调
     *
     * @param response 请求响应体
     * @param id       请求id
     */
    public abstract T transResponse(Response response, int id) throws Exception;

    /**
     * 主线程回调：网络请求失败后的回调
     */
    public abstract void onResponseError(Call call, Exception e, int id);

    /**
     * 请求成功回调：网络请求成功后的回调
     */
    public abstract void onResponseSuccess(T response, int id);

    /**
     * 取消网络请求回调
     */
    public void onCancle(Call call, int id)
    {

    }

    /**
     * 默认回调
     */
    public static OkCallback CALLBACK_DEFAULT = new OkCallback()
    {

        @Override
        public Object transResponse(Response response, int id) throws Exception
        {
            return null;
        }

        @Override
        public void onResponseError(Call call, Exception e, int id)
        {

        }

        @Override
        public void onResponseSuccess(Object response, int id)
        {

        }
    };

}