package com.lwk.familycontact.project.call.presenter;

import android.os.Handler;

import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.im.helper.HxCallHelper;
import com.lwk.familycontact.project.call.view.HxVoiceCallView;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;
import com.lwk.familycontact.utils.other.ThreadManager;

/**
 * Created by LWK
 * TODO 实时语音通话界面Presenter
 * 2016/10/21
 */
public class HxVoiceCallPresenter
{
    private HxVoiceCallView mViewImpl;
    private Handler mMainHandler;

    public HxVoiceCallPresenter(HxVoiceCallView viewImpl, Handler handler)
    {
        this.mViewImpl = viewImpl;
        this.mMainHandler = handler;
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
                    mViewImpl.setName(name);
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
                    mViewImpl.setHead(url);
                }
            });
        }
    }

    /**
     * 拨打电话
     */
    public void startVoiceCall(String phone)
    {
        try
        {
            HxCallHelper.getInstance().startVoiceCall(phone);
        } catch (EMServiceNotReadyException e)
        {
            mViewImpl.showError(R.string.call_state_unknow_error);
        }
    }

    /**
     * 接听通话
     */
    public void answerCall()
    {
        try
        {
            HxCallHelper.getInstance().answerCall();
        } catch (EMNoActiveCallException e)
        {
            mViewImpl.showError(R.string.call_state_cannot_answer);
        }
    }

    /**
     * 结束通话
     */
    public void endCall()
    {
        try
        {
            HxCallHelper.getInstance().endCall();
        } catch (EMNoActiveCallException e)
        {
            mViewImpl.showError(0);
        }
    }

    /**
     * 拒接通话
     */
    public void rejectCall()
    {
        try
        {
            HxCallHelper.getInstance().rejectCall();
        } catch (EMNoActiveCallException e)
        {
            mViewImpl.showError(0);
        }
    }
}
