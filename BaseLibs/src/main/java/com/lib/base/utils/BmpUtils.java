package com.lib.base.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.lib.base.log.KLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Function:图片压缩工具类
 */
public class BmpUtils
{
    /**
     * 压缩图片并另存为新图
     * 【只需要指定最大宽度px，压缩过程中会按照原来的宽高比压缩】
     *
     * @param originFilePath 图片路径
     * @param reqWidth       最大宽度px
     * @param savedFilePath  新图存储路径
     * @param savedFileName  新图片名字
     * @return 新图绝对路径
     */
    public static String compressAndSavePicture(String originFilePath, int reqWidth, String savedFilePath, String savedFileName)
    {
        KLog.d("BmpUtils.compressAndSavePicture():originFilePath = " + originFilePath);
        KLog.d("BmpUtils.compressAndSavePicture():savedFilePath = " + savedFilePath);
        KLog.d("BmpUtils.compressAndSavePicture():savedFileName = " + savedFileName);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(originFilePath, options);
        int originWidth = options.outWidth;
        int originHeight = options.outHeight;
        KLog.d("BmpUtils.compressAndSavePicture():originWidth = " + originWidth);
        KLog.d("BmpUtils.compressAndSavePicture():originHeight = " + originHeight);
        if (originWidth > reqWidth)
        {
            int reqHeight = (reqWidth * originHeight) / originWidth;
            KLog.d("BmpUtils.compressAndSavePicture():reqWidth = " + reqWidth);
            KLog.d("BmpUtils.compressAndSavePicture():reqHeight = " + reqHeight);
            options.inSampleSize = calculateInSampleSize(originWidth, originHeight, reqWidth, reqHeight);
        } else
        {
            options.inSampleSize = 1;
        }
        KLog.d("BmpUtils.compressAndSavePicture():inSampleSize = " + options.inSampleSize);
        options.inJustDecodeBounds = false;
        //获取压缩位图 Decode bitmap with inSampleSize set
        Bitmap scaleBmp = BitmapFactory.decodeFile(originFilePath, options);
        if (scaleBmp == null)
            return null;
        //质量压缩【经测试指定目标大小没用】
        Bitmap massCompressBmp = getMassCompressBmp(scaleBmp, 300);
        //检查图片旋转角度
        Bitmap finalBmp = adjustPicRotate(massCompressBmp, originFilePath);
        //保存图片到本地，并返回路径
        String newPath = saveBmp(finalBmp, savedFilePath, savedFileName);

        //回收资源
        scaleBmp.recycle();
        massCompressBmp.recycle();
        System.gc();

        return newPath;
    }

    //计算inSampleSize
    private static int calculateInSampleSize(int originWidth, int originHeight, int reqWidth, int reqHeight)
    {
        int inSampleSize = 1;
        if (originWidth > reqWidth || originHeight > reqHeight)
        {
            //Math.ceil(int value):表示取不小于value的最小整数
            int scaleWidth = (int) Math.ceil((originWidth * 1.0f) / reqWidth);
            int scaleHeight = (int) Math.ceil((originHeight * 1.0f) / reqHeight);
            inSampleSize = Math.max(scaleWidth, scaleHeight);
        }
        return inSampleSize;
    }

    /**
     * 将某个位图进行质量压缩【类型必须为JPEG】
     */
    public static Bitmap getMassCompressBmp(Bitmap image, int maxSize)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 40;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        // 循环判断如果压缩后图片是否大于最大值,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxSize)
        {
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 检查图片角度是否旋转了90度，如果是则反转
     *
     * @param bitmap 需要旋转的图片位图
     * @param path   图片的路径
     */
    public static Bitmap adjustPicRotate(Bitmap bitmap, String path)
    {
        int degree = getPicRotate(path);
        if (degree > 0)
        {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            m.setRotate(degree); // 旋转angle度
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
        }
        return bitmap;
    }

    /**
     * 读取图片文件旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片旋转的角度
     */
    public static int getPicRotate(String path)
    {
        int degree = 0;
        try
        {
            if (StringUtil.isEmpty(path))
            {
                return -1;
            }
            ExifInterface exifInterface = new ExifInterface(path);

            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e)
        {
            KLog.e("BmpUtils.getPicRotate():filePath = " + path + "\n获取图片旋转角度失败：" + e.toString());
        }
        return degree;
    }

    /**
     * 保存图片
     *
     * @param bitmap   需要保存的图片
     * @param saveName 图片保存的名称
     * @return 返回保存后的图片地址
     */
    public static String saveBmp(Bitmap bitmap, String savePath, String saveName)
    {
        String resultPath = null;
        try
        {
            //保存位置
            File file = new File(savePath, saveName);
            if (file.exists())
                file.delete();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
            bitmap = null;
            System.gc();
            resultPath = file.getAbsolutePath();
        } catch (IOException e)
        {
            KLog.e("BmpUtils.saveBmp(): savePath = " + savePath + "\nsaveName = " + saveName + "\n保存图片失败：" + e.toString());
        }
        return resultPath;
    }
}
