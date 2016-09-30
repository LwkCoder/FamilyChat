package com.lwk.familycontact.project.contact.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.base.utils.ResUtils;
import com.lib.base.widget.CommonActionBar;
import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.ImagePickerOptions;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imagepicker.model.ImagePickerMode;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.project.common.FCCache;
import com.lwk.familycontact.project.contact.presenter.AddContactPresenter;
import com.lwk.familycontact.utils.other.TextLightUtils;

import java.util.List;

/**
 * 添加通讯录界面
 */
public class AddContactActivity extends FCBaseActivity implements AddContactView
{
    private static final String INTENT_KEY_PHONE = "phone";
    private static final String INTENT_KEY_REGIST = "isRegist";
    private AddContactPresenter mPresenter;
    private String mPhone;
    private boolean mIsRegist;
    private View mLlContent;
    private TextView mTvDesc;
    private ImageView mImgHead;
    private EditText mEdName;
    private String mHead;

    /**
     * 跳转到添加好友界面的公共方法
     *
     * @param activity 跳转前Activity
     * @param phone    传入的手机号
     */
    public static void skip(Activity activity, String phone, boolean isRegist)
    {
        Intent intent = new Intent(activity, AddContactActivity.class);
        intent.putExtra(INTENT_KEY_PHONE, phone);
        intent.putExtra(INTENT_KEY_REGIST, isRegist);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null)
        {
            mPhone = intent.getStringExtra(INTENT_KEY_PHONE);
            mIsRegist = intent.getBooleanExtra(INTENT_KEY_REGIST, false);
        }
    }

    @Override
    protected int setContentViewId()
    {
        mPresenter = new AddContactPresenter(this);
        return R.layout.activity_add_contact;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_add_contact);
        actionBar.setTitleText(R.string.tv_add_contact_title);
        actionBar.setLeftLayoutAsBack(this);

        mTvDesc = findView(R.id.tv_add_contact_desc);
        mLlContent = findView(R.id.ll_add_contact_content);
        mImgHead = findView(R.id.img_add_contact_head);
        mEdName = findView(R.id.ed_add_contact_name);
        addClick(mImgHead);
        addClick(R.id.btn_add_contact_save);
    }

    @Override
    protected void initData()
    {
        mPresenter.judgeData(mPhone);
    }

    @Override
    public void onUserExist()
    {
        String exDesc = ResUtils.getString(this, R.string.tv_add_contact_warning).replaceFirst("%%1", mPhone);
        SpannableString desc = TextLightUtils.matcherSearchTitle(Color.BLUE, exDesc, mPhone);
        mTvDesc.setText(desc);
        mLlContent.setVisibility(View.GONE);
    }

    @Override
    public void onUserNotExist()
    {
        String exDesc = ResUtils.getString(this, R.string.tv_add_contact_desc).replaceFirst("%%1", mPhone);
        SpannableString desc = TextLightUtils.matcherSearchTitle(Color.BLUE, exDesc, mPhone);
        mTvDesc.setText(desc);
        mLlContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserSaved()
    {
        showShortToast(R.string.hint_save_success);
        finish();
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.img_add_contact_head:
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
                        {
                            mHead = list.get(0).getImagePath();
                            CommonUtils.getInstance().getImageDisplayer()
                                    .display(AddContactActivity.this, mImgHead, mHead, 300, 300);
                        }
                    }
                });
                break;
            case R.id.btn_add_contact_save:
                mPresenter.saveNewData(mPhone, mEdName.getText().toString().trim(), mHead, mIsRegist);
                break;
        }
    }
}
