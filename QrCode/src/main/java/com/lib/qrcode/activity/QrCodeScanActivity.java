package com.lib.qrcode.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.lib.qrcode.QrCodeHelper;
import com.lib.qrcode.R;
import com.lib.qrcode.camera.CameraManager;
import com.lib.qrcode.decode.DecodeThread;
import com.lib.qrcode.utils.BeepManager;
import com.lib.qrcode.utils.InactivityTimer;
import com.lib.qrcode.utils.OtherUtils;
import com.lib.qrcode.utils.QrCodeScanActivityHandler;

/**
 * 二维码扫描界面
 */
public class QrCodeScanActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener
{
    private static final int REQUEST_CODE_CAMERA = 110;
    private static final String TAG = QrCodeScanActivity.class.getSimpleName();
    private CameraManager mCameraManager;
    private QrCodeScanActivityHandler mQrHandler;
    private InactivityTimer mInactivityTimer;
    private BeepManager mBeepManager;

    private SurfaceView mScanPreview = null;
    private RelativeLayout mScanContainer;
    private RelativeLayout mScanCropView;
    private ImageView mScanLine;
    private ImageView mImgLight;
    private Rect mCropRect = null;
    private boolean mIsHasSurface = false;
    private boolean mIsLightOn = false;
    private boolean mHasCheckPermission;

    public Handler getQrHandler()
    {
        return mQrHandler;
    }

    public CameraManager getCameraManager()
    {
        return mCameraManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //保持屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //透明状态栏
        OtherUtils.changStatusbarTransparent(this);
        setContentView(R.layout.activity_qrcode_scan);
        initUI();
    }

    protected void initUI()
    {
        //全屏设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            View statusBar = findViewById(R.id.view_qrcode_actionbar_status);
            statusBar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
            layoutParams.height = OtherUtils.getStatusBarHeight(this);
            statusBar.setLayoutParams(layoutParams);
        }

        //闪光灯
        mImgLight = (ImageView) findViewById(R.id.img_qrcode_light);

        mScanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        mScanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        mScanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        mScanLine = (ImageView) findViewById(R.id.capture_scan_line);
        //设置阴影
        View shadowTop = findViewById(R.id.capture_mask_top);
        View shadowBottom = findViewById(R.id.capture_mask_bottom);
        View shadowLeft = findViewById(R.id.capture_mask_left);
        View shadowRight = findViewById(R.id.capture_mask_right);
        shadowTop.setAlpha(0.5f);
        shadowBottom.setAlpha(0.5f);
        shadowLeft.setAlpha(0.5f);
        shadowRight.setAlpha(0.5f);

        mInactivityTimer = new InactivityTimer(this);
        mBeepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation
                .RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.85f);
        animation.setDuration(2500);
        animation.setRepeatCount(-1);
        animation.setInterpolator(new AccelerateDecelerateInterpolator(QrCodeScanActivity.this, null));
        animation.setRepeatMode(Animation.RESTART);
        mScanLine.startAnimation(animation);

        findViewById(R.id.ll_qrcode_actionbar_left_back).setOnClickListener(this);
        mImgLight.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        mCameraManager = new CameraManager(getApplication());

        mQrHandler = null;

        if (mIsHasSurface)
        {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(mScanPreview.getHolder());
        } else
        {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            mScanPreview.getHolder().addCallback(this);
        }

        mInactivityTimer.onResume();
    }

    @Override
    protected void onPause()
    {
        if (mQrHandler != null)
        {
            mQrHandler.quitSynchronously();
            mQrHandler = null;
        }
        mInactivityTimer.onPause();
        mBeepManager.close();
        mCameraManager.closeDriver();
        if (!mIsHasSurface)
        {
            mScanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (holder == null)
        {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!mIsHasSurface)
        {
            mIsHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mIsHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle)
    {
        mInactivityTimer.onActivity();
        mBeepManager.playBeepSoundAndVibrate();

        Intent resultIntent = new Intent();
        bundle.putInt(QrCodeHelper.QRCODE_RESULT_WIDTH, mCropRect.width());
        bundle.putInt(QrCodeHelper.QRCODE_RESULT_HEIGHT, mCropRect.height());
        bundle.putString(QrCodeHelper.QRCODE_RESULT_CONTENT, rawResult.getText());
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        QrCodeScanActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder)
    {
        Log.e("ll", "检查二维码权限");

        //sdk23以上需要检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !mHasCheckPermission)
        {
            int checkResult = checkSelfPermission(Manifest.permission.CAMERA);
            //没有权限时
            if (checkResult != PackageManager.PERMISSION_GRANTED)
            {
                //对权限做出解释
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                {
                    new AlertDialog.Builder(this).setCancelable(false)
                            .setTitle(R.string.dialog_qrcode_permission_title)
                            .setMessage(R.string.dialog_qrcode_permission_camera_message)
                            .setPositiveButton(R.string.dialog_qrcode_permission_confirm, new DialogInterface.OnClickListener()
                            {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                                }
                            }).create().show();
                    return;
                }

                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                return;
            }

            //打开相机
            openCamera(surfaceHolder);
        } else
        {
            //sdk22以下直接打开相机
            openCamera(surfaceHolder);
        }
    }

    private void openCamera(SurfaceHolder surfaceHolder)
    {
        if (surfaceHolder == null)
        {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen())
        {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try
        {
            mCameraManager.openDriver(surfaceHolder);
            // Creating the mQrHandler starts the preview, which can also throw a
            // RuntimeException.
            if (mQrHandler == null)
            {
                mQrHandler = new QrCodeScanActivityHandler(this, mCameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (Exception e)
        {
            Log.w(TAG, "Unexpected error initializing camera", e);
            Toast.makeText(this,R.string.error_dialog_qrcode_scan_message,Toast.LENGTH_LONG).show();
        }
    }

    public void restartPreviewAfterDelay(long delayMS)
    {
        if (mQrHandler != null)
        {
            mQrHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
    }

    public Rect getCropRect()
    {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop()
    {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        mScanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - OtherUtils.getStatusBarHeight(this);

        int cropWidth = mScanCropView.getWidth();
        int cropHeight = mScanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = mScanContainer.getWidth();
        int containerHeight = mScanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.ll_qrcode_actionbar_left_back)
        {
            finish();
        } else if (id == R.id.img_qrcode_light)
        {
            if (mIsLightOn)
            {
                mCameraManager.closeFlashLight();
                mImgLight.setImageResource(R.mipmap.icon_qrcode_light_normal);
            } else
            {
                mCameraManager.openFlashLight();
                mImgLight.setImageResource(R.mipmap.icon_qrcode_light_pressed);
            }
            mIsLightOn = !mIsLightOn;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_CAMERA:

                mHasCheckPermission = true;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    openCamera(mScanPreview.getHolder());
                } else
                {
                    Toast.makeText(this, R.string.warning_qrcode_permission_camera_denied, Toast.LENGTH_SHORT).show();
                    //处理NerverAsk
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                        new AlertDialog.Builder(this).setCancelable(false)
                                .setTitle(R.string.dialog_qrcode_permission_title)
                                .setMessage(R.string.dialog_qrcode_permission_camera_nerver_ask_message)
                                .setNegativeButton(R.string.dialog_qrcode_permission_nerver_ask_cancel, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.dismiss();
                                        QrCodeScanActivity.this.finish();
                                    }
                                })
                                .setPositiveButton(R.string.dialog_qrcode_permission_nerver_ask_confirm, new DialogInterface.OnClickListener()
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
