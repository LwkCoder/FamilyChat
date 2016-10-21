package com.lwk.familycontact.project.contact.view;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lib.base.widget.CommonActionBar;
import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.ImagePickerOptions;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imagepicker.model.ImagePickerMode;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.call.view.HxVoiceCallActivity;
import com.lwk.familycontact.project.chat.view.HxChatActivity;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.project.common.FCCache;
import com.lwk.familycontact.project.contact.presenter.UserDetailPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.ProfileUpdateEventBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 联系人资料详情界面
 */
@RuntimePermissions
public class ContactDetailActivity extends FCBaseActivity implements UserDetailView
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
        Intent intent = new Intent(activity, ContactDetailActivity.class);
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
        addClick(R.id.btn_user_detail_system_call);
        addClick(R.id.btn_user_detail_voice_call);
        addClick(R.id.btn_user_detail_send_msg);
        addClick(R.id.btn_user_detail_video_call);
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
            mTvPhone.setText(PhoneUtils.formatPhoneNumAsRegular(mUserBean.getPhone(), " - "));
            String localHead = mUserBean.getLocalHead();
            if (StringUtil.isNotEmpty(localHead))
                CommonUtils.getInstance().getImageDisplayer()
                        .display(this, mImgHead, localHead, 300, 300);
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
            case R.id.btn_user_detail_system_call:
                ContactDetailActivityPermissionsDispatcher.callSystemPhoneWithCheck(this);
                break;
            case R.id.btn_user_detail_voice_call:
                HxVoiceCallActivity.start(this, mUserBean.getPhone(), false);
                break;
            case R.id.btn_user_detail_send_msg:
                HxChatActivity.start(this, mUserBean.getPhone(), mUserBean);
                break;
            case R.id.btn_user_detail_video_call:
                break;
        }
    }

    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void callSystemPhone()
    {
        if (mUserBean != null && StringUtil.isNotEmpty(mUserBean.getPhone()))
        {
            PhoneUtils.callPhone(this, mUserBean.getPhone());
        }
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    public void showRationaleForCallPhone(final PermissionRequest request)
    {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_call_phone_message)
                .setPositiveButton(R.string.dialog_permission_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                }).create().show();
    }

    @OnPermissionDenied(Manifest.permission.CALL_PHONE)
    public void onCallPhonePermissionDenied()
    {
        showLongToast(R.string.warning_permission_callphone_denied);
    }

    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    public void onCallPhonePermissionNeverAsk()
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.warning_permission_callphone_denied)
                .setMessage(R.string.dialog_permission_call_phone_nerver_ask_message)
                .setNegativeButton(R.string.dialog_imagepicker_permission_nerver_ask_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        showLongToast(R.string.warning_permission_callphone_denied);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_permission_nerver_ask_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                        startActivity(intent);
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ContactDetailActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
