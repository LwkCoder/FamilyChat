package com.lwk.familycontact.utils.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;

import com.lib.base.utils.ViewFinder;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO Dialog基类
 * 2016/11/4
 */
public abstract class FCBaseDialog implements FCBaseDialogInterface
{
    protected Activity mContext;
    protected Dialog mDialog;
    protected View mContentView;
    protected DialogInterface.OnDismissListener mDismissListener;
    protected DialogInterface.OnShowListener mOnShowListener;
    protected DialogInterface.OnCancelListener mCancelListener;
    protected ViewFinder mViewFinder;

    public FCBaseDialog(Activity context)
    {
        this.mContext = context;
    }

    @Override
    public void show()
    {
        if (mDialog != null)
            return;

        //初始化dialog
        mDialog = new Dialog(mContext, R.style.BaseMyDialog);
        mDialog.setCancelable(isCancelable());
        mDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside());
        mContentView = mContext.getLayoutInflater().inflate(getContentViewId(), (ViewGroup) mContext.findViewById(android.R.id.content), false);
        mDialog.setContentView(mContentView);
        //设置监听
        if (mOnShowListener != null)
            mDialog.setOnShowListener(mOnShowListener);
        if (mCancelListener != null)
            mDialog.setOnCancelListener(mCancelListener);
        if (mDismissListener != null)
            mDialog.setOnDismissListener(mDismissListener);
        //初始化布局
        mViewFinder = new ViewFinder(mContentView);
        initUI(mContentView);
        //执行show
        showDialog();
    }

    //真正执行show的方法
    private void showDialog()
    {
        if (!isDialogShowing())
            mDialog.show();
    }

    @Override
    public void dismiss()
    {
        if (isDialogShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public boolean isDialogShowing()
    {
        return mDialog != null ? mDialog.isShowing() : false;
    }

    /**
     * 查找View
     */
    protected <T extends View> T findView(int resId)
    {
        return mViewFinder.findView(resId);
    }

    /**
     * 添加点击监听到onClick()中
     */
    protected void addClick(int id, View.OnClickListener listener)
    {
        mViewFinder.addClick(id, listener);
    }

    /**
     * 添加点击监听到onClick()中
     */
    protected void addClick(View view, View.OnClickListener listener)
    {
        mViewFinder.addClick(view, listener);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener l)
    {
        this.mDismissListener = l;
    }

    public void setOnShowListener(DialogInterface.OnShowListener l)
    {
        this.mOnShowListener = l;
    }

    public void setCancelListener(DialogInterface.OnCancelListener l)
    {
        this.mCancelListener = l;
    }
}
