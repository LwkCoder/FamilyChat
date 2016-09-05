package com.lib.rcvadapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.lib.rcvadapter.bean.RcvSecBean;
import com.lib.rcvadapter.holder.RcvHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView带Section通用适配器
 * [泛型S代表Section数据，泛型C代表普通数据]
 */
public abstract class RcvSectionAdapter<S, C> extends RcvSingleAdapter<RcvSecBean<S, C>>
{
    protected static final int VIEW_TYPE_SECTION = Integer.MAX_VALUE - 3;
    protected int mSectionLayoutId;
    protected Map<S, Integer> mSectionMap = new HashMap<>();

    public RcvSectionAdapter(Context context, int sectionLayoutId, int contentLayoutId, List<RcvSecBean<S, C>> datas)
    {
        super(context, contentLayoutId, datas);
        this.mSectionLayoutId = sectionLayoutId;
        setSectionMap();
    }

    //根据数据重新记录Section对应的位置
    protected void setSectionMap()
    {
        mSectionMap.clear();
        int p = 0;
        for (RcvSecBean<S, C> secBean : mDataList)
        {
            if (secBean.isSection())
            {
                S section = secBean.getSection();
                if (secBean != null)
                    mSectionMap.put(section, p + getHeadCounts());
            }
            p++;
        }
    }

    @Override
    public void addHeadView(View... headViews)
    {
        super.addHeadView(headViews);
        setSectionMap();
    }

    @Override
    public int getItemViewType(int position)
    {
        return isInSectionPos(position) ? VIEW_TYPE_SECTION : super.getItemViewType(position);
    }

    @Override
    public RcvHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_SECTION)
        {
            RcvHolder holder = RcvHolder.get(mContext, parent, mSectionLayoutId);
            setSectionListener(parent, holder, viewType);
            return holder;
        } else
            return super.onCreateViewHolder(parent, viewType);
    }

    private void setSectionListener(final ViewGroup parent, final RcvHolder viewHolder, int viewType)
    {
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mSectionClickListener != null)
                {
                    int position = viewHolder.getAdapterPosition();
                    mSectionClickListener.onSectionClick(parent, viewHolder, mDataList.get(position - getHeadCounts()).getSection(), position);
                }
            }
        });
    }

    @Override
    protected void setListener(ViewGroup parent, final RcvHolder viewHolder, int viewType)
    {
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener != null)
                {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, mDataList.get(position - getHeadCounts()).getContent(), position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (mOnItemLongClickListener != null)
                {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemLongClickListener.onItemLongClick(v, viewHolder, mDataList.get(position - getHeadCounts()).getContent(), position);
                }
                return false;
            }
        });
    }

    @Override
    public void setData(RcvHolder holder, RcvSecBean<S, C> itemData, int position)
    {
        if (holder.getItemViewType() == VIEW_TYPE_SECTION)
            setSectionLayout(holder, itemData.getSection(), position);
        else
            setContentLayout(holder, itemData.getContent(), position);
    }

    /**
     * 设置Section所属布局数据
     */
    public abstract void setSectionLayout(RcvHolder holder, S sectionData, int position);

    /**
     * 设置正常内容所属布局数据
     */
    public abstract void setContentLayout(RcvHolder holder, C contentData, int position);

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager)
        {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
            {
                @Override
                public int getSpanSize(int position)
                {
                    if (isInSectionPos(position)
                            || isInHeadViewPos(position)
                            || isInFootViewPos(position)
                            || isInLoadMorePos(position)
                            || isInEmptyStatus())
                        return gridManager.getSpanCount();
                    else
                        return 1;
                }
            });
            gridManager.setSpanCount(gridManager.getSpanCount());
        }
    }

    @Override
    public void onViewAttachedToWindow(RcvHolder holder)
    {
        if (holder.getItemViewType() == VIEW_TYPE_SECTION)
        {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams)
            {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
        super.onViewAttachedToWindow(holder);
    }

    /**
     * 根据Section查找其对应的position
     * [没有对应的位置会返回-1]
     */
    public int getPositionBySection(S section)
    {
        return mSectionMap.containsKey(section) ? mSectionMap.get(section) : -1;
    }

    protected boolean isInSectionPos(int p)
    {
        return mSectionMap.containsValue(p);
    }

    @Override
    public void refreshDatas(List<RcvSecBean<S, C>> data)
    {
        mDataList.clear();
        if (data != null && data.size() > 0)
            mDataList.addAll(data);
        setSectionMap();
        notifyDataSetChanged();
    }

    @Override
    public int addData(RcvSecBean<S, C> scRcvSecBean)
    {
        if (scRcvSecBean != null)
        {
            mDataList.add(scRcvSecBean);
            setSectionMap();
            notifyDataSetChanged();
        }
        return mDataList.indexOf(scRcvSecBean);
    }

    @Override
    public void addDatas(List<RcvSecBean<S, C>> data)
    {
        if (data != null && data.size() > 0)
        {
            mDataList.addAll(data);
            setSectionMap();
            notifyDataSetChanged();
        }
    }

    /*****************************
     * 监听
     ***************************************/

    private onSectionClickListener<S> mSectionClickListener;

    public interface onSectionClickListener<S>
    {
        void onSectionClick(View view, RcvHolder holder, S section, int position);
    }

    public void setOnSectionClickListenr(onSectionClickListener<S> sectionClickListenr)
    {
        this.mSectionClickListener = sectionClickListenr;
    }
}
