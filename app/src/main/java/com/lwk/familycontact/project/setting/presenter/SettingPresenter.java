package com.lwk.familycontact.project.setting.presenter;

import android.content.Context;

import com.lib.base.app.ActivityManager;
import com.lib.base.log.KLog;
import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.login.view.LoginActivity;
import com.lwk.familycontact.project.setting.view.SettingImpl;
import com.lwk.familycontact.storage.sp.SpSetting;

/**
 * Created by LWK
 * TODO 设置界面Presenter
 * 2016/8/25
 */
public class SettingPresenter
{
    private SettingImpl mSettingView;

    public SettingPresenter(SettingImpl settingView)
    {
        this.mSettingView = settingView;
    }

    public boolean isDialFeedBackEnable(Context context)
    {
        return SpSetting.isDialFeedBackEnable(context);
    }

    public boolean isVoiceMsgHandFreeEnable(Context context)
    {
        return SpSetting.isVoiceMsgHandFreeEnable(context);
    }

    public void setDialFeendBackEnable(Context context, boolean enable)
    {
        SpSetting.setDialFeendBackEnable(context, enable);
    }

    public void setVoiceMsgHandFreeEnable(Context context, boolean enable)
    {
        SpSetting.setVoiceMsgHandFreeEnable(context, enable);
    }

    public void logout()
    {
        mSettingView.showLogoutDialog();
        HxSdkHelper.getInstance().logout(new FCCallBack()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {
                doLogout();
            }

            @Override
            public void onSuccess(Object o)
            {
                doLogout();
            }
        });
    }

    private void doLogout()
    {
        mSettingView.closeLogoutDialog();
        mSettingView.logoutSuccess();
        ActivityManager.getInstance().finishAllActivityExceptOne(LoginActivity.class);
    }
}
