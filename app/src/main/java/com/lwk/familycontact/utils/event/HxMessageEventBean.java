package com.lwk.familycontact.utils.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by LWK
 * TODO 环信消息相关的通知
 * 2016/9/20
 */
public class HxMessageEventBean implements Parcelable
{
    /**
     * 接收到新消息的flag
     */
    public static final int NEW_MESSAGE_RECEIVED = 1;

    /**
     * 消息状态更改的flag
     */
    public static final int MESSAGE_STATUS_CHANGED = 2;

    public int flag;

    public List<EMMessage> msgList;

    public EMMessage.Status status;

    public HxMessageEventBean()
    {
    }

    public HxMessageEventBean(int flag, List<EMMessage> msgList)
    {
        this.flag = flag;
        this.msgList = msgList;
    }

    public HxMessageEventBean(int flag, List<EMMessage> msgList, EMMessage.Status status)
    {
        this.flag = flag;
        this.msgList = msgList;
        this.status = status;
    }

    public int getFlag()
    {
        return flag;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public List<EMMessage> getMsgList()
    {
        return msgList;
    }

    public void setMsgList(List<EMMessage> msgList)
    {
        this.msgList = msgList;
    }

    public Object getStatus()
    {
        return status;
    }

    public void setStatus(EMMessage.Status status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "HxMessageEventBean{" +
                "flag=" + flag +
                ", msgList=" + msgList +
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
        dest.writeInt(this.flag);
        dest.writeTypedList(this.msgList);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
    }

    protected HxMessageEventBean(Parcel in)
    {
        this.flag = in.readInt();
        this.msgList = in.createTypedArrayList(EMMessage.CREATOR);
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : EMMessage.Status.values()[tmpStatus];
    }

    public static final Parcelable.Creator<HxMessageEventBean> CREATOR = new Parcelable.Creator<HxMessageEventBean>()
    {
        @Override
        public HxMessageEventBean createFromParcel(Parcel source)
        {
            return new HxMessageEventBean(source);
        }

        @Override
        public HxMessageEventBean[] newArray(int size)
        {
            return new HxMessageEventBean[size];
        }
    };
}
