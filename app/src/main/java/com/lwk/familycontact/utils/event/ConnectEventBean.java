package com.lwk.familycontact.utils.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LWK
 * TODO 环信连接状态改变后的通知
 * 2016/8/8
 */
public class ConnectEventBean implements Parcelable
{
    private boolean isConnect;

    private int errorMsgId;

    public ConnectEventBean(boolean isConnect)
    {
        this.isConnect = isConnect;
    }

    public ConnectEventBean(boolean isConnect, int errorMsgId)
    {
        this.isConnect = isConnect;
        this.errorMsgId = errorMsgId;
    }

    public boolean isConnect()
    {
        return isConnect;
    }

    public void setConnect(boolean connect)
    {
        isConnect = connect;
    }

    public int getErrorMsgId()
    {
        return errorMsgId;
    }

    public void setErrorMsgId(int errorMsgId)
    {
        this.errorMsgId = errorMsgId;
    }

    @Override
    public String toString()
    {
        return "ConnectEventBean{" +
                "isConnect=" + isConnect +
                ", errorMsgId=" + errorMsgId +
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
        dest.writeByte(this.isConnect ? (byte) 1 : (byte) 0);
        dest.writeInt(this.errorMsgId);
    }

    public ConnectEventBean()
    {
    }

    protected ConnectEventBean(Parcel in)
    {
        this.isConnect = in.readByte() != 0;
        this.errorMsgId = in.readInt();
    }

    public static final Parcelable.Creator<ConnectEventBean> CREATOR = new Parcelable.Creator<ConnectEventBean>()
    {
        @Override
        public ConnectEventBean createFromParcel(Parcel source)
        {
            return new ConnectEventBean(source);
        }

        @Override
        public ConnectEventBean[] newArray(int size)
        {
            return new ConnectEventBean[size];
        }
    };
}
