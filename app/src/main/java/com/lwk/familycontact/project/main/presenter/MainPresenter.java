package com.lwk.familycontact.project.main.presenter;

import android.os.Handler;

import com.lwk.familycontact.project.common.version.CheckVersionUtils;
import com.lwk.familycontact.project.common.version.VersionBean;
import com.lwk.familycontact.project.main.model.MainModel;
import com.lwk.familycontact.project.main.view.MainView;
import com.lwk.familycontact.utils.other.ThreadManager;

/**
 * Created by LWK
 * TODO MainActivity的Presenter
 * 2016/8/17
 */
public class MainPresenter
{
    private MainView mMainView;
    private MainModel mMainModel;
    private Handler mMainHandler;

    public MainPresenter(MainView mainView, Handler handler)
    {
        this.mMainView = mainView;
        this.mMainHandler = handler;
        this.mMainModel = new MainModel();
    }

    //刷新左边Tab的角标数据
    public void refreshLeftTabBadge()
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                final int count = mMainModel.getAllUnreadMsgCount();
                if (count == 0)
                    mMainHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mMainView.onHideFirstBadgeNum();
                        }
                    });
                else
                    mMainHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mMainView.onShowFirstBadgeNum(count);
                        }
                    });
            }
        });
    }

    //刷新中间Tab的角标数据
    public void refreshMiddleTabBadge()
    {
        int num = mMainModel.getUnreadFriendNotifyNum();
        if (num == 0)
            mMainView.onHideMiddleBadgeNum();
        else
            mMainView.onShowMiddleBadgeNum(num);
    }

    /**
     * 检查版本更新
     */
    public void checkVersion()
    {
        CheckVersionUtils.getInstance().checkVersion(false, new CheckVersionUtils.onCheckVersionListener()
        {
            @Override
            public void onNewVersionAvaiable(VersionBean versionBean)
            {
                if (mMainView != null)
                    mMainView.showVersionDialog(versionBean);
            }
        });
    }
}
