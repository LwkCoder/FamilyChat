package com.lwk.familycontact.project.chat.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.lib.base.utils.ScreenUtils;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.chat.presenter.HxShortVideoPlayPresenter;
import com.timqi.sectorprogressview.ColorfulRingProgressView;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

/**
 * 短视频播放界面
 */
public class HxShortVideoPlayActivity extends FCBaseActivity implements HxShortVideoPlayView
{
    private static final String INTENT_KEY_MESSAGE = "message";
    private EMMessage mMessage;
    private HxShortVideoPlayPresenter mPresenter;
    private View mViewDownload;
    private ColorfulRingProgressView mPgvDownload;
    private TextView mTvDownload;
    private ScalableVideoView mVideoView;

    /**
     * 跳转到短视频播放界面的公共方法
     *
     * @param activity 发起跳转的公共方法
     * @param message  短视频消息
     */
    public static void start(Activity activity, EMMessage message)
    {
        Intent intent = new Intent(activity, HxShortVideoPlayActivity.class);
        intent.putExtra(INTENT_KEY_MESSAGE, message);
        activity.startActivity(intent);
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        ScreenUtils.changeStatusBarColor(this, Color.BLACK);
        mMessage = getIntent().getParcelableExtra(INTENT_KEY_MESSAGE);
        if (mMessage == null)
        {
            showError(R.string.error_shortvideo_play, true);
            return;
        }
    }

    @Override
    protected int setContentViewId()
    {
        mPresenter = new HxShortVideoPlayPresenter(this);
        return R.layout.activity_hx_short_video_play;
    }

    @Override
    protected void initUI()
    {
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.initData(mMessage);
    }

    @Override
    public void showProgressView()
    {
        ViewStub viewStub = findView(R.id.vs_shortvideo_download_progress);
        mViewDownload = viewStub.inflate();
        mPgvDownload = findView(R.id.cpgv_download_progress);
        mTvDownload = findView(R.id.tv_download_progress);
    }

    @Override
    public void updateDownloadProgress(float progress)
    {
        if (mPgvDownload != null)
            mPgvDownload.setPercent(progress);
        if (mTvDownload != null)
            mTvDownload.setText(progress + "%");
    }

    @Override
    public void hideProgressView()
    {
        if (mViewDownload != null)
            mViewDownload.setVisibility(View.GONE);
    }

    @Override
    public void startPlayVideo(String path)
    {
        //这里之所以在需要播放视频的时候才能Inflate
        //是因为：如果直接初始化后，视频却需要下载，下载完后再播放，此时VideoView的TextTureView很可能已经被回收了导致黑屏
        ViewStub viewStub = findView(R.id.vs_shortvideo_videoview);
        viewStub.inflate();
        mVideoView = findView(R.id.svv_shortvideo_play);
        try
        {
            mVideoView.setDataSource(path);
            mVideoView.setVolume(1f, 1f);
            mVideoView.setLooping(true);
            mVideoView.prepareAsync(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mVideoView.start();
                }
            });
        } catch (IOException e)
        {
            showError(R.string.error_shortvideo_play, true);
        }
    }

    @Override
    public void showError(int errMsgResId, boolean needFinish)
    {
        showLongToast(errMsgResId);
        if (needFinish)
            finish();
    }

    @Override
    protected void onClick(int id, View v)
    {

    }

    @Override
    protected void onDestroy()
    {
        mPresenter.onDestory();
        super.onDestroy();
    }
}
