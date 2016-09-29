package com.lwk.familycontact.project.setting.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;

import com.cengalabs.flatui.views.FlatToggleButton;
import com.lib.base.utils.ResUtils;
import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.login.view.LoginActivity;
import com.lwk.familycontact.project.setting.presenter.SettingPresenter;
import com.lwk.familycontact.utils.other.AnimationController;

/**
 * 设置界面
 */
public class SettingActivity extends FCBaseActivity implements CompoundButton.OnCheckedChangeListener
        , SettingImpl
{
    private SettingPresenter mPresenter;
    private FlatToggleButton mFtgDialFeedBack;
    private FlatToggleButton mFtgMsgNotice;
    private View mViewNoticeVoice;
    private FlatToggleButton mFtgMsgNoticeVoice;
    private View mViewNoticeVibrate;
    private FlatToggleButton mFtgMsgNoticeVibrate;
    private FlatToggleButton mFtgVoiceMsgHandFree;
    private FlatToggleButton mFtgTextInputFirst;
    private ProgressDialog mDialog;
    private AnimationController mAnimationController;
    private final int FADE_ANIM_DURATION = 175;

    @Override
    protected int setContentViewId()
    {
        mPresenter = new SettingPresenter(this);
        mAnimationController = new AnimationController();
        return R.layout.activity_setting;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_setting);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setTitleText(R.string.tv_setting_title);

        mFtgDialFeedBack = findView(R.id.ftg_setting_dial_feedback);
        mFtgMsgNotice = findView(R.id.ftg_setting_msg_notice);
        mViewNoticeVoice = findView(R.id.fl_setting_msg_notice_voice);
        mFtgMsgNoticeVoice = findView(R.id.ftg_setting_msg_notice_voice);
        mViewNoticeVibrate = findView(R.id.fl_setting_msg_notice_vibrate);
        mFtgMsgNoticeVibrate = findView(R.id.ftg_setting_msg_notice_vibrate);
        mFtgVoiceMsgHandFree = findView(R.id.ftg_setting_msg_voice_handfree);
        mFtgTextInputFirst = findView(R.id.ftg_setting_chat_text_input_first);

        addClick(R.id.btn_setting_logout);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mFtgDialFeedBack.setChecked(mPresenter.isDialFeedBackEnable(this));
        mFtgMsgNotice.setChecked(mPresenter.isMsgNoticeEnable(this));
        mFtgMsgNoticeVoice.setChecked(mPresenter.isMsgNoticeVoiceEnable(this));
        mFtgMsgNoticeVibrate.setChecked(mPresenter.isMsgNoticeVibrateEnable(this));
        mFtgVoiceMsgHandFree.setChecked(mPresenter.isVoiceMsgHandFreeEnable(this));
        mFtgTextInputFirst.setChecked(mPresenter.isChatTextInputModeFirst(this));

        mFtgDialFeedBack.setOnCheckedChangeListener(this);
        mFtgMsgNotice.setOnCheckedChangeListener(this);
        mFtgMsgNoticeVoice.setOnCheckedChangeListener(this);
        mFtgMsgNoticeVibrate.setOnCheckedChangeListener(this);
        mFtgVoiceMsgHandFree.setOnCheckedChangeListener(this);
        mFtgTextInputFirst.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.btn_setting_logout:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_setting_logout_confirm_title)
                        .setMessage(R.string.dialog_setting_logout_confirm_message)
                        .setPositiveButton(R.string.confrim, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                mPresenter.logout();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId())
        {
            case R.id.ftg_setting_dial_feedback:
                mPresenter.setDialFeendBackEnable(this, isChecked);
                break;
            case R.id.ftg_setting_msg_notice:
                mPresenter.setMsgNoticeEnable(this, isChecked);
                break;
            case R.id.ftg_setting_msg_notice_voice:
                mPresenter.setMsgNoticeVoiceEnable(this, isChecked);
                break;
            case R.id.ftg_setting_msg_notice_vibrate:
                mPresenter.setMsgNoticeVibrateEnable(this, isChecked);
                break;
            case R.id.ftg_setting_msg_voice_handfree:
                mPresenter.setVoiceMsgHandFreeEnable(this, isChecked);
                break;
            case R.id.ftg_setting_chat_text_input_first:
                mPresenter.setChatTextInputModeFirst(this, isChecked);
                break;
        }
    }

    @Override
    public void hideNoticeLayout()
    {
        mAnimationController.fadeOut(mViewNoticeVoice, FADE_ANIM_DURATION, 0);
        mAnimationController.fadeOut(mViewNoticeVibrate, FADE_ANIM_DURATION, 0);
    }

    @Override
    public void showNoticeLayout()
    {
        mFtgMsgNoticeVoice.setChecked(mPresenter.isMsgNoticeVoiceEnable(this));
        mFtgMsgNoticeVibrate.setChecked(mPresenter.isMsgNoticeVibrateEnable(this));
        mAnimationController.fadeIn(mViewNoticeVoice, FADE_ANIM_DURATION, 0);
        mAnimationController.fadeIn(mViewNoticeVibrate, FADE_ANIM_DURATION, 0);
    }

    @Override
    public void logoutSuccess()
    {
        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
    }

    @Override
    public void showLogoutDialog()
    {
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage(ResUtils.getString(this, R.string.dialog_setting_logout_message));
        mDialog.show();
    }

    @Override
    public void closeLogoutDialog()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
    }
}
