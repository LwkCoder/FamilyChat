package com.lwk.familycontact.utils.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.lwk.familycontact.storage.db.user.UserBean;

/**
 * Created by LWK
 * TODO 用户资料更新事件
 * 2016/8/15
 */
public class ProfileUpdateEventBean implements Parcelable
{
    /**
     * 更新姓名
     */
    public static final int FLAG_UPDATE_NAME = 100;

    /**
     * 更新头像
     */
    public static final int FLAG_UPDATE_HEAD = 101;

    private UserBean userBean;
    private String phone;
    private int flag;

    public ProfileUpdateEventBean(UserBean userBean, int flag)
    {
        this.userBean = userBean;
        if (userBean != null)
            this.phone = userBean.getPhone();
        this.flag = flag;
    }


    public UserBean getUserBean()
    {
        return userBean;
    }

    public void setUserBean(UserBean userBean)
    {
        this.userBean = userBean;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public int getFlag()
    {
        return flag;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    @Override
    public String toString()
    {
        return "ProfileUpdateEventBean{" +
                "userBean=" + userBean +
                ", phone='" + phone + '\'' +
                ", flag=" + flag +
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
        dest.writeParcelable(this.userBean, flags);
        dest.writeString(this.phone);
        dest.writeInt(this.flag);
    }

    protected ProfileUpdateEventBean(Parcel in)
    {
        this.userBean = in.readParcelable(UserBean.class.getClassLoader());
        this.phone = in.readString();
        this.flag = in.readInt();
    }

    public static final Creator<ProfileUpdateEventBean> CREATOR = new Creator<ProfileUpdateEventBean>()
    {
        @Override
        public ProfileUpdateEventBean createFromParcel(Parcel source)
        {
            return new ProfileUpdateEventBean(source);
        }

        @Override
        public ProfileUpdateEventBean[] newArray(int size)
        {
            return new ProfileUpdateEventBean[size];
        }
    };
}
