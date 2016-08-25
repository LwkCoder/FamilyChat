package com.lwk.familycontact.project.setting;

import android.view.View;
import android.widget.CompoundButton;

import com.cengalabs.flatui.views.FlatToggleButton;
import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.storage.sp.SpSetting;

/**
 * 设置界面
 */
public class SettingActivity extends FCBaseActivity implements CompoundButton.OnCheckedChangeListener
{
    private FlatToggleButton mFtgDialFeedBack;

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

        mFtgDialFeedBack = findView(R.id.ftg_setting_dial_feedback);
        mFtgDialFeedBack.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mFtgDialFeedBack.setChecked(SpSetting.isDialFeedBackEnable(this));
    }

    @Override
    protected void onClick(int id, View v)
    {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.ftg_setting_dial_feedback:
                SpSetting.setDialFeendBackEnable(this, isChecked);
                break;
        }
    }
}
