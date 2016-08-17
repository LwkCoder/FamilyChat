package com.lwk.familycontact.project.main.view;

import android.app.Activity;
import android.view.View;

import com.lwk.familycontact.R;
import com.lwk.familycontact.widget.BasePop;

/**
 * Created by LWK
 * TODO MainActivity的菜单
 * 2016/8/17
 */
public class MainMenuPop extends BasePop implements View.OnClickListener
{
    private onMenuClickListener mListener;

    public MainMenuPop(Activity context, onMenuClickListener listener)
    {
        super(context);
        this.mListener = listener;
    }

    @Override
    protected int setContentViewId()
    {
        return R.layout.layout_main_plus_menu_pop;
    }

    @Override
    protected int setAnimStyle()
    {
        return 0;
    }

    @Override
    protected void initUI(View contentView)
    {
        contentView.findViewById(R.id.ll_main_menu_profile).setOnClickListener(this);
        contentView.findViewById(R.id.ll_main_menu_adduser).setOnClickListener(this);
        contentView.findViewById(R.id.ll_main_menu_setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ll_main_menu_profile:
                if (mListener != null)
                    mListener.onClickProfile();
                break;
            case R.id.ll_main_menu_adduser:
                if (mListener != null)
                    mListener.onClickAddUser();
                break;
            case R.id.ll_main_menu_setting:
                if (mListener != null)
                    mListener.onClickSetting();
                break;
        }
        dismiss();
    }

    public interface onMenuClickListener
    {
        void onClickProfile();

        void onClickAddUser();

        void onClickSetting();
    }
}
