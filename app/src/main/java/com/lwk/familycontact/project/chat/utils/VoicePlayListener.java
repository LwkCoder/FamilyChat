package com.lwk.familycontact.project.chat.utils;

/**
 * Created by LWK
 * TODO 语音消息播放监听
 * 2016/9/22
 */
public interface VoicePlayListener
{
    void startPlay();

    void endPlay();

    void error(int errorCode,int errMsgResId);
}
