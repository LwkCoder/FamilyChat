package com.lwk.familycontact.project.profile;

import android.view.View;

import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;

/**
 * 个人中心界面
 */
public class UserProfileActivity extends FCBaseActivity
{

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_user_profile;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_user_profile);
        actionBar.setTitleText(R.string.tv_user_profile_title);
        actionBar.setLeftLayoutAsBack(this);
    }

    @Override
    protected void onClick(int id, View v)
    {

    }
}
