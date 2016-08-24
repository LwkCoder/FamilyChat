package com.lwk.familycontact.project.dial.task;

import android.os.AsyncTask;
import android.util.Pair;

import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.impl.RcvSortSectionImpl;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LWK
 * TODO 根据手机号模糊搜索匹配数据的异步任务
 * 2016/8/19
 */
public class DialSearchTask extends AsyncTask<Void, Void, Pair<Boolean, List<UserBean>>>
{
    private String mPhoneNum;
    private FCCallBack<Pair<Boolean, List<UserBean>>> mCallBack;

    public DialSearchTask(String phoneNum, FCCallBack<Pair<Boolean, List<UserBean>>> callBack)
    {
        this.mPhoneNum = phoneNum;
        this.mCallBack = callBack;
    }

    @Override
    protected Pair<Boolean, List<UserBean>> doInBackground(Void... params)
    {
        boolean hasUser = UserDao.getInstance().hasUser(mPhoneNum);
        //查询数据库所有数据并排序，将#开头的数据放在最后
        List<UserBean> resultList = UserDao.getInstance().queryUsersLikePhone(mPhoneNum);
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

        return new Pair<>(hasUser, resultList);
    }

    @Override
    protected void onPostExecute(Pair<Boolean, List<UserBean>> list)
    {
        super.onPostExecute(list);
        if (mCallBack != null)
            mCallBack.onSuccess(list);
    }
}
