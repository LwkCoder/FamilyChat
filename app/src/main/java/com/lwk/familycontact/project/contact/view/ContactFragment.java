package com.lwk.familycontact.project.contact.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lib.base.utils.StringUtil;
import com.lib.ptrview.CommonPtrLayout;
import com.lib.quicksidebar.QuickSideBarTipsView;
import com.lib.quicksidebar.QuickSideBarView;
import com.lib.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.lib.rcvadapter.decoration.RcvLinearDecoration;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.contact.adapter.ContactAdapter;
import com.lwk.familycontact.project.contact.presenter.ContactPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.List;

/**
 * Created by LWK
 * TODO 通讯录片段
 * 2016/8/2
 */
public class ContactFragment extends BaseFragment implements ContactImpl, CommonPtrLayout.OnRefreshListener, OnQuickSideBarTouchListener
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
        mPresenter.refreshContactData(getActivity());
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
    public void refreshAllUsersSuccess(List<UserBean> allUserList)
    {
        mPtrLayout.notifyRefreshSuccess();
        mAdapter.refreshDataInSection(allUserList);
    }

    @Override
    public void refreshAllUsersFail(int errorMsgId)
    {
        mPtrLayout.notifyRefreshFail();
    }
}
