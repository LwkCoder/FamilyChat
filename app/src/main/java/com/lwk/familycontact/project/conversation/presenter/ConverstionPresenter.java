package com.lwk.familycontact.project.conversation.presenter;

import android.os.Handler;

import com.lwk.familycontact.im.bean.HxConversation;
import com.lwk.familycontact.project.conversation.model.ConversationModel;
import com.lwk.familycontact.project.conversation.view.ConversationImpl;
import com.lwk.familycontact.utils.other.ThreadManager;

import java.util.List;

/**
 * Created by LWK
 * TODO 会话片段Presenter
 * 2016/9/20
 */
public class ConverstionPresenter
{
    private Handler mMainHandler;
    private ConversationImpl mViewImpl;
    private ConversationModel mModel;

    public ConverstionPresenter(ConversationImpl viewImpl, Handler handler)
    {
        this.mViewImpl = viewImpl;
        this.mMainHandler = handler;
        mModel = new ConversationModel();
    }

    /**
     * 获取所有会话
     */
    public void loadAllConversations()
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                final List<HxConversation> conversationsList = mModel.getAllConversations();
                if (conversationsList != null && conversationsList.size() > 0)
                    mMainHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mViewImpl.onLoadAllConversationSuccess(conversationsList);
                        }
                    });
            }
        });
    }
}
