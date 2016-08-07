package com.lwk.familycontact.project.setting;

import android.os.Bundle;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 设置片段
 * 2016/8/2
 */
public class SettingFragment extends BaseFragment
{
    public static SettingFragment newInstance()
    {
        SettingFragment settingFragment = new SettingFragment();
        Bundle bundle = new Bundle();
        settingFragment.setArguments(bundle);
        return settingFragment;
    }

    @Override
    protected int setRootLayoutId()
    {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initUI()
    {

    }

    @Override
    protected void onClick(int id, View v)
    {

    }
}
