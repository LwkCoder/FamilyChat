package com.lwk.familycontact.project.contact.presenter;

import com.hyphenate.exceptions.HyphenateException;
import com.lib.base.log.KLog;
import com.lib.base.utils.PhoneUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.im.HxSdkHelper;
import com.lwk.familycontact.project.common.FCError;
import com.lwk.familycontact.project.contact.view.AddFriendImpl;

/**
 * Created by LWK
 * TODO 添加好友界面Presenter
 * 2016/8/26
 */
public class AddFriendPresenter
{
    private AddFriendImpl mAddFriendView;

    public AddFriendPresenter(AddFriendImpl addFriendView)
    {
        this.mAddFriendView = addFriendView;
    }

    public void sendRequest(String phone)
    {
        if (StringUtil.isEmpty(phone))
        {
            mAddFriendView.phoneEmptyWarning();
            return;
        }

        if (!PhoneUtils.isMobileNO(phone))
        {
            mAddFriendView.phoneErrorWarning();
            return;
        }

        try
        {
            HxSdkHelper.getInstance().addFriend(phone);
            mAddFriendView.sendRequestSuccess();
        } catch (HyphenateException e)
        {
            KLog.e("AddFriendActivity sendRequest fail:" + e.toString());
            mAddFriendView.sendRequestFail(FCError.ADD_FRIEND_FAIL, FCError.getErrorMsgIdFromCode(e.getErrorCode()));
        }
    }
}
