package com.lwk.familycontact.project.contact.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.utils.StringUtil;
import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.contact.presenter.UserDetailPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * 用户资料详情界面
 */
public class UserDetailActivity extends FCBaseActivity implements UserDetailImpl
{
    private static final String INTENT_KEY = "user_data_key";
    private UserDetailPresenter mPresenter;
    private UserBean mUserBean;
    private CommonActionBar mActionBar;
    private ImageView mImgHead;
    private FlatTextView mTvName;
    private FlatTextView mTvPhone;

    /**
     * 跳转到该界面的公用方法
     *
     * @param activity 跳转前的activity
     * @param userBean 传递的用户数据对象
     */
    public static void skip(Activity activity, UserBean userBean)
    {
        Intent intent = new Intent(activity, UserDetailActivity.class);
        intent.putExtra(INTENT_KEY, userBean);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        mUserBean = getIntent().getParcelableExtra(INTENT_KEY);
        mPresenter = new UserDetailPresenter(this);
    }

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initUI()
    {
        mActionBar = findView(R.id.cab_user_detail);
        mActionBar.setLeftLayoutAsBack(this);
        mImgHead = findView(R.id.img_user_detail_head);
        mTvName = findView(R.id.tv_user_detail_name);
        mTvPhone = findView(R.id.tv_user_detail_phone);
    }

    @Override
    protected void initData()
    {
        super.initData();
        if (mUserBean != null)
        {
            mActionBar.setTitleText(mUserBean.getDisplayName());
            mTvName.setText(mUserBean.getDisplayName());
            mTvPhone.setText(mUserBean.getPhone());
            String localHead = mUserBean.getLocalHead();
            if (StringUtil.isNotEmpty(localHead))
                Glide.with(this).load(localHead).override(300, 300).into(mImgHead);
            else
                mImgHead.setImageResource(R.drawable.default_avatar);
        }
    }

    @Override
    protected void onClick(int id, View v)
    {

    }
}
