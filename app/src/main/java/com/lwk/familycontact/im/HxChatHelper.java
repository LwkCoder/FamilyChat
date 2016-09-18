package com.lwk.familycontact.im;

/**
 * Created by LWK
 * TODO 环信聊天帮助类
 * 2016/9/18
 */
public class HxChatHelper
{
    private HxChatHelper()
    {
    }

    private static final class HxChatHelperHolder
    {
        private static HxChatHelper instance = new HxChatHelper();
    }

    public static HxChatHelper getInstance()
    {
        return HxChatHelperHolder.instance;
    }



}
