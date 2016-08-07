package com.lib.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lib.base.R;


/**
 * 绘制背景大图的SurfaceView
 * 适用于界面背景大图的展示
 * [SurfaceView中绘制相当于在非UI线程中？？？]
 */
public class BgSurfaceView extends SurfaceView implements SurfaceHolder.Callback
        , Runnable
{
    private float mViewWidth = 0;
    private float mViewHeight = 0;
    private int mResourceId = 0;
    private Context mContext = null;
    private volatile boolean isRunning = false;
    private SurfaceHolder mSurfaceHolder = null;
    private boolean mHasDraw = false;

    public BgSurfaceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initRootSurfaceView(context, attrs, defStyleAttr, 0);
    }

    public BgSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initRootSurfaceView(context, attrs, 0, 0);
    }

    private void initRootSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        mContext = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BgSurfaceView, defStyleAttr, defStyleRes);
        int n = a.getIndexCount();
        mViewWidth = displayMetrics.widthPixels;
        mViewHeight = displayMetrics.heightPixels;
        for (int index = 0; index < n; index++)
        {
            int attr = a.getIndex(index);
            if (attr == R.styleable.BgSurfaceView_bg_Sfv)
                mResourceId = a.getResourceId(attr, 0);
            else if (attr == R.styleable.BgSurfaceView_width_Sfv)
                mViewWidth = a.getDimension(attr, displayMetrics.widthPixels);
            else if (attr == R.styleable.BgSurfaceView_height_Sfv)
                mViewHeight = a.getDimension(attr, displayMetrics.heightPixels);
        }
        a.recycle();
        setUpHolder();
    }

    private Bitmap getDrawBitmap(Context context, float width, float height)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mResourceId);
        Bitmap resultBitmap = zoomImage(bitmap, width, height);
        return resultBitmap;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        drawBackGround(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        isRunning = false;
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    @Override
    public void run()
    {
        while (isRunning)
        {
            synchronized (mSurfaceHolder)
            {
                if (!mSurfaceHolder.getSurface().isValid())
                {
                    continue;
                }
                if (mResourceId != 0)
                {
                    drawBackGround(mSurfaceHolder);
                }
            }
            isRunning = false;
            break;
        }
    }

    /**
     * 代码中设置大图的方法
     *
     * @param resId 资源id
     */
    public void setBgResourceId(int resId)
    {
        if (mResourceId == resId)
            return;
        mResourceId = resId;
        setUpHolder();
    }

    private void setUpHolder()
    {
        if (mResourceId != 0)
        {
            if (mHasDraw)
            {
                drawBackGround(mSurfaceHolder);
            } else
            {
                mSurfaceHolder = getHolder();
                mSurfaceHolder.addCallback(this);
                mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
            }
        }
    }

    private void drawBackGround(SurfaceHolder holder)
    {
        Canvas canvas = holder.lockCanvas();
        Bitmap bitmap = getDrawBitmap(mContext, mViewWidth, mViewHeight);
        if (bitmap == null)
            return;
        canvas.drawBitmap(bitmap, 0, 0, null);
        bitmap.recycle();
        holder.unlockCanvasAndPost(canvas);
        mHasDraw = true;
    }

    private Bitmap zoomImage(Bitmap bgimage, float newWidth, float newHeight)
    {
        if (bgimage == null)
            return bgimage;
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        if (bitmap != bgimage)
        {
            bgimage.recycle();
            bgimage = null;
        }
        return bitmap;
    }
}
