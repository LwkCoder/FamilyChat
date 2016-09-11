package com.lwk.familycontact.project.notify.view;

import com.lwk.familycontact.storage.db.invite.InviteBean;

import java.util.List;

/**
 * Created by LWK
 * TODO 新的好友通知界面Impl
 * 2016/9/6
 */
public interface NewFriendImpl
{
    void onRefreshAllNotifySuccess(List<InviteBean> list);

    void showHandlingDialog();

    void closeHandingDialog();

    void showHandlingError(int status, int errResId);

    void onNotifyStatusChanged();

}
