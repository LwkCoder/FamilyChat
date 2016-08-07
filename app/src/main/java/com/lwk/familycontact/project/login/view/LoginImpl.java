package com.lwk.familycontact.project.login.view;

/**
 * Created by LWK
 * TODO 登录界面View
 * 2016/8/4
 */
public interface LoginImpl
{
    void setLastLoginPhone(String phone);

    void setLastLoginPwd(String pwd);

    void showPhoneEmptyWarning();

    void showPwdEmptyWarning();

    void showPhoneErrorWarning();

    void showLoginDialog();

    void closeLoginDialog();

    void showLoginFailMsg(int msgId);

    void loginSuccess();
}
