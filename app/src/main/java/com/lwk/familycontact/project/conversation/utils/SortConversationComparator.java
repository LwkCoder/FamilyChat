package com.lwk.familycontact.project.conversation.utils;

import com.hyphenate.chat.EMConversation;

import java.util.Comparator;

/**
 * Created by LWK
 * 片段排序算法
 * 2016/9/20
 */
public class SortConversationComparator implements Comparator<EMConversation>
{
    @Override
    public int compare(EMConversation con01, EMConversation con02)
    {
        if (con01.getLastMessage() == null)
            return -1;
        else if (con02.getLastMessage() == null)
            return -1;

        long timeStamp01 = con01.getLastMessage().getMsgTime();
        long timeStamp02 = con02.getLastMessage().getMsgTime();
        if (timeStamp01 == timeStamp02)
            return 0;
        else if (timeStamp01 < timeStamp02)
            return 1;
        else
            return -1;
    }
}
