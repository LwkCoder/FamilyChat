package com.lwk.familycontact.project.contact.presenter;

import com.lib.base.utils.StringUtil;
import com.lib.imagepicker.bean.ImageBean;
import com.lwk.familycontact.project.contact.view.UserDetailImpl;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;
import com.lwk.familycontact.utils.event.EventBusHelper;
import com.lwk.familycontact.utils.event.ProfileUpdateEventBean;
import com.lwk.familycontact.utils.other.ThreadManager;

/**
 * Created by LWK
 * TODO 用户资料界面Presenter
 * 2016/8/12
 */
public class UserDetailPresenter
{
    private UserDetailImpl mUserDetailView;

    public UserDetailPresenter(UserDetailImpl userDetailView)
    {
        this.mUserDetailView = userDetailView;
    }

    /**
     * 更新用户头像
     *
     * @param phone     手机号
     * @param imageBean 头像图片数据
     */
    public void updateUserLocalHead(final String phone, final ImageBean imageBean)
    {
        if (imageBean == null || StringUtil.isEmpty(imageBean.getImagePath()))
        {
            mUserDetailView.updateLocalHeadFail();
            return;
        }

        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                int lineNum = UserDao.getInstance().updateUserLocalHead(phone, imageBean.getImagePath());
                if (lineNum <= 0)
                {
                    mUserDetailView.updateLocalHeadFail();
                } else
                {
                    //发送用户资料更新事件
                    UserBean userBean = UserDao.getInstance().queryUserByPhone(phone);
                    ProfileUpdateEventBean eventBean = new ProfileUpdateEventBean(userBean);
                    EventBusHelper.getInstance().post(eventBean);
                }
            }
        });
    }
}
