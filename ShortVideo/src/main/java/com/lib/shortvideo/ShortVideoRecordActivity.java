package com.lib.shortvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.shortvideo.utils.FileUtil;
import com.lib.shortvideo.videoview.camera.CameraHelper;
import com.lib.shortvideo.videoview.recorder.WXLikeVideoRecorder;
import com.lib.shortvideo.videoview.views.CameraPreviewView;
import com.lib.shortvideo.videoview.views.RecordProgressBar;

import java.util.List;

/**
 * 短视频录制界面
 */
public class ShortVideoRecordActivity extends Activity implements View.OnClickListener, View.OnTouchListener, RecordProgressBar.onTimeUpdateListener
{
    private final static String TAG = "ShortVideoRecord";
    private static final String INTENT_KEY_OUTPUT_WIDTH = "out_put_width";
    private static final String INTENT_KEY_OUTPUT_HEIGHT = "out_put_height";
    private static final String INTENT_KEY_OUTPUT_FLODER = "out_put_floder";
    private static final String INTENT_KEY_MAX_DURATION = "max_duration";
    private static final String INTENT_KEY_MIN_DURATION = "min_duration";
    private static final String INTENT_KEY_RESULT_CODE = "resultCode";
    public static final String INTENT_KEY_RESULT_PATH = "ShortVideoPath";
    public static final String INTENT_KEY_RESULT_TIME = "ShortVideoTime";

    //输出宽度：默认540
    private int mOutPutWidth = 540;
    //输出高度：默认540
    private int mOutPutHeight = 540;
    //输出路径：默认sd卡
    private String mOutPutFloder = Environment.getExternalStorageDirectory().getAbsolutePath();
    //最小录制时间：默认1000ms
    private long mMinDuration;
    //最大录制时间：默认6000ms
    private long mMaxDuration;

    private int mCameraId;
    private Camera mCamera;
    private WXLikeVideoRecorder mRecorder;
    private int CANCEL_RECORD_OFFSET;
    private float mDownX, mDownY;
    private boolean isCancelRecord = false;
    private RecordProgressBar mPgbRecording;
    private ImageView mImgController;
    private Button mBtnLight;
    private TextView mTvCancelHint;
    private boolean isFlashLightOn = false;
    private int mResultCode;

    /**
     * 跳转到录制短视频界面的公共方法
     *
     * @param activity     发起跳转的Activity
     * @param requestCode  请求码
     * @param resultCode   结果码
     * @param outputWidth  输出视频的宽度
     * @param outputHeight 输出视频的高度
     * @param outputFolder 输出视频的路径
     * @param minDuration  最小录制时间，单位:秒
     * @param maxDuration  最大录制时间，单位:秒
     */
    public static void start(Activity activity, int requestCode, int resultCode,
                             int outputWidth, int outputHeight, String outputFolder,
                             int minDuration, int maxDuration)
    {
        Intent intent = new Intent(activity, ShortVideoRecordActivity.class);
        intent.putExtra(INTENT_KEY_OUTPUT_WIDTH, outputWidth);
        intent.putExtra(INTENT_KEY_OUTPUT_HEIGHT, outputHeight);
        intent.putExtra(INTENT_KEY_OUTPUT_FLODER, outputFolder);
        intent.putExtra(INTENT_KEY_MIN_DURATION, minDuration);
        intent.putExtra(INTENT_KEY_MAX_DURATION, maxDuration);
        intent.putExtra(INTENT_KEY_RESULT_CODE, resultCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //保持屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_short_video_record);
        getIntentData();
        initUI();
    }

    //获取Intent数据
    private void getIntentData()
    {
        Intent intent = getIntent();
        mOutPutWidth = intent.getIntExtra(INTENT_KEY_OUTPUT_WIDTH, 540);
        mOutPutHeight = intent.getIntExtra(INTENT_KEY_OUTPUT_HEIGHT, 540);
        mOutPutFloder = intent.getStringExtra(INTENT_KEY_OUTPUT_FLODER);
        mMaxDuration = intent.getIntExtra(INTENT_KEY_MAX_DURATION, 6) * 1000;//转为毫秒
        mMinDuration = intent.getIntExtra(INTENT_KEY_MIN_DURATION, 1) * 1000;//转为毫秒
        mResultCode = intent.getIntExtra(INTENT_KEY_RESULT_CODE, 0);
        CANCEL_RECORD_OFFSET = -getResources().getDimensionPixelSize(R.dimen.short_video_cancel_distance);
    }

    private void initUI()
    {
        String hintEx = getString(R.string.tv_shortvideo_record_hint);
        String hint = hintEx.replaceFirst("%%1", String.valueOf(mMinDuration / 1000)).replaceFirst("%%2", String.valueOf(mMaxDuration / 1000));
        ((TextView) findViewById(R.id.tv_short_video_record_hint)).setText(hint);
        mPgbRecording = (RecordProgressBar) findViewById(R.id.rpgb_short_video_record);
        mPgbRecording.setRunningTime(mMaxDuration);
        mImgController = (ImageView) findViewById(R.id.img_short_video_record_controller);
        mBtnLight = (Button) findViewById(R.id.btn_short_video_record_light);
        mTvCancelHint = (TextView) findViewById(R.id.tv_shortvideo_cancel_hint);

        mPgbRecording.setOnTimeUpdateListener(this);
        mImgController.setOnTouchListener(this);
        mBtnLight.setOnClickListener(this);

        //TODO 进行权限检查
        initCameraAndRecorder();
    }

    // 初始化摄像头和录像机
    private void initCameraAndRecorder()
    {
        mCameraId = CameraHelper.getDefaultCameraID();
        mCamera = CameraHelper.getCameraInstance(mCameraId);
        if (null == mCamera)
        {
            Toast.makeText(this, R.string.error_short_video_record_open_camera, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mRecorder = new WXLikeVideoRecorder(this, mOutPutFloder);
        mRecorder.setOutputSize(mOutPutWidth, mOutPutHeight);
        mRecorder.setMaxRecordTime(mMaxDuration);
        CameraPreviewView preview = (CameraPreviewView) findViewById(R.id.cpv_shortvideo_record);
        preview.setCamera(mCamera, mCameraId);
        mRecorder.setCameraPreviewView(preview);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.btn_short_video_record_light)
            switchFlashLight();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mRecorder != null)
        {
            // 录制时退出，直接舍弃视频
            if (mRecorder.isRecording())
            {
                // 页面不可见就要停止录制
                mRecorder.stopRecording();
                Log.w(TAG, "短视频录制过程中界面关闭，删除视频文件");
                FileUtil.deleteFile(mRecorder.getFilePath());
                mRecorder.resetParams();
            }
        }
        releaseCamera();              // release the camera immediately on pause event
        finish();
    }

    private void releaseCamera()
    {
        if (mCamera != null)
        {
            mCamera.setPreviewCallback(null);
            // 释放前先停止预览
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * 开始录制
     */
    private synchronized void startRecord()
    {
        if (mRecorder.isRecording())
            return;

        if (canRecord())
        {
            // 录制视频
            if (mRecorder.startRecording())
            {
                mPgbRecording.start();
                mBtnLight.setEnabled(false);
                mBtnLight.setBackgroundResource(R.drawable.ic_shortvideo_light_disable);
                mTvCancelHint.setVisibility(View.VISIBLE);
                setCancelHintNormal();
            } else
            {
                Toast.makeText(this, R.string.error_short_video_record_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 判断是否可以录制视频
     */
    private boolean canRecord()
    {
        if (!FileUtil.isSDCardMounted())
        {
            Toast.makeText(this, R.string.error_short_video_record_sdcard, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 停止录制
     */
    private synchronized void stopRecord()
    {
        mBtnLight.setEnabled(true);
        mBtnLight.setBackgroundResource(R.drawable.ic_shortvideo_light_normal);
        mTvCancelHint.setVisibility(View.INVISIBLE);
        mRecorder.stopRecording();
        mPgbRecording.stop();
        String videoPath = mRecorder.getFilePath();
        // 没有录制视频
        if (null == videoPath)
            return;

        long time = mRecorder.getStopTime() - mRecorder.getStartTime();
        boolean isTooShort = time <= mMinDuration;
        Log.i(TAG, "短视频录制完成：路径=" + videoPath + ",时长=" + time + "ms");
        // 若取消录制或视频太短，则删除文件
        if (isCancelRecord || isTooShort)
        {
            Log.w(TAG, "短视频录制完成后需要删除：" + videoPath);
            FileUtil.deleteFile(videoPath);
            if (isTooShort)
                Toast.makeText(this, R.string.warning_shortvideo_time_too_short, Toast.LENGTH_SHORT).show();
        } else
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_KEY_RESULT_PATH, videoPath);
            intent.putExtra(INTENT_KEY_RESULT_TIME, time);
            setResult(mResultCode, intent);
            finish();
        }
        mRecorder.resetParams();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mImgController.setImageResource(R.drawable.img_shortvideo_controller_pressed);
                isCancelRecord = false;
                mDownX = event.getX();
                mDownY = event.getY();
                startRecord();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRecorder.isRecording())
                    return false;

                float y = event.getY();
                if (y - mDownY < CANCEL_RECORD_OFFSET)
                {
                    if (!isCancelRecord)
                    {
                        isCancelRecord = true;
                        mPgbRecording.setState(RecordProgressBar.STATE_CANCEL);
                        setCancelHintRelease();
                    }
                } else
                {
                    isCancelRecord = false;
                    mPgbRecording.setState(RecordProgressBar.STATE_RUNNING);
                    setCancelHintNormal();
                }
                break;
            case MotionEvent.ACTION_UP:
                mImgController.setImageResource(R.drawable.img_shortvideo_controller_normal);
                stopRecord();
                break;
            case MotionEvent.ACTION_CANCEL:
                mImgController.setImageResource(R.drawable.img_shortvideo_controller_normal);
                isCancelRecord = true;
                stopRecord();
                break;
        }
        return true;
    }

    @Override
    public void onTimeUpdate(long time)
    {
        if (time >= mMaxDuration)
        {
            Log.w(TAG, "短视频录制超时");
            stopRecord();
        }
    }

    /**
     * 切换闪光灯
     */
    private synchronized void switchFlashLight()
    {
        if (mCamera != null)
        {
            if (isFlashLightOn)
                closeFlashLight();
            else
                openFlashLight();
        }
    }

    /**
     * 开启闪光灯
     */
    private void openFlashLight()
    {
        if (mCamera != null && !isFlashLightOn)
        {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters == null)
                return;
            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes == null)
                return;
            String flashMode = parameters.getFlashMode();
            if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode))
            {
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH))
                {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                }
            }
            isFlashLightOn = true;
            mBtnLight.setBackgroundResource(R.drawable.ic_shortvideo_light_pressed);
        }
    }

    /**
     * 关闭闪光灯
     */
    private void closeFlashLight()
    {
        if (mCamera != null && isFlashLightOn)
        {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters == null)
                return;
            List<String> flashModes = parameters.getSupportedFlashModes();
            String flashMode = parameters.getFlashMode();
            if (flashModes == null)
                return;
            if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode))
            {
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF))
                {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(parameters);
                }
            }
            isFlashLightOn = false;
            mBtnLight.setBackgroundResource(R.drawable.ic_shortvideo_light_normal);
        }
    }

    //设置取消正常提醒
    private void setCancelHintNormal()
    {
        if (mTvCancelHint != null)
        {
            mTvCancelHint.setText(R.string.tv_shortvideo_cancel_hint_normal);
            mTvCancelHint.setTextColor(Color.WHITE);
        }
    }

    //设置取消释放手指提醒
    private void setCancelHintRelease()
    {
        if (mTvCancelHint != null)
        {
            mTvCancelHint.setText(R.string.tv_shortvideo_cancel_hint_release);
            mTvCancelHint.setTextColor(Color.RED);
        }
    }
}
