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
import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.ImagePickerOptions;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imagepicker.model.ImagePickerMode;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.common.FCCache;
import com.lwk.familycontact.project.contact.presenter.UserDetailPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.ProfileUpdateEventBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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
        EventBusHelper.getInstance().regist(this);
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

        addClick(mImgHead);
    }

    @Override
    protected void initData()
    {
        super.initData();
        setUserProfile();
    }

    private void setUserProfile()
    {
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
        switch (id)
        {
            case R.id.img_user_detail_head:
                if (mUserBean == null)
                    return;

                ImagePickerOptions options = new ImagePickerOptions.Builder()
                        .pickMode(ImagePickerMode.SINGLE)
                        .cachePath(FCCache.getInstance().getUserHeadCachePath())
                        .needCrop(true)
                        .showCamera(true)
                        .build();
                ImagePicker.getInstance().pickWithOptions(this, options, new ImagePicker.OnSelectedListener()
                {
                    @Override
                    public void onSelected(List<ImageBean> list)
                    {
                        if (list != null && list.size() > 0)
                            mPresenter.updateUserLocalHead(mUserBean.getPhone(), list.get(0));
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBusHelper.getInstance().unregist(this);
    }

    @Override
    public void updateLocalHeadFail()
    {
        showShortToast(R.string.error_unknow);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userProfileUpdated(ProfileUpdateEventBean eventBean)
    {
        String phone = eventBean.getPhone();
        if (StringUtil.isNotEmpty(phone) && mUserBean != null
                && StringUtil.isEquals(mUserBean.getPhone(), phone))
        {
            mUserBean = eventBean.getUserBean();
            setUserProfile();
        }
    }
}
