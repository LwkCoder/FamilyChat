package com.lwk.familycontact.project.call.presenter;

import android.os.Handler;

import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.project.call.view.HxVoiceCallView;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;
import com.lwk.familycontact.utils.other.ThreadManager;

/**
 * Created by LWK
 * TODO 实时语音通话界面Presenter
 * 2016/10/21
 */
public class HxVoiceCallPresenter extends HxCallPresenter
{

    public HxVoiceCallPresenter(HxVoiceCallView viewImpl, Handler handler)
    {
        super(viewImpl, handler);
    }

    public void setOpData(final String phone)
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                UserBean userBean = UserDao.getInstance().queryUserByPhone(phone);
                if (userBean != null)
                {
                    setOpHead(userBean.getLocalHead());
                    setOpName(userBean.getDisplayName());
                } else
                {
                    setOpName(phone);
                }
            }
        });
    }

    //设置对方名字
    private void setOpName(final String name)
    {
        if (StringUtil.isNotEmpty(name))
        {
            mMainHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    ((HxVoiceCallView) mViewImpl).setName(name);
                }
            });
        }
    }

    //设置对方头像
    private void setOpHead(final String url)
    {
        if (StringUtil.isNotEmpty(url))
        {
            mMainHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    ((HxVoiceCallView) mViewImpl).setHead(url);
                }
            });
        }
    }
}
