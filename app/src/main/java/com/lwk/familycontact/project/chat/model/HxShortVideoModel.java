package com.lwk.familycontact.project.chat.model;

import com.lwk.familycontact.project.chat.utils.DownLoadVideoTask;
import com.lwk.familycontact.project.common.FCCache;
import com.lwk.familycontact.storage.db.video.ShortVideoBean;
import com.lwk.familycontact.storage.db.video.ShortVideoDao;

/**
 * Created by LWK
 * TODO 短视频播放界面数据层
 * 2016/10/19
 */
public class HxShortVideoModel
{
    private DownLoadVideoTask mDownLoadVideoTask;

    /**
     * 根据msgid查询数据库
     */
    public ShortVideoBean queryDataByMsgId(String msgId)
    {
        return ShortVideoDao.getInstance().queryDataByMsgId(msgId);
    }

    /**
     * 下载短视频
     *
     * @param url      远程地址
     * @param listener 监听
     */
    public void downLoadVideo(String url, DownLoadVideoTask.onDownLoadListener listener)
    {
        mDownLoadVideoTask = new DownLoadVideoTask(url, createSavaPath(), listener);
        mDownLoadVideoTask.execute();
    }

    //创建保存视频的绝对路径
    private String createSavaPath()
    {
        return new StringBuffer().append(FCCache.getInstance().getVideoCachePath())
                .append("VID_").append(String.valueOf(System.currentTimeMillis()))
                .append(".mp4").toString();
    }

    //保存数据库数据
    public void saveDbData(String msgId, String filePath)
    {
        ShortVideoDao.getInstance().saveOrUpdate(new ShortVideoBean(msgId, filePath));
    }

    /**
     * 取消下载
     */
    public void cancelTask()
    {
        if (mDownLoadVideoTask != null)
            mDownLoadVideoTask.cancel(true);
        mDownLoadVideoTask = null;
    }
}
