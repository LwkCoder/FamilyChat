package com.lwk.familycontact.project.contact.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.lib.base.log.KLog;
import com.lib.base.sp.Sp;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.impl.RcvSortSectionImpl;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.im.helper.HxSdkHelper;
import com.lwk.familycontact.storage.db.invite.InviteDao;
import com.lwk.familycontact.storage.db.user.UserBean;
import com.lwk.familycontact.storage.db.user.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LWK
 * TODO 通讯录数据层
 * 2016/8/10
 */
public class ContactModel
{
    //允许不自动刷新的最大间隔时间【一周】
    private final long MAX_REFRESH_DURATION = 604800000;
    private String mSpkey;
    private final String regEx = "[`~!@#$%^&*()=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——|{}【】‘；：”“’。，、？-]";

    public ContactModel()
    {
        mSpkey = new StringBuffer().append(HxSdkHelper.getInstance().getCurLoginUser())
                .append("refresh").toString();
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
                String oriPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phone = formatPhoneNum(oriPhone);
                String nameSystem = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim();
                UserBean userBean = new UserBean(nameSystem, phone, null, false);
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

    //去除手机号中所有特殊字符和空格
    private String formatPhoneNum(String phone)
    {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(phone);
        return m.replaceAll("").replaceAll("\\s*", "").trim();
    }

    //获取未读的好友通知数量
    public int getUnreadFriendNotifyNum()
    {
        return InviteDao.getInstance().getUnreadNotifyNum();
    }

    /**
     * 是否需要自动刷新
     * [判断条件：该账号从未刷新过就必须刷新，否则判断距离上次刷新时间是否大于一周时间间隔]
     */
    public boolean needAutoRefresh()
    {
        long lastRefreshTime = Sp.getLong(FCApplication.getInstance(), mSpkey, 0L);
        if (lastRefreshTime == 0L)
            return true;
        long cha = System.currentTimeMillis() - lastRefreshTime;
        if (cha >= MAX_REFRESH_DURATION)
            return true;
        else
            return false;
    }

    //同步刷新时间
    public void syncAutoRefreshTime()
    {
        Sp.putLong(FCApplication.getInstance(), mSpkey, System.currentTimeMillis());
    }

    //获取数据库中联系人数据，并按照字母排序，将#开头的放在最后
    public List<UserBean> getContactDataInDb()
    {
        //查询数据库所有数据并排序，将#开头的数据放在最后
        //因为"#"字符的ASCII值比字母小，在查询数据库的时候#开头的排序在最前，不知道该怎么做，所以暂时用这种最笨的方式将#开头的数据排到最后去
        List<UserBean> resultList = UserDao.getInstance().queryAllUsersSortByFirstChar();
        List<UserBean> defCharList = new ArrayList<>();
        for (UserBean userBean : resultList)
        {
            if (StringUtil.isEquals(userBean.getFirstChar(), RcvSortSectionImpl.DEF_SECTION))
                defCharList.add(userBean);
        }

        if (defCharList.size() > 0)
        {
            resultList.removeAll(defCharList);
            resultList.addAll(defCharList);
        }
        return resultList;
    }
}
