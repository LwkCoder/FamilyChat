package com.lwk.familycontact.project.chat.view;

/**
 * Created by LWK
 * TODO 聊天界面View实现的接口
 * 2016/9/20
 */
public interface HxChatImpl
{
    void onRefreshActionBarTitle(String title);

    void refershAdapterStatus();

    void showError(int errorCode,int errMsgResId);
}
