package com.lwk.familycontact.project.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.lib.base.log.KLog;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.main.view.MainActivity;
import com.lwk.familycontact.project.login.view.LoginActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 欢迎界面
 */
@RuntimePermissions
public class SplashActivity extends FCBaseActivity
{
    private static final int ANIM_DURATION = 2500;
    private AlphaAnimation mAnimation;
    private View mLlContent;
    private TextView mTvAuthorDesc;
    private long mLoginStartTime, mLoginEndTime, mLoginTime;

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_splash;
    }

    @Override
    protected void initUI()
    {
        mLlContent = findView(R.id.ll_splash_content);
        mTvAuthorDesc = findView(R.id.tv_splash_author_desc);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mAnimation = new AlphaAnimation(0f, 1f);
        mAnimation.setDuration(ANIM_DURATION);
        mAnimation.setFillAfter(true);
        mLlContent.startAnimation(mAnimation);
        mTvAuthorDesc.startAnimation(mAnimation);

        mLoginStartTime = System.currentTimeMillis();
        //自动登录
        KLog.d("SplashActivit--->是否能自动登录:" + HxSdkHelper.getInstance().canAutoLogin());
        if (HxSdkHelper.getInstance().canAutoLogin())
        {
            HxSdkHelper.getInstance().loadHxLocalData();
            mLoginEndTime = System.currentTimeMillis();
            mLoginTime = mLoginEndTime - mLoginStartTime;
            if (mLoginTime > ANIM_DURATION)
            {
                SplashActivityPermissionsDispatcher.skipToActivityWithCheck(SplashActivity.this, MainActivity.class);
            } else
            {
                mMainHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        SplashActivityPermissionsDispatcher.skipToActivityWithCheck(SplashActivity.this, MainActivity.class);
                    }
                }, ANIM_DURATION - mLoginTime);
            }
        }
        //手动登录
        else
        {
            mLoginEndTime = System.currentTimeMillis();
            mLoginTime = mLoginEndTime - mLoginStartTime;
            mMainHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    SplashActivityPermissionsDispatcher.skipToActivityWithCheck(SplashActivity.this, LoginActivity.class);
                }
            }, ANIM_DURATION - mLoginTime);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void skipToActivity(Class clazz)
    {
        startActivity(new Intent(SplashActivity.this, clazz));
        finish();
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showRationaleForSdcard(final PermissionRequest request)
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_sdcard_message)
                .setPositiveButton(R.string.dialog_permission_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                }).create().show();
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void onSdcardPermissionDenied()
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.dialog_permission_sdcard_denied)
                .create();
        dialog.show();
        mMainHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                dialog.dismiss();
                finish();
            }
        }, 1500);
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void onSdcardPermissionNerverAsk()
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_sdcard_nerver_ask_message)
                .setNegativeButton(R.string.dialog_permission_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        finish();
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
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onBackPressed()
    {
        //不准后退
    }

    @Override
    protected void onClick(int id, View v)
    {

    }
}
