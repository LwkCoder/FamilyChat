package com.lwk.familycontact.project.chat.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.lib.base.toast.ToastUtils;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO 文本消息ItemView基类
 * 2016/9/21
 */
public abstract class TextMessageBaseItemView extends HxChatBaseItemView
{

    public TextMessageBaseItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public void setMessageData(RcvHolder holder, EMMessage emMessage, int position)
    {
        final EMTextMessageBody textMessageBody = (EMTextMessageBody) emMessage.getBody();
        TextView tvMessage = holder.findView(R.id.tv_chat_listitem_text_content);
        tvMessage.setText(textMessageBody.getMessage());
        tvMessage.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                ClipboardManager manager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setText(textMessageBody.getMessage());
                ToastUtils.showShortMsg(mContext, R.string.toast_text_be_copyed);
                return true;
            }
        });

        setMessage(holder, emMessage, position);
    }

    public abstract void setMessage(RcvHolder holder, EMMessage emMessage, int position);
}
