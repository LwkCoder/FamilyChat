package com.lwk.familycontact.project.conversation.model;

import com.hyphenate.chat.EMConversation;
import com.lwk.familycontact.im.bean.HxConversation;
import com.lwk.familycontact.im.helper.HxChatHelper;
import com.lwk.familycontact.project.conversation.utils.SortConversationComparator;
import com.lwk.familycontact.storage.db.user.UserDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by LWK
 * TODO 会话片段数据层
 * 2016/9/20
 */
public class ConversationModel
{
    public ConversationModel()
    {
    }

    /**
     * 获取所有会话
     */
    public List<HxConversation> getAllConversations()
    {
        List<HxConversation> resultList = new ArrayList<>();

        //获取环信会话对象并排序
        List<EMConversation> allConversations = new ArrayList<>();
        Map<String, EMConversation> conversationMap = HxChatHelper.getInstance().getAllConversations();
        if (conversationMap != null && conversationMap.size() > 0)
        {
            allConversations.addAll(conversationMap.values());
            Collections.sort(allConversations, new SortConversationComparator());
        }
        //关联用户数据
        for (EMConversation conversation : allConversations)
        {
            HxConversation hxConversation = new HxConversation();
            hxConversation.setEmConversation(conversation);
            hxConversation.setUserBean(UserDao.getInstance().queryUserByPhone(conversation.conversationId()));
            resultList.add(hxConversation);
        }

        return resultList;
    }

    /**
     * 删除会话
     */
    public void delConversation(HxConversation conversation)
    {
        HxChatHelper.getInstance().delConversation(conversation.getEmConversation().conversationId(), true);
    }
}
