package com.lwk.familycontact.project.notify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lib.base.date.DateUtils;
import com.lib.base.utils.ResUtils;
import com.lib.rcvadapter.RcvSingleAdapter;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.notify.presenter.NewFriendPresenter;
import com.lwk.familycontact.storage.db.invite.InviteBean;
import com.lwk.familycontact.storage.db.invite.InviteStatus;

import java.util.Date;
import java.util.List;

/**
 * Created by LWK
 * TODO 新的好友通知界面适配器
 * 2016/9/6
 */
public class NewFriendNotifyAdapter extends RcvSingleAdapter<InviteBean>
{
    private NewFriendPresenter mPresenter;

    public NewFriendNotifyAdapter(Context context, List<InviteBean> datas, NewFriendPresenter presenter)
    {
        super(context, R.layout.layout_new_friend_notify_listitem, datas);
        this.mPresenter = presenter;
    }

    @Override
    public void setData(RcvHolder holder, final InviteBean itemData, int position)
    {
        //设置时间
        holder.setTvText(R.id.tv_new_friend_notify_listitem_time, DateUtils.getTimeDescribe(mContext, new Date(itemData.getStamp())));
        //根据状态设置其余UI
        TextView tvDesc = holder.findView(R.id.tv_new_friend_notify_listitem_desc);
        View btnView = holder.findView(R.id.ll_new_friend_notify_listitem);
        TextView tvStatus = holder.findView(R.id.tv_new_friend_notify_listitem_status);
        int status = itemData.getStatus();
        switch (status)
        {
            case InviteStatus.ORIGIN:
                btnView.setVisibility(View.VISIBLE);
                tvStatus.setVisibility(View.GONE);
                //设置描述
                setDesc1(tvDesc, itemData.getOpPhone());
                //设置BUTTON
                holder.setClickListener(R.id.btn_new_friend_notify_listitem_agree, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mPresenter.agreeNewFriendRequest(itemData);
                    }
                });
                holder.setClickListener(R.id.btn_new_friend_notify_listitem_reject, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mPresenter.rejectNewFriendRequest(itemData);
                    }
                });
                break;
            case InviteStatus.AGREED:
                btnView.setVisibility(View.GONE);
                tvStatus.setVisibility(View.VISIBLE);
                //设置描述和状态
                setDesc1(tvDesc, itemData.getOpPhone());
                tvStatus.setBackgroundResource(R.drawable.shape_rect_green_dark_4radius);
                tvStatus.setText(R.string.hasAgreed);
                break;
            case InviteStatus.REJECTED:
                btnView.setVisibility(View.GONE);
                tvStatus.setVisibility(View.VISIBLE);
                //设置描述和状态
                setDesc1(tvDesc, itemData.getOpPhone());
                tvStatus.setBackgroundResource(R.drawable.shape_rect_red_dark_4radius);
                tvStatus.setText(R.string.hasRejected);
                break;
            case InviteStatus.BE_AGREED:
                btnView.setVisibility(View.GONE);
                tvStatus.setVisibility(View.VISIBLE);
                //设置描述和状态
                setDesc2(tvDesc, itemData.getOpPhone());
                tvStatus.setBackgroundResource(R.drawable.shape_rect_green_dark_4radius);
                tvStatus.setText(R.string.opHasAgreed);
                break;
            case InviteStatus.BE_REJECTED:
                btnView.setVisibility(View.GONE);
                tvStatus.setVisibility(View.VISIBLE);
                //设置描述和状态
                setDesc2(tvDesc, itemData.getOpPhone());
                tvStatus.setBackgroundResource(R.drawable.shape_rect_red_dark_4radius);
                tvStatus.setText(R.string.opHasRejected);
                break;
        }
    }

    //设置描述【当此通知对本方来说是接收方】
    private void setDesc1(TextView tvDesc, String phone)
    {
        String desc = ResUtils.getString(mContext, R.string.tv_new_friend_notify_listitem_desc01).replaceFirst("%%1", phone);
        tvDesc.setText(desc);
    }

    //设置描述【当此通知对本方来说是发送方】
    private void setDesc2(TextView tvDesc, String phone)
    {
        String desc = ResUtils.getString(mContext, R.string.tv_new_friend_notify_listitem_desc02).replaceFirst("%%1", phone);
        tvDesc.setText(desc);
    }
}
