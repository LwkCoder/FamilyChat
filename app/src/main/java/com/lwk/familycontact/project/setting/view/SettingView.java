package com.lwk.familycontact.project.setting.view;

import com.lwk.familycontact.project.common.version.VersionBean;

/**
 * Created by LWK
 * TODO 设置界面View
 * 2016/8/25
 */
public interface SettingView
{
    void hideNoticeLayout();

    void showNoticeLayout();

    void logoutSuccess();

    void showLogoutDialog();

    void closeLogoutDialog();

    void showVersionDialog(VersionBean versionBean);
}
