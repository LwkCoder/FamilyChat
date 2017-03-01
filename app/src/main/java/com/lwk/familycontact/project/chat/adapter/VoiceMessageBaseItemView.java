package com.lwk.familycontact.project.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.holder.RcvHolder;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.chat.presenter.HxChatPresenter;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.widget.FrameAnimImageView;

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

    private final int ANIM_DURATION = 350;
    private final Integer[] mAnimLeft = new Integer[]{R.drawable.ic_voice_left01, R.drawable.ic_voice_left02, R.drawable.ic_voice_left03};
    private final Integer[] mAnimRight = new Integer[]{R.drawable.ic_voice_right01, R.drawable.ic_voice_right02, R.drawable.ic_voice_right03};

    public VoiceMessageBaseItemView(Context mContext, HxChatPresenter mPresenter, UserBean mUserBean)
    {
        super(mContext, mPresenter, mUserBean);
    }

    @Override
    public void setMessageData(RcvHolder holder, final EMMessage emMessage, final int position)
    {
        final EMVoiceMessageBody messageBody = (EMVoiceMessageBody) emMessage.getBody();
        final FrameAnimImageView imgLabel = holder.findView(R.id.img_chat_listitem_voice_content);
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

        if (mPresenter.getCurPlayVoicePosition() == position)
        {
            if (emMessage.direct() == EMMessage.Direct.SEND)
                imgLabel.start(mAnimRight, true, ANIM_DURATION);
            else
                imgLabel.start(mAnimLeft, true, ANIM_DURATION);
        } else
        {
            imgLabel.stop();
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
        holder.setClickListener(R.id.fl_chat_listitem_voice_content, new VoiceClickListener(imgLabel, emMessage, position));

        setMessage(holder, emMessage, position);
    }

    private class VoiceClickListener implements View.OnClickListener
    {
        private FrameAnimImageView mImageView;
        private EMMessage mMessage;
        private int mPosition;
        private EMVoiceMessageBody mBody;

        VoiceClickListener(FrameAnimImageView imageView, EMMessage message, int position)
        {
            this.mImageView = imageView;
            this.mMessage = message;
            this.mPosition = position;
            this.mBody = (EMVoiceMessageBody) mMessage.getBody();
        }

        @Override
        public void onClick(View v)
        {
            if (mPresenter.getCurPlayVoicePosition() == mPosition)
            {
                mPresenter.stopPlayVoiceMessage();
                return;
            }

            mPresenter.stopPlayVoiceMessage();
            mMessage.setListened(true);
            mPresenter.setCurVoicePlayPosition(mPosition);
            if (mMessage.direct() == EMMessage.Direct.SEND)
                mImageView.start(mAnimRight, true, ANIM_DURATION);
            else
                mImageView.start(mAnimLeft, true, ANIM_DURATION);

            String localUrl = mBody.getLocalUrl();
            if (StringUtil.isNotEmpty(localUrl) && new File(localUrl).exists())
                mPresenter.clickVoiceMessage(localUrl);
            else
                mPresenter.clickVoiceMessage(mBody.getRemoteUrl());
        }
    }

    public abstract void setMessage(RcvHolder holder, EMMessage emMessage, int position);
}
