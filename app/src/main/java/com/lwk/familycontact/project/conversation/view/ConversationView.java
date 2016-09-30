package com.lwk.familycontact.project.conversation.view;

import com.lwk.familycontact.im.bean.HxConversation;

import java.util.List;

/**
 * Created by LWK
 * TODO 会话片段View实现的接口
 * 2016/9/20
 */
public interface ConversationView
{
    void onLoadAllConversationSuccess(List<HxConversation> list);

    void onConversationBeDeleted(HxConversation conversation);

    void scrollToTop();
}
