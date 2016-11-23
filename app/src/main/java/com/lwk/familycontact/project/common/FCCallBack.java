package com.lwk.familycontact.project.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by LWK
 * TODO 通用回调
 * 2016/8/4
 */
public abstract class FCCallBack<T>
{
    private Type mType;

    public FCCallBack()
    {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass)
    {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof ParameterizedType)
            return ((ParameterizedType) superclass).getActualTypeArguments()[0];
        else
            return String.class;
    }

    //失败回调
    public abstract void onFail(int status, int errorMsgResId);

    //成功回调
    public abstract void onSuccess(T t);
}
