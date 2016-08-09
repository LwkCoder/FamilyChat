package com.lib.quicksidebar.listener;

/**
 * QuickSideBar触摸监听
 */
public interface OnQuickSideBarTouchListener {
    void onLetterChanged(String letter, int position, float y);
    void onLetterTouching(boolean touching);
}
