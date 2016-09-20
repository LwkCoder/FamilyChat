package com.lwk.familycontact.project.chat.presenter;

import com.lwk.familycontact.project.chat.model.HxChatModel;
import com.lwk.familycontact.project.chat.view.HxChatImpl;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.other.ThreadManager;

/**
 * Created by LWK
 * TODO 聊天界面Presenter
 * 2016/9/20
 */
public class HxChatPresenter
{
    private HxChatModel mModel;
    private HxChatImpl mViewImpl;

    public HxChatPresenter(HxChatImpl viewImpl)
    {
        this.mViewImpl = viewImpl;
        mModel = new HxChatModel();
    }

    /**
     * 设置ActionBar的title
     */
    public void setActionBarTitle(String conversationId, UserBean userBean)
    {
        if (userBean != null)
            mViewImpl.onRefreshActionBarTitle(userBean.getDisplayName());
        else
            mViewImpl.onRefreshActionBarTitle(conversationId);
    }

    /**
     * 将会话未读消息数清零
     */
    public void clearConversationUnreadCount(final String phone)
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                mModel.clearConversationUnreadCount(phone);
                //发送通知刷新未读消息数
                EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_UNREAD_MSG));
            }
        });
    }
}
