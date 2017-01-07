package com.lwk.familycontact.project.common.version;

import com.alibaba.fastjson.JSON;
import com.lib.base.log.KLog;
import com.lwk.okhttp.callback.OkCallback;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by LWK
 * TODO 版本信息网络请求回调
 * 2017/1/5
 */
public abstract class OkVersionResultCallBack extends OkCallback<VersionBean>
{
    @Override
    public VersionBean transResponse(Response response, int id) throws Exception
    {
        String result = response.body().string();
        KLog.json(result);
        return JSON.parseObject(result, VersionBean.class);
    }

    @Override
    public void onResponseError(Call call, Exception e, int id)
    {
        KLog.e("获取版本信息失败：" + e.toString());
        onError(id);
    }

    @Override
    public void onResponseSuccess(VersionBean response, int id)
    {
        if (response != null)
            onSuccess(response);
        else
            onError(-1);
    }

    public abstract void onSuccess(VersionBean versionBean);

    public abstract void onError(int errorCode);
}
