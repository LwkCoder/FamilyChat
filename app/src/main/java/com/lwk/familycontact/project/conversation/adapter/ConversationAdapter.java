package com.lwk.familycontact.project.conversation.adapter;

import android.content.Context;

import com.lib.rcvadapter.RcvSingleAdapter;
import com.lib.rcvadapter.holder.RcvHolder;

import java.util.List;

/**
 * Created by LWK
 * TODO 会话片段适配器
 * 2016/8/8
 */
public class ConversationAdapter extends RcvSingleAdapter<String>
{
    public ConversationAdapter(Context context, int layoutId, List<String> datas)
    {
        super(context, layoutId, datas);
    }

    @Override
    public void setData(RcvHolder holder, String itemData, int position)
    {

    }
}
