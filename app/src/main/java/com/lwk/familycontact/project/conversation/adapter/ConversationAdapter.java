package com.lwk.familycontact.project.conversation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatTextView;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.joooonho.SelectableRoundedImageView;
import com.lib.base.date.DateUtils;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.RcvSingleAdapter;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.im.bean.HxConversation;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.Date;
import java.util.List;

/**
 * Created by LWK
 * TODO 会话片段适配器
 * 2016/8/8
 */
public class ConversationAdapter extends RcvSingleAdapter<HxConversation>
{
    public ConversationAdapter(Context context, List<HxConversation> datas)
    {
        super(context, R.layout.layout_conversation_listitem, datas);
    }

    @Override
    public void setData(RcvHolder holder, HxConversation itemData, int position)
    {
        SelectableRoundedImageView rImgHead = holder.findView(R.id.img_conversation_listitem_head);
        TextView tvUnread = holder.findView(R.id.tv_conversation_listitem_unread);
        FlatTextView fTvName = holder.findView(R.id.tv_conversation_listitem_name);
        TextView tvLastMsg = holder.findView(R.id.tv_conversation_listitem_last_msg);
        TextView tvTime = holder.findView(R.id.tv_conversation_listitem_time);
        ProgressBar pgbSending = holder.findView(R.id.pgb_conversation_listitem_status_sending);
        ImageView imgSendFail = holder.findView(R.id.img_conversation_listitem_status_fail);

        UserBean userBean = itemData.getUserBean();
        EMConversation emConversation = itemData.getEmConversation();
        EMMessage lastMessage = emConversation.getLastMessage();
        //设置头像、名字
        if (userBean != null)
        {
            String head = userBean.getLocalHead();
            if (StringUtil.isNotEmpty(head))

                CommonUtils.getInstance().getImageDisplayer()
                        .display(mContext, rImgHead, head, 300, 300);
            else
                rImgHead.setImageResource(R.drawable.default_avatar);
            fTvName.setText(userBean.getDisplayName());
        } else
        {
            rImgHead.setImageResource(R.drawable.default_avatar);
            fTvName.setText(emConversation.getUserName());
        }
        //设置未读消息数量
        int unreadCount = emConversation.getUnreadMsgCount();
        if (unreadCount != 0)
        {
            tvUnread.setVisibility(View.VISIBLE);
            String unreadEx = mContext.getResources().getString(R.string.tv_conversation_listitem_unread_ex);
            String unreadStr = unreadEx.replaceFirst("%%1", String.valueOf(unreadCount));
            tvUnread.setText(unreadStr);
        } else
        {
            tvUnread.setVisibility(View.GONE);
        }

        if (lastMessage == null)
            return;
        //设置时间
        long timeStamp = lastMessage.getMsgTime();
        tvTime.setText(DateUtils.getTimeDescribe(mContext, new Date(timeStamp)));
        //设置最后消息的描述
        EMMessage.Type lastMsgType = lastMessage.getType();
        if (lastMsgType == EMMessage.Type.VOICE)
        {
            tvLastMsg.setText(R.string.msg_type_desc_voice);
        } else if (lastMsgType == EMMessage.Type.IMAGE)
        {
            tvLastMsg.setText(R.string.msg_type_desc_image);
        } else if (lastMsgType == EMMessage.Type.VIDEO)
        {
            tvLastMsg.setText(R.string.msg_type_desc_video);
        } else if (lastMsgType == EMMessage.Type.LOCATION)
        {
            tvLastMsg.setText(R.string.msg_type_desc_location);
        } else if (lastMsgType == EMMessage.Type.FILE)
        {
            tvLastMsg.setText(R.string.msg_type_desc_file);
        } else if (lastMsgType == EMMessage.Type.TXT)
        {
            EMTextMessageBody txtBody = (EMTextMessageBody) lastMessage.getBody();
            tvLastMsg.setText(txtBody.getMessage());
        }
        //设置最后消息的状态
        EMMessage.Status status = lastMessage.status();
        if (status == EMMessage.Status.CREATE || status == EMMessage.Status.INPROGRESS)
        {
            pgbSending.setVisibility(View.VISIBLE);
            imgSendFail.setVisibility(View.GONE);
        } else if (status == EMMessage.Status.FAIL)
        {
            pgbSending.setVisibility(View.GONE);
            imgSendFail.setVisibility(View.VISIBLE);
        } else
        {
            pgbSending.setVisibility(View.GONE);
            imgSendFail.setVisibility(View.GONE);
        }
    }
}
