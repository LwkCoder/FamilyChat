package com.lwk.familycontact.project.regist.view;

import android.app.ProgressDialog;
import android.view.View;

import com.cengalabs.flatui.views.FlatEditText;
import com.lib.base.utils.ResUtils;
import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.regist.presenter.RegistPresenter;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.RegistEventBean;

/**
 * 注册界面
 */
public class RegistActivity extends FCBaseActivity implements RegistImpl
{

    private RegistPresenter mPresenter;
    private FlatEditText mEdPhone;
    private FlatEditText mEdPwd;
    private ProgressDialog mDialog;

    @Override
    protected int setContentViewId()
    {
        mPresenter = new RegistPresenter(this);
        return R.layout.activity_regist;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_regist);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setTitleText(R.string.tv_regist_title);

        mEdPhone = findView(R.id.fed_phone);
        mEdPwd = findView(R.id.fed_pwd);
        addClick(R.id.btn_regist_confirm);
    }

    @Override
    protected void onClick(int id, View v)
    {
        String phone = mEdPhone.getText().toString().trim();
        String pwd = mEdPwd.getText().toString().trim();
        mPresenter.regist(phone, pwd);
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
    public void showRegistDialog()
    {
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage(ResUtils.getString(this, R.string.pgb_regist_dialog_content));
        mDialog.show();
    }

    @Override
    public void closeRegistDialog()
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
    }

    @Override
    public void showRegistFailMsg(int msgId)
    {
        showLongToast(msgId);
    }

    @Override
    public void registSuccess()
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                showShortToast(R.string.hint_regist_success);
                String phone = mEdPhone.getText().toString().trim();
                String pwd = mEdPwd.getText().toString().trim();
                EventBusHelper.getInstance().post(new RegistEventBean(phone, pwd));
                finish();
            }
        });
    }
}
