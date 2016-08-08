package com.lib.rcvadapter;

import android.content.Context;

import com.lib.rcvadapter.holder.RcvHolder;
import com.lib.rcvadapter.view.RcvBaseItemView;

import java.util.List;

/**
 * Function:RecycleView通用布局适配器【所有子布局样式统一】
 */
public abstract class RcvSingleAdapter<T> extends RcvMutilAdapter<T>
{
    protected int mLayoutId;

    public RcvSingleAdapter(Context context, final int layoutId, List<T> datas)
    {
        super(context, datas);
        this.mLayoutId = layoutId;
        addItemView(new RcvBaseItemView<T>()
        {
            @Override
            public int getItemViewLayoutId()
            {
                return mLayoutId;
            }

            @Override
            public boolean isForViewType(T item, int position)
            {
                return true;
            }

            @Override
            public void setData(RcvHolder holder, T t, int position)
            {
                RcvSingleAdapter.this.setData(holder, t, position);
            }
        });
    }

    public abstract void setData(RcvHolder holder, T itemData, int position);
}
