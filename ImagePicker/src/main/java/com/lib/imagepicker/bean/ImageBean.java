package com.lib.imagepicker.bean;

import android.os.Parcel;
import android.os.Parcelable;


import com.lib.imagepicker.utils.OtherUtils;

import java.io.File;

/**
 * Function:图片实体类
 */
public class ImageBean implements Parcelable
{
    //图片id
    private String imageId;
    //缩略图地址
    private String thumbnailPath;
    //原图地址
    private String imagePath;
    //缩略图文件是否存在
    private boolean isThumbFileExist;
    //最后修改时间
    private Long lastModified;
    //所在文件夹的id
    private String floderId;

    public ImageBean()
    {
    }

    public String getImageId()
    {
        return imageId;
    }

    public void setImageId(String imageId)
    {
        this.imageId = imageId;
    }

    public String getThumbnailPath()
    {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath)
    {
        this.thumbnailPath = thumbnailPath;
        if (OtherUtils.isEmpty(thumbnailPath))
        {
            setIsThumbFileExist(false);
            return;
        }
        if (new File(thumbnailPath).exists())
            setIsThumbFileExist(true);
        else
            setIsThumbFileExist(false);
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public boolean isThumbFileExist()
    {
        return isThumbFileExist;
    }

    public void setIsThumbFileExist(boolean isThumbFileExist)
    {
        this.isThumbFileExist = isThumbFileExist;
    }

    public Long getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Long lastModified)
    {
        this.lastModified = lastModified;
    }

    public String getFloderId()
    {
        return floderId;
    }

    public void setFloderId(String floderId)
    {
        this.floderId = floderId;
    }

    @Override
    public String toString()
    {
        return "ImageBean{" +
                "imageId='" + imageId + '\'' +
                ", thumbnailPath='" + thumbnailPath + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", isThumbFileExist=" + isThumbFileExist +
                ", lastModified=" + lastModified +
                ", floderId=" + floderId +
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
        dest.writeString(this.imageId);
        dest.writeString(this.thumbnailPath);
        dest.writeString(this.imagePath);
        dest.writeByte(isThumbFileExist ? (byte) 1 : (byte) 0);
        dest.writeValue(this.lastModified);
        dest.writeString(this.floderId);
    }

    protected ImageBean(Parcel in)
    {
        this.imageId = in.readString();
        this.thumbnailPath = in.readString();
        this.imagePath = in.readString();
        this.isThumbFileExist = in.readByte() != 0;
        this.lastModified = (Long) in.readValue(Long.class.getClassLoader());
        this.floderId = in.readString();
    }

    public static final Parcelable.Creator<ImageBean> CREATOR = new Parcelable.Creator<ImageBean>()
    {
        @Override
        public ImageBean createFromParcel(Parcel source)
        {
            return new ImageBean(source);
        }

        @Override
        public ImageBean[] newArray(int size)
        {
            return new ImageBean[size];
        }
    };
}
