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

    public InviteBean()
    {
    }

    public InviteBean(String opPhone, long stamp)
    {
        this.opPhone = opPhone;
        this.stamp = stamp;
        this.status = InviteStatus.ORIGIN;
    }

    public InviteBean(String opPhone, long stamp, int status)
    {
        this.opPhone = opPhone;
        this.stamp = stamp;
        this.status = status;
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

    @Override
    public String toString()
    {
        return "InviteBean{" +
                "opPhone='" + opPhone + '\'' +
                ", stamp=" + stamp +
                ", status=" + status +
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
    }

    protected InviteBean(Parcel in)
    {
        this.opPhone = in.readString();
        this.stamp = in.readLong();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<InviteBean> CREATOR = new Parcelable.Creator<InviteBean>()
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
