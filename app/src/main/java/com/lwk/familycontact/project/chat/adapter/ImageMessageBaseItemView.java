package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.joooonho.SelectableRoundedImageView;
import com.lib.base.utils.ScreenUtils;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.io.File;

/**
 * Created by LWK
 * TODO 图片消息ItemView基类
 * 2016/9/22
 */
public abstract class ImageMessageBaseItemView extends HxChatBaseItemView
{
    //ImageView最大尺寸
    private final int MAX_IMAGE_SIZE = ScreenUtils.getScreenWidth(FCApplication.getInstance()) / 4;
    //ImageView最小尺寸
    private final int MIN_IMAGE_SIZE = FCApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.img_chat_listitem_image_content_min_size);
    //ImageView宽高
    private int mLayoutWidth = 0, mLayoutHeight = 0;
    private final int PADDING_TOP = FCApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.img_chat_listitem_image_content_padding_top);
    private final int PADDING_BOTTOM = FCApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.img_chat_listitem_image_content_padding_bottom);
    private final int PADDING_LEFT = FCApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.img_chat_listitem_image_content_padding_left);
    private final int PADDING_RIGHT = FCApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.img_chat_listitem_image_content_padding_right);

    public ImageMessageBaseItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public void setMessageData(RcvHolder holder, final EMMessage emMessage, final int position)
    {
        EMImageMessageBody messageBody = (EMImageMessageBody) emMessage.getBody();
        //根据图片高度计算ImageView的尺寸
        int imgWidth = messageBody.getWidth();
        int imgHeight = messageBody.getHeight();
        if (imgWidth >= MAX_IMAGE_SIZE)
        {
            mLayoutWidth = MAX_IMAGE_SIZE;
            mLayoutHeight = mLayoutWidth * imgHeight / imgWidth;
        } else if (imgHeight >= MAX_IMAGE_SIZE)
        {
            mLayoutHeight = MAX_IMAGE_SIZE;
            mLayoutWidth = mLayoutHeight * imgWidth / imgHeight;
        } else
        {
            mLayoutWidth = imgWidth >= MIN_IMAGE_SIZE ? imgWidth : MIN_IMAGE_SIZE;
            mLayoutHeight = imgHeight >= MIN_IMAGE_SIZE ? imgWidth : MIN_IMAGE_SIZE;
        }

        SelectableRoundedImageView imageView = holder.findView(R.id.img_chat_listitem_img_content);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = mLayoutWidth + PADDING_LEFT + PADDING_RIGHT;
        layoutParams.height = mLayoutHeight + PADDING_TOP + PADDING_BOTTOM;
        imageView.setLayoutParams(layoutParams);

        //判断显示图地址
        String localUrl = messageBody.getLocalUrl();
        String remoteUrl = messageBody.getRemoteUrl();
        if (emMessage.direct() == EMMessage.Direct.SEND && StringUtil.isNotEmpty(localUrl) && new File(localUrl).exists())
            CommonUtils.getInstance().getImageDisplayer()
                    .display(mContext, imageView, localUrl, mLayoutWidth, mLayoutHeight);
        else
            CommonUtils.getInstance().getImageDisplayer()
                    .display(mContext, imageView, remoteUrl, mLayoutWidth, mLayoutHeight);

        //设置点击事件
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.showImageDetail(emMessage, position);
            }
        });

        setMessage(holder, emMessage, position);
    }

    public abstract void setMessage(RcvHolder holder, EMMessage emMessage, int position);
}
