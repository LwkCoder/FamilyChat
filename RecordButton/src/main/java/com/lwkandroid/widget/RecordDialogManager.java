package com.lwkandroid.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 录音dialog管理类
 * Created by LuoWK on 15/10/4.
 */
public class RecordDialogManager
{
    private Context mContext;
    private Dialog mDialog;
    private ImageView mImg_Icon;
    private ImageView mImg_Voice;
    private TextView mTv_Label;

    public RecordDialogManager(Context context)
    {
        this.mContext = context;
    }

    //显示dialog
    public void showRecordingDialog()
    {
        if (mDialog == null)
        {
            mDialog = new Dialog(mContext, R.style.RecordDialogTheme);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog, null);
            mDialog.setContentView(layout);

            mImg_Icon = (ImageView) layout.findViewById(R.id.img_dialog_record_icon);
            mImg_Voice = (ImageView) layout.findViewById(R.id.img_dialog_record_voice);
            mTv_Label = (TextView) layout.findViewById(R.id.tv_dialog_record_label);
        }

        mDialog.show();
    }

    //显示录音状态
    public void recording()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mImg_Icon.setVisibility(View.VISIBLE);
            mImg_Voice.setVisibility(View.VISIBLE);
            mTv_Label.setVisibility(View.VISIBLE);

            mImg_Icon.setImageResource(R.mipmap.dialog_record_icon);
            mTv_Label.setText(R.string.str_recorddialog_recording);
            mTv_Label.setBackgroundResource(R.drawable.dialog_label_normal_bg);
        }
    }

    //显示打算取消录音状态
    public void wantToCancel()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mImg_Icon.setVisibility(View.VISIBLE);
            mImg_Voice.setVisibility(View.GONE);
            mTv_Label.setVisibility(View.VISIBLE);

            mImg_Icon.setImageResource(R.mipmap.dialog_record_cancel);
            mTv_Label.setText(R.string.str_recordbtn_state_want_cancel);
            mTv_Label.setBackgroundResource(R.drawable.dialog_label_warning_bg);
        }
    }

    //显示录音时长太短状态
    public void tooShort()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mImg_Icon.setVisibility(View.VISIBLE);
            mImg_Voice.setVisibility(View.GONE);
            mTv_Label.setVisibility(View.VISIBLE);

            mImg_Icon.setImageResource(R.mipmap.voice_too_short);
            mTv_Label.setText(R.string.str_recorddialog_too_short);
            mTv_Label.setBackgroundResource(R.drawable.dialog_label_warning_bg);
        }
    }

    //显示Sd卡不存在的提醒
    public void noSdcard()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mImg_Icon.setVisibility(View.VISIBLE);
            mImg_Voice.setVisibility(View.GONE);
            mTv_Label.setVisibility(View.VISIBLE);

            mImg_Icon.setImageResource(R.mipmap.voice_too_short);
            mTv_Label.setText(R.string.str_recordbtn_state_noSDcard);
            mTv_Label.setBackgroundResource(R.drawable.dialog_label_warning_bg);
        }
    }

    //关闭dialog
    public void dismissDialog()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 根据音量等级更新音量图片
     *
     * @param level 1～7
     */
    public void updateVoiceLevel(int level)
    {
        if (mDialog != null && mDialog.isShowing())
        {
            //            mImg_Icon.setVisibility(View.VISIBLE);
            //            mImg_Voice.setVisibility(View.VISIBLE);
            //            mTv_Label.setVisibility(View.VISIBLE);

            //根据资源名称获取资源id
            int resId = mContext.getResources().getIdentifier("v" + level,
                    "mipmap", mContext.getPackageName());
            mImg_Voice.setImageResource(resId);
        }
    }

}
