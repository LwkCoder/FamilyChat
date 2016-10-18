package com.lwk.familycontact.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lib.base.log.KLog;
import com.lwk.familycontact.storage.db.invite.InviteBean;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.video.ShortVideoBean;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LWK
 * TODO 数据库OpenHelper
 * 2016/8/8
 */
public class DbOpenHelper extends OrmLiteSqliteOpenHelper
{
    private DbOpenHelper(Context context)
    {
        super(context, DbParams.getDbName(), null, DbParams.DB_VERSION);
    }

    private static DbOpenHelper mInstance;

    public static DbOpenHelper getInstance(Context context)
    {
        if (mInstance == null)
        {
            synchronized (DbOpenHelper.class)
            {
                if (mInstance == null)
                    mInstance = new DbOpenHelper(context);
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource)
    {
        try
        {
            TableUtils.createTable(connectionSource, UserBean.class);
            TableUtils.createTable(connectionSource, InviteBean.class);
            TableUtils.createTable(connectionSource, ShortVideoBean.class);
        } catch (SQLException e)
        {
            KLog.e("DbOpenHelper.onCreate() fail : " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {

    }

    //保存Dao的Map
    private Map<String, Dao> daos = new HashMap<>();

    //获取每个表的Dao（操作对象）
    public synchronized Dao getDao(Class clazz)
    {
        Dao dao = null;
        try
        {
            String className = clazz.getSimpleName();

            if (daos.containsKey(className))
            {
                dao = daos.get(className);
            }
            if (dao == null)
            {
                dao = super.getDao(clazz);
                daos.put(className, dao);
            }

        } catch (SQLException e)
        {
            KLog.e("Ormlite.getDao() Create Dao Fail : " + e.toString());
        }
        return dao;
    }

    @Override
    public void close()
    {
        super.close();
        //释放资源
        for (String key : daos.keySet())
        {
            Dao dao = daos.get(key);
            dao = null;
        }
        mInstance = null;
    }
}
