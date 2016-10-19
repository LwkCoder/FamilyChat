package com.lwk.familycontact.storage.db.video;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lib.base.log.KLog;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.storage.db.BaseDao;
import com.lwk.familycontact.storage.db.DbOpenHelper;

import java.sql.SQLException;

/**
 * Created by LWK
 * TODO 短视频远程下载数据表Dao
 * 2016/10/18
 */
public class ShortVideoDao extends BaseDao<ShortVideoBean, Integer>
{
    private final String TAG = this.getClass().getSimpleName();

    private ShortVideoDao(Context context)
    {
        super(context);
    }

    private static final class ShortVideoDaoHolder
    {
        private static ShortVideoDao instance = new ShortVideoDao(FCApplication.getInstance());
    }

    public static ShortVideoDao getInstance()
    {
        return ShortVideoDaoHolder.instance;
    }

    @Override
    public String setLogTag()
    {
        return TAG;
    }

    @Override
    public Dao<ShortVideoBean, Integer> getDao()
    {
        if (mHelper == null || !mHelper.isOpen())
        {
            mHelper = null;
            mHelper = DbOpenHelper.getInstance(FCApplication.getInstance());
        }
        return mHelper.getDao(ShortVideoBean.class);
    }

    /**
     * 根据消息id查询短视频远程下载数据
     *
     * @param msgId 消息id
     * @return 短视频远程下载数据对象
     */
    public ShortVideoBean queryDataByMsgId(String msgId)
    {
        ShortVideoBean videoBean = null;
        try
        {
            QueryBuilder<ShortVideoBean, Integer> queryBuilder = getDao().queryBuilder();
            queryBuilder.where().eq(ShortVideoDbConfig.MESSAGE_ID, msgId);
            videoBean = getDao().queryForFirst(queryBuilder.prepare());
        } catch (SQLException e)
        {
            KLog.e(TAG + " ShortVideoDao.queryDataByMsgId fail:" + e.toString());
        }
        return videoBean;
    }
}
