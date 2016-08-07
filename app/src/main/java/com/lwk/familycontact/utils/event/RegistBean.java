package com.lwk.familycontact.utils.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LWK
 * TODO 注册成功后传递给登录界面的数据
 * 2016/8/5
 */
public class RegistBean implements Parcelable
{
    private String phone;

    private String pwd;

    public RegistBean(String phone, String pwd)
    {
        this.phone = phone;
        this.pwd = pwd;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    @Override
    public String toString()
    {
        return "RegistBean{" +
                "phone='" + phone + '\'' +
                ", pwd='" + pwd + '\'' +
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
        dest.writeString(this.phone);
        dest.writeString(this.pwd);
    }

    protected RegistBean(Parcel in)
    {
        this.phone = in.readString();
        this.pwd = in.readString();
    }

    public static final Parcelable.Creator<RegistBean> CREATOR = new Parcelable.Creator<RegistBean>()
    {
        @Override
        public RegistBean createFromParcel(Parcel source)
        {
            return new RegistBean(source);
        }

        @Override
        public RegistBean[] newArray(int size)
        {
            return new RegistBean[size];
        }
    };
}
