package com.lib.imrecordbutton;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by LWK
 * TODO 录音器各状态View的Dialog显示
 * 2016/9/18
 */
public class IMRecordDialog implements IMRecordViewImpl
{
    private Context mContext;
    private Dialog mDialog;
    private View mLlVoice;
    private ImageView mImgVoiceLevel;
    private ImageView mImgWarning;
    private TextView mTvStatus;

    public IMRecordDialog(Context context)
    {
        this.mContext = context;
    }

    @Override
    public void showView(IMRecordButton button)
    {
        if (mDialog == null)
        {
            mDialog = new Dialog(mContext, R.style.IMRecordButtonStyle);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.layout_imrecord_dialog, null);
            mDialog.setContentView(layout);

            mLlVoice = layout.findViewById(R.id.ll_im_record_dialog_voice);
            mImgVoiceLevel = (ImageView) layout.findViewById(R.id.img_im_record_dialog_voice_level);
            mImgWarning = (ImageView) layout.findViewById(R.id.img_im_record_dialog_voice_warning);
            mTvStatus = (TextView) layout.findViewById(R.id.tv_im_record_dialog_status);
        }
        if (!mDialog.isShowing())
            mDialog.show();
    }

    @Override
    public void onRecording(IMRecordButton button)
    {
        if (isDialogShowing())
        {
            mLlVoice.setVisibility(View.VISIBLE);
            mImgWarning.setVisibility(View.INVISIBLE);
            mTvStatus.setText(R.string.im_record_dialog_status_recording);
            mTvStatus.setBackgroundResource(R.drawable.bg_tv_im_record_dialog_recording);
        }
    }

    @Override
    public void onUpdateVoiceLevel(IMRecordButton button, int level)
    {
        if (isDialogShowing())
        {
            //根据资源名称获取资源id
            int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
            mImgVoiceLevel.setImageResource(resId);
        }
    }

    @Override
    public void onWantCancel(IMRecordButton button)
    {
        if (isDialogShowing())
        {
            mLlVoice.setVisibility(View.INVISIBLE);
            mImgWarning.setVisibility(View.VISIBLE);
            mImgWarning.setImageResource(R.drawable.img_im_record_dialog_want_cancel);
            mTvStatus.setText(R.string.im_record_button_status_want_cancel);
            mTvStatus.setBackgroundResource(R.drawable.bg_tv_im_recrod_dialog_warning);
        }
    }

    @Override
    public void onRecordTooShort(IMRecordButton button)
    {
        if (isDialogShowing())
        {
            mLlVoice.setVisibility(View.INVISIBLE);
            mImgWarning.setVisibility(View.VISIBLE);
            mImgWarning.setImageResource(R.drawable.img_im_record_dialog_warning);
            mTvStatus.setText(R.string.im_record_dialog_status_too_short);
            mTvStatus.setBackgroundResource(R.drawable.bg_tv_im_recrod_dialog_warning);
        }
    }

    @Override
    public void onError(IMRecordButton button, int errorCode)
    {
        switch (errorCode)
        {
            case IMRecordError.NO_SDCARD:
                noSdCardWarning();
                break;
            default:
                unknowError();
                break;
        }
    }

    @Override
    public void onFinish(IMRecordButton button)
    {
        if (isDialogShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    //SD卡不存在时的提醒
    private void noSdCardWarning()
    {
        if (isDialogShowing())
        {
            mLlVoice.setVisibility(View.INVISIBLE);
            mImgWarning.setVisibility(View.VISIBLE);
            mImgWarning.setImageResource(R.drawable.img_im_record_dialog_warning);
            mTvStatus.setText(R.string.warning_im_record_no_sdcard);
            mTvStatus.setBackgroundResource(R.drawable.bg_tv_im_recrod_dialog_warning);
        }
    }

    private void unknowError()
    {
        if (isDialogShowing())
        {
            mLlVoice.setVisibility(View.INVISIBLE);
            mImgWarning.setVisibility(View.VISIBLE);
            mImgWarning.setImageResource(R.drawable.img_im_record_dialog_warning);
            mTvStatus.setText(R.string.warning_im_record_unknow_error);
            mTvStatus.setBackgroundResource(R.drawable.bg_tv_im_recrod_dialog_warning);
        }
    }

    private boolean isDialogShowing()
    {
        return mDialog != null && mDialog.isShowing();
    }
}
