package com.lib.base.glide;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lib.base.R;
import com.lib.base.utils.ScreenUtils;

/**
 * Glide使用帮助类
 */
public class GlideHelper
{
    //默认加载中占位图
    public static final int IMG_LOADING = R.drawable.glide_default_picture;
    //默认加载失败占位图
    public static final int IMG_LOADFAIL = R.drawable.glide_default_picture;
    //硬盘缓存大小

    /**
     * 加载本地资源文件xml中的图片
     */
    public static void setImgByResId(Context c, int resId, ImageView img)
    {
        Glide.with(c).load(resId).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .animate(R.anim.glide_fade_in).into(img);
    }

    /**
     * 根据图片Uri加载图片
     */
    public static void setImgByUri(Context c, Uri uri, ImageView img)
    {
        Glide.with(c).load(uri).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(R.anim.glide_fade_in).into(img);
    }

    /**
     * 加载网络图片、sd卡里的文件【推荐使用指定分辨率的方法】
     */
    public static void setImgByUrl(Context c, String url, ImageView img)
    {
        setImgByUrl(c, url, img, 0, 0);
    }

    /**
     * 加载网络图片、sd卡里的文件【推荐使用指定分辨率的方法】
     * 【可自定义占位图、错误图】
     */
    public static void setImgByUrl(Context c, String url, ImageView img
            , int loadingImgResId, int loadFailImgResId)
    {
        setImgByUrl(c, url, img, loadingImgResId, loadFailImgResId, 0, 0, false);
    }

    /**
     * 加载网络图片、sd卡里的文件【推荐使用这个】
     * 【占位图均使用默认版】
     *
     * @param context   上下文
     * @param url       图片地址
     * @param img       待加载控件
     * @param maxWidth  横向最大分辨率
     * @param maxHeight 纵向最大分辨率
     */
    public static void setImgByUrlWithCompress(Context context, String url, ImageView img, int maxWidth, int maxHeight)
    {
        setImgByUrl(context, url, img, 0, 0, maxWidth, maxHeight, false);
    }

    /**
     * 加载网络图片、sd卡里的图片【推荐使用这个】
     *
     * @param c                上下文
     * @param url              图片地址
     * @param img              待加载控件
     * @param loadingImgResId  加载中占位图【无要求则传0，使用默认占位图】
     * @param loadFailImgResId 加载失败占位图【无要求则传0，使用默认占位图】
     * @param maxWidth         图片宽度最大分辨率【无要求就传0，默认最大值为屏幕横向最大分辨率】
     * @param maxHeight        图片高度最大分辨率【无要求则传0，默认最大值为屏幕纵向最大分辨率】
     * @param skipMemoryCache  是否跳过内存缓存
     */
    public static void setImgByUrl(Context c, String url, ImageView img
            , int loadingImgResId, int loadFailImgResId
            , int maxWidth, int maxHeight
            , boolean skipMemoryCache)
    {
        if (loadingImgResId <= 0)
            loadingImgResId = IMG_LOADING;
        if (loadFailImgResId <= 0)
            loadFailImgResId = IMG_LOADFAIL;
        if (maxWidth <= 0)
            maxWidth = ScreenUtils.getScreenWidth(c);
        if (maxHeight <= 0)
            maxHeight = ScreenUtils.getScreenHeight(c);

        Glide.with(c).load(url)
                .asBitmap()
                .centerCrop()
                .override(maxWidth, maxHeight)
                .placeholder(loadingImgResId)
                .error(loadFailImgResId)
                .skipMemoryCache(skipMemoryCache)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(R.anim.glide_fade_in)
                .into(img);
    }

    /**
     * 设置通知栏内的图片
     * 【调用此方法后无需再调用NotificationManager.notify()】
     *
     * @param c              上下文
     * @param url            图片地址
     * @param remoteView     通知栏自定义布局
     * @param imgId          待设置控件id
     * @param width          图片宽度
     * @param height         图片高度
     * @param notification   通知对象
     * @param notificationId 通知id
     */
    public static void setImgIntoNotification(Context c, String url
            , RemoteViews remoteView, int imgId
            , int width, int height
            , Notification notification, int notificationId)
    {
        NotificationTarget target = new NotificationTarget(c, remoteView, imgId, notification, notificationId);
        Glide.with(c).load(url).asBitmap().override(width, height).into(target);
    }

    /**
     * 直接获取图片的位图【适用于网络加载】
     *
     * @param c         上下文
     * @param url       地址
     * @param maxWidth  横向最大分辨率
     * @param maxHeight 纵向最大分辨率
     * @param callBack  回调
     */
    public static void getBitmap(Context c, String url, int maxWidth, int maxHeight, final LoadBitmapCallBack callBack)
    {
        Glide.with(c).load(url).asBitmap().override(maxWidth, maxHeight).into(new SimpleTarget<Bitmap>()
        {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
            {
                Bitmap bitmap = null;
                if (resource != null)
                    bitmap = resource.copy(Bitmap.Config.ARGB_8888, true);
                if (callBack != null)
                    callBack.onLoadFinish(bitmap);
            }
        });
    }

    /**
     * 直接获取图片的位图
     *
     * @param c         上下文
     * @param url       地址
     * @param maxWidth  横向最大分辨率
     * @param maxHeight 纵向最大分辨率
     * @param defResId  失败后默认图片的资源id
     * @return
     */
    public static Bitmap getBitmap(Context c, String url, int maxWidth, int maxHeight, int... defResId)
    {
        Bitmap result = null;
        try
        {
            result = Glide.with(c).load(url).asBitmap().centerCrop().into(maxWidth, maxHeight).get();
        } catch (Exception e)
        {
            if (defResId != null && defResId.length > 0)
            {
                BitmapDrawable drawable = (BitmapDrawable) c.getResources().getDrawable(defResId[0]);
                result = drawable.getBitmap();
            }
        }
        return result;
    }

    /**
     * 直接获取图片的位图
     *
     * @param c         上下文
     * @param resId     本地图片资源id
     * @param maxWidth  横向最大分辨率
     * @param maxHeight 纵向最大分辨率
     * @return
     */
    public static Bitmap getBitmap(Context c, int resId, int maxWidth, int maxHeight)
    {
        Bitmap result = null;
        try
        {
            result = Glide.with(c).load(resId).asBitmap().centerCrop().into(maxWidth, maxHeight).get();
        } catch (Exception e)
        {
            BitmapDrawable drawable = (BitmapDrawable) c.getResources().getDrawable(resId);
            result = drawable.getBitmap();
        }
        return result;
    }
}
