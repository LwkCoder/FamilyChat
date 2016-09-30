package com.lwk.familycontact.project.contact.view;

/**
 * Created by LWK
 * TODO 添加好友界面View
 * 2016/8/26
 */
public interface AddFriendView
{
    void phoneEmptyWarning();

    void phoneErrorWarning();

    void sendRequestSuccess();

    void sendRequestFail(int code,int errMsgId);
}
