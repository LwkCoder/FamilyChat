package com.lwk.familycontact.project.dial.view;

import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.List;

/**
 * Created by LWK
 * TODO 拨号器片段View
 * 2016/8/19
 */
public interface DialImpl
{
    void resetSearchResult();

    void showAddContact(String phone);

    void closeAddContact();

    void onSearchResultEmpty(String phone);

    void onSearchResultSuccess(List<UserBean> resultList);
}
