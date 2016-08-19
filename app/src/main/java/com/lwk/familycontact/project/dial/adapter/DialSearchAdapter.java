package com.lwk.familycontact.project.dial.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.RcvSingleAdapter;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.List;

/**
 * Created by LWK
 * TODO 拨号盘搜索结果适配器
 * 2016/8/19
 */
public class DialSearchAdapter extends RcvSingleAdapter<UserBean>
{
    public DialSearchAdapter(Context context, List<UserBean> datas)
    {
        super(context, R.layout.layout_dial_search_listitem, datas);
    }

    @Override
    public void setData(RcvHolder holder, UserBean itemData, int position)
    {
        ImageView imgHead = holder.findView(R.id.img_dial_search_head);
        String localHead = itemData.getLocalHead();
        if (StringUtil.isNotEmpty(localHead))
            Glide.with(mContext).load(localHead).override(120, 120).into(imgHead);
        else
            imgHead.setImageResource(R.drawable.default_avatar);

        holder.setTvText(R.id.tv_user_dial_search_name, itemData.getDisplayName());
        holder.setTvText(R.id.tv_user_dial_search_phone, itemData.getPhone());
    }
}
