package com.lwk.familycontact.project.chat.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lib.base.utils.ScreenUtils;
import com.lib.base.widget.CommonActionBar;
import com.lib.imagepicker.view.widget.ViewPagerFixed;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.project.chat.adapter.HxImageDetailAdapter;
import com.lwk.familycontact.project.chat.presenter.HxImageDetailPresenter;

import java.util.List;

/**
 * 查看大图消息界面
 */
public class HxImageDetailActivity extends FCBaseActivity implements HxImageDetailImpl
{
    //会话类型：单聊
    public static final int CON_TYPE_CHAT = 1;
    //会话类型：群聊
    public static final int CON_TYPE_GROUP_CHAT = 2;
    //会话类型：聊天室
    public static final int CON_TYPE_CHAT_ROOM = 3;
    //会话类型：讨论组
    public static final int CON_TYPE_DISCUSS_GROUP = 4;
    //会话类型：客服
    public static final int CON_TYPE_HELP_DESK = 5;
    //IntentKey:会话类型
    private static final String INTENT_KEY_CON_TYPE = "conType";
    //IntentKey:会话id
    private static final String INTENT_KEY_CON_ID = "conId";
    //IntentKey:起始图片消息msgId
    private static final String INTENT_KEY_FIRST_MSGID = "firstMsgId";
    private HxImageDetailPresenter mPresenter;
    private EMConversation.EMConversationType mConType;
    private String mConId;
    private String mFirstMsgId;
    private View mViewContent;
    private CommonActionBar mActionBar;
    private ViewPagerFixed mViewPager;
    private HxImageDetailAdapter mAdapter;

    /**
     * 跳转到大图消息界面的公共方法
     *
     * @param conType    会话类型
     * @param conId      会话id
     * @param firstMsgId 第一张图的消息id
     */
    public static void start(Activity activity, EMConversation.EMConversationType conType, String conId, String firstMsgId)
    {
        Intent intent = new Intent(activity, HxImageDetailActivity.class);
        intent.putExtra(INTENT_KEY_CON_TYPE, emConType2IntConType(conType));
        intent.putExtra(INTENT_KEY_CON_ID, conId);
        intent.putExtra(INTENT_KEY_FIRST_MSGID, firstMsgId);
        activity.startActivity(intent);
    }

    //EMConversationType转为int类型的标识
    private static int emConType2IntConType(EMConversation.EMConversationType type)
    {
        if (type == EMConversation.EMConversationType.Chat)
            return CON_TYPE_CHAT;
        else if (type == EMConversation.EMConversationType.GroupChat)
            return CON_TYPE_GROUP_CHAT;
        else if (type == EMConversation.EMConversationType.ChatRoom)
            return CON_TYPE_CHAT_ROOM;
        else if (type == EMConversation.EMConversationType.DiscussionGroup)
            return CON_TYPE_DISCUSS_GROUP;
        else if (type == EMConversation.EMConversationType.HelpDesk)
            return CON_TYPE_HELP_DESK;
        return CON_TYPE_CHAT;
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null)
        {
            int conType = intent.getIntExtra(INTENT_KEY_CON_TYPE, CON_TYPE_CHAT);
            mConId = intent.getStringExtra(INTENT_KEY_CON_ID);
            mFirstMsgId = intent.getStringExtra(INTENT_KEY_FIRST_MSGID);
            switch (conType)
            {
                case CON_TYPE_CHAT:
                    mConType = EMConversation.EMConversationType.Chat;
                    break;
                case CON_TYPE_GROUP_CHAT:
                    mConType = EMConversation.EMConversationType.GroupChat;
                    break;
                case CON_TYPE_CHAT_ROOM:
                    mConType = EMConversation.EMConversationType.ChatRoom;
                    break;
                case CON_TYPE_DISCUSS_GROUP:
                    mConType = EMConversation.EMConversationType.DiscussionGroup;
                    break;
                case CON_TYPE_HELP_DESK:
                    mConType = EMConversation.EMConversationType.HelpDesk;
                    break;
            }
        }
    }

    @Override
    protected int setContentViewId()
    {
        mPresenter = new HxImageDetailPresenter(this, mMainHandler);
        return R.layout.activity_hx_image_detail;
    }

    @Override
    protected void initUI()
    {
        mActionBar = findView(R.id.cab_chat_image_detail);
        mActionBar.setLeftLayoutAsBack(this);
        mViewContent = findView(R.id.fl_chat_image_detail_root);
        mViewPager = findView(R.id.vp_chat_image_detail);

        mAdapter = new HxImageDetailAdapter(this, null);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mAdapter.setPhotoViewClickListener(new HxImageDetailAdapter.PhotoViewClickListener()
        {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1)
            {
                onImageSingleTap();
            }
        });
    }

    @Override
    protected void initData()
    {
        super.initData();
        mPresenter.scanImageData(mConType, mConId, mFirstMsgId);
    }

    @Override
    public void onScanDataSuccess(List<EMMessage> list, int startPosition)
    {
        mAdapter.setData(list);
        mViewPager.setCurrentItem(startPosition, false);
        refreshActionBar(startPosition);
    }

    @Override
    protected void onClick(int id, View v)
    {

    }

    //ViewPager滑动监听
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener()
    {
        @Override
        public void onPageSelected(int position)
        {
            super.onPageSelected(position);
            refreshActionBar(position);
        }
    };

    //刷新Actiobar上显示的当前位置
    private void refreshActionBar(int curPosition)
    {
        if (mActionBar != null)
            mActionBar.setTitleText(getString(com.lib.imagepicker.R.string.tv_imagepicker_pager_titlecount, curPosition + 1, mAdapter.getCount()));
    }

    /**
     * 根据单击来隐藏/显示Actionbar
     */
    private void onImageSingleTap()
    {
        if (mActionBar == null)
            return;
        if (mActionBar.getVisibility() == View.VISIBLE)
        {
            mActionBar.setAnimation(AnimationUtils.loadAnimation(this, com.lib.imagepicker.R.anim.imagepicker_actionbar_dismiss));
            mActionBar.setVisibility(View.GONE);
            //更改状态栏为透明
            ScreenUtils.changeStatusBarColor(this, getResources().getColor(R.color.transparent_00000000));
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            if (Build.VERSION.SDK_INT >= 16)
                mViewContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else
        {
            mActionBar.setAnimation(AnimationUtils.loadAnimation(this, com.lib.imagepicker.R.anim.imagepicker_actionbar_show));
            mActionBar.setVisibility(View.VISIBLE);
            //改回状态栏颜色
            ScreenUtils.changeStatusBarColor(this, getResources().getColor(R.color.colorPrimaryDark));
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            if (Build.VERSION.SDK_INT >= 16)
                mViewContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
