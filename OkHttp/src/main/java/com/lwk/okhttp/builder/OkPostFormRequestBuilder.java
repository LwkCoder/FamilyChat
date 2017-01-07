package com.lwk.okhttp.builder;


import com.lwk.okhttp.request.OkPostFormRequest;
import com.lwk.okhttp.request.OkRequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PostForm请求的requst的构造器
 * [基于Post的表单提交,即常用Post提交方式]
 */
public class OkPostFormRequestBuilder extends OkHttpRequestBuilder<OkPostFormRequestBuilder> implements OkParamsImpl
{
    private List<FileInput> files = new ArrayList<>();

    @Override
    public OkRequestCall build()
    {
        return new OkPostFormRequest(url, tag, params, headers, files, responseOnMainThread, id).build();
    }

    public OkPostFormRequestBuilder setFiles(String key, Map<String, File> files)
    {
        for (String filename : files.keySet())
        {
            this.files.add(new FileInput(key, filename, files.get(filename)));
        }
        return this;
    }

    public OkPostFormRequestBuilder addFile(String key, String filename, File file)
    {
        files.add(new FileInput(key, filename, file));
        return this;
    }

    public static class FileInput
    {
        public String key;
        public String filename;
        public File file;

        public FileInput(String key, String filename, File file)
        {
            this.key = key;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString()
        {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }

    @Override
    public OkPostFormRequestBuilder setParams(Map<String, String> params)
    {
        this.params = params;
        return this;
    }

    @Override
    public OkPostFormRequestBuilder addParams(String key, String val)
    {
        if (this.params == null)
            params = new LinkedHashMap<>();
        params.put(key, val);
        return this;
    }

}
