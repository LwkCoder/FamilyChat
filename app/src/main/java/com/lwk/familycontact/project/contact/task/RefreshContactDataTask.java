package com.lwk.familycontact.project.contact.task;

import android.content.Context;
import android.os.AsyncTask;

import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.impl.RcvSortSectionImpl;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.common.FCError;
import com.lwk.familycontact.project.contact.model.ContactModel;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LWK
 * TODO 刷新本机通讯录联系人的异步任务
 * 2016/8/10
 */
public class RefreshContactDataTask extends AsyncTask<Void, Void, List<UserBean>>
{
    private Context mContext;
    private ContactModel mContactModel;
    private FCCallBack<List<UserBean>> mCallBack;

    public RefreshContactDataTask(Context context, ContactModel contactModel, FCCallBack<List<UserBean>> callBack)
    {
        this.mContext = context.getApplicationContext();
        this.mContactModel = contactModel;
        this.mCallBack = callBack;
    }

    @Override
    protected List<UserBean> doInBackground(Void... params)
    {
        //获取手机联系人，并将数据插入/更新到数据库中
        List<UserBean> phoneContactList = mContactModel.getPhoneContactData(mContext);
        if (phoneContactList != null)
        {
            for (UserBean userBean : phoneContactList)
            {
                //这里log会有很多warning，不用管！
                //warning代表无法保存数据，是因为本地数据库已经存在该条数据，这时只需要更新名字就行了
                int lineNum = UserDao.getInstance().save(userBean);
                if (lineNum <= 0)
                    UserDao.getInstance().updateUserName(userBean);
            }
        }

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

        return resultList;
    }

    @Override
    protected void onPostExecute(List<UserBean> userList)
    {
        super.onPostExecute(userList);
        if (userList != null)
        {
            if (mCallBack != null)
                mCallBack.onSuccess(userList);
        } else
        {
            if (mCallBack != null)
                mCallBack.onFail(FCError.ASYNC_PHONE_CONTACT_FAIL, R.string.error_unknow);
        }
    }
}
