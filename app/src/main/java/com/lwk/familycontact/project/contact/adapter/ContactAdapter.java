package com.lwk.familycontact.project.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.RcvSortSectionAdatper;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.List;

/**
 * Created by LWK
 * TODO 通讯录界面适配器
 * 2016/8/10
 */
public class ContactAdapter extends RcvSortSectionAdatper<UserBean>
{
    private int COLOR_REGIST = Color.parseColor("#2828ff");
    private int COLOR_NOT_REGIST = Color.parseColor("#202020");

    public ContactAdapter(Context context, List<UserBean> datas)
    {
        super(context, R.layout.layout_contact_section_listitem, R.layout.layout_contact_content_listitem, datas);
    }

    @Override
    public void setSectionLayout(RcvHolder holder, String sectionData, int position)
    {
        holder.setTvText(R.id.tv_contact_section, sectionData);
    }

    @Override
    public void setContentLayout(RcvHolder holder, UserBean userBean, int position)
    {
        FlatTextView ftvName = holder.findView(R.id.tv_contact_name);
        FlatTextView ftvPhone = holder.findView(R.id.tv_contact_phone);
        ImageView imgHead = holder.findView(R.id.img_contact_head);

        if (userBean.isRegist())
            ftvName.setTextColor(COLOR_REGIST);
        else
            ftvName.setTextColor(COLOR_NOT_REGIST);
        ftvName.setText(userBean.getDisplayName());
        ftvPhone.setText(userBean.getPhone());

        if (StringUtil.isNotEmpty(userBean.getLocalHead()))
            Glide.with(mContext).load(userBean.getLocalHead()).into(imgHead);
        else
            imgHead.setImageResource(R.mipmap.ic_launcher);
    }
}
