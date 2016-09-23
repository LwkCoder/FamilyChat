package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO 短视频消息ItemView基类
 * 2016/9/23
 */
public abstract class VideoMessageBaseItemView extends HxChatBaseItemView
{
    public VideoMessageBaseItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public void setMessageData(RcvHolder holder, EMMessage emMessage, int position)
    {

        setMessage(holder, emMessage, position);
    }

    public abstract void setMessage(RcvHolder holder, EMMessage emMessage, int position);
}
