package com.lwk.familycontact.project.login.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;

import com.cengalabs.flatui.views.FlatEditText;
import com.lib.base.utils.ResUtils;
import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.MainActivity;
import com.lwk.familycontact.project.login.presenter.LoginPresenter;
import com.lwk.familycontact.project.regist.view.RegistActivity;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.RegistBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 登录界面
 */
public class LoginActivity extends FCBaseActivity implements LoginImpl
{
    private LoginPresenter mPresenter;
    private FlatEditText mEdPhone;
    private FlatEditText mEdPwd;
    private ProgressDialog mDialog;

    @Override
    protected int setContentViewId()
    {
        EventBusHelper.getInstance().regist(this);
        mPresenter = new LoginPresenter(this);
        return R.layout.activity_login;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_login);
        actionBar.setLeftLayoutAsBackWithoutText(this);
        actionBar.setTitleText(R.string.tv_login_title);
        actionBar.setRightTvText(R.string.tv_login_regist);
        actionBar.setRightLayoutClickListener(this);

        mEdPhone = findView(R.id.fed_phone);
        mEdPwd = findView(R.id.fed_pwd);
        addClick(R.id.btn_login_confirm);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.setLastLoginPhone(this);
        mPresenter.setLastLoginPwd(this);
    }

    @Override
    public void setLastLoginPhone(String phone)
    {
        mEdPhone.setText(phone);
    }

    @Override
    public void setLastLoginPwd(String pwd)
    {
        mEdPwd.setText(pwd);
    }

    @Override
    public void showPhoneEmptyWarning()
    {
        showShortToast(R.string.warning_phone_can_not_empty);
    }

    @Override
    public void showPwdEmptyWarning()
    {
        showShortToast(R.string.warning_pwd_can_not_empty);
    }

    @Override
    public void showPhoneErrorWarning()
    {
        showShortToast(R.string.warning_phone_error);
    }

    @Override
    public void showLoginDialog()
    {
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage(ResUtils.getString(this, R.string.pgb_login_dialog_content));
        mDialog.show();
    }

    @Override
    public void closeLoginDialog()
    {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    @Override
    public void showLoginFailMsg(int msgId)
    {
        showLongToast(msgId);
    }

    @Override
    public void loginSuccess()
    {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.fl_common_actionbar_right:
                startActivity(new Intent(LoginActivity.this, RegistActivity.class));
                break;
            case R.id.btn_login_confirm:
                String phone = mEdPhone.getText().toString().trim();
                String pwd = mEdPwd.getText().toString().trim();
                mPresenter.startLogin(this, phone, pwd);
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBusHelper.getInstance().unregist(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveRegistBean(RegistBean registBean)
    {
        final String phone = registBean.getPhone();
        final String pwd = registBean.getPwd();
        mEdPhone.setText(phone);
        mEdPwd.setText(pwd);
        mMainHanlder.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mPresenter.startLogin(LoginActivity.this, phone, pwd);
            }
        }, 1000);
    }

}
