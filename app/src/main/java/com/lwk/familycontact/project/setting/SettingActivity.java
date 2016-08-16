package com.lwk.familycontact.project.setting;

import android.view.View;

import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;

/**
 * 设置界面
 */
public class SettingActivity extends FCBaseActivity
{

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_setting;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_setting);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setTitleText(R.string.tv_setting_title);
    }

    @Override
    protected void onClick(int id, View v)
    {

    }
}
