package com.lib.rcvadapter;

import android.content.Context;

import com.lib.rcvadapter.bean.RcvSecBean;
import com.lib.rcvadapter.impl.RcvSortSectionImpl;

import java.util.HashSet;
import java.util.List;

/**
 * RecyclerView联系人样式适配器
 * [泛型T需要实现接口RcvSortSectionImpl，用以指定section]
 * [传入数据前请先对数据进行排序]
 */
public abstract class RcvSortSectionAdatper<T> extends RcvSectionAdapter<String, T>
{
    //数据默认的section
    protected String mDataDefSectionChar;

    public RcvSortSectionAdatper(Context context, int sectionLayoutId, int contentLayoutId, List<T> datas)
    {
        this(context, sectionLayoutId, contentLayoutId, RcvSortSectionImpl.DEF_SECTION, datas);
    }

    public RcvSortSectionAdatper(Context context, int sectionLayoutId, int contentLayoutId, String defSectionChar, List<T> datas)
    {
        super(context, sectionLayoutId, contentLayoutId, null);
        this.mDataDefSectionChar = defSectionChar;
        transDatas(datas);
    }

    //处理数据，将其转换为内部识别数据
    private void transDatas(List<T> dataList)
    {
        if (dataList == null || dataList.size() == 0)
            return;
        mDataList.clear();
        HashSet<String> sectionSet = new HashSet<>();
        for (T t : dataList)
        {
            RcvSortSectionImpl sectionHead = (RcvSortSectionImpl) t;
            String section = sectionHead.getSection();
            if (section == null || section.length() == 0)
                section = mDataDefSectionChar;

            if (!sectionSet.contains(section))
            {
                sectionSet.add(section);
                mDataList.add(new RcvSecBean<String, T>(true, section, null));
            }

            mDataList.add(new RcvSecBean<String, T>(false, null, t));
        }
        setSectionMap();
        notifyDataSetChanged();
    }

    /**
     * 刷新数据的方法
     */
    public void refreshDataInSection(List<T> dataList)
    {
        transDatas(dataList);
    }
}
