package com.lwk.familycontact.project.conversation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.app.BaseFragment;
import com.lwk.familycontact.R;
import com.lwk.familycontact.im.HxConnectListener;
import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.conversation.adapter.ConversationAdapter;
import com.lwk.familycontact.utils.event.ConnectEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by LWK
 * TODO 会话片段
 * 2016/8/2
 */
public class ConversationFragment extends BaseFragment
{
    private HxConnectListener hxConnectListener;
    private boolean mIsConnected = true;
    private RecyclerView mRecyclerView;
    private ConversationAdapter mAdapter;
    //掉线时提示栏
    private View mLayoutConnectStatus;
    private FlatTextView mTvConnectStatus;

    public static ConversationFragment newInstance()
    {
        ConversationFragment conversationFragment = new ConversationFragment();
        Bundle bundle = new Bundle();
        conversationFragment.setArguments(bundle);
        return conversationFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBusHelper.getInstance().regist(this);
    }

    @Override
    protected int setRootLayoutId()
    {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initUI()
    {
        mRecyclerView = findView(R.id.rcv_conversation);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ConversationAdapter(getActivity(), R.layout.layout_conversation_listitem, null);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData()
    {
        super.initData();
        hxConnectListener = new HxConnectListener();
        HxSdkHelper.getInstance().addConnectListener(hxConnectListener);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBusHelper.getInstance().unregist(this);
        HxSdkHelper.getInstance().removeConnectListener(hxConnectListener);
    }

    @Override
    protected void onClick(int id, View v)
    {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusChanged(ConnectEventBean eventBean)
    {
        boolean isConnect = eventBean.isConnect();
        //从掉线到上线
        if (isConnect && !mIsConnected && mAdapter != null)
        {
            if (mLayoutConnectStatus != null)
            {
                mAdapter.removeHeadViewAt(0);
                mLayoutConnectStatus = null;
                mTvConnectStatus = null;
            }
        }
        //从上线到掉线
        else if (!isConnect)
        {
            //原来是上线的话就添加headview
            if (mIsConnected)
            {
                mLayoutConnectStatus = getActivity().getLayoutInflater().inflate(R.layout.layout_disconnect_headview,
                        (ViewGroup) getActivity().findViewById(android.R.id.content), false);
                mTvConnectStatus = (FlatTextView) mLayoutConnectStatus.findViewById(R.id.tv_connect_status_headview);
                mTvConnectStatus.setText(eventBean.getErrorMsgId());
                mAdapter.addHeadView(mLayoutConnectStatus);
            }
            //原来是下线的话就更新提示语
            //之所以要这么做的原因是环信连接监听在断开连接的时候会回调两次disConnected接口
            else if (mLayoutConnectStatus != null && mTvConnectStatus != null)
            {
                mTvConnectStatus.setText(eventBean.getErrorMsgId());
            }
        }

        mIsConnected = isConnect;
    }
}
