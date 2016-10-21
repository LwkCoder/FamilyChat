package com.lwk.familycontact.project.chat.presenter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.project.chat.model.HxShortVideoModel;
import com.lwk.familycontact.project.chat.utils.DownLoadVideoTask;
import com.lwk.familycontact.project.chat.view.HxShortVideoPlayView;
import com.lwk.familycontact.storage.db.video.ShortVideoBean;
import com.lwk.familycontact.storage.db.video.ShortVideoDao;

import java.io.File;

/**
 * Created by LWK
 * TODO 短视频播放界面Presenter
 * 2016/10/19
 */
public class HxShortVideoPlayPresenter
{
    private HxShortVideoPlayView mViewImpl;
    private HxShortVideoModel mModel;

    public HxShortVideoPlayPresenter(HxShortVideoPlayView viewImpl)
    {
        this.mViewImpl = viewImpl;
        this.mModel = new HxShortVideoModel();
    }

    /**
     * 初始化视频数据
     */
    public void initData(EMMessage message)
    {
        String msgId = message.getMsgId();
        EMVideoMessageBody messageBody = (EMVideoMessageBody) message.getBody();
        //检查文件是否存在本地文件
        String localUrl = messageBody.getLocalUrl();
        if (new File(localUrl).exists())
        {
            //存在就直接播放
            mViewImpl.startPlayVideo(localUrl);
            return;
        }

        //检查是否下载过
        ShortVideoBean videoBean = mModel.queryDataByMsgId(msgId);
        if (videoBean == null)
        {
            //没有下载过就直接下载
            downLoadRemoteUrlVideo(msgId, messageBody.getRemoteUrl());
        } else
        {
            //下载过就检查文件是否还在
            String downLoadUrl = videoBean.getLocal_url();
            if (new File(downLoadUrl).exists())
            {
                //下载的文件存在就直接播放
                mViewImpl.startPlayVideo(downLoadUrl);
            } else
            {
                //下载的文件不存在就重新下载
                ShortVideoDao.getInstance().delete(videoBean);
                downLoadRemoteUrlVideo(msgId, messageBody.getRemoteUrl());
            }
        }
    }

    //下载视频
    private void downLoadRemoteUrlVideo(final String msgId, String remoteUrl)
    {
        if (StringUtil.isEmpty(remoteUrl))
        {
            mViewImpl.showError(R.string.error_shortvideo_play, true);
            return;
        }

        mModel.downLoadVideo(remoteUrl, new DownLoadVideoTask.onDownLoadListener()
        {
            @Override
            public void onDownloadStart()
            {
                mViewImpl.showProgressView();
            }

            @Override
            public void onProgressUpdated(float progress)
            {
                mViewImpl.updateDownloadProgress(progress);
            }

            @Override
            public void onDownloadSuccess(String filePath)
            {
                mModel.saveDbData(msgId, filePath);
                mViewImpl.hideProgressView();
                mViewImpl.startPlayVideo(filePath);
            }

            @Override
            public void onDownloadFail()
            {
                mViewImpl.hideProgressView();
                mViewImpl.showError(R.string.error_shortvideo_download, true);
            }
        });
    }

    /**
     * 界面销毁时候取消下载任务
     */
    public void onDestory()
    {
        mModel.cancelTask();
    }
}
