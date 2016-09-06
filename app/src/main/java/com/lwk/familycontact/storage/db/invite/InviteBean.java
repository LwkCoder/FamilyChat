package com.lwk.familycontact.storage.db.invite;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 邀请信息实体类
 */
@DatabaseTable(tableName = InviteDbConfig.TABLE_NAME)
public class InviteBean implements Parcelable
{
    @DatabaseField(columnName = InviteDbConfig.OP_PHONE)
    private String opPhone;

    @DatabaseField(columnName = InviteDbConfig.STAMP, id = true)
    private long stamp;

    @DatabaseField(columnName = InviteDbConfig.STATUS)
    private int status;

    @DatabaseField(columnName = InviteDbConfig.READ)
    private boolean read;

    public InviteBean()
    {
    }

    public InviteBean(String opPhone, long stamp)
    {
        this.opPhone = opPhone;
        this.stamp = stamp;
        this.status = InviteStatus.ORIGIN;
        this.read = false;
    }

    public InviteBean(String opPhone, long stamp, int status)
    {
        this.opPhone = opPhone;
        this.stamp = stamp;
        this.status = status;
        this.read = false;
    }

    public String getOpPhone()
    {
        return opPhone;
    }

    public void setOpPhone(String opPhone)
    {
        this.opPhone = opPhone;
    }

    public long getStamp()
    {
        return stamp;
    }

    public void setStamp(long stamp)
    {
        this.stamp = stamp;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public boolean isRead()
    {
        return read;
    }

    public void setRead(boolean read)
    {
        this.read = read;
    }

    @Override
    public String toString()
    {
        return "InviteBean{" +
                "opPhone='" + opPhone + '\'' +
                ", stamp=" + stamp +
                ", status=" + status +
                ", read=" + read +
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
        dest.writeString(this.opPhone);
        dest.writeLong(this.stamp);
        dest.writeInt(this.status);
        dest.writeByte(this.read ? (byte) 1 : (byte) 0);
    }

    protected InviteBean(Parcel in)
    {
        this.opPhone = in.readString();
        this.stamp = in.readLong();
        this.status = in.readInt();
        this.read = in.readByte() != 0;
    }

    public static final Creator<InviteBean> CREATOR = new Creator<InviteBean>()
    {
        @Override
        public InviteBean createFromParcel(Parcel source)
        {
            return new InviteBean(source);
        }

        @Override
        public InviteBean[] newArray(int size)
        {
            return new InviteBean[size];
        }
    };
}
