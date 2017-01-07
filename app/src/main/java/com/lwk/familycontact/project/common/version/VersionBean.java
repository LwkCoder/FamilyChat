package com.lwk.familycontact.project.common.version;

/**
 * 版本信息
 */
public class VersionBean
{
    private int code;
    private String desc_title;
    private String desc_msg;
    private String path;
    private int force_update;

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getDesc_title()
    {
        return desc_title;
    }

    public void setDesc_title(String desc_title)
    {
        this.desc_title = desc_title;
    }

    public String getDesc_msg()
    {
        return desc_msg;
    }

    public void setDesc_msg(String desc_msg)
    {
        this.desc_msg = desc_msg;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isForceUpdate()
    {
        return force_update == 1;
    }

    public void setForceUpdate(int i)
    {
        this.force_update = i;
    }

    @Override
    public String toString()
    {
        return "VersionBean{" +
                "code=" + code +
                ", desc_title='" + desc_title + '\'' +
                ", desc_msg='" + desc_msg + '\'' +
                ", path='" + path + '\'' +
                ", force_update=" + force_update +
                '}';
    }
}
