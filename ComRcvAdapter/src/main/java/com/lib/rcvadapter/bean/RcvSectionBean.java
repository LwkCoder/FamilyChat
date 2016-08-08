package com.lib.rcvadapter.bean;

/**
 * 索引标识实体类
 */
public class RcvSectionBean<T>
{
    private boolean isSection;

    private String sectionChar;

    private T t;

    public RcvSectionBean(boolean isSection, String sectionChar)
    {
        this.isSection = isSection;
        this.sectionChar = sectionChar;
    }

    public RcvSectionBean(T t)
    {
        this.t = t;
        this.isSection = false;
    }

    public boolean isSection()
    {
        return isSection;
    }

    public void setSection(boolean section)
    {
        isSection = section;
    }

    public String getSectionChar()
    {
        return sectionChar;
    }

    public void setSectionChar(String sectionChar)
    {
        this.sectionChar = sectionChar;
    }

    public T getT()
    {
        return t;
    }

    public void setT(T t)
    {
        this.t = t;
    }

    @Override
    public String toString()
    {
        return "RcvSectionBean{" +
                "isSection=" + isSection +
                ", sectionChar='" + sectionChar + '\'' +
                ", t=" + t +
                '}';
    }
}
