package com.lwk.familycontact.project.contact.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.impl.RcvSortSectionImpl;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.contact.model.ContactModel;
import com.lwk.familycontact.project.contact.task.RefreshContactDataTask;
import com.lwk.familycontact.project.contact.view.ContactImpl;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;

import java.util.ArrayList;
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
     * 刷新所有通讯录数据[环信好友+本机通讯录]
     *
     * @param context 上下文环境
     */
    public void refreshAllContactData(Context context)
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
                mContactView.refreshContactNum();
            }

            @Override
            public void onSuccess(List<UserBean> resultList)
            {
                mContactView.refreshAllUsersSuccess(true, resultList);
                mContactView.refreshContactNum();
            }
        });
        mRefreshContactDataTask.executeOnExecutor(Executors.newCachedThreadPool());
    }

    /**
     * 刷新数据库中好友数据
     * [不获取系统通讯录中好友]
     */
    public void refreshContactDataInDb(final boolean isPtrRefresh)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //查询数据库所有数据并排序，将#开头的数据放在最后
                List<UserBean> resultList = UserDao.getInstance().queryAllUsersSortByFirstChar();
                List<UserBean> defCharList = new ArrayList<>();
                for (UserBean userBean : resultList)
                {
                    if (StringUtil.isEquals(userBean.getFirstChar(), RcvSortSectionImpl.DEF_SECTION))
                        defCharList.add(userBean);
                }

                if (defCharList.size() > 0)
                {
                    resultList.removeAll(defCharList);
                    resultList.addAll(defCharList);
                }

                mContactView.refreshAllUsersSuccess(isPtrRefresh, resultList);
                mContactView.refreshContactNum();
            }
        }).start();
    }
}
