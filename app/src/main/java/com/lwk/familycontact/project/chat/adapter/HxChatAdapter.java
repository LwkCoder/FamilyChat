package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.lib.base.date.DateUtils;
import com.lib.rcvadapter.RcvMutilAdapter;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.Date;
import java.util.List;

/**
 * Created by LWK
 * TODO 聊天界面适配器
 * 2016/9/21
 */
public class HxChatAdapter extends RcvMutilAdapter<EMMessage>
{
    protected final int MIN_SHOW_TIME = 900000;//15分钟
    private HxChatPresenter mPresenter;
    private UserBean mUserBean;

    public HxChatAdapter(Context context, List<EMMessage> datas, HxChatPresenter presenter, UserBean userBean)
    {
        super(context, datas);
        this.mPresenter = presenter;
        this.mUserBean = userBean;
        addItemView(new LeftTextMessageItemView(context, presenter, userBean));
        addItemView(new RightTextMessageItemView(context, presenter, userBean));
        addItemView(new LeftImageMessageItemView(context, presenter, userBean));
        addItemView(new RightImageMessageItemView(context, presenter, userBean));
        addItemView(new LeftVoiceMessageItemView(context, presenter, userBean));
        addItemView(new RightVoiceMessageItemView(context, presenter, userBean));
    }

    @Override
    public void setData(RcvHolder holder, EMMessage message, int position)
    {
        super.setData(holder, message, position);
        setTimeStamp(holder, message, position);
    }

    //设置时间
    // [position==0或者和上一条信息时间间隔大于15分钟的时候显示时间]
    protected void setTimeStamp(RcvHolder holder, EMMessage emMessage, int position)
    {
        long curTimeStamp = emMessage.getMsgTime();
        if (position == 0
                || Math.abs(curTimeStamp - mDataList.get(position - 1).getMsgTime()) >= MIN_SHOW_TIME)
        {
            holder.setVisibility(R.id.tv_chat_listitem_time_stamp, View.VISIBLE);
            holder.setTvText(R.id.tv_chat_listitem_time_stamp, DateUtils.getTimeDescribe(mContext, new Date(curTimeStamp)));
        } else
        {
            holder.setVisibility(R.id.tv_chat_listitem_time_stamp, View.GONE);
        }
    }

}
