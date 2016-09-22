package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO 右边文本消息ItemView
 * 2016/9/22
 */
public class RightTextMessageItemView extends TextMessageBaseItemView
{
    public RightTextMessageItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public int getItemViewLayoutId()
    {
        return R.layout.layout_chat_right_text_listitem;
    }

    @Override
    public boolean isForViewType(EMMessage item, int position)
    {
        if (item.direct() == EMMessage.Direct.SEND && item.getType() == EMMessage.Type.TXT)
            return true;
        else
            return false;
    }

    @Override
    public void setMessage(RcvHolder holder, EMMessage emMessage, int position)
    {

    }
}
