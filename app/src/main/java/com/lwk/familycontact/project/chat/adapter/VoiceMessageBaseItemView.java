package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.io.File;

/**
 * Created by LWK
 * TODO 语音消息ItemView基类
 * 2016/9/22
 */
public abstract class VoiceMessageBaseItemView extends HxChatBaseItemView
{
    //最短时长
    private final int MIN_VOICE_LENGTH = 1;
    //最长时长
    private final int MAX_VOICE_LENGTH = 6;
    //布局最宽距离
    private final int MAX_LAYOUT_WIDTH = FCApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.fl_chat_listitem_voice_content_max_width);
    //布局最窄距离
    private final int MIN_LAYOUT_WIDTH = FCApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.fl_chat_listitem_voice_content_min_width);

    public VoiceMessageBaseItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public void setMessageData(RcvHolder holder, final EMMessage emMessage, final int position)
    {
        final EMVoiceMessageBody messageBody = (EMVoiceMessageBody) emMessage.getBody();
        ImageView imgLabel = holder.findView(R.id.img_chat_listitem_voice_content);
        TextView tvLength = holder.findView(R.id.tv_chat_listitem_voice_content);
        View vLayout = holder.findView(R.id.fl_chat_listitem_voice_content);

        int voiceLength = messageBody.getLength();
        ViewGroup.LayoutParams layoutParams = vLayout.getLayoutParams();
        if (voiceLength <= MIN_VOICE_LENGTH)
            layoutParams.width = MIN_LAYOUT_WIDTH;
        else if (voiceLength >= MAX_VOICE_LENGTH)
            layoutParams.width = MAX_LAYOUT_WIDTH;
        else
            layoutParams.width = MIN_LAYOUT_WIDTH
                    + (MAX_LAYOUT_WIDTH - MIN_LAYOUT_WIDTH)
                    * (voiceLength - MIN_VOICE_LENGTH) / 5;//5=MAX_VOICE_LENGTH-MIN_VOICE_LENGTH
        vLayout.setLayoutParams(layoutParams);

        AnimationDrawable mAnimDrawable = null;
        if (mPresenter.getCurPlayVoicePosition() == position && mAnimDrawable == null)
        {
            if (emMessage.direct() == EMMessage.Direct.SEND)
                imgLabel.setImageResource(R.drawable.anim_voice_play_right);
            else
                imgLabel.setImageResource(R.drawable.anim_voice_play_left);
            mAnimDrawable = (AnimationDrawable) imgLabel.getDrawable();
            mAnimDrawable.start();
        } else
        {
            if (mAnimDrawable != null && mAnimDrawable.isRunning())
            {
                mAnimDrawable.stop();
                mAnimDrawable = null;
            }

            if (emMessage.direct() == EMMessage.Direct.SEND)
                imgLabel.setImageResource(R.drawable.ic_voice_right03);
            else
                imgLabel.setImageResource(R.drawable.ic_voice_left03);
        }

        //设置时长
        String lengthEx = mContext.getResources().getString(R.string.tv_chat_listitem_voice_length_Ex);
        String length = lengthEx.replaceFirst("%%1", String.valueOf(voiceLength));
        tvLength.setText(length);

        //是否已听
        if (emMessage.direct() == EMMessage.Direct.RECEIVE)
        {
            TextView tvUnlisten = holder.findView(R.id.tv_chat_listitem_voice_unlisten);
            if (emMessage.isListened())
                tvUnlisten.setVisibility(View.GONE);
            else
                tvUnlisten.setVisibility(View.VISIBLE);
        }

        //设置点击事件
        holder.setClickListener(R.id.fl_chat_listitem_voice_content, null);
        holder.setClickListener(R.id.fl_chat_listitem_voice_content, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String localUrl = messageBody.getLocalUrl();
                if (StringUtil.isNotEmpty(localUrl) && new File(localUrl).exists())
                    mPresenter.clickVoiceMessage(emMessage, localUrl, position);
                else
                    mPresenter.clickVoiceMessage(emMessage, messageBody.getRemoteUrl(), position);
            }
        });

        setMessage(holder, emMessage, position);
    }

    public abstract void setMessage(RcvHolder holder, EMMessage emMessage, int position);
}
