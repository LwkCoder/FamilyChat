package com.lwk.familycontact.im;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.lib.base.app.ActivityManager;
import com.lib.base.log.KLog;
import com.lib.base.toast.ToastUtils;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.common.FCError;
import com.lwk.familycontact.project.login.view.LoginActivity;
import com.lwk.familycontact.utils.event.ConnectEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

/**
 * Created by LWK
 * TODO 环信连接监听
 * 2016/8/8
 */
public class HxConnectListener implements EMConnectionListener
{
    private Handler mHandler;

    public HxConnectListener()
    {
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onConnected()
    {
        KLog.d("HxConnectListener onConnected");
        //发送Event
        ConnectEventBean eventBean = new ConnectEventBean(true, -1);
        EventBusHelper.getInstance().post(eventBean);
    }

    @Override
    public void onDisconnected(int i)
    {
        KLog.e("HxConnectListener--->onDisconnected：code=" + i);
        switch (i)
        {
            //账号被移除
            case EMError.USER_REMOVED:
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        userBeRemoved();
                    }
                });
                break;
            case EMError.USER_NOT_FOUND:
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        userBeRemoved();
                    }
                });
                break;
            //账号在其他地方登录
            case EMError.USER_LOGIN_ANOTHER_DEVICE:
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        userReLogined();
                    }
                });
                break;
            default:
                ConnectEventBean eventBean = new ConnectEventBean(false);
                eventBean.setErrorMsgId(FCError.getErrorMsgIdFromCode(i));
                EventBusHelper.getInstance().post(eventBean);
                break;
        }
    }

    //执行用户账号被移除后的操作
    private void userBeRemoved()
    {
        HxSdkHelper.getInstance().logout(null);
        Activity activity = ActivityManager.getInstance().getPopActivity();
        if (activity instanceof LoginActivity)
            return;
        ToastUtils.showLongMsg(FCApplication.getIntance(), R.string.warning_user_be_removed);
        activity.startActivity(new Intent(activity, LoginActivity.class));
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ActivityManager.getInstance().finishAllActivityExceptOne(LoginActivity.class);
            }
        }, 500);
    }

    //执行用户在其他手机上登录后的操作
    public void userReLogined()
    {
        HxSdkHelper.getInstance().logout(null);
        final Activity activity = ActivityManager.getInstance().getPopActivity();
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(R.string.dialog_user_relogin_title)
                .setMessage(R.string.dialog_user_relogin_content)
                .setPositiveButton(R.string.confrim, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (activity instanceof LoginActivity)
                        {
                            dialog.dismiss();
                            return;
                        }
                        activity.startActivity(new Intent(activity, LoginActivity.class));

                        mHandler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ActivityManager.getInstance().finishAllActivityExceptOne(LoginActivity.class);
                            }
                        }, 500);
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
