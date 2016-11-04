package com.lwk.familycontact.project.chat.view;

import android.app.Activity;
import android.view.View;

import com.lwk.familycontact.R;
import com.lwk.familycontact.utils.dialog.FCBaseDialog;

/**
 * Created by LWK
 * TODO 聊天界面更多种类消息的入口Dialog
 * 2016/9/30
 */
public class HxChatPlusDialog extends FCBaseDialog implements View.OnClickListener
{
    //照片/图片
    public static final int ITEM_PHOTO = 1;
    //短视频
    public static final int ITEM_VIDEO = 2;
    //语音通话
    public static final int ITEM_VOICE_CALL = 3;
    //视频通话
    public static final int ITEM_VIDEO_CALL = 4;

    private onChatPlusItemSelectedListener mListener;

    public HxChatPlusDialog(Activity context)
    {
        super(context);
    }

    @Override
    public int getContentViewId()
    {
        return R.layout.dialog_chat_plus;
    }

    @Override
    public boolean isCancelable()
    {
        return true;
    }

    @Override
    public boolean isCanceledOnTouchOutside()
    {
        return true;
    }

    @Override
    public void initUI(View contentView)
    {
        addClick(R.id.ll_chat_plus_dialog_pic, this);
        addClick(R.id.ll_chat_plus_dialog_video, this);
        addClick(R.id.ll_chat_plus_dialog_voice_call, this);
        addClick(R.id.ll_chat_plus_dialog_video_call, this);
    }

    @Override
    public void onClick(View v)
    {
        int position = 0;
        switch (v.getId())
        {
            case R.id.ll_chat_plus_dialog_pic:
                position = ITEM_PHOTO;
                break;
            case R.id.ll_chat_plus_dialog_video:
                position = ITEM_VIDEO;
                break;
            case R.id.ll_chat_plus_dialog_voice_call:
                position = ITEM_VOICE_CALL;
                break;
            case R.id.ll_chat_plus_dialog_video_call:
                position = ITEM_VIDEO_CALL;
                break;
        }
        if (mListener != null)
            mListener.onPlusItemSelected(position);
        dismiss();
    }

    public void setOnChatPlusItemSelectedListener(onChatPlusItemSelectedListener l)
    {
        this.mListener = l;
    }

    public interface onChatPlusItemSelectedListener
    {
        void onPlusItemSelected(int position);
    }
}
