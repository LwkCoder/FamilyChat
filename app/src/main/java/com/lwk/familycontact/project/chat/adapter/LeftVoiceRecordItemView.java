package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.chat.utils.HxMsgAttrConstant;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO 左边语音通话记录ItemView
 * 2016/9/21
 */
public class LeftVoiceRecordItemView extends TextMessageBaseItemView
{
    public LeftVoiceRecordItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public int getItemViewLayoutId()
    {
        return R.layout.layout_chat_left_voice_record_listitem;
    }

    @Override
    public boolean isForViewType(EMMessage item, int position)
    {
        int attr = item.getIntAttribute(HxMsgAttrConstant.TXT_ATTR_KEY, HxMsgAttrConstant.NORMAL_TEXT_MSG);

        if (item.direct() == EMMessage.Direct.RECEIVE && item.getType() == EMMessage.Type.TXT
                && attr == HxMsgAttrConstant.VOICE_CALL_RECORD)
            return true;
        else
            return false;
    }

    @Override
    public void setMessage(RcvHolder holder, EMMessage emMessage, int position)
    {

    }

}
