package com.lwk.familycontact.project.main.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lib.base.widget.CommonActionBar;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.common.version.CheckVersionUtils;
import com.lwk.familycontact.project.common.version.VersionBean;
import com.lwk.familycontact.project.contact.view.AddFriendActivity;
import com.lwk.familycontact.project.contact.view.ContactFragment;
import com.lwk.familycontact.project.conversation.view.ConversationFragment;
import com.lwk.familycontact.project.dial.view.DialFragment;
import com.lwk.familycontact.project.main.presenter.MainPresenter;
import com.lwk.familycontact.project.profile.UserProfileActivity;
import com.lwk.familycontact.project.setting.view.SettingActivity;
import com.lwk.familycontact.utils.event.ComNotifyConfig;
import com.lwk.familycontact.utils.event.ComNotifyEventBean;
import com.lwk.familycontact.utils.event.EventBusHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * MainActivity
 * 管理三个主片段
 */
public class MainActivity extends FCBaseActivity implements MainView
        , BottomNavigationBar.OnTabSelectedListener
        , MainMenuPop.onMenuClickListener
{
    private MainPresenter mPresenter;
    private CommonActionBar mActionBar;
    private BottomNavigationBar mNavigationBar;
    private BadgeItem mBadge01;
    private BadgeItem mBadge02;
    private Fragment mCurFragment;
    private ConversationFragment mFragment01;
    private ContactFragment mFragment02;
    private DialFragment mFragment03;
    private MainMenuPop mMenuPop;

    @Override
    protected int setContentViewId()
    {
        mPresenter = new MainPresenter(this, mMainHandler);
        EventBusHelper.getInstance().regist(this);
        return R.layout.activity_main;
    }

    @Override
    protected void initUI()
    {
        mActionBar = findView(R.id.cab_main);
        mActionBar.setRightImgResource(R.drawable.ic_cab_plus_menu);
        mActionBar.setRightLayoutClickListener(this);

        mNavigationBar = findView(R.id.bnb_main);
        mBadge01 = createBadgeItem();
        mBadge02 = createBadgeItem();
        //这里真他妈扯淡！
        //源码中将firstSelectedPosition设置为0
        //在navigationbar调用initialise()后就将firstSelectedPosition赋值给内部变量mSelectedPosition，但是不触发监听
        //此时手动调用selectTab(0, true)时，监听里触发的不是onTabSelected()，而是他妈的onTabReselected()！！！！
        //所以想要在一开始默认选中第一个tab并且能触发监听里的onTabSelected()，就必须先将firstSelectedPosition设置为小于0的数
        mNavigationBar.setFirstSelectedPosition(-1);
        mNavigationBar
                .addItem(createBottomNavigationItem(R.drawable.ic_tab_conversation, R.string.label_main_tab01).setBadgeItem(mBadge01))
                .addItem(createBottomNavigationItem(R.drawable.ic_tab_contact, R.string.label_main_tab02).setBadgeItem(mBadge02))
                .addItem(createBottomNavigationItem(R.drawable.ic_tab_dial, R.string.label_main_tab03))
                .initialise();
        mNavigationBar.setTabSelectedListener(this);
        //默认选中第一个tab
        mNavigationBar.selectTab(0, true);
    }

    //创建navigationbar的tab
    private BottomNavigationItem createBottomNavigationItem(@NonNull int imgResId, @NonNull int titleResId)
    {
        BottomNavigationItem navigationItem = new BottomNavigationItem(imgResId, titleResId);
        navigationItem.setActiveColorResource(R.color.bnbActiveColor);
        navigationItem.setInActiveColorResource(R.color.bnbInActiveColor);
        return navigationItem;
    }

    //创建tab的角标
    private BadgeItem createBadgeItem()
    {
        BadgeItem badgeItem = new BadgeItem();
        badgeItem.setHideOnSelect(false);
        badgeItem.setBorderColor(Color.WHITE);
        badgeItem.setBorderWidth(1);
        badgeItem.setGravity(Gravity.RIGHT | Gravity.TOP);
        badgeItem.setTextColor(Color.WHITE);
        badgeItem.setBackgroundColor(Color.RED);
        return badgeItem;
    }

    @Override
    protected void initData()
    {
        super.initData();
        //刷新各Tab的角标
        mPresenter.refreshLeftTabBadge();
        mPresenter.refreshMiddleTabBadge();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //检查版本更新
        mPresenter.checkVersion();
    }

    @Override
    protected void onClick(int id, View v)
    {
        switch (id)
        {
            case R.id.fl_common_actionbar_right:
                if (mMenuPop != null)
                {
                    mMenuPop.dismiss();
                    mMenuPop = null;
                }

                mMenuPop = new MainMenuPop(this, this);
                mMenuPop.showAsDropDown(mActionBar.getRightLayout(), 0, 0);
                break;
        }
    }

    @Override
    public void onTabSelected(int position)
    {
        switch (position)
        {
            case 0:
                if (mFragment01 == null)
                    mFragment01 = ConversationFragment.newInstance();
                checkFragment(mFragment01);
                mActionBar.setTitleText(R.string.tv_main_actionbar_tab01);
                break;
            case 1:
                if (mFragment02 == null)
                    mFragment02 = ContactFragment.newInstance();
                checkFragment(mFragment02);
                mActionBar.setTitleText(R.string.tv_main_actionbar_tab02);
                break;
            case 2:
                if (mFragment03 == null)
                    mFragment03 = DialFragment.newInstance();
                checkFragment(mFragment03);
                mActionBar.setTitleText(R.string.tv_main_actionbar_tab03);
                break;
        }
    }

    @Override
    public void onTabUnselected(int position)
    {
    }

    @Override
    public void onTabReselected(int position)
    {
        switch (position)
        {
            case 1:
                mFragment02.scrollToTop();
                break;
            case 2:
                mFragment01.scrollToTop();
                break;
        }
    }

    //切换片段的方法
    private void checkFragment(Fragment fragment)
    {
        //第一次加载片段
        if (mCurFragment == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.ll_main_container, fragment).commit();
            mCurFragment = fragment;
            return;
        }

        //相同的tab不做操作
        if (mCurFragment == fragment)
        {
            return;
        } else
        {
            if (fragment.isAdded())
                getSupportFragmentManager().beginTransaction()
                        .hide(mCurFragment).show(fragment).commit();
            else
                getSupportFragmentManager().beginTransaction()
                        .hide(mCurFragment).add(R.id.ll_main_container, fragment).commit();
            mCurFragment = fragment;
        }
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    @Override
    public void onClickProfile()
    {
        startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
    }

    @Override
    public void onClickAddUser()
    {
        startActivity(new Intent(MainActivity.this, AddFriendActivity.class));
    }

    @Override
    public void onClickSetting()
    {
        startActivity(new Intent(MainActivity.this, SettingActivity.class));
    }


    @Override
    protected void onDestroy()
    {
        EventBusHelper.getInstance().unregist(this);
        super.onDestroy();
    }

    @Override
    public void onShowFirstBadgeNum(int num)
    {
        if (mBadge01 != null)
        {
            mBadge01.show();
            mBadge01.setText(String.valueOf(num));
        }
    }

    @Override
    public void onHideFirstBadgeNum()
    {
        if (mBadge01 != null)
            mBadge01.hide();
    }

    @Override
    public void onShowMiddleBadgeNum(int num)
    {
        if (mBadge02 != null)
        {
            mBadge02.setText(String.valueOf(num));
            mBadge02.show();
        }
    }

    @Override
    public void onHideMiddleBadgeNum()
    {
        if (mBadge02 != null)
            mBadge02.hide();
    }

    @Override
    public void showVersionDialog(VersionBean versionBean)
    {
        CheckVersionUtils.getInstance().showVersionDialog(this, versionBean);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyEventReceived(ComNotifyEventBean eventBean)
    {
        switch (eventBean.getFlag())
        {
            case ComNotifyConfig.REFRESH_USER_INVITE:
                mPresenter.refreshMiddleTabBadge();
                break;
            case ComNotifyConfig.REFRESH_UNREAD_MSG:
                mPresenter.refreshLeftTabBadge();
                break;
        }
    }
}
