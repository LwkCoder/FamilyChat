package com.lwk.familycontact.project.call.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.lwk.familycontact.R;
import com.lwk.familycontact.im.helper.HxCallHelper;
import com.lwk.familycontact.project.call.presenter.HxVideoCallPrenter;
import com.superrtc.sdk.VideoView;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 实时视频通话界面
 */
@RuntimePermissions
public class HxVideoCallActivity extends HxBaseCallActivity implements HxVideoCallView
{
    private HxVideoCallPrenter mPresenter;
    private View mViewHeader;
    private EMOppositeSurfaceView mSfvOpposite;
    private EMLocalSurfaceView mSfvLocal;

    /**
     * 跳转到该界面的公共方法
     *
     * @param context      启动context
     * @param phone        手机号
     * @param isComingCall 是否为接收方
     */
    public static void start(Context context, String phone, boolean isComingCall)
    {
        Intent intent = new Intent(context, HxVideoCallActivity.class);
        intent.putExtra(INTENT_KEY_PHONE, phone);
        intent.putExtra(INTENT_KEY_IS_COMING_CALL, isComingCall);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected int setContentViewId()
    {
        mPresenter = new HxVideoCallPrenter(this, mMainHandler);
        return R.layout.activity_hx_video_call;
    }

    @Override
    protected void initUI()
    {
        mViewHeader = findView(R.id.rl_videocall_header);
        super.initUI();
        mSfvOpposite = findView(R.id.sfv_videocall_opposite);
        mSfvOpposite.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

        mSfvLocal = findView(R.id.sfv_videocall_local);
        mSfvLocal.setZOrderMediaOverlay(true);
        mSfvLocal.setZOrderOnTop(true);
        mSfvLocal.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

        mPresenter.setSurfaceView(mSfvLocal, mSfvOpposite);
    }

    @Override
    public void setOpUserData()
    {
        mPresenter.setOpData(mOpPhone);
    }

    @Override
    public void doOutgoingCall()
    {
        //检查权限再打电话
        HxVideoCallActivityPermissionsDispatcher.startVideoCallWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA})
    public void startVideoCall()
    {
        mPresenter.startVideoCall(mOpPhone);
    }

    @Override
    public void doAfterAccepted()
    {
        //隐藏Header
        if (mViewHeader != null)
            mViewHeader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void doAnswercall()
    {
        HxVideoCallActivityPermissionsDispatcher.answerCallWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA})
    public void answerCall()
    {
        mPresenter.answerCall();
    }

    @Override
    public void doRejectCall()
    {
        mPresenter.rejectCall();
    }

    @Override
    public void doEndCall()
    {
        mPresenter.endCall();
    }

    @OnShowRationale({Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA})
    public void showRationaleForVideoCall(final PermissionRequest request)
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_video_call_message)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                })
                .create().show();
    }

    @OnPermissionDenied({Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA})
    public void onVideoCallPermissionDenied()
    {
        showLongToast(R.string.warning_permission_video_call_denied);
        if (mIsComingCall)
            mPresenter.rejectCall();
        else
            mPresenter.endCall();
        finish();
    }

    @OnNeverAskAgain({Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA})
    public void onNeverAskVideoCall()
    {
        new AlertDialog.Builder(this).setCancelable(false)
                .setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_video_call_nerver_ask_message)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        if (mIsComingCall)
                            mPresenter.rejectCall();
                        else
                            mPresenter.endCall();
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
                        if (mIsComingCall)
                            mPresenter.rejectCall();
                        else
                            mPresenter.endCall();
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HxVideoCallActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onDestroy()
    {
        mPresenter.stopVideoRecord();
        if (mSfvLocal != null)
        {
            mSfvLocal.getRenderer().dispose();
            mSfvLocal = null;
        }
        if (mSfvOpposite != null)
        {
            mSfvOpposite.getRenderer().dispose();
            mSfvOpposite = null;
        }
        HxCallHelper.getInstance().removeCallStateChangeListener(mStateChangeListener);
        super.onDestroy();
    }
}
