package com.lib.rcvadapter.bean;

/**
 */
public class RcvSecBean<S,C>
{
    private boolean isSection;

    private S section;

    private C content;

    public RcvSecBean(boolean isSection, S section, C content)
    {
        this.isSection = isSection;
        this.section = section;
        this.content = content;
    }

    public boolean isSection()
    {
        return isSection;
    }

    public void setIsSection(boolean section)
    {
        isSection = section;
    }

    public S getSection()
    {
        return section;
    }

    public void setSection(S section)
    {
        this.section = section;
    }

    public C getContent()
    {
        return content;
    }

    public void setContent(C content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "RcvSecBean{" +
                "isSection=" + isSection +
                ", section=" + section +
                ", content=" + content +
                '}';
    }
}
