package com.lwk.familycontact.project.contact.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.contact.model.ContactModel;
import com.lwk.familycontact.project.contact.task.RefreshContactDataTask;
import com.lwk.familycontact.project.contact.view.ContactImpl;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by LWK
 * TODO 通讯录界面Presenter
 * 2016/8/9
 */
public class ContactPresenter
{
    private ContactImpl mContactView;
    private ContactModel mModel;
    private RefreshContactDataTask mRefreshContactDataTask;

    public ContactPresenter(ContactImpl contactView)
    {
        this.mContactView = contactView;
        mModel = new ContactModel();
    }

    /**
     * 刷新本机通讯录数据
     *
     * @param context 上下文环境
     */
    public void refreshContactData(Context context)
    {
        if (mRefreshContactDataTask != null && mRefreshContactDataTask.getStatus() != AsyncTask.Status.FINISHED)
        {
            mRefreshContactDataTask.cancel(true);
            mRefreshContactDataTask = null;
        }

        mRefreshContactDataTask = new RefreshContactDataTask(context, mModel, new FCCallBack<List<UserBean>>()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {
                mContactView.refreshAllUsersFail(errorMsgResId);
            }

            @Override
            public void onSuccess(List<UserBean> userBeen)
            {
                mContactView.refreshAllUsersSuccess(userBeen);
            }
        });
        mRefreshContactDataTask.executeOnExecutor(Executors.newCachedThreadPool());
    }
}
