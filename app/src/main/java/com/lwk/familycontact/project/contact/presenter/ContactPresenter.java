package com.lwk.familycontact.project.contact.presenter;

import com.lwk.familycontact.project.contact.view.ContactImpl;

/**
 * Created by LWK
 * TODO 通讯录界面Presenter
 * 2016/8/9
 */
public class ContactPresenter
{
    private ContactImpl mContactView;

    public ContactPresenter(ContactImpl contactView)
    {
        this.mContactView = contactView;
    }
}
