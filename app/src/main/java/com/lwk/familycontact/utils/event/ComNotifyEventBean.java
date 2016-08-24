package com.lwk.familycontact.utils.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LWK
 * TODO 常规通知
 * 2016/8/24
 */
public class ComNotifyEventBean implements Parcelable
{
    private int flag;

    public ComNotifyEventBean(int flag)
    {
        this.flag = flag;
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
        return "ComNotifyEventBean{" +
                "flag=" + flag +
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
        dest.writeInt(this.flag);
    }

    protected ComNotifyEventBean(Parcel in)
    {
        this.flag = in.readInt();
    }

    public static final Parcelable.Creator<ComNotifyEventBean> CREATOR = new Parcelable.Creator<ComNotifyEventBean>()
    {
        @Override
        public ComNotifyEventBean createFromParcel(Parcel source)
        {
            return new ComNotifyEventBean(source);
        }

        @Override
        public ComNotifyEventBean[] newArray(int size)
        {
            return new ComNotifyEventBean[size];
        }
    };
}
