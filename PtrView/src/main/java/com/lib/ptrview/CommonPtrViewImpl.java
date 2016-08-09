package com.lib.ptrview;

/**
 *
 */
public interface CommonPtrViewImpl
{
    /**
     * 重置
     */
    public void onReset();


    /**
     * 下拉高度大于头部高度
     */
    public void onPrepare();


    /**
     * 下拉/上拉过程中[手指没抬起]
     *
     * @param currentPercent 下拉高度与头部高度比例
     */
    public void onPulling(float currentPercent);

    /**
     * 放手后
     */
    public void onRelease();

    /**
     * 刷新/加载成功
     */
    void onSuccess();

    /**
     * 刷新/加载失败
     */
    void onFail();


    /**
     * 是否为下拉模式
     */
    public void setIsPullDownMode(boolean isPullDownMode);
}
