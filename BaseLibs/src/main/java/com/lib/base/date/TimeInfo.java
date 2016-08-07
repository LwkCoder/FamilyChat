package com.lib.base.date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Function:计时时间对象
 */
public class TimeInfo implements Parcelable
{
    private long startTime;
    private long endTime;

    public TimeInfo()
    {
    }

    public TimeInfo(long startTime, long endTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    @Override
    public String toString()
    {
        return "TimeInfo{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
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
        dest.writeLong(this.startTime);
        dest.writeLong(this.endTime);
    }

    protected TimeInfo(Parcel in)
    {
        this.startTime = in.readLong();
        this.endTime = in.readLong();
    }

    public static final Parcelable.Creator<TimeInfo> CREATOR = new Parcelable.Creator<TimeInfo>()
    {
        public TimeInfo createFromParcel(Parcel source)
        {
            return new TimeInfo(source);
        }

        public TimeInfo[] newArray(int size)
        {
            return new TimeInfo[size];
        }
    };
}
