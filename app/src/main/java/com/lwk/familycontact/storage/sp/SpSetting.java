package com.lwk.familycontact.storage.sp;

import android.content.Context;

import com.lib.base.sp.Sp;

/**
 * Created by LWK
 * TODO 通用sp设置
 * 2016/8/19
 */
public class SpSetting
{
    /**
     * 获取最近登录的手机号
     */
    public static String getLastLoginPhone(Context context)
    {
        return Sp.getString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PHONE);
    }

    /**
     * 设置最近登录的手机号
     */
    public static void setLastLoginPhone(Context context, String phone)
    {
        Sp.putString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PHONE, phone);
    }

    /**
     * 获取最近登录的密码
     */
    public static String getLastLoginPwd(Context context)
    {
        return Sp.getString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PWD);
    }

    /**
     * 设置最近登录的密码
     */
    public static void setLastLoginPwd(Context context, String pwd)
    {
        Sp.putString(context.getApplicationContext(), SpKeys.LAST_LOGIN_PWD, pwd);
    }

    /**
     * 是否允许拨号按键触摸反馈
     */
    public static boolean isDialFeedBackEnable(Context context)
    {
        return Sp.getBoolean(context.getApplicationContext(), SpKeys.DIAL_FEEDBACK, true);
    }

    /**
     * 设置是否允许拨号按键触摸反馈
     */
    public static void setDialFeendBackEnable(Context context, boolean enable)
    {
        Sp.putBoolean(context, SpKeys.DIAL_FEEDBACK, enable);
    }

    /**
     * 语音消息播放是否免提
     */
    public static boolean isVoiceMsgHandFreeEnable(Context context)
    {
        return Sp.getBoolean(context.getApplicationContext(), SpKeys.VOICE_MSG_HANDFREE, true);
    }

    /**
     * 设置语音消息播放是否免提
     */
    public static void setVoiceMsgHandFreeEnable(Context context, boolean enable)
    {
        Sp.putBoolean(context.getApplicationContext(), SpKeys.VOICE_MSG_HANDFREE, enable);
    }

    /**
     * 是否开启新消息提醒
     */
    public static boolean isNewMsgNotice(Context context)
    {
        return Sp.getBoolean(context.getApplicationContext(), SpKeys.NEW_MSG_NOTICE, true);
    }

    /**
     * 设置是否开启新消息提醒
     */
    public static void setNewMsgNotice(Context context, boolean enable)
    {
        Sp.putBoolean(context.getApplicationContext(), SpKeys.NEW_MSG_NOTICE, enable);
    }

    /**
     * 是否开启新消息铃声提醒
     */
    public static boolean isNewMsgNoticeVoice(Context context)
    {
        return isNewMsgNotice(context) ?
                Sp.getBoolean(context.getApplicationContext(), SpKeys.NEW_MSG_NOTICE_VOICE, true) : false;
    }

    /**
     * 设置是否开启新消息铃声提醒
     */
    public static void setNewMsgNoticeVoice(Context context, boolean enable)
    {
        Sp.putBoolean(context.getApplicationContext(), SpKeys.NEW_MSG_NOTICE_VOICE, enable);
    }

    /**
     * 是否开启新消息震动提醒
     */
    public static boolean isNewMsgNoticeVibrate(Context context)
    {
        return isNewMsgNotice(context) ?
                Sp.getBoolean(context.getApplicationContext(), SpKeys.NEW_MSG_NOTICE_VIBRATE, true) : false;
    }

    /**
     * 设置是否开启新消息震动提醒
     */
    public static void setNewMsgNoticeVibrate(Context context, boolean enable)
    {
        Sp.putBoolean(context.getApplicationContext(), SpKeys.NEW_MSG_NOTICE_VIBRATE, enable);
    }

    /**
     * 进入聊天界面时是否优先展示文字输入模式
     */
    public static boolean isChatTextInputModeFirst(Context context)
    {
        return Sp.getBoolean(context.getApplicationContext(), SpKeys.CHAT_TEXT_INPUT_FIRST, true);
    }

    /**
     * 设置进入聊天界面时是否优先展示文字输入模式
     */
    public static void setChatTextInputModeFirst(Context context, boolean b)
    {
        Sp.putBoolean(context.getApplicationContext(), SpKeys.CHAT_TEXT_INPUT_FIRST, b);
    }
}
