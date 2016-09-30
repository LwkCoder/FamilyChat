package com.lwk.familycontact.project.login.presenter;

import android.content.Context;

import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.im.helper.HxSdkHelper;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.login.view.LoginView;
import com.lwk.familycontact.storage.sp.SpSetting;

/**
 * Created by LWK
 * TODO 登录界面Presenter
 * 2016/8/4
 */
public class LoginPresenter
{
    private LoginView mLoginView;

    public LoginPresenter(LoginView loginView)
    {
        this.mLoginView = loginView;
    }

    public void setLastLoginPhone(Context context)
    {
        String phone = SpSetting.getLastLoginPhone(context);
        mLoginView.setLastLoginPhone(phone);
    }

    public void setLastLoginPwd(Context context)
    {
        String pwd = SpSetting.getLastLoginPwd(context);
        mLoginView.setLastLoginPwd(pwd);
    }

    public void startLogin(final Context context, final String phone, final String pwd)
    {
        if (StringUtil.isEmpty(phone))
        {
            mLoginView.showPhoneEmptyWarning();
            return;
        }

        if (StringUtil.isEmpty(pwd))
        {
            mLoginView.showPwdEmptyWarning();
            return;
        }

        if (!PhoneUtils.isMobileNO(phone))
        {
            mLoginView.showPhoneErrorWarning();
            return;
        }

        mLoginView.showLoginDialog();
        HxSdkHelper.getInstance().login(phone, pwd, new FCCallBack()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {
                mLoginView.closeLoginDialog();
                mLoginView.showLoginFailMsg(errorMsgResId);
            }

            @Override
            public void onSuccess(Object o)
            {
                mLoginView.closeLoginDialog();
                //存储最近登录账号的数据
                SpSetting.setLastLoginPhone(context, phone);
                SpSetting.setLastLoginPwd(context, pwd);
                //通知界面登录完成
                mLoginView.loginSuccess();
            }
        });
    }
}
