package com.lwk.familycontact.project.chat.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.lib.base.log.KLog;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.widget.shortvideo.ShortVideoRecorder;

import java.io.File;

public class HxShortVideoRecordActivity extends FCBaseActivity
{
    private ShortVideoRecorder mVideoRecorder;
    private boolean mRecordFinish;

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_hx_short_video_record;
    }

    @Override
    protected void initUI()
    {

        Button button = findView(R.id.btn_video_record);
        button.setOnTouchListener(onTouchListener);
        mVideoRecorder = findView(R.id.shortVideoRecorder);
        mVideoRecorder.setOnShortVideoRecordFinishListener(new ShortVideoRecorder.OnShortVideoRecordFinishListener()
        {
            @Override
            public void onShortVideoRecordFinish(File videoFile, int seconds)
            {
                mRecordFinish = true;
                KLog.e("视频地址:" + videoFile + "\n视频时间：" + seconds);
            }
        });
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mRecordFinish = false;
                    mVideoRecorder.startRecord();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!mRecordFinish)
                        mVideoRecorder.stopRecord();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (!mRecordFinish)
                        mVideoRecorder.stopRecord();
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onClick(int id, View v)
    {

    }

    @Override
    protected void onDestroy()
    {
        mVideoRecorder.relesaseResource();
        super.onDestroy();
    }
}
