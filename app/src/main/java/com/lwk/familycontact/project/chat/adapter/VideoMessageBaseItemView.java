package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.joooonho.SelectableRoundedImageView;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by LWK
 * TODO 短视频消息ItemView基类
 * 2016/9/23
 */
public abstract class VideoMessageBaseItemView extends HxChatBaseItemView
{
    public VideoMessageBaseItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public void setMessageData(RcvHolder holder, final EMMessage emMessage, final int position)
    {
        SelectableRoundedImageView imgThumb = holder.findView(R.id.img_chat_listitem_video_content);
        TextView tvDuration = holder.findView(R.id.tv_chat_listitem_video_content_duration);
        TextView tvFileLength = holder.findView(R.id.tv_chat_listitem_video_content_filelength);

        EMVideoMessageBody messageBody = (EMVideoMessageBody) emMessage.getBody();

        //缩略图
        String localThumb = messageBody.getLocalThumb();
        String remoteThumb = messageBody.getThumbnailUrl();
        if (StringUtil.isNotEmpty(localThumb) && new File(localThumb).exists())
            CommonUtils.getInstance().getImageDisplayer()
                    .display(mContext, imgThumb, localThumb, 240, 300);
        else
            CommonUtils.getInstance().getImageDisplayer()
                    .display(mContext, imgThumb, remoteThumb, 240, 300);
        //视频时长
        String durationEx = mContext.getResources().getString(R.string.tv_chat_listitem_video_duration_Ex);
        String duration = durationEx.replaceFirst("%%1", String.valueOf(messageBody.getDuration()));
        tvDuration.setText(duration);
        //视频大小
        tvFileLength.setText(formetFileSize(messageBody.getVideoFileLength()));

        //点击播放
        holder.setClickListener(R.id.img_chat_listitem_video_start, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPresenter.clickVideoMessage(emMessage, position);
            }
        });
        setMessage(holder, emMessage, position);
    }

    /**
     * 转换文件大小描述
     */
    private String formetFileSize(long fileLength)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileLength == 0)
        {
            return wrongSize;
        }
        if (fileLength < 1024)
        {
            fileSizeString = df.format((double) fileLength) + "B";
        } else if (fileLength < 1048576)
        {
            fileSizeString = df.format((double) fileLength / 1024) + "KB";
        } else if (fileLength < 1073741824)
        {
            fileSizeString = df.format((double) fileLength / 1048576) + "MB";
        } else
        {
            fileSizeString = df.format((double) fileLength / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public abstract void setMessage(RcvHolder holder, EMMessage emMessage, int position);
}
