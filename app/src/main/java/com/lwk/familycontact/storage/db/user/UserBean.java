package com.lwk.familycontact.storage.db.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.lib.base.utils.StringUtil;
import com.lib.rcvadapter.impl.RcvSortSectionImpl;
import com.lwk.familycontact.utils.other.PinYin;

/**
 * Created by LWK
 * TODO 通讯录资料实体类
 * 2016/8/8
 */
@DatabaseTable(tableName = UserDbConfig.TABLE_NAME)
public class UserBean implements Parcelable, RcvSortSectionImpl
{
    @DatabaseField(columnName = UserDbConfig.NAME)
    private String name;

    @DatabaseField(columnName = UserDbConfig.PHONE, id = true)
    private String phone;

    @DatabaseField(columnName = UserDbConfig.LOCAL_HEAD)
    private String localHead;

    @DatabaseField(columnName = UserDbConfig.DISPLAY_NAME)
    private String displayName;

    @DatabaseField(columnName = UserDbConfig.FIRST_CHAR)
    private String firstChar;

    @DatabaseField(columnName = UserDbConfig.SIMPLE_SPELL)
    private String simpleSpell;

    @DatabaseField(columnName = UserDbConfig.FULL_SPELL)
    private String fullSpell;

    public UserBean(String name, String phone, String localHead)
    {
        this.name = name;
        this.phone = phone;
        this.localHead = localHead;
        updateDisplayNameAndSpell();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getLocalHead()
    {
        return localHead;
    }

    public void setLocalHead(String localHead)
    {
        this.localHead = localHead;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getFirstChar()
    {
        return firstChar;
    }

    public void setFirstChar(String firstChar)
    {
        this.firstChar = firstChar;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public void setSimpleSpell(String simpleSpell)
    {
        this.simpleSpell = simpleSpell;
    }

    public String getFullSpell()
    {
        return fullSpell;
    }

    public void setFullSpell(String fullSpell)
    {
        this.fullSpell = fullSpell;
    }

    @Override
    public String toString()
    {
        return "UserBean{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", localHead='" + localHead + '\'' +
                ", displayName='" + displayName + '\'' +
                ", firstChar='" + firstChar + '\'' +
                ", simpleSpell='" + simpleSpell + '\'' +
                ", fullSpell='" + fullSpell + '\'' +
                '}';
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.localHead);
        dest.writeString(this.displayName);
        dest.writeString(this.firstChar);
        dest.writeString(this.simpleSpell);
        dest.writeString(this.fullSpell);
    }

    protected UserBean(Parcel in)
    {
        this.name = in.readString();
        this.phone = in.readString();
        this.localHead = in.readString();
        this.displayName = in.readString();
        this.firstChar = in.readString();
        this.simpleSpell = in.readString();
        this.fullSpell = in.readString();
    }

    public static final Parcelable.Creator<UserBean> CREATOR = new Parcelable.Creator<UserBean>()
    {
        @Override
        public UserBean createFromParcel(Parcel source)
        {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size)
        {
            return new UserBean[size];
        }
    };

    /**
     * 更新内部显示名、简拼、全拼、首字母
     */
    public void updateDisplayNameAndSpell()
    {
        //设置显示名：用户名不为空就设置为用户名，否则设置为手机号码
        if (StringUtil.isNotEmpty(name))
            setDisplayName(name);
        else if (StringUtil.isNotEmpty(phone))
            setDisplayName(name);

        if (StringUtil.isNotEmpty(displayName))
        {
            //设置简拼
            setSimpleSpell(PinYin.getSimple(displayName));
            //设置全拼
            setFullSpell(PinYin.getFull(displayName));
            //设置首字母
            if (StringUtil.isNotEmpty(fullSpell))
            {
                String tempChar = fullSpell.substring(0, 1);
                if (tempChar.matches("[A-Z]"))
                    setFirstChar(tempChar);
                else
                    setFirstChar(RcvSortSectionImpl.DEF_SECTION);
            } else
            {
                setFirstChar(RcvSortSectionImpl.DEF_SECTION);
            }
        }
    }

    @Override
    public String getSection()
    {
        return firstChar;
    }
}
