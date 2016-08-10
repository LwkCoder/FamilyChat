package com.lwk.familycontact.project.contact.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.lib.base.log.KLog;
import com.lwk.familycontact.storage.db.user.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LWK
 * TODO 通讯录数据层
 * 2016/8/10
 */
public class ContactModel
{
    public ContactModel()
    {
    }

    /**
     * 获取手机通讯录
     *
     * @param context 上下文环境
     * @return 所有联系人
     */
    public List<UserBean> getPhoneContactData(Context context)
    {
        List<UserBean> resultList = null;
        try
        {
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor == null)
                return resultList;
            boolean hasData = cursor.moveToFirst();
            while (hasData)
            {
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s*", "");//替换所有空格字符
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim();
                UserBean userBean = new UserBean(name, phone, null, false);
                if (resultList == null)
                    resultList = new ArrayList<>();
                resultList.add(userBean);
                hasData = cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e)
        {
            KLog.e("ContactModel getPhoneContactData fail : " + e.toString());
        }

        return resultList;
    }
}
