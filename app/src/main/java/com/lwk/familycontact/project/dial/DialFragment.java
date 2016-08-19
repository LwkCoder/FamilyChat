package com.lwk.familycontact.project.dial;

import android.os.Bundle;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.dial.presenter.DialPresenter;
import com.lwk.familycontact.project.dial.view.DialImpl;
import com.lwk.familycontact.storage.sp.SpSetting;
import com.lwk.familycontact.widget.dial.DialPadView;

/**
 * Created by LWK
 * TODO 拨号器片段
 * 2016/8/2
 */
public class DialFragment extends BaseFragment implements DialImpl, DialPadView.onCallListener
{
    private DialPresenter mPresenter;
    private DialPadView mDialPadView;

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
        mPresenter = new DialPresenter(this);
        return R.layout.fragment_dial;
    }

    @Override
    protected void initUI()
    {
        mDialPadView = findView(R.id.dialpad);
        mDialPadView.setOnCallListener(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mDialPadView.onStart();
        mDialPadView.setFeedBackEnable(SpSetting.isDialFeedBackEnable(getActivity()));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mDialPadView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mDialPadView.onStop();
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
        }
    }

    @Override
    public void onCall(String phone)
    {
        if (StringUtil.isEmpty(phone))
        {
            showShortToast(R.string.warning_dial_phone_empty);
            return;
        }
        //拨号
        PhoneUtils.callPhone(getActivity(), phone);
    }
}
