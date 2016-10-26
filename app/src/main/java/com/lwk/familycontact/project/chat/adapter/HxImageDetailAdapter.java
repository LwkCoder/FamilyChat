package com.lwk.familycontact.project.chat.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.lib.base.utils.ScreenUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.common.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by LWK
 * TODO 查看大图界面适配器
 * 2016/9/28
 */
public class HxImageDetailAdapter extends PagerAdapter
{
    private final int mScreenWidth = ScreenUtils.getScreenWidth(FCApplication.getInstance());
    private final int mScreenHeight = ScreenUtils.getScreenHeight(FCApplication.getInstance());
    private Activity mActivity;
    private List<EMMessage> mDataList = new ArrayList<>();
    public PhotoViewClickListener mListener;

    public HxImageDetailAdapter(Activity activity, List<EMMessage> list)
    {
        this.mActivity = activity;
        if (list != null && list.size() > 0)
            mDataList.addAll(list);
    }

    public void setData(List<EMMessage> list)
    {
        mDataList.clear();
        if (list != null && list.size() > 0)
            mDataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mDataList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        PhotoView photoView = new PhotoView(mActivity);
        photoView.setBackgroundColor(Color.BLACK);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setEnabled(true);
        EMImageMessageBody messageBody = (EMImageMessageBody) mDataList.get(position).getBody();
        String localUrl = messageBody.getLocalUrl();
        String remoteUrl = messageBody.getRemoteUrl();
        if (StringUtil.isNotEmpty(localUrl) && new File(localUrl).exists())
            CommonUtils.getInstance().getImageDisplayer().display(mActivity, photoView, localUrl, mScreenWidth, mScreenHeight
                    , R.drawable.pic_image_detail_place_holder, R.drawable.pic_image_detail_fail);
        else
            CommonUtils.getInstance().getImageDisplayer().display(mActivity, photoView, remoteUrl, mScreenWidth, mScreenHeight
                    , R.drawable.pic_image_detail_place_holder, R.drawable.pic_image_detail_fail);

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener()
        {
            @Override
            public void onPhotoTap(View view, float x, float y)
            {
                if (mListener != null)
                    mListener.OnPhotoTapListener(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener)
    {
        this.mListener = listener;
    }

    public interface PhotoViewClickListener
    {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
