package com.lwk.familycontact.storage.db.user;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lwk.familycontact.storage.db.BaseDao;

/**
 * Created by LWK
 * TODO 通讯录资料数据表操作Dao
 * 2016/8/8
 */
public class UserDao extends BaseDao<UserBean, Integer>
{
    private final String TAG = this.getClass().getSimpleName();

    public UserDao(Context context)
    {
        super(context);
    }

    @Override
    public String setLogTag()
    {
        return TAG;
    }

    @Override
    public Dao<UserBean, Integer> getDao()
    {
        return mHelper.getDao(UserBean.class);
    }
}
