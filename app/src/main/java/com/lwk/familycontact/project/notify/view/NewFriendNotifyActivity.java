package com.lwk.familycontact.project.notify.view;

import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.notify.adapter.NewFriendNotifyAdapter;
import com.lwk.familycontact.project.notify.presenter.NewFriendPresenter;
import com.lwk.familycontact.storage.db.invite.InviteBean;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 新的好友通知详情界面
 */
public class NewFriendNotifyActivity extends FCBaseActivity implements NewFriendImpl
{
    private NewFriendPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private NewFriendNotifyAdapter mAdapter;
    private ProgressDialog mDialog;

    @Override
    protected int setContentViewId()
    {
        mPresenter = new NewFriendPresenter(this, mMainHandler);
        EventBusHelper.getInstance().regist(this);
        return R.layout.activity_new_friend_notify;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_new_friend_notify);
        actionBar.setTitleText(R.string.tv_new_friend_notify_title);
        actionBar.setLeftLayoutAsBack(this);

        mRecyclerView = findView(R.id.rcv_new_friend_notify);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new NewFriendNotifyAdapter(this, null, mPresenter);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.refreshAllNotify();
    }

    @Override
    protected void onClick(int id, View v)
    {

    }

    @Override
    public void onRefreshAllNotifyEmpty()
    {

    }

    @Override
    public void onRefreshAllNotifySuccess(List<InviteBean> list)
    {
        mAdapter.refreshDatas(list);
    }

    @Override
    public void showHandlingDialog()
    {

    }

    @Override
    public void closeHandingDialog()
    {

    }

    @Override
    public void showHandlingError(int status, int errResId)
    {
        showShortToast(errResId);
    }

    @Override
    public void onNotifyStatusChanged()
    {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyEventReceived(ComNotifyEventBean eventBean)
    {
        switch (eventBean.getFlag())
        {
            case ComNotifyConfig.REFRESH_USER_INVITE:
                mPresenter.refreshAllNotify();
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        EventBusHelper.getInstance().unregist(this);
        super.onDestroy();
    }
}
