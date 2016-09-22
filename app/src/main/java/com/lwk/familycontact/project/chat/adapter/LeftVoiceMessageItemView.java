package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO
 * 2016/9/22
 */
public class LeftVoiceMessageItemView extends VoiceMessageBaseItemView
{
    public LeftVoiceMessageItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public int getItemViewLayoutId()
    {
        return R.layout.layout_chat_left_voice_listitem;
    }

    @Override
    public boolean isForViewType(EMMessage item, int position)
    {
        if (item.direct() == EMMessage.Direct.RECEIVE && item.getType() == EMMessage.Type.VOICE)
            return true;
        else
            return false;
    }

    @Override
    public void setMessage(RcvHolder holder, EMMessage emMessage, int position)
    {

    }
}
