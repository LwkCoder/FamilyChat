package com.lwk.familycontact.project.notify.presenter;

import android.os.Handler;

import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.notify.model.NewFriendModel;
import com.lwk.familycontact.project.notify.view.NewFriendImpl;
import com.lwk.familycontact.storage.db.invite.InviteBean;
import com.lwk.familycontact.storage.db.invite.InviteStatus;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.other.ThreadManager;

import java.util.List;

/**
 * Created by LWK
 * TODO 新的好友通知界面Presenter
 * 2016/9/6
 */
public class NewFriendPresenter
{
    private Handler mMainHandler;
    private NewFriendImpl mNewFriendView;
    private NewFriendModel mNewFriendModel;

    public NewFriendPresenter(NewFriendImpl newFriendView, Handler handler)
    {
        this.mMainHandler = handler;
        this.mNewFriendView = newFriendView;
        this.mNewFriendModel = new NewFriendModel();
    }

    //从本地获取所有通知
    public void refreshAllNotify()
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                handlerRefreshSuccess(mNewFriendModel.getAllInviteNotify());
            }
        });
    }

    private void handlerRefreshSuccess(final List<InviteBean> list)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                mNewFriendView.onRefreshAllNotifySuccess(list);
            }
        });
    }

    /**
     * 同意某条好友请求
     */
    public void agreeNewFriendRequest(final InviteBean inviteBean)
    {
        HxSdkHelper.getInstance().agreeNewFriendInvite(inviteBean.getOpPhone(), new FCCallBack()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {
                mNewFriendView.showHandlingError(status, errorMsgResId);
            }

            @Override
            public void onSuccess(Object o)
            {
                //同步数据库
                inviteBean.setStatus(InviteStatus.AGREED);
                mNewFriendModel.updateNotify(inviteBean);
                //刷新通知界面
                mNewFriendView.onNotifyStatusChanged();
                //添加/更新新好友数据
                mNewFriendModel.addOrUpdateNewUserData(inviteBean.getOpPhone());
                //发送Eventbus通知通讯录刷新
                EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_CONTACT_IN_DB));
            }
        });
    }

    /**
     * 拒绝某条好友请求
     */
    public void rejectNewFriendRequest(final InviteBean inviteBean)
    {
        HxSdkHelper.getInstance().rejectNewFriendInvite(inviteBean.getOpPhone(), new FCCallBack()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {
                mNewFriendView.showHandlingError(status, errorMsgResId);
            }

            @Override
            public void onSuccess(Object o)
            {
                //同步数据库
                inviteBean.setStatus(InviteStatus.REJECTED);
                mNewFriendModel.updateNotify(inviteBean);
                //刷新通知界面
                mNewFriendView.onNotifyStatusChanged();
            }
        });
    }

    /**
     * 将所有通知标记为已读
     */
    public void markAllNotifyAsRead()
    {
        mNewFriendModel.martAllInviteAsRead();
        //通知相关界面刷新
        EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_USER_INVITE));
    }

    //清空所有通知
    public void clearAllNotify()
    {
        mNewFriendView.showHandlingDialog();
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                mNewFriendModel.clearAllInvite();
                mNewFriendView.closeHandingDialog();
                refreshAllNotify();
            }
        });
    }
}
