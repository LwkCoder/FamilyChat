package com.lwk.familycontact.storage.db.user;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lib.base.log.KLog;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.storage.db.BaseDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LWK
 * TODO 通讯录资料数据表操作Dao
 * 2016/8/8
 */
public class UserDao extends BaseDao<UserBean, Integer>
{
    private final String TAG = this.getClass().getSimpleName();

    private UserDao(Context context)
    {
        super(context);
    }

    private static final class UserDaoHolder
    {
        private static UserDao instance = new UserDao(FCApplication.getIntance());
    }

    public static UserDao getInstance()
    {
        return UserDaoHolder.instance;
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

    public void updateUserFromHxServer()
    {
        HxSdkHelper.getInstance().asyncUserListFromServer(new FCCallBack<List<String>>()
        {
            @Override
            public void onFail(int status, int errorMsgResId)
            {

            }

            @Override
            public void onSuccess(List<String> userList)
            {
                if (userList == null || userList.size() == 0)
                    return;

                List<UserBean> newUserList = new ArrayList<>();
                for (String phone : userList)
                {
                    //将已有的数据标记为注册
                    int lineNUm = updateUserAsRegisted(phone);
                    //没有的数据批量加入list，稍后直接批量插入数据库
                    if (lineNUm <= 0)
                    {
                        UserBean userBean = new UserBean(phone);
                        newUserList.add(userBean);
                    }
                }
                //将表中没有的数据直接批量插入
                if (userList.size() > 0)
                    saveList(newUserList);
            }
        });
    }

    /**
     * 将某个手机号的用户标记为已注册
     *
     * @param phone 手机号
     * @return 更新成功后返回表中对应的行数，失败代表不存在该phone的数据
     */
    public int updateUserAsRegisted(String phone)
    {
        int lineNum = -1;
        try
        {
            UpdateBuilder<UserBean, Integer> updateBuilder = getDao().updateBuilder();
            updateBuilder.updateColumnValue(UserDbConfig.IS_REGIST, true);
            updateBuilder.where().eq(UserDbConfig.PHONE, phone);
            lineNum = getDao().update(updateBuilder.prepare());
        } catch (SQLException e)
        {
            KLog.e(TAG + " UserDao.updateUserAsRegisted fail : " + e.toString());
        }
        return lineNum;
    }

    /**
     * 更新某个用户的昵称
     *
     * @param userBean 更新数据
     * @return 更新成功后返回表中对应的行数，失败代表不存在该phone的数据
     */
    public int updateUserName(UserBean userBean)
    {
        int lineNum = -1;
        try
        {
            UpdateBuilder<UserBean, Integer> updateBuilder = getDao().updateBuilder();
            updateBuilder.updateColumnValue(UserDbConfig.NAME, userBean.getName());
            updateBuilder.updateColumnValue(UserDbConfig.DISPLAY_NAME, userBean.getDisplayName());
            updateBuilder.updateColumnValue(UserDbConfig.FIRST_CHAR, userBean.getFirstChar());
            updateBuilder.updateColumnValue(UserDbConfig.SIMPLE_SPELL, userBean.getSimpleSpell());
            updateBuilder.updateColumnValue(UserDbConfig.FULL_SPELL, userBean.getFullSpell());

            updateBuilder.where().eq(UserDbConfig.PHONE, userBean.getPhone());
            lineNum = getDao().update(updateBuilder.prepare());
        } catch (SQLException e)
        {
            KLog.e(TAG + " UserDao.updateUserName fail : " + e.toString());
        }
        return lineNum;
    }


    /**
     * 查询所有用户数据并按照首字母排序
     *
     * @return 所有用户数据list
     */
    public List<UserBean> queryAllUsersSortByFirstChar()
    {
        List<UserBean> allUserList = null;
        try
        {
            QueryBuilder<UserBean, Integer> queryBuilder = getDao().queryBuilder();
            queryBuilder.orderBy(UserDbConfig.FIRST_CHAR, true).orderBy(UserDbConfig.FULL_SPELL, true);
            allUserList = getDao().query(queryBuilder.prepare());
        } catch (SQLException e)
        {
            KLog.e(TAG + " UserDao.queryAllUsersSortByFirstChar fail : " + e.toString());
        }
        return allUserList;
    }
}
