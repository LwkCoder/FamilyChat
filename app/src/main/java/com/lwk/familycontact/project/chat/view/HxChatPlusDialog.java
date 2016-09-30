package com.lwk.familycontact.project.chat.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;

import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 聊天界面更多种类消息的入口Dialog
 * 2016/9/30
 */
public class HxChatPlusDialog implements View.OnClickListener
{
    //照片/图片
    public static final int ITEM_PHOTO = 1;
    //短视频
    public static final int ITEM_VIDEO = 2;
    //语音通话
    public static final int ITEM_VOICE_CALL = 3;
    //视频通话
    public static final int ITEM_VIDEO_CALL = 4;
    private Activity mContext;
    private Dialog mDialog;
    private onChatPlusItemSelectedListener mListener;

    public HxChatPlusDialog(Activity activity)
    {
        this.mContext = activity;
    }

    public void show()
    {
        if (mDialog == null)
        {
            mDialog = new Dialog(mContext, R.style.BaseMyDialog);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
            View layout =
                    //                    mContext.getLayoutInflater().inflate(R.layout.dialog_chat_plus, null);
                    mContext.getLayoutInflater().inflate(R.layout.dialog_chat_plus, (ViewGroup) mContext.findViewById(android.R.id.content), false);
            mDialog.setContentView(layout);
            //            Window window = mDialog.getWindow();
            //            WindowManager.LayoutParams lp = window.getAttributes();
            //            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            //            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //            lp.gravity = Gravity.CENTER;
            //            window.setAttributes(lp);

            layout.findViewById(R.id.ll_chat_plus_dialog_pic).setOnClickListener(this);
            layout.findViewById(R.id.ll_chat_plus_dialog_video).setOnClickListener(this);
            layout.findViewById(R.id.ll_chat_plus_dialog_voice_call).setOnClickListener(this);
            layout.findViewById(R.id.ll_chat_plus_dialog_video_call).setOnClickListener(this);
        }

        if (!mDialog.isShowing())
            mDialog.show();
    }

    public void dismiss()
    {
        if (isDialogShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private boolean isDialogShowing()
    {
        return mDialog != null && mDialog.isShowing();
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
