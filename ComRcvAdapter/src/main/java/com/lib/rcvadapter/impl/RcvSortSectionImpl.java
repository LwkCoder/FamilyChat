package com.lib.rcvadapter.impl;

/**
 * 被索引的数据需要实现此接口来确定Section字符
 */
public interface RcvSortSectionImpl
{
    /**
     * 默认的section
     */
    String DEF_SECTION = "#";

    /**
     * 实体类实现此接口后需要指定section
     */
    String getSection();
}
