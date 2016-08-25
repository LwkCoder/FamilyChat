package com.lwk.familycontact.project.setting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;

import com.cengalabs.flatui.views.FlatToggleButton;
import com.lib.base.utils.ResUtils;
import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.login.view.LoginActivity;
import com.lwk.familycontact.project.setting.presenter.SettingPresenter;
import com.lwk.familycontact.project.setting.view.SettingImpl;

/**
 * 设置界面
 */
public class SettingActivity extends FCBaseActivity implements CompoundButton.OnCheckedChangeListener
        , SettingImpl
{
    private SettingPresenter mPresenter;
    private FlatToggleButton mFtgDialFeedBack;
    private FlatToggleButton mFtgVoiceMsgHandFree;
    private ProgressDialog mDialog;

    @Override
    protected int setContentViewId()
    {
        mPresenter = new SettingPresenter(this);
        return R.layout.activity_setting;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_setting);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setTitleText(R.string.tv_setting_title);

        mFtgDialFeedBack = findView(R.id.ftg_setting_dial_feedback);
        mFtgVoiceMsgHandFree = findView(R.id.ftg_setting_msg_voice_handfree);
        mFtgDialFeedBack.setOnCheckedChangeListener(this);
        mFtgVoiceMsgHandFree.setOnCheckedChangeListener(this);

        addClick(R.id.btn_setting_logout);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mFtgDialFeedBack.setChecked(mPresenter.isDialFeedBackEnable(this));
        mFtgVoiceMsgHandFree.setChecked(mPresenter.isVoiceMsgHandFreeEnable(this));
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.btn_setting_logout:
                mPresenter.logout();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.ftg_setting_dial_feedback:
                mPresenter.setDialFeendBackEnable(this, isChecked);
                break;
            case R.id.ftg_setting_msg_voice_handfree:
                mPresenter.setVoiceMsgHandFreeEnable(this, isChecked);
                break;
        }
    }

    @Override
    public void logoutSuccess()
    {
        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
    }

    @Override
    public void showLogoutDialog()
    {
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage(ResUtils.getString(this, R.string.dialog_setting_logout_message));
        mDialog.show();
    }

    @Override
    public void closeLogoutDialog()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
    }
}
