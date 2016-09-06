package com.lwk.familycontact.project.main.presenter;

import com.lwk.familycontact.project.main.model.MainModel;
import com.lwk.familycontact.project.main.view.MainImpl;

/**
 * Created by LWK
 * TODO MainActivity的Presenter
 * 2016/8/17
 */
public class MainPresenter
{
    private MainImpl mMainView;
    private MainModel mMainModel;

    public MainPresenter(MainImpl mainView)
    {
        this.mMainView = mainView;
        this.mMainModel = new MainModel();
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
}
