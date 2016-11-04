package com.lwk.familycontact.utils.dialog;

import android.view.View;

/**
 * Created by LWK
 * TODO Dialog基类接口
 * 2016/11/4
 */
public interface FCBaseDialogInterface
{
    int getContentViewId();

    boolean isCancelable();

    boolean isCanceledOnTouchOutside();

    void initUI(View contentView);

    void show();

    void dismiss();

    boolean isDialogShowing();
}
