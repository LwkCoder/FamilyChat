package com.lwk.familycontact.project.contact;

import android.os.Bundle;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 通讯录片段
 * 2016/8/2
 */
public class ContactFragment extends BaseFragment
{
    public static ContactFragment newInstance()
    {
        ContactFragment contactFragment = new ContactFragment();
        Bundle bundle = new Bundle();
        contactFragment.setArguments(bundle);
        return contactFragment;
    }

    @Override
    protected int setRootLayoutId()
    {
        return R.layout.fragment_contact;
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
