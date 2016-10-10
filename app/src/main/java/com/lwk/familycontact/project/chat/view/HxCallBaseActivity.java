package com.lwk.familycontact.project.chat.view;

import com.lwk.familycontact.base.FCBaseActivity;
import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO 实时通话基类
 * 2016/10/10
 */
public abstract class HxCallBaseActivity extends FCBaseActivity
{
    protected String mOpPhone;
    protected UserBean mOpUserBean;
}
