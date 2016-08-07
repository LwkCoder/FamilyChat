package com.lwk.familycontact.project.regist.presenter;

import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.regist.view.RegistImpl;

/**
 * Created by LWK
 * TODO 注册界面Presenter
 * 2016/8/5
 */
public class RegistPresenter
{
    private RegistImpl mRegistView;

    public RegistPresenter(RegistImpl registView)
    {
        this.mRegistView = registView;
    }

    public void regist(String phone, String pwd)
    {
        if (StringUtil.isEmpty(phone))
        {
            mRegistView.showPhoneEmptyWarning();
            return;
        }

        if (StringUtil.isEmpty(pwd))
        {
            mRegistView.showPwdEmptyWarning();
            return;
        }

        if (!PhoneUtils.isMobileNO(phone))
        {
            mRegistView.showPhoneErrorWarning();
            return;
        }

        mRegistView.showRegistDialog();
        HxSdkHelper.getInstance().regist(phone, pwd, new FCCallBack()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {
                mRegistView.closeRegistDialog();
                mRegistView.showRegistFailMsg(errorMsgResId);
            }

            @Override
            public void onSuccess(Object o)
            {
                mRegistView.closeRegistDialog();
                mRegistView.registSuccess();
            }
        });
    }
}
