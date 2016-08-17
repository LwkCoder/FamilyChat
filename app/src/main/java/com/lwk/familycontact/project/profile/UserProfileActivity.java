package com.lwk.familycontact.project.profile;

import android.view.View;
import android.widget.TextView;

import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.im.HxSdkHelper;

/**
 * 我的资料界面
 */
public class UserProfileActivity extends FCBaseActivity
{
    private TextView mTvPhone;
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

        mTvPhone = findView(R.id.tv_user_profile_phone);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mTvPhone.setText(HxSdkHelper.getInstance().getCurLoginUser());
    }

    @Override
    protected void onClick(int id, View v)
    {

    }
}
