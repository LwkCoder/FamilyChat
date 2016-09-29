package com.lwk.familycontact.project.conversation.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.app.BaseFragment;
import com.lib.rcvadapter.RcvMutilAdapter;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.im.bean.HxConversation;
import com.lwk.familycontact.project.chat.view.HxChatActivity;
import com.lwk.familycontact.project.conversation.adapter.ConversationAdapter;
import com.lwk.familycontact.project.conversation.presenter.ConverstionPresenter;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.ConnectEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.ProfileUpdateEventBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by LWK
 * TODO 会话片段
 * 2016/8/2
 */
public class ConversationFragment extends BaseFragment implements ConversationImpl
        , RcvMutilAdapter.onItemClickListener<HxConversation>
        , RcvMutilAdapter.onItemLongClickListener<HxConversation>
{
    private ConverstionPresenter mPresenter;
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
        mPresenter = new ConverstionPresenter(this, mMainHandler);
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initUI()
    {
        mRecyclerView = findView(R.id.rcv_conversation);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ConversationAdapter(getActivity(), null);
        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty_view
                , (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        TextView tvEmpty = (TextView) emptyView.findViewById(R.id.tv_empty_view);
        tvEmpty.setText(R.string.tv_conversation_empty);
        mAdapter.setEmptyView(emptyView);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mPresenter.loadAllConversations();
    }

    @Override
    public void onLoadAllConversationSuccess(List<HxConversation> list)
    {
        if (mAdapter != null)
            mAdapter.refreshDatas(list);
    }

    @Override
    public void onItemClick(View view, RcvHolder holder, HxConversation itemData, int position)
    {
        HxChatActivity.start(getActivity(), itemData.getEmConversation().conversationId(), itemData.getUserBean());
    }

    @Override
    public void onItemLongClick(View view, RcvHolder holder, final HxConversation itemData, int position)
    {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_del_conversation_title)
                .setMessage(R.string.dialog_del_conversation_message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.confrim, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        mPresenter.delConversation(itemData);
                    }
                }).create().show();
    }

    @Override
    public void onConversationBeDeleted(HxConversation conversation)
    {
        if (mAdapter != null)
            mAdapter.deleteData(conversation);
    }

    @Override
    protected void onClick(int id, View v)
    {
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBusHelper.getInstance().unregist(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyEventReceived(ComNotifyEventBean eventBean)
    {
        switch (eventBean.getFlag())
        {
            case ComNotifyConfig.REFRESH_UNREAD_MSG:
                mPresenter.loadAllConversations();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userProfileUpdated(ProfileUpdateEventBean eventBean)
    {
        //用户资料更新要重新刷新数据
        if (eventBean.getUserBean() != null)
            mPresenter.loadAllConversations();
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
