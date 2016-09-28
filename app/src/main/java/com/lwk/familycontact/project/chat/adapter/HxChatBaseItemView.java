package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.chat.EMMessage;
import com.lib.base.toast.ToastUtils;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lib.rcvadapter.view.RcvBaseItemView;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO 消息ItemView基类
 * 2016/9/21
 */
public abstract class HxChatBaseItemView extends RcvBaseItemView<EMMessage>
{
    protected Context mContext;
    protected HxChatPresenter mPresenter;
    protected UserBean mUserBean;

    public HxChatBaseItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        this.mContext = mContext;
        this.mPresenter = mPresenter;
        this.mUserBean = mUserBean;
    }

    @Override
    public void setData(RcvHolder holder, final EMMessage emMessage, final int position)
    {
        setUserData(holder, emMessage, position);
        setMessageStatus(holder, emMessage, position);
        setMessageData(holder, emMessage, position);
    }

    //设置用户资料
    protected void setUserData(RcvHolder holder, EMMessage emMessage, final int position)
    {
        if (emMessage.direct() == EMMessage.Direct.RECEIVE)
        {
            //设置头像
            if (mUserBean != null && StringUtil.isNotEmpty(mUserBean.getLocalHead()))
                CommonUtils.getInstance().getImageDisplayer()
                        .display(mContext, (ImageView) holder.findView(R.id.img_chat_listitem_head),
                                mUserBean.getLocalHead(), 150, 150);
            else
                holder.setImgResource(R.id.img_chat_listitem_head, R.drawable.default_avatar);
            //设置名字
            //单聊不显示名字
            if (emMessage.getChatType() == EMMessage.ChatType.Chat)
            {
                holder.setVisibility(R.id.tv_chat_listitem_name, View.GONE);
            } else
            {
                holder.setVisibility(R.id.tv_chat_listitem_name, View.VISIBLE);
                if (mUserBean != null)
                    holder.setTvText(R.id.tv_chat_listitem_name, mUserBean.getDisplayName());
                else
                    holder.setTvText(R.id.tv_chat_listitem_name, emMessage.getUserName());
            }
        } else
        {
            //本方头像为默认头像
            holder.setImgResource(R.id.img_chat_listitem_head, R.drawable.default_avatar);
            //不显示名字
            holder.setVisibility(R.id.tv_chat_listitem_name, View.GONE);
        }

        holder.setClickListener(R.id.img_chat_listitem_head, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ToastUtils.showShortMsg(mContext, "pos=" + position);
            }
        });
    }

    //发送方消息设置状态
    protected void setMessageStatus(RcvHolder holder, final EMMessage emMessage, final int position)
    {
        if (emMessage.direct() == EMMessage.Direct.SEND)
        {
            //设置消息状态
            EMMessage.Status status = emMessage.status();
            if (status == EMMessage.Status.CREATE || status == EMMessage.Status.INPROGRESS)
            {
                holder.setVisibility(R.id.pgb_chat_listitem_sending, View.VISIBLE);
                holder.setVisibility(R.id.img_chat_listitem_resend, View.GONE);
            } else if (status == EMMessage.Status.SUCCESS)
            {
                holder.setVisibility(R.id.pgb_chat_listitem_sending, View.GONE);
                holder.setVisibility(R.id.img_chat_listitem_resend, View.GONE);
            } else
            {
                holder.setVisibility(R.id.pgb_chat_listitem_sending, View.GONE);
                holder.setVisibility(R.id.img_chat_listitem_resend, View.VISIBLE);
                holder.setClickListener(R.id.img_chat_listitem_resend, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mPresenter.resendMessage(emMessage, position);
                    }
                });
            }
        }
    }

    public abstract void setMessageData(RcvHolder holder, EMMessage emMessage, int position);
}
