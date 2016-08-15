package com.lwk.familycontact.project.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.RcvSortSectionAdatper;
import com.lib.rcvadapter.bean.RcvSecBean;
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
        ftvPhone.setText(PhoneUtils.formatPhoneNumAsRegular(userBean.getPhone(), " - "));

        if (StringUtil.isNotEmpty(userBean.getLocalHead()))
            Glide.with(mContext).load(userBean.getLocalHead()).override(200, 200).into(imgHead);
        else
            imgHead.setImageResource(R.drawable.default_avatar);
    }

    /**
     * 更新某个用户资料
     */
    public void updateUserProfile(UserBean userBean)
    {
        for (RcvSecBean<String, UserBean> bean : mDataList)
        {
            UserBean eachUserBean = bean.getContent();
            if (eachUserBean != null && StringUtil.isEquals(userBean.getPhone(), eachUserBean.getPhone()))
            {
                eachUserBean.setRegist(userBean.isRegist());
                eachUserBean.setName(userBean.getName());
                eachUserBean.setLocalHead(userBean.getLocalHead());
                eachUserBean.updateDisplayNameAndSpell();
                notifyDataSetChanged();
                return;
            }
        }
    }
}
