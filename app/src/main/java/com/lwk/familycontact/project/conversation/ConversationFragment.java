package com.lwk.familycontact.project.conversation;

import android.os.Bundle;
import android.view.View;

import com.lib.base.app.BaseFragment;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 会话片段
 * 2016/8/2
 */
public class ConversationFragment extends BaseFragment
{
    public static ConversationFragment newInstance()
    {
        ConversationFragment conversationFragment = new ConversationFragment();
        Bundle bundle = new Bundle();
        conversationFragment.setArguments(bundle);
        return conversationFragment;
    }

    @Override
    protected int setRootLayoutId()
    {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initUI()
    {
    }

    @Override
    protected void initData()
    {
        super.initData();
    }

    @Override
    protected void onClick(int id, View v)
    {
    }
}
