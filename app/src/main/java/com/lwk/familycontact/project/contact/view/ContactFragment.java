package com.lwk.familycontact.project.contact.view;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lib.ptrview.CommonPtrLayout;
import com.lib.quicksidebar.QuickSideBarTipsView;
import com.lib.quicksidebar.QuickSideBarView;
import com.lib.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.contact.presenter.ContactPresenter;

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
        mPtrLayout = findView(R.id.ptr_layout_contact);
        mSidebar = findView(R.id.siderbar_contact);
        mSidebarTipView = findView(R.id.siderbar_tips_view_contact);
        mSidebar.setOnQuickSideBarTouchListener(this);

        mPtrLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh()
    {
        mMainHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mPtrLayout.notifyRefreshSuccess();
            }
        }, 1000);
    }

    @Override
    public void onLetterChanged(String letter, int position, float y)
    {
        mSidebarTipView.setText(letter, position, y);
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
}
