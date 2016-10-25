package com.lwk.familycontact.project.contact.presenter;

import com.lib.base.sp.Sp;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lib.imagepicker.bean.ImageBean;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.contact.view.UserDetailView;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;
import com.lwk.familycontact.storage.sp.SpKeys;
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
    private UserDetailView mUserDetailView;

    public UserDetailPresenter(UserDetailView userDetailView)
    {
        this.mUserDetailView = userDetailView;
    }

    /**
     * 初始化数据
     */
    public void initData(UserBean userBean)
    {
        if (userBean != null && mUserDetailView != null)
        {
            mUserDetailView.setName(userBean.getDisplayName());
            mUserDetailView.setPhone(PhoneUtils.formatPhoneNumAsRegular(userBean.getPhone(), " - "));
            String localHead = userBean.getLocalHead();
            if (StringUtil.isNotEmpty(localHead))
                mUserDetailView.setHead(localHead);
            else
                mUserDetailView.setDefaultHead();
            if (!userBean.isRegist())
                mUserDetailView.nonFriend();
        }
    }

    /**
     * 检查是否第一次进入该界面
     */
    public void checkIfFirstEnter()
    {
        if (Sp.getBoolean(FCApplication.getInstance(), SpKeys.IS_FIRST_ENTER_CONTACT_DETAIL, true))
        {
            mUserDetailView.showFirstEnterDialog();
            Sp.putBoolean(FCApplication.getInstance(), SpKeys.IS_FIRST_ENTER_CONTACT_DETAIL, false);
        }
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
