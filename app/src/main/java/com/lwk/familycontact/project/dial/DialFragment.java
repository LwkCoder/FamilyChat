package com.lwk.familycontact.project.dial;

import android.os.Bundle;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lwk.familycontact.R;

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
    }

    @Override
    protected void onClick(int id, View v)
    {
    }
}
