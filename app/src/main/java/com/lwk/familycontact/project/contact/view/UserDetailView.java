package com.lwk.familycontact.project.contact.view;

/**
 * Created by LWK
 * TODO
 * 2016/8/12
 */
public interface UserDetailView
{
    void setDefaultHead();

    void setHead(String url);

    void setName(String name);

    void setPhone(String phone);

    void nonFriend();

    void showFirstEnterDialog();

    void updateLocalHeadFail();
}
