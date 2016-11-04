package com.lwk.familycontact.project.contact.view;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import com.lib.base.toast.ToastUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.utils.dialog.FCBaseDialog;

/**
 * Created by LWK
 * TODO 修改app内备注名的dialog
 * 2016/11/4
 */
public class UpdateNameAppDialog extends FCBaseDialog implements View.OnClickListener
{
    private EditText mEdContent;
    private String mOriginName;
    private onNameAppUpdateListener mListener;

    public UpdateNameAppDialog(Activity context, String originName)
    {
        super(context);
        this.mOriginName = originName;
    }

    @Override
    public int getContentViewId()
    {
        return R.layout.dialog_update_nameapp;
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
        mEdContent = findView(R.id.ed_dialog_update_nameapp_content);
        if (StringUtil.isNotEmpty(mOriginName))
        {
            mEdContent.setText(mOriginName);
            mEdContent.setSelection(mOriginName.length());
        }
        addClick(R.id.img_dialog_update_nameapp_close, this);
        addClick(R.id.btn_dialog_update_nameapp_confirm, this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_dialog_update_nameapp_close:
                dismiss();
                break;
            case R.id.btn_dialog_update_nameapp_confirm:
                String nameApp = mEdContent.getText().toString().trim();
                if (StringUtil.isEmpty(nameApp))
                {
                    ToastUtils.showShortMsg(mContext, R.string.warning_dialog_update_nameapp_can_not_empty);
                    return;
                }

                if (mListener != null)
                    mListener.onNameAppUpdated(nameApp);
                dismiss();
                break;
        }
    }

    public void setOnNameAppUpdateListener(onNameAppUpdateListener listener)
    {
        this.mListener = listener;
    }

    public interface onNameAppUpdateListener
    {
        void onNameAppUpdated(String name);
    }
}
