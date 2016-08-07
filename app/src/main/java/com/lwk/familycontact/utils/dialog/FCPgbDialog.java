package com.lwk.familycontact.utils.dialog;

import android.view.View;
import android.widget.TextView;

import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 定制的ProgressDialog
 * 2016/8/5
 */
public class FCPgbDialog extends FCBaseDialog
{
    private String mContent;
    private int mContentResId = -1;

    public FCPgbDialog()
    {
    }

    public FCPgbDialog setContent(String content)
    {
        this.mContent = content;
        return this;
    }

    public FCPgbDialog setContentResId(int contentResId)
    {
        this.mContentResId = contentResId;
        return this;
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.dialog_progress;
    }

    @Override
    public void initUI(View layout)
    {
        TextView tvContent = (TextView) layout.findViewById(R.id.tv_pgb_dialog_content);
        if (StringUtil.isNotEmpty(mContent))
        {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(mContent);
        } else if (mContentResId != -1)
        {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(mContentResId);
        }
    }


}
