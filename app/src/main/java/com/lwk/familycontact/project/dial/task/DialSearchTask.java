package com.lwk.familycontact.project.dial.task;

import android.os.AsyncTask;

import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;

import java.util.List;

/**
 * Created by LWK
 * TODO 根据手机号模糊搜索匹配数据的异步任务
 * 2016/8/19
 */
public class DialSearchTask extends AsyncTask<Void, Void, List<UserBean>>
{
    private String mPhoneNum;
    private FCCallBack<List<UserBean>> mCallBack;

    public DialSearchTask(String phoneNum, FCCallBack<List<UserBean>> callBack)
    {
        this.mPhoneNum = phoneNum;
        this.mCallBack = callBack;
    }

    @Override
    protected List<UserBean> doInBackground(Void... params)
    {
        return UserDao.getInstance().queryUsersLikePhone(mPhoneNum);
    }

    @Override
    protected void onPostExecute(List<UserBean> list)
    {
        super.onPostExecute(list);
        if (mCallBack != null)
            mCallBack.onSuccess(list);
    }
}
