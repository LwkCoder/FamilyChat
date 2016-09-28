package com.lwk.familycontact.project.chat.model;

import android.util.Pair;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.im.helper.HxChatHelper;

import java.util.List;

/**
 * Created by LWK
 * TODO 查看大图界面数据层
 * 2016/9/28
 */
public class HxImageDetailModel
{
    public HxImageDetailModel()
    {
    }

    //初始化数据
    public Pair<List<EMMessage>, Integer> initData(EMConversation.EMConversationType conType, String conId, String firstVisiableMsgId)
    {
        int startPosition = 0;
        List<EMMessage> messageList = HxChatHelper.getInstance().searchMsgsInConByMsgType(conType, conId, EMMessage.Type.IMAGE);
        if (StringUtil.isNotEmpty(firstVisiableMsgId))
        {
            for (int i = 0; i < messageList.size(); i++)
            {
                if (StringUtil.isEquals(messageList.get(i).getMsgId(), firstVisiableMsgId))
                {
                    startPosition = i;
                    break;
                }
            }
        }
        return new Pair<>(messageList, startPosition);
    }
}
