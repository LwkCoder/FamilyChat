package com.lib.shortvideo.videoview.recorder;

import android.hardware.Camera;
import android.view.Surface;

/**
 * Created by LWK
 * TODO 录像Record接口
 * 2016/11/1
 */
public interface RecorderInterface
{
    void initRecorder(Camera camera, int cameraId, Surface surface, String filePath);

    boolean startRecord();

    void stopRecord();

    void releaseRecorder();

    boolean isRecording();
}
