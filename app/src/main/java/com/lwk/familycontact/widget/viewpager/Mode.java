package com.lwk.familycontact.widget.viewpager;

/**
 * 视差切换ViewPager切换模式
 * github：https://github.com/ybq/ParallaxViewPager
 */
public enum Mode
{

    LEFT_OVERLAY(0), RIGHT_OVERLAY(1), NONE(2);
    int value;

    Mode(int value)
    {
        this.value = value;
    }
}
