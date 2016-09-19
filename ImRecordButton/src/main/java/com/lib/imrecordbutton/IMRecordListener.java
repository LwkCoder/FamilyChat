package com.lib.imrecordbutton;

/**
 * Created by LWK
 * TODO 录音监听
 * 2016/9/19
 */
public interface IMRecordListener
{
    void startRecord();

    void recordFinish(float seconds, String filePath);
}
