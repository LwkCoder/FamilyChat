package com.lwk.familycontact.project.regist.view;

/**
 * Created by LWK
 * TODO 注册界面View
 * 2016/8/5
 */
public interface RegistView
{
    void showPhoneEmptyWarning();

    void showPwdEmptyWarning();

    void showPhoneErrorWarning();

    void showRegistDialog();

    void closeRegistDialog();

    void showRegistFailMsg(int msgId);

    void registSuccess();
}
