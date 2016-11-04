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
    @DatabaseField(columnName = UserDbConfig.NAME_SYSTEM)
    private String nameSystem;

    @DatabaseField(columnName = UserDbConfig.NAME_APP)
    private String nameApp;

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

    @DatabaseField(columnName = UserDbConfig.IS_REGIST)
    private boolean isRegist;

    public UserBean()
    {
    }

    public UserBean(String phone)
    {
        this.phone = phone;
        this.isRegist = true;
        updateDisplayNameAndSpell();
    }

    public UserBean(String nameSystem, String phone, String localHead, boolean isRegist)
    {
        this.nameSystem = nameSystem;
        this.phone = phone;
        this.localHead = localHead;
        this.isRegist = isRegist;
        updateDisplayNameAndSpell();
    }

    public String getNameSystem()
    {
        return nameSystem;
    }

    public void setNameSystem(String nameSystem)
    {
        this.nameSystem = nameSystem;
        updateDisplayNameAndSpell();
    }

    public String getNameApp()
    {
        return nameApp;
    }

    public void setNameApp(String nameApp)
    {
        this.nameApp = nameApp;
        updateDisplayNameAndSpell();
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
        updateDisplayNameAndSpell();
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

    public boolean isRegist()
    {
        return isRegist;
    }

    public void setRegist(boolean regist)
    {
        isRegist = regist;
    }

    @Override
    public String toString()
    {
        return "UserBean{" +
                "nameSystem='" + nameSystem + '\'' +
                ", nameApp='" + nameApp + '\'' +
                ", phone='" + phone + '\'' +
                ", localHead='" + localHead + '\'' +
                ", displayName='" + displayName + '\'' +
                ", firstChar='" + firstChar + '\'' +
                ", simpleSpell='" + simpleSpell + '\'' +
                ", fullSpell='" + fullSpell + '\'' +
                ", isRegist=" + isRegist +
                '}';
    }

    /**
     * 更新内部显示名、简拼、全拼、首字母
     */
    public void updateDisplayNameAndSpell()
    {
        //设置显示名：有备注名就显示备注名，没有备注名显示系统通讯录中的名字，否则显示手机号
        if (StringUtil.isNotEmpty(nameApp))
            setDisplayName(nameApp);
        else if (StringUtil.isNotEmpty(nameSystem))
            setDisplayName(nameSystem);
        else if (StringUtil.isNotEmpty(phone))
            setDisplayName(phone);

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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.nameSystem);
        dest.writeString(this.nameApp);
        dest.writeString(this.phone);
        dest.writeString(this.localHead);
        dest.writeString(this.displayName);
        dest.writeString(this.firstChar);
        dest.writeString(this.simpleSpell);
        dest.writeString(this.fullSpell);
        dest.writeByte(this.isRegist ? (byte) 1 : (byte) 0);
    }

    protected UserBean(Parcel in)
    {
        this.nameSystem = in.readString();
        this.nameApp = in.readString();
        this.phone = in.readString();
        this.localHead = in.readString();
        this.displayName = in.readString();
        this.firstChar = in.readString();
        this.simpleSpell = in.readString();
        this.fullSpell = in.readString();
        this.isRegist = in.readByte() != 0;
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>()
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
}
