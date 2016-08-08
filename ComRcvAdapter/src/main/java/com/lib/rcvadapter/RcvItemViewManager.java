package com.lib.rcvadapter;

import android.util.SparseArray;

import com.lib.rcvadapter.holder.RcvHolder;
import com.lib.rcvadapter.view.RcvBaseItemView;

/**
 * Function:RecyclerView子布局管理器
 */
public class RcvItemViewManager<T>
{
    private SparseArray<RcvBaseItemView<T>> mAllItemViews = new SparseArray();

    public int getItemViewCount()
    {
        return mAllItemViews.size();
    }

    public RcvItemViewManager<T> addItemView(RcvBaseItemView<T> itemView)
    {
        int viewType = mAllItemViews.size();
        if (itemView != null)
        {
            mAllItemViews.put(viewType, itemView);
            viewType++;
        }
        return this;
    }

    public RcvItemViewManager<T> addItemView(int viewType, RcvBaseItemView<T> itemView)
    {
        if (mAllItemViews.get(viewType) != null)
        {
            throw new IllegalArgumentException(
                    "An ItemView is already registered for the viewType = "
                            + viewType
                            + ". Already registered ItemView is "
                            + mAllItemViews.get(viewType));
        }
        mAllItemViews.put(viewType, itemView);
        return this;
    }

    public RcvItemViewManager<T> removeItemView(RcvBaseItemView<T> itemView)
    {
        if (itemView == null)
        {
            throw new NullPointerException("ItemViewis null");
        }

        int indexToRemove = mAllItemViews.indexOfValue(itemView);
        if (indexToRemove >= 0)
            mAllItemViews.removeAt(indexToRemove);
        return this;
    }

    public RcvItemViewManager<T> removeItemView(int itemType)
    {
        int indexToRemove = mAllItemViews.indexOfKey(itemType);

        if (indexToRemove >= 0)
            mAllItemViews.removeAt(indexToRemove);
        return this;
    }

    public int getItemViewType(T item, int position)
    {
        int itemViewCounts = mAllItemViews.size();
        for (int i = itemViewCounts - 1; i >= 0; i--)
        {
            RcvBaseItemView<T> itemView = mAllItemViews.valueAt(i);
            if (itemView.isForViewType(item, position))
                return mAllItemViews.keyAt(i);
        }
        throw new IllegalArgumentException("No ItemView added that matches position=" + position + " in data source");
    }

    public void setData(RcvHolder holder, T item, int position)
    {
        int itemViewCounts = mAllItemViews.size();
        for (int i = 0; i < itemViewCounts; i++)
        {
            RcvBaseItemView<T> itemView = mAllItemViews.valueAt(i);

            if (itemView.isForViewType(item, position))
            {
                itemView.setData(holder, item, position);
                return;
            }
        }
        throw new IllegalArgumentException(
                "No RcvItemViewManager added that matches position=" + position + " in data source");
    }


    public int getItemViewLayoutId(int viewType)
    {
        return mAllItemViews.get(viewType).getItemViewLayoutId();
    }

    public int getItemViewType(RcvBaseItemView itemView)
    {
        return mAllItemViews.indexOfValue(itemView);
    }

    public int getItemViewLayoutId(T item, int position)
    {
        int itemViewCounts = mAllItemViews.size();
        for (int i = itemViewCounts - 1; i >= 0; i--)
        {
            RcvBaseItemView<T> itemView = mAllItemViews.valueAt(i);
            if (itemView.isForViewType(item, position))
                return itemView.getItemViewLayoutId();
        }
        throw new IllegalArgumentException("No ItemView added that matches position=" + position + " in data source");
    }
}
