package com.lwk.familycontact.storage.db.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by LWK
 * TODO 短视频远程下载数据实体类
 * 2016/10/18
 */
@DatabaseTable(tableName = ShortVideoDbConfig.TABLE_NAME)
public class ShortVideoBean implements Parcelable
{
    @DatabaseField(columnName = ShortVideoDbConfig.MESSAGE_ID, id = true)
    private String msg_id;

    @DatabaseField(columnName = ShortVideoDbConfig.LOCAL_URL)
    private String local_url;

    public ShortVideoBean()
    {
    }

    public ShortVideoBean(String msg_id, String local_url)
    {
        this.msg_id = msg_id;
        this.local_url = local_url;
    }

    public String getLocal_url()
    {
        return local_url;
    }

    public void setLocal_url(String local_url)
    {
        this.local_url = local_url;
    }

    public String getMsg_id()
    {
        return msg_id;
    }

    public void setMsg_id(String msg_id)
    {
        this.msg_id = msg_id;
    }

    @Override
    public String toString()
    {
        return "ShortVideoBean{" +
                "msg_id='" + msg_id + '\'' +
                ", local_url='" + local_url + '\'' +
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
        dest.writeString(this.msg_id);
        dest.writeString(this.local_url);
    }

    protected ShortVideoBean(Parcel in)
    {
        this.msg_id = in.readString();
        this.local_url = in.readString();
    }

    public static final Parcelable.Creator<ShortVideoBean> CREATOR = new Parcelable.Creator<ShortVideoBean>()
    {
        @Override
        public ShortVideoBean createFromParcel(Parcel source)
        {
            return new ShortVideoBean(source);
        }

        @Override
        public ShortVideoBean[] newArray(int size)
        {
            return new ShortVideoBean[size];
        }
    };
}
