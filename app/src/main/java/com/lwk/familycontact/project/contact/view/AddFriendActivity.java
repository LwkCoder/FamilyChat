package com.lwk.familycontact.project.contact.view;

import android.content.Intent;
import android.view.View;

import com.cengalabs.flatui.views.FlatButton;
import com.cengalabs.flatui.views.FlatEditText;
import com.lib.base.widget.CommonActionBar;
import com.lib.qrcode.QrCodeHelper;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.contact.presenter.AddFriendPresenter;

/**
 * 查找并添加好友界面
 */
public class AddFriendActivity extends FCBaseActivity implements AddFriendImpl
{
    private AddFriendPresenter mPresenter;
    private FlatEditText mFedPhone;
    private FlatButton mFbtnConfirm;

    @Override
    protected int setContentViewId()
    {
        mPresenter = new AddFriendPresenter(this);
        return R.layout.activity_add_friend;
    }

    @Override
    protected void initUI()
    {
        CommonActionBar actionBar = findView(R.id.cab_add_friend);
        actionBar.setTitleText(R.string.tv_add_friend_title);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setRightImgResource(R.drawable.ic_add_friend_qrcode);
        actionBar.setRightLayoutClickListener(this);
        mFedPhone = findView(R.id.fed_add_friend_phone);
        mFbtnConfirm = findView(R.id.btn_add_friend_confirm);
        addClick(mFbtnConfirm);
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.btn_add_friend_confirm:
                mPresenter.sendRequest(mFedPhone.getText().toString().trim());
                break;
            case R.id.fl_common_actionbar_right:
                QrCodeHelper.goToQrcodeScanActivity(this);
                break;
        }
    }

    @Override
    public void phoneEmptyWarning()
    {
        showLongToast(R.string.warning_add_friend_empty_phone);
    }

    @Override
    public void sendRequestSuccess()
    {
        showShortToast(R.string.send_request_success);
        finish();
    }

    @Override
    public void sendRequestFail(int code, int errMsgId)
    {
        showShortToast(errMsgId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == QrCodeHelper.QRCODE_REQUEST_CODE)
        {
            String result = data.getExtras().getString(QrCodeHelper.QRCODE_RESULT_CONTENT);
            mFedPhone.setText(result);
            mFbtnConfirm.performClick();
        }
    }
}
