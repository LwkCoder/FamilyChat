package com.lib.shortvideo.videoview.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by LWK
 * TODO 相机管理类
 * 2016/11/1
 */
public class CameraMgr
{
    private final String TAG = "CameraMgr";
    private Camera mCamera;
    private int mCameraId = -1;
    private boolean mIsLightOn;
    private int mPreviewWidth;
    private int mPreviewHeight;

    public boolean initCamera(Context context, SurfaceHolder holder) throws IOException
    {
        releaseCameraResource();

        //有打开过就直接打开
        if (mCameraId != -1)
        {
            mCamera = Camera.open(mCameraId);
        } else
        {
            //没有打开过默认打开后置摄像头
            if (checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK))
                mCamera = Camera.open(mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        if (mCamera == null)
            return false;

        setPortrait();
        setOrientation(context);
        mCamera.setPreviewDisplay(holder);
        mCamera.startPreview();
        return true;
    }

    /**
     * 检查是否有摄像头
     *
     * @param facing 前置还是后置
     */
    private boolean checkCameraFacing(int facing)
    {
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++)
        {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing)
            {
                return true;
            }
        }
        return false;
    }

    //设置竖屏
    private void setPortrait()
    {
        if (mCamera != null)
        {
            //设置竖屏
            Camera.Parameters params = mCamera.getParameters();
            params.set("orientation", "portrait");
            mCamera.setParameters(params);
        }
    }

    //设置旋转角度
    private void setOrientation(Context context)
    {
        if (mCamera != null)
        {
            if (context instanceof Activity)
            {
                Activity activity = (Activity) context;
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(mCameraId, info);
                int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0;
                switch (rotation)
                {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                }

                int result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360;
                } else
                {
                    result = (info.orientation - degrees + 360) % 360;
                }
                mCamera.setDisplayOrientation(result);

            } else
            {
                //设置旋转角度
                if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                    mCamera.setDisplayOrientation(90);
                else
                    mCamera.setDisplayOrientation(270);
            }
        }
    }

    public void unlock()
    {
        if (mCamera != null)
            mCamera.unlock();
    }

    public void lock()
    {
        if (mCamera != null)
            mCamera.lock();
    }

    /**
     * 获取最优预览尺寸
     *
     * @param targetHeight
     */
    public Camera.Size getOptimalPreviewSize(int targetHeight)
    {
        if (mCamera == null)
            return null;

        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

        final double MIN_ASPECT_RATIO = 1.0;
        final double MAX_ASPECT_RATIO = 1.7;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        for (Camera.Size size : sizes)
        {
            double ratio = (double) size.width / size.height;
            if (ratio < MIN_ASPECT_RATIO || ratio > MAX_ASPECT_RATIO)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes)
            {
                if (Math.abs(size.height - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 调整预览尺寸
     */
    public void adjustPreviewSize(Camera.Size size, SurfaceHolder holder)
    {
        if (mCamera != null)
        {
            if (holder.getSurface() == null)
                return;
            try
            {
                mCamera.stopPreview();
            } catch (Exception e)
            {
            }

            Camera.Parameters parameters = mCamera.getParameters();
            mPreviewWidth = size.width;//这才是支持的录制尺寸宽度
            mPreviewHeight = size.height;//这才是支持的录制尺寸高度
            parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
            mCamera.setParameters(parameters);

            try
            {
                mCamera.startPreview();
            } catch (Exception e)
            {
            }
        }
    }

    /**
     * 设置相机对焦模式
     *
     * @param focusMode
     */
    public void setCameraFocusMode(String focusMode)
    {
        if (mCamera == null)
            return;

        Camera.Parameters parameters = mCamera.getParameters();
        List<String> sfm = parameters.getSupportedFocusModes();
        if (sfm.contains(focusMode))
        {
            parameters.setFocusMode(focusMode);
        }
        mCamera.setParameters(parameters);
    }


    /**
     * 开启闪光灯
     */
    public boolean openFlashLight()
    {
        if (mCamera != null && !mIsLightOn)
        {
            try
            {
                Camera.Parameters parameters = mCamera.getParameters();
                if (parameters == null)
                    return false;
                List<String> flashModes = parameters.getSupportedFlashModes();
                if (flashModes == null)
                    return false;
                String flashMode = parameters.getFlashMode();
                if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode))
                {
                    if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH))
                    {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(parameters);
                        return mIsLightOn = true;
                    }
                }
            } catch (Exception e)
            {
                Log.e(TAG, "openFlashLight fail:" + e.toString());
                return false;
            }

        }
        return false;
    }

    /**
     * 关闭闪光灯
     */
    public boolean closeFlashLight()
    {
        if (mCamera != null && mIsLightOn)
        {
            try
            {
                Camera.Parameters parameters = mCamera.getParameters();
                if (parameters == null)
                    return false;
                List<String> flashModes = parameters.getSupportedFlashModes();
                String flashMode = parameters.getFlashMode();
                if (flashModes == null)
                    return false;
                if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode))
                {
                    if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF))
                    {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(parameters);
                        mIsLightOn = false;
                        return true;
                    }
                }
            } catch (Exception e)
            {
                Log.e(TAG, "closeFlashLight fail:" + e.toString());
                return false;
            }
        }
        return false;
    }

    public Camera getCamera()
    {
        return mCamera;
    }

    public int getCameraId()
    {
        return mCameraId;
    }

    public boolean isLightOn()
    {
        return mIsLightOn;
    }

    /**
     * 获取预览尺寸宽度
     */
    public int getPreviewWidth()
    {
        return mPreviewWidth;
    }

    /**
     * 获取预览尺寸高度
     */
    public int getPreviewHeight()
    {
        return mPreviewHeight;
    }

    /**
     * 释放摄像头资源
     */
    public void releaseCameraResource()
    {
        if (mCamera != null)
        {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
