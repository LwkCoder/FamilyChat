package com.lwk.familycontact.project.chat.view;

/**
 * Created by LWK
 * TODO 短视频播放界面接口
 * 2016/10/19
 */
public interface HxShortVideoPlayView
{

    void showProgressView();

    void updateDownloadProgress(float progress);

    void hideProgressView();

    void startPlayVideo(String path);

    void showError(int errMsgResId, boolean needFinish);
}
