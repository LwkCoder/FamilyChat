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
    private UserBean userBean;

    private String phone;

    public ProfileUpdateEventBean(UserBean userBean)
    {
        this.userBean = userBean;
        if (userBean != null)
            this.phone = userBean.getPhone();
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

    @Override
    public String toString()
    {
        return "ProfileUpdateEventBean{" +
                "userBean=" + userBean +
                ", phone='" + phone + '\'' +
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
    }

    protected ProfileUpdateEventBean(Parcel in)
    {
        this.userBean = in.readParcelable(UserBean.class.getClassLoader());
        this.phone = in.readString();
    }

    public static final Parcelable.Creator<ProfileUpdateEventBean> CREATOR = new Parcelable.Creator<ProfileUpdateEventBean>()
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
