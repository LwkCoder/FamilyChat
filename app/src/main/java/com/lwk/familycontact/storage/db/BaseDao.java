package com.lwk.familycontact.storage.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;
import com.lib.base.log.KLog;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by LWK
 * TODO Dao基类
 * 2016/8/8
 */
public abstract class BaseDao<T, ID>
{
    protected String LOG_TAG;
    protected DbOpenHelper mHelper;
    protected Context mContext;

    public BaseDao(Context context)
    {
        if (context == null)
        {
            //如果为空，则扔出非法参数异常
            throw new IllegalArgumentException("The params of CONTEXT for ormlite BaseDao constructor can't be null!");
        }
        //避免产生内存泄露，使用getApplicationContext()
        mContext = context.getApplicationContext();
        //获得单例helper
        mHelper = DbOpenHelper.getInstance(mContext);
        //设置日志LogTag
        LOG_TAG = setLogTag();
    }

    /**
     * 设置日志Tag
     */
    public abstract String setLogTag();

    /**
     * 抽象方法，重写提供Dao,在子类里提供了简单的泛型实现，传递实体类Class即可
     *
     * @return Dao类
     */
    public abstract Dao<T, ID> getDao();

    /**
     * 增，带事务操作
     *
     * @param t 泛型实体类
     * @return 影响的行数
     */
    public int save(T t)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int save = dao.create(t);
            dao.commit(databaseConnection);
            return save;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.save() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.save.rollback() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.save.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 增，带事务操作
     *
     * @param t 泛型实体类集合
     * @return 影响的行数
     */
    public int saveList(List<T> t)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            for (T item : t)
            {
                dao.create(item);
            }
            dao.commit(databaseConnection);
            return t.size();
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.saveList() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.saveList.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.saveList.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 增或更新，带事务操作
     *
     * @param t 泛型实体类
     * @return Dao.CreateOrUpdateStatus
     */
    public Dao.CreateOrUpdateStatus saveOrUpdate(T t)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            Dao.CreateOrUpdateStatus orUpdate = dao.createOrUpdate(t);
            dao.commit(databaseConnection);
            return orUpdate;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.saveOrUpdate() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.saveOrUpdate.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.saveOrUpdate.endThreadConnection() fail:" + e.toString());
            }
        }
        return null;
    }

    /**
     * 增或更新，带事务操作
     *
     * @param list 泛型实体类集合
     * @return 影响的行数
     */
    public int saveOrUpdateList(List<T> list)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            for (T t : list)
            {
                dao.createOrUpdate(t);
            }
            dao.commit(databaseConnection);
            return list.size();
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.saveOrUpdateList() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.saveOrUpdateList.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.saveOrUpdateList.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 增（如果不存在） 带事物操作
     *
     * @param t 泛型实体类
     * @return 结果数据
     */
    public T saveIfNotExist(T t)
    {
        T resultData = null;
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            resultData = dao.createIfNotExists(t);
            dao.commit(databaseConnection);
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.saveOrUpdate() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.saveOrUpdate.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.saveOrUpdate.endThreadConnection() fail:" + e.toString());
            }
        }
        return resultData;
    }

    /**
     * 删，带事务操作
     *
     * @param t 泛型实体类
     * @return 影响的行数
     */
    public int delete(T t)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int delete = dao.delete(t);
            dao.commit(databaseConnection);
            return delete;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.delete() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.delete.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.delete.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 删，带事务操作
     *
     * @param list 泛型实体类集合
     * @return 影响的行数
     */
    public int deleteList(List<T> list)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int delete = dao.delete(list);
            dao.commit(databaseConnection);
            return delete;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.deleteList() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.deleteList.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.deleteList.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 删，带事务操作
     *
     * @param columnNames  列名数组
     * @param columnValues 列名对应值数组
     * @return 影响的行数
     */
    public int deleteByColumn(String[] columnNames, Object[] columnValues)
    {
        List<T> list = queryByColumns(columnNames, columnValues);
        if (null != list && !list.isEmpty())
        {
            Dao<T, ID> dao = getDao();
            DatabaseConnection databaseConnection = null;
            try
            {
                databaseConnection = dao.startThreadConnection();
                dao.setAutoCommit(databaseConnection, false);
                int delete = dao.delete(list);
                dao.commit(databaseConnection);
                return delete;
            } catch (Exception e)
            {
                KLog.e(LOG_TAG + " Dao.deleteByColumn() fail:" + e.toString());
                try
                {
                    dao.rollBack(databaseConnection);
                } catch (java.sql.SQLException e1)
                {
                    KLog.e(LOG_TAG + " Dao.deleteByColumn.rollBack() fail:" + e.toString());
                }
            } finally
            {
                try
                {
                    dao.endThreadConnection(databaseConnection);
                } catch (java.sql.SQLException e)
                {
                    KLog.e(LOG_TAG + " Dao.deleteByColumn.endThreadConnection() fail:" + e.toString());
                }
            }
        }
        return 0;
    }

    /**
     * 删，带事务操作
     *
     * @param id id值
     * @return 影响的行数
     */
    public int deleteById(ID id)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int delete = dao.deleteById(id);
            dao.commit(databaseConnection);
            return delete;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.deleteById() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.deleteById.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.deleteById.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 删，带事务操作
     *
     * @param ids id集合
     * @return 影响的行数
     */
    public int deleteByIds(List<ID> ids)
    {
        Dao<T, ID> dao = getDao();

        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int delete = dao.deleteIds(ids);
            dao.commit(databaseConnection);
            return delete;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.deleteByIds() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.deleteByIds.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.deleteByIds.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 删，带事务操作
     *
     * @param builder DeleteBuilder<T,ID>类
     * @return 影响的行数
     */
    public int deleteByBuilder(DeleteBuilder<T, ID> builder)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int delete = dao.delete(builder.prepare());
            dao.commit(databaseConnection);
            return delete;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.deleteByBuilder() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.deleteByBuilder.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.deleteByBuilder.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 改，带事务操作
     *
     * @param t 泛型实体类
     * @return 影响的行数
     */
    public int update(T t)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int update = dao.update(t);
            dao.commit(databaseConnection);
            return update;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.update() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.update.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.update.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 改，带事务操作
     *
     * @param builder UpdateBuilder对象
     * @return 影响的行数
     */
    public int updateByBuilder(UpdateBuilder<T, ID> builder)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            int update = dao.update(builder.prepare());
            dao.commit(databaseConnection);
            return update;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.updateByBuilder() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.updateByBuilder.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.updateByBuilder.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 查，带事务操作
     *
     * @return 查询结果集合
     */
    public List<T> queryAll()
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            List<T> query = dao.queryForAll();
            dao.commit(databaseConnection);
            return query;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.queryAll() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.queryAll.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.queryAll.endThreadConnection() fail:" + e.toString());
            }
        }
        return null;
    }

    /**
     * 查，带事务操作
     *
     * @param builder QueryBuilder对象
     * @return 查询结果集合
     */
    public List<T> queryByBuilder(QueryBuilder<T, ID> builder)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            List<T> query = dao.query(builder.prepare());
            dao.commit(databaseConnection);
            return query;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.queryByBuilder() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.queryByBuilder.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.queryByBuilder.endThreadConnection() fail:" + e.toString());
            }
        }
        return null;
    }

    /**
     * 查，带事务操作
     *
     * @param columnName  列名
     * @param columnValue 列名对应值
     * @return 查询结果集合
     */
    public List<T> queryByColumn(String columnName, String columnValue)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            QueryBuilder<T, ID> queryBuilder = getDao().queryBuilder();
            queryBuilder.where().eq(columnName, columnValue);
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            List<T> query = dao.query(preparedQuery);
            //also can use dao.queryForEq(columnName,columnValue);
            dao.commit(databaseConnection);
            return query;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.queryByColumn() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.queryByColumn.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.queryByColumn.endThreadConnection() fail:" + e.toString());
            }
        }
        return null;
    }

    /**
     * 查，带事务操作
     *
     * @param columnNames
     * @param columnValues
     * @return 查询结果集合
     */
    public List<T> queryByColumns(String[] columnNames, Object[] columnValues)
    {
        if (columnNames.length != columnNames.length)
        {
            throw new InvalidParameterException("params size is not equal");
        }

        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            QueryBuilder<T, ID> queryBuilder = getDao().queryBuilder();
            Where<T, ID> wheres = queryBuilder.where();
            for (int i = 0; i < columnNames.length; i++)
            {
                if (i == 0)
                    wheres.eq(columnNames[i], columnValues[i]);
                else
                    wheres.and().eq(columnNames[i], columnValues[i]);
            }
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            List<T> query = dao.query(preparedQuery);
            dao.commit(databaseConnection);
            return query;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.queryByColumns() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.queryByColumns.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.queryByColumns.endThreadConnection() fail:" + e.toString());
            }
        }
        return null;
    }

    /**
     * 查，带事务操作
     *
     * @param map 列名与值组成的map
     * @return 查询结果集合
     */
    public List<T> queryByColumnsMap(Map<String, Object> map)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            QueryBuilder<T, ID> queryBuilder = getDao().queryBuilder();
            if (!map.isEmpty())
            {
                Where<T, ID> wheres = queryBuilder.where();
                Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
                String key = null;
                Object value = null;
                for (int i = 0; iterator.hasNext(); i++)
                {
                    Map.Entry<String, Object> next = iterator.next();
                    key = next.getKey();
                    value = next.getValue();
                    if (i == 0)
                    {
                        wheres.eq(key, value);
                    } else
                    {
                        wheres.and().eq(key, value);
                    }
                }
            }
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            List<T> query = dao.query(preparedQuery);
            dao.commit(databaseConnection);
            return query;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.queryByColumnsMap() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.queryByColumnsMap.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.queryByColumnsMap.endThreadConnection() fail:" + e.toString());
            }
        }
        return null;
    }

    /**
     * 查，带事务操作
     *
     * @param id id值
     * @return 查询结果集合
     */
    public T queryById(ID id)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            T t = dao.queryForId(id);
            dao.commit(databaseConnection);
            return t;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.queryById() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.queryById.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.queryById.endThreadConnection() fail:" + e.toString());
            }
        }
        return null;
    }

    /**
     * 判断表是否存在
     *
     * @return 表是否存在
     */
    public boolean isTableExists()
    {
        boolean exist = false;
        try
        {
            exist = getDao().isTableExists();
        } catch (java.sql.SQLException e)
        {
            KLog.e(LOG_TAG + " Dao.isTableExists() fail:" + e.toString());
        }
        return exist;
    }


    /**
     * 获得记录数
     *
     * @return 记录数
     */
    public long count()
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            long count = dao.countOf();
            dao.commit(databaseConnection);
            return count;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.count() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.count.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.count.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }

    /**
     * 获得记录数
     *
     * @param builder QueryBuilder类
     * @return 记录数
     */
    public long countByBuilder(QueryBuilder<T, ID> builder)
    {
        Dao<T, ID> dao = getDao();
        DatabaseConnection databaseConnection = null;
        try
        {
            databaseConnection = dao.startThreadConnection();
            dao.setAutoCommit(databaseConnection, false);
            long count = dao.countOf(builder.prepare());
            dao.commit(databaseConnection);
            return count;
        } catch (Exception e)
        {
            KLog.e(LOG_TAG + " Dao.countByBuilder() fail:" + e.toString());
            try
            {
                dao.rollBack(databaseConnection);
            } catch (java.sql.SQLException e1)
            {
                KLog.e(LOG_TAG + " Dao.countByBuilder.rollBack() fail:" + e.toString());
            }
        } finally
        {
            try
            {
                dao.endThreadConnection(databaseConnection);
            } catch (java.sql.SQLException e)
            {
                KLog.e(LOG_TAG + " Dao.countByBuilder.endThreadConnection() fail:" + e.toString());
            }
        }
        return 0;
    }
}