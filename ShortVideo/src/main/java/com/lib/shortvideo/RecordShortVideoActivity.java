package com.lib.shortvideo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Toast;

import com.lib.shortvideo.videoview.recorder.ShortVideoRecorder;

import java.util.ArrayList;
import java.util.List;

/**
 * 短视频录制界面
 */
public class RecordShortVideoActivity extends Activity
{
    private static final String INTENT_KEY_MAX_DURATION = "max_duration";
    private static final String INTENT_KEY_MIN_DURATION = "min_duration";
    private static final String INTENT_KEY_HW_RATE = "hw_rate";
    private static final String INTENT_KEY_OUTPUT_FLODER = "out_put_floder";
    private static final String INTENT_KEY_RESULT_CODE = "resultCode";

    private final int REQUEST_CODE_PERMISSION = 111;
    public static final String INTENT_KEY_RESULT_PATH = "path";
    public static final String INTENT_KEY_RESULT_DURATION = "duration";

    //最大录制时间
    private long mMaxDuration;
    //最小录制时间
    private long mMinDuration;
    //高宽比
    private float mHWRate;
    //缓存文件夹
    private String mOutPutFolder;
    //返回码
    private int mResultCode;

    private ShortVideoRecorder mRecorder;


    /**
     * 跳转到该界面的功能方法
     *
     * @param activity     发起跳转的Activity
     * @param maxDuration  最大录制时间[单位：秒]
     * @param minDuration  最小录制时间[单位：秒]
     * @param hwRate       视频高宽比
     * @param outputFolder 缓存文件夹路径
     * @param requestCode  请求码
     * @param resultCode   结果码
     */
    public static void start(Activity activity, int maxDuration, int minDuration, float hwRate, String outputFolder, int requestCode, int resultCode)
    {
        Intent intent = new Intent(activity, RecordShortVideoActivity.class);
        intent.putExtra(INTENT_KEY_MAX_DURATION, maxDuration);
        intent.putExtra(INTENT_KEY_MIN_DURATION, minDuration);
        intent.putExtra(INTENT_KEY_HW_RATE, hwRate);
        intent.putExtra(INTENT_KEY_OUTPUT_FLODER, outputFolder);
        intent.putExtra(INTENT_KEY_RESULT_CODE, resultCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //保持屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_record_short_video);

        getIntentData();
        initUI();
    }

    private void getIntentData()
    {
        Intent intent = getIntent();
        mMaxDuration = intent.getIntExtra(INTENT_KEY_MAX_DURATION, 10) * 1000;
        mMinDuration = intent.getIntExtra(INTENT_KEY_MIN_DURATION, 1) * 1000;
        mHWRate = intent.getFloatExtra(INTENT_KEY_HW_RATE, 1.0f);
        mOutPutFolder = intent.getStringExtra(INTENT_KEY_OUTPUT_FLODER);
        mResultCode = intent.getIntExtra(INTENT_KEY_RESULT_CODE, 0);
    }

    private void initUI()
    {
        //sdk23以上需要检查权限:拍照和录音任何一个没有授权都不给使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            boolean isSdcardGranted = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean isCameraGranted = checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            boolean isAudioGranted = checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            if (!isSdcardGranted || !isCameraGranted || !isAudioGranted)
            {
                final List<String> permissionList = new ArrayList<>();
                if (!isSdcardGranted)
                    permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (!isCameraGranted)
                    permissionList.add(android.Manifest.permission.CAMERA);
                if (!isAudioGranted)
                    permissionList.add(android.Manifest.permission.RECORD_AUDIO);
                //对权限做出解释
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)
                        || shouldShowRequestPermissionRationale(android.Manifest.permission.RECORD_AUDIO))
                {
                    new AlertDialog.Builder(this).setCancelable(false)
                            .setTitle(R.string.dialog_shortvideo_permission_title)
                            .setMessage(R.string.dialog_shortvideo_permission_message)
                            .setPositiveButton(R.string.dialog_shortvideo_positive, new DialogInterface.OnClickListener()
                            {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    requestPermissions(permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSION);
                                }
                            }).create().show();
                } else
                {
                    requestPermissions(permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSION);
                }
            } else
            {
                //有权限直接初始化
                inflateRecord();
            }
        } else
        {
            //sdk23以下直接初始化
            inflateRecord();
        }
    }

    private void inflateRecord()
    {
        ViewStub vs = (ViewStub) findViewById(R.id.vs_shortvideo_record);
        vs.inflate();
        mRecorder = (ShortVideoRecorder) findViewById(R.id.svr_recorder);
        mRecorder.setMaxDuration(mMaxDuration);
        mRecorder.setMinDuration(mMinDuration);
        mRecorder.setCacheFolder(mOutPutFolder);
        mRecorder.setHWRate(mHWRate);
        mRecorder.setOnRecordListener(new ShortVideoRecorder.OnRecordListener()
        {
            @Override
            public void onRecordFinish(String filePath, long duration)
            {
                Intent intent = new Intent();
                intent.putExtra(INTENT_KEY_RESULT_PATH, filePath);
                intent.putExtra(INTENT_KEY_RESULT_DURATION, duration);
                setResult(mResultCode, intent);
                finish();
            }

            @Override
            public void onRecordError(int errResId)
            {
                Toast.makeText(RecordShortVideoActivity.this, errResId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_PERMISSION:
                //检查权限结果，任何一个权限没有就不给使用
                boolean allGrant = true;
                for (int i = 0; i < grantResults.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                        allGrant = false;
                }
                if (allGrant)
                {
                    inflateRecord();
                } else
                {
                    Toast.makeText(this, R.string.warning_shortvideo_permission_denied, Toast.LENGTH_LONG).show();
                    if (!shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            !shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) ||
                            !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO))
                        //处理NerverAsk
                        new AlertDialog.Builder(this).setCancelable(false)
                                .setTitle(R.string.dialog_shortvideo_permission_title)
                                .setMessage(R.string.dialog_shortvideo_permission_nerver_ask_message)
                                .setNegativeButton(R.string.dialog_shortvideo_negetive, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                                .setPositiveButton(R.string.dialog_shortvideo_nerver_ask_positive, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).create().show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
