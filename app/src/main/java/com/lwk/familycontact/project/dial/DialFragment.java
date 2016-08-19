package com.lwk.familycontact.project.dial;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.lib.base.app.BaseFragment;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.decoration.RcvLinearDecoration;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.dial.adapter.DialSearchAdapter;
import com.lwk.familycontact.project.dial.presenter.DialPresenter;
import com.lwk.familycontact.project.dial.view.DialImpl;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.sp.SpSetting;
import com.lwk.familycontact.utils.other.AnimationController;
import com.lwk.familycontact.widget.dial.DialPadView;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by LWK
 * TODO 拨号器片段
 * 2016/8/2
 */
@RuntimePermissions
public class DialFragment extends BaseFragment implements DialImpl, DialPadView.onCallListener, DialPadView.onTextChangedListener
{
    private DialPresenter mPresenter;
    private DialPadView mDialPadView;
    private AnimationController mAnimController;
    private ImageButton mBtnShowKeyboard;
    private RecyclerView mRecyclerView;
    private DialSearchAdapter mAdapter;

    public static DialFragment newInstance()
    {
        DialFragment dialFragment = new DialFragment();
        Bundle bundle = new Bundle();
        dialFragment.setArguments(bundle);
        return dialFragment;
    }

    @Override
    protected int setRootLayoutId()
    {
        mPresenter = new DialPresenter(this);
        mAnimController = new AnimationController();
        return R.layout.fragment_dial;
    }

    @Override
    protected void initUI()
    {
        mDialPadView = findView(R.id.dialpad);
        mBtnShowKeyboard = findView(R.id.btn_dial_search_keyboard);
        mRecyclerView = findView(R.id.rcv_dial_search_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RcvLinearDecoration(getActivity(), RcvLinearDecoration.VERTICAL_LIST));
        mAdapter = new DialSearchAdapter(getActivity(), null);
        mRecyclerView.setAdapter(mAdapter);
        mDialPadView.setOnCallListener(this);
        mDialPadView.setOnTextChangedListener(this);
    }

    @Override
    protected void initData()
    {
        super.initData();
        showKeyBoard();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mDialPadView.onStart();
        mDialPadView.setFeedBackEnable(SpSetting.isDialFeedBackEnable(getActivity()));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mDialPadView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mDialPadView.onStop();
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
        }
    }

    //显示键盘
    private void showKeyBoard()
    {
        mAnimController.slideTranslateOutToBottom(mBtnShowKeyboard, 200, 0);
        mAnimController.slideTranslateInFromVBottom(mDialPadView, 350, 100);
    }

    //隐藏键盘
    private void hideKeyBoard()
    {
        mAnimController.slideTranslateOutToBottom(mDialPadView, 350, 0);
        mAnimController.slideTranslateInFromVBottom(mBtnShowKeyboard, 200, 150);
    }

    @Override
    public void onCall(String phone)
    {
        if (StringUtil.isEmpty(phone))
        {
            showShortToast(R.string.warning_dial_phone_empty);
            return;
        }

        DialFragmentPermissionsDispatcher.startSystemCallWithCheck(this, phone);
    }

    //拨号
    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void startSystemCall(String phone)
    {
        PhoneUtils.callPhone(getActivity(), phone);
    }

    @Override
    public void onTextChanged(String s)
    {
        mPresenter.searchUsers(s);
    }

    @Override
    public void resetSearchResult()
    {
        mAdapter.refreshDatas(null);
    }

    @Override
    public void onSearchResultEmpty(String phone)
    {
        mAdapter.refreshDatas(null);
        //Todo 添加到通讯录
    }

    @Override
    public void onSearchResultSuccess(List<UserBean> resultList)
    {
        mAdapter.refreshDatas(resultList);
    }

    @OnShowRationale(Manifest.permission.CALL_PHONE)
    public void showRationaleForCallPhone(final PermissionRequest request)
    {
        new AlertDialog.Builder(getActivity())
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
        new AlertDialog.Builder(getActivity()).setCancelable(false)
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
        DialFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        if (!hidden && mDialPadView != null)
            mDialPadView.clearInput();
    }
}
