package com.lwk.familycontact.project.chat.view;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by LWK
 * TODO 查看大图界面Impl
 * 2016/9/28
 */
public interface HxImageDetailView
{
    void onScanDataSuccess(List<EMMessage> list, int startPosition);

    void showDownloadDialog();

    void closeDownloadDialog();

    void showDownloadSuccessToast(String msg);

    void showDownliadFailToast();
}
