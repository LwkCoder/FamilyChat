package com.lwk.familycontact.storage.db.invite;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lib.base.log.KLog;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.storage.db.BaseDao;
import com.lwk.familycontact.storage.db.DbOpenHelper;

import java.sql.SQLException;

/**
 * 邀请信息表操作类
 */
public class InviteDao extends BaseDao<InviteBean, Integer>
{
    private final String TAG = this.getClass().getSimpleName();

    private InviteDao(Context context)
    {
        super(context);
    }

    private static final class InviteDaoHolder
    {
        private static InviteDao instance = new InviteDao(FCApplication.getInstance());
    }

    public static InviteDao getInstance()
    {
        return InviteDaoHolder.instance;
    }

    @Override
    public String setLogTag()
    {
        return TAG;
    }

    @Override
    public Dao<InviteBean, Integer> getDao()
    {
        if (mHelper == null || !mHelper.isOpen())
        {
            mHelper = null;
            mHelper = DbOpenHelper.getInstance(FCApplication.getInstance());
        }
        return mHelper.getDao(InviteBean.class);
    }

    /**
     * 存储新的邀请，前提是本地数据库中不存在相同未处理过的邀请信息
     *
     * @param inviteBean 邀请信息
     * @return 存储成功or失败
     */
    public boolean saveIfNotHandled(InviteBean inviteBean)
    {
        boolean saved = false;
        try
        {
            QueryBuilder<InviteBean, Integer> queryBuilder = getDao().queryBuilder();
            queryBuilder.where().eq(InviteDbConfig.OP_PHONE, inviteBean.getOpPhone())
                    .and().eq(InviteDbConfig.STATUS, InviteStatus.ORIGIN);
            InviteBean bean = getDao().queryForFirst(queryBuilder.prepare());
            if (bean == null && save(inviteBean) != -1)
                saved = true;
        } catch (SQLException e)
        {
            KLog.e(TAG + "InviteDao.saveIfNotHandled fail:" + e.toString());
        }
        return saved;
    }

    /**
     * 获取未读好友通知数量
     */
    public int getUnreadNotifyNum()
    {
        int num = 0;
        try
        {
            QueryBuilder<InviteBean, Integer> queryBuilder = getDao().queryBuilder();
            queryBuilder.where().eq(InviteDbConfig.READ, false);
            num = getDao().query(queryBuilder.prepare()).size();
        } catch (SQLException e)
        {
            KLog.e(TAG + "InviteDao.getUnreadNotifyNum fail:" + e.toString());
        }
        return num;
    }

    /**
     * 将所有未读通知设置为已读
     */
    public void setAllNotifyRead()
    {
        try
        {
            UpdateBuilder<InviteBean, Integer> updateBuilder = getDao().updateBuilder();
            updateBuilder.where().eq(InviteDbConfig.READ, false);
            updateBuilder.updateColumnValue(InviteDbConfig.READ, true);
            getDao().update(updateBuilder.prepare());
        } catch (SQLException e)
        {
            KLog.e(TAG + "InviteDao.setAllNotifyRead fail:" + e.toString());
        }
    }
}
