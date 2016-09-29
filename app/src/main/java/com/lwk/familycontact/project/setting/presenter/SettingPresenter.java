package com.lwk.familycontact.project.setting.presenter;

import android.content.Context;

import com.lib.base.app.AppManager;
import com.lwk.familycontact.im.helper.HxSdkHelper;
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

    public boolean isMsgNoticeEnable(Context context)
    {
        return SpSetting.isNewMsgNotice(context);
    }

    public void setMsgNoticeEnable(Context context, boolean enable)
    {
        SpSetting.setNewMsgNotice(context, enable);
        if (enable)
            mSettingView.showNoticeLayout();
        else
            mSettingView.hideNoticeLayout();
    }

    public boolean isMsgNoticeVoiceEnable(Context context)
    {
        return SpSetting.isNewMsgNoticeVoice(context);
    }

    public void setMsgNoticeVoiceEnable(Context context, boolean enable)
    {
        SpSetting.setNewMsgNoticeVoice(context, enable);
    }

    public boolean isMsgNoticeVibrateEnable(Context context)
    {
        return SpSetting.isNewMsgNoticeVibrate(context);
    }

    public void setMsgNoticeVibrateEnable(Context context, boolean enable)
    {
        SpSetting.setNewMsgNoticeVibrate(context, enable);
    }

    public boolean isDialFeedBackEnable(Context context)
    {
        return SpSetting.isDialFeedBackEnable(context);
    }

    public void setDialFeendBackEnable(Context context, boolean enable)
    {
        SpSetting.setDialFeendBackEnable(context, enable);
    }

    public boolean isVoiceMsgHandFreeEnable(Context context)
    {
        return SpSetting.isVoiceMsgHandFreeEnable(context);
    }

    public void setVoiceMsgHandFreeEnable(Context context, boolean enable)
    {
        SpSetting.setVoiceMsgHandFreeEnable(context, enable);
    }

    public boolean isChatTextInputModeFirst(Context context)
    {
        return SpSetting.isChatTextInputModeFirst(context);
    }

    public void setChatTextInputModeFirst(Context context, boolean b)
    {
        SpSetting.setChatTextInputModeFirst(context, b);
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
        AppManager.getInstance().finishAllActivityExceptOne(LoginActivity.class);
    }
}
