package com.lwk.familycontact.project.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.cengalabs.flatui.views.FlatTextView;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.RcvSortSectionAdatper;
import com.lib.rcvadapter.bean.RcvSecBean;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.common.CommonUtils;
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
    private int mUserBeanCount;

    public ContactAdapter(Context context, List<UserBean> datas)
    {
        super(context, R.layout.layout_contact_section_listitem, R.layout.layout_contact_content_listitem, datas);
        this.mUserBeanCount = datas != null ? datas.size() : 0;
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
            CommonUtils.getInstance()
                    .getImageDisplayer()
                    .display(mContext, imgHead, userBean.getLocalHead(), 200, 200, R.drawable.default_avatar, R.drawable.default_avatar);
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

    @Override
    public void refreshDataInSection(List<UserBean> dataList)
    {
        super.refreshDataInSection(dataList);
        this.mUserBeanCount = dataList != null ? dataList.size() : 0;
    }

    /**
     * 获取实际联系人的数量
     */
    public int getUserBeanCount()
    {
        return mUserBeanCount;
    }
}
