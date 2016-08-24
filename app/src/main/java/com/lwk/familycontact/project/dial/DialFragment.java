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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lib.base.app.BaseFragment;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.RcvMutilAdapter;
import com.lib.rcvadapter.decoration.RcvLinearDecoration;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.contact.view.AddContactActivity;
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
public class DialFragment extends BaseFragment implements DialImpl
        , DialPadView.onCallListener
        , DialPadView.onTextChangedListener
        , RcvMutilAdapter.onItemClickListener<UserBean>
{
    private DialPresenter mPresenter;
    private DialPadView mDialPadView;
    private AnimationController mAnimController;
    private ImageButton mBtnShowKeyboard;
    private RecyclerView mRecyclerView;
    private DialSearchAdapter mAdapter;
    private View mFootView;
    private TextView mTvAddContact;
    private boolean mIsKeyboardShow;
    private boolean mHasAddFootView;

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
        addClick(mBtnShowKeyboard);
        mRecyclerView = findView(R.id.rcv_dial_search_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RcvLinearDecoration(getActivity(), RcvLinearDecoration.VERTICAL_LIST));
        mAdapter = new DialSearchAdapter(getActivity(), null);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnTouchListener(mRcvTouchListener);
        mDialPadView.setOnCallListener(this);
        mDialPadView.setOnTextChangedListener(this);

        //创建HeadView
        mFootView = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_dial_search_add_contact
                        , (ViewGroup) getActivity().findViewById(android.R.id.content)
                        , false);
        mFootView.setOnClickListener(mAddContactListener);
        mTvAddContact = (TextView) mFootView.findViewById(R.id.tv_dial_search_add_contact);
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
            case R.id.btn_dial_search_keyboard:
                showKeyBoard();
                break;
        }
    }

    private View.OnTouchListener mRcvTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            int action = event.getAction();
            int startY = 0, moveY = 0, chaY = 0;
            switch (action)
            {
                case MotionEvent.ACTION_DOWN:
                    startY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveY = (int) event.getY();
                    chaY = moveY - startY;
                    if (chaY > 50)
                        hideKeyBoard();
                    break;
            }
            return false;
        }
    };

    //显示键盘
    private void showKeyBoard()
    {
        if (!mIsKeyboardShow)
        {
            mAnimController.scaleOut(mBtnShowKeyboard, 150, 0);
            mAnimController.slideTranslateInFromVBottom(mDialPadView, 250, 75);
            mIsKeyboardShow = true;
        }
    }

    //隐藏键盘
    private void hideKeyBoard()
    {
        if (mIsKeyboardShow)
        {
            mAnimController.slideTranslateOutToBottom(mDialPadView, 250, 0);
            mAnimController.scaleIn(mBtnShowKeyboard, 150, 150);
            mIsKeyboardShow = false;
        }
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
    public synchronized void showAddContact(String phone)
    {
        if (!mHasAddFootView)
        {
            mAdapter.addFootView(mFootView);
            mHasAddFootView = true;
        }
        mTvAddContact.setText(phone);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public synchronized void closeAddContact()
    {
        if (mHasAddFootView)
        {
            mAdapter.clearFootViews();
            mHasAddFootView = false;
        }
    }

    private View.OnClickListener mAddContactListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            addPhoneToContact();
        }
    };

    @Override
    public void onSearchResultEmpty(String phone)
    {
        mAdapter.refreshDatas(null);
    }

    @Override
    public void onSearchResultSuccess(List<UserBean> resultList)
    {
        mAdapter.refreshDatas(resultList);
    }

    @Override
    public void onItemClick(View view, RcvHolder holder, UserBean userBean, int position)
    {
        String phone = userBean.getPhone();
        if (StringUtil.isNotEmpty(phone))
            DialFragmentPermissionsDispatcher.startSystemCallWithCheck(this, phone);
    }

    //拨号
    @NeedsPermission(Manifest.permission.CALL_PHONE)
    public void startSystemCall(String phone)
    {
        PhoneUtils.callPhone(getActivity(), phone);
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

    public void addPhoneToContact()
    {
        AddContactActivity.skip(getActivity(), mDialPadView.getInput(), false);
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
        {
            mDialPadView.clearInput();
            showKeyBoard();
        }
    }
}
