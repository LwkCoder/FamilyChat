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
 * TODO 联系人资料详情界面Presenter
 * 2016/8/12
 */
public class ContactDetailPresenter
{
    private UserDetailView mUserDetailView;

    public ContactDetailPresenter(UserDetailView userDetailView)
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
     * 更新联系人头像
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
                    ProfileUpdateEventBean eventBean = new ProfileUpdateEventBean(userBean, ProfileUpdateEventBean.FLAG_UPDATE_HEAD);
                    EventBusHelper.getInstance().post(eventBean);
                }
            }
        });
    }

    /**
     * 更新联系人app内备注名
     */
    public void updateUserNameApp(final UserBean userBean)
    {
        if (userBean == null)
        {
            mUserDetailView.updateNameFail();
            return;
        }

        final String phone = userBean.getPhone();
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                int lineNum = UserDao.getInstance().updateUserName(userBean);
                if (lineNum <= 0)
                {
                    mUserDetailView.updateNameFail();
                } else
                {
                    //发送用户资料更新事件
                    UserBean userBean = UserDao.getInstance().queryUserByPhone(phone);
                    ProfileUpdateEventBean eventBean = new ProfileUpdateEventBean(userBean, ProfileUpdateEventBean.FLAG_UPDATE_NAME);
                    EventBusHelper.getInstance().post(eventBean);
                }
            }
        });
    }
}
