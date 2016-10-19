package com.lwk.familycontact.project.chat.utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LWK
 * TODO 短视频下载异步任务
 * 2016/10/19
 */
public class DownLoadVideoTask extends AsyncTask<Void, Float, String>
{
    //下载地址
    private String mDownLoadUrl;
    //保存路径（绝对路径）
    private String mFileSavePath;
    //进度更新
    private onDownLoadListener mListener;

    public DownLoadVideoTask(String downloadUrl, String savePath)
    {
        this.mDownLoadUrl = downloadUrl;
        this.mFileSavePath = savePath;
    }

    public DownLoadVideoTask(String downloadUrl, String savePath, onDownLoadListener listener)
    {
        this.mDownLoadUrl = downloadUrl;
        this.mFileSavePath = savePath;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if (mListener != null)
            mListener.onDownloadStart();
    }

    @Override
    protected String doInBackground(Void... params)
    {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(mDownLoadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            output = new FileOutputStream(mFileSavePath);
            byte data[] = new byte[2048];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1)
            {
                if (isCancelled())
                {
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((float) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e)
        {
            return null;
        } finally
        {
            try
            {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
                if (connection != null)
                    connection.disconnect();
            } catch (IOException ignored)
            {
            }
        }
        return mFileSavePath;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        if (TextUtils.isEmpty(s))
        {
            if (mListener != null)
                mListener.onDownloadFail();
        } else
        {
            if (mListener != null)
                mListener.onDownloadSuccess(s);
        }
    }

    @Override
    protected void onProgressUpdate(Float... values)
    {
        super.onProgressUpdate(values);
        if (mListener != null)
            mListener.onProgressUpdated(values[0]);
    }

    public void setOnDownloadListener(onDownLoadListener listener)
    {
        this.mListener = listener;
    }

    public interface onDownLoadListener
    {
        void onDownloadStart();

        void onProgressUpdated(float progress);

        void onDownloadSuccess(String filePath);

        void onDownloadFail();
    }
}
