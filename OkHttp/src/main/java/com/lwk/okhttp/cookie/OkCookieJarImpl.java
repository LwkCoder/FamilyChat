package com.lwk.okhttp.cookie;


import com.lwk.okhttp.cookie.store.OkCookieStore;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by zhy on 16/3/10.
 */
public class OkCookieJarImpl implements CookieJar
{
    private OkCookieStore okCookieStore;

    public OkCookieJarImpl(OkCookieStore okCookieStore)
    {
        if (okCookieStore == null)
            throw new IllegalArgumentException("OkCookieJarImpl: okCookieStore can not be null !");
        this.okCookieStore = okCookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        okCookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url)
    {
        return okCookieStore.get(url);
    }

    public OkCookieStore getOkCookieStore()
    {
        return okCookieStore;
    }
}
