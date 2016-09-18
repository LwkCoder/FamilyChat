package com.lib.imrecordbutton;

/**
 * Created by LWK
 * TODO 录音按钮各状态View实现接口
 * 2016/9/18
 */
public interface IMRecordViewImpl
{
    //初始化并显示View
    void showView(IMRecordButton button);

    //录音中
    void onRecording(IMRecordButton button);

    //更新录音音量等级
    void onUpdateVoiceLevel(IMRecordButton button, int level);

    //打算取消
    void onWantCancel(IMRecordButton button);

    //录音太短
    void onRecordTooShort(IMRecordButton button);

    //录音出错
    void onError(IMRecordButton button, int errorCode);

    //录音完成【无论是否录音成功】
    void onFinish(IMRecordButton button);
}
