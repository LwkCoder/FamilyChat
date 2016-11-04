package com.lwk.familycontact.project.contact.presenter;

import com.lwk.familycontact.project.contact.view.AddContactView;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

/**
 * Created by LWK
 * TODO 添加通讯录界面Presenter
 * 2016/8/24
 */
public class AddContactPresenter
{
    private AddContactView mAddContactView;

    public AddContactPresenter(AddContactView addContactView)
    {
        this.mAddContactView = addContactView;
    }

    public void judgeData(String phone)
    {
        boolean hasUser = UserDao.getInstance().hasUser(phone);
        if (hasUser)
            mAddContactView.onUserExist();
        else
            mAddContactView.onUserNotExist();
    }

    public void saveNewData(String phone, String name, String localHead, boolean isRegist)
    {
        UserBean userBean = new UserBean(phone);
        userBean.setNameApp(name);
        userBean.setLocalHead(localHead);
        userBean.setRegist(isRegist);
        int lineNum = UserDao.getInstance().save(userBean);
        if (lineNum != -1)
        {
            //发送Eventbus通知通讯录刷新
            EventBusHelper.getInstance().post(new ComNotifyEventBean(ComNotifyConfig.REFRESH_CONTACT_IN_DB));
        }
        mAddContactView.onUserSaved();
    }
}
