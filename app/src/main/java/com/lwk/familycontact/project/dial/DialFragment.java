package com.lwk.familycontact.project.dial;

import android.os.Bundle;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lwk.familycontact.R;
import com.lwk.familycontact.storage.db.user.UserDao;

/**
 * Created by LWK
 * TODO 拨号器片段
 * 2016/8/2
 */
public class DialFragment extends BaseFragment
{
    public static DialFragment newInstance()
    {
        DialFragment dialFragment = new DialFragment();
        Bundle bundle = new Bundle();
        dialFragment.setArguments(bundle);
        return dialFragment;
    }

    @Override
    protected int setRootLayoutId()
    {
        return R.layout.fragment_dial;
    }

    @Override
    protected void initUI()
    {
        addClick(R.id.btn_test01);
        addClick(R.id.btn_test02);
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.btn_test01:
                UserDao.getInstance().updateUserAsRegisted("123");
                break;
            case R.id.btn_test02:
                UserDao.getInstance().updateUserAsRegisted("456");
                break;
        }
    }
}
