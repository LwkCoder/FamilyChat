package com.lwk.familycontact.project.splash;

import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.lib.base.log.KLog;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.MainActivity;
import com.lwk.familycontact.project.login.view.LoginActivity;

/**
 * 欢迎界面
 */
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
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else
            {
                mMainHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
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
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, ANIM_DURATION - mLoginTime);
        }
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
