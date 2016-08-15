package com.lwk.familycontact.project.contact.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lib.base.utils.StringUtil;
import com.lib.ptrview.CommonPtrLayout;
import com.lib.quicksidebar.QuickSideBarTipsView;
import com.lib.quicksidebar.QuickSideBarView;
import com.lib.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.lib.rcvadapter.RcvMutilAdapter;
import com.lib.rcvadapter.decoration.RcvLinearDecoration;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.contact.adapter.ContactAdapter;
import com.lwk.familycontact.project.contact.presenter.ContactPresenter;
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
 * Created by LWK
 * TODO 通讯录片段
 * 2016/8/2
 */

@RuntimePermissions
public class ContactFragment extends BaseFragment implements ContactImpl, CommonPtrLayout.OnRefreshListener, OnQuickSideBarTouchListener, RcvMutilAdapter.onItemClickListener<UserBean>
{
    private ContactPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private CommonPtrLayout mPtrLayout;
    private QuickSideBarTipsView mSidebarTipView;
    private QuickSideBarView mSidebar;
    private ContactAdapter mAdapter;


    public static ContactFragment newInstance()
    {
        ContactFragment contactFragment = new ContactFragment();
        Bundle bundle = new Bundle();
        contactFragment.setArguments(bundle);
        return contactFragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        EventBusHelper.getInstance().regist(this);
    }

    @Override
    protected int setRootLayoutId()
    {
        mPresenter = new ContactPresenter(this);
        return R.layout.fragment_contact;
    }

    @Override
    protected void initUI()
    {
        mRecyclerView = findView(R.id.common_ptrview_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new RcvLinearDecoration(getActivity(), RcvLinearDecoration.VERTICAL_LIST));
        mAdapter = new ContactAdapter(getActivity(), null);
        mAdapter.openItemShowingAnim();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mPtrLayout = findView(R.id.ptr_layout_contact);
        mSidebar = findView(R.id.siderbar_contact);
        mSidebarTipView = findView(R.id.siderbar_tips_view_contact);
        mSidebar.setOnQuickSideBarTouchListener(this);

        mPtrLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPtrLayout.autoRefresh();
    }

    @Override
    public void onRefresh()
    {
        ContactFragmentPermissionsDispatcher.refreshContactDataWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    public void refreshContactData()
    {
        mPresenter.refreshAllContactData(getActivity());
    }

    @OnShowRationale({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    public void showRationaleForContactPermission(final PermissionRequest request)
    {
        new AlertDialog.Builder(getActivity()).setCancelable(false)
                .setTitle(R.string.dialog_permission_contact_title)
                .setMessage(R.string.dialog_permission_contact_message)
                .setPositiveButton(R.string.dialog_permission_contact_confirm, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        request.proceed();
                    }
                })
                .create().show();
    }

    @OnPermissionDenied({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    public void onContactPermissionDenied()
    {
        showShortToast(R.string.warning_permission_contact_denied);
        //权限被拒绝后获取数据库里环信好友
        mPresenter.refreshContactDataInHx();
    }

    @OnNeverAskAgain({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    public void onNeverAskContactPermission()
    {
        //权限被拒绝后获取数据库里环信好友
        mPresenter.refreshContactDataInHx();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ContactFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onItemClick(View view, RcvHolder holder, UserBean userBean, int position)
    {
        UserDetailActivity.skip(getActivity(), userBean);
    }

    @Override
    public void onLetterChanged(String letter, int position, float y)
    {
        mSidebarTipView.setText(letter, position, y);
        if (StringUtil.isEquals(letter, mSidebar.getFirstLetters()))
        {
            mRecyclerView.scrollToPosition(0);
        } else if (StringUtil.isEquals(letter, mSidebar.getLastLetters()))
        {
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        } else
        {
            int p = mAdapter.getPositionBySection(letter);
            if (p != -1)
                mRecyclerView.scrollToPosition(p);
        }
    }

    @Override
    public void onLetterTouching(boolean touching)
    {
        //可以自己加入动画效果渐显渐隐
        mSidebarTipView.setVisibility(touching ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onClick(int id, View v)
    {

    }

    @Override
    public void refreshAllUsersSuccess(final List<UserBean> allUserList)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                mPtrLayout.notifyRefreshSuccess();
                mAdapter.refreshDataInSection(allUserList);
            }
        });
    }

    @Override
    public void refreshAllUsersFail(int errorMsgId)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                mPtrLayout.notifyRefreshFail();
            }
        });
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        EventBusHelper.getInstance().unregist(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userProfileUpdated(ProfileUpdateEventBean eventBean)
    {
        UserBean userBean = eventBean.getUserBean();
        if (userBean != null)
            mAdapter.updateUserProfile(userBean);
    }
}
