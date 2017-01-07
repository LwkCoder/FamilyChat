package com.lwk.familycontact.project.main.view;

import com.lwk.familycontact.project.common.version.VersionBean;

/**
 * Created by LWK
 * TODO MainActivity交互接口
 * 2016/8/17
 */
public interface MainView
{
    void onShowFirstBadgeNum(int num);

    void onHideFirstBadgeNum();

    void onShowMiddleBadgeNum(int num);

    void onHideMiddleBadgeNum();

    void showVersionDialog(VersionBean versionBean);
}
