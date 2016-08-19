package com.lib.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lib.qrcode.activity.QrCodeScanActivity;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Function:二维码相关帮助类
 * Created by LWK
 * 2016/6/14
 */
public class QrCodeHelper
{
    /**
     * 跳转到二维码扫描界面的requestCode
     */
    public static final int QRCODE_REQUEST_CODE = 111;

    /**
     * 扫描成功后代表最终截取区域的宽度
     */
    public static final String QRCODE_RESULT_WIDTH = "qrWidth";

    /**
     * 扫描成功后代表最终截取区域的宽度
     */
    public static final String QRCODE_RESULT_HEIGHT = "qrHeight";

    /**
     * 扫描成功后代表最终内容
     */
    public static final String QRCODE_RESULT_CONTENT = "qrContent";

    /**
     * 跳转到二维码扫描界面
     * 注意：
     * ①需要添加权限：
     * <uses-permission android:name="android.permission.CAMERA"/>
     * <uses-permission android:name="android.permission.VIBRATE"/>
     * <p/>
     * ②扫描成功后需要在跳转前的Activity下添加以下代码:
     *
     * @Override protected void onActivityResult(int requestCode, int resultCode, Intent data)
     * {
     * super.onActivityResult(requestCode, resultCode, data);
     * if (resultCode != RESULT_OK)
     * return;
     * if (requestCode == QrCodeHelper.QRCODE_REQUEST_CODE)
     * {
     * String result = data.getExtras().getString(QrCodeHelper.QRCODE_RESULT_CONTENT);
     * }
     * }
     */
    public static void goToQrcodeScanActivity(Activity activity)
    {
        Intent intent = new Intent(activity, QrCodeScanActivity.class);
        activity.startActivityForResult(intent, QRCODE_REQUEST_CODE);
    }

    /**
     * 创建二维码
     *
     * @param content   content 待转换的字符串
     * @param widthPix  widthPix 输出结果bmp的宽（px）
     * @param heightPix heightPix 输出结果bmp的高（px）
     * @param logoBm    logoBm 中间的logo(默认大小为最终二维码大小的1/5)
     * @return 二维码bmp
     */
    public static Bitmap createQrCode(String content, int widthPix, int heightPix, Bitmap logoBm)
    {
        try
        {
            if (content == null || content.length() == 0)
                return null;
            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix,
                    heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++)
            {
                for (int x = 0; x < widthPix; x++)
                {
                    if (bitMatrix.get(x, y))
                        pixels[y * widthPix + x] = 0xff000000;
                    else
                        pixels[y * widthPix + x] = 0xffffffff;
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            if (logoBm != null)
                bitmap = addLogo(bitmap, logoBm);
            return bitmap;
        } catch (WriterException e)
        {
            Log.e("QrCodeHelper", "create qrcode error:" + e.toString());
        }
        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo)
    {
        if (src == null)
            return null;
        if (logo == null)
            return src;
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0)
            return null;
        if (logoWidth == 0 || logoHeight == 0)
            return src;
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try
        {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e)
        {
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 识别本地二维码图片
     *
     * @param filePath 本地图片绝对路径
     * @return 二维码包含的内容
     */
    public static String recognizeQrCode(String filePath)
    {
        if (filePath == null || filePath.length() == 0)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true; // 先获取原大小
        BitmapFactory.decodeFile(filePath, options);
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false; // 获取新的大小

        Bitmap scanBitmap = BitmapFactory.decodeFile(filePath, options);
        return recognizeQrCode(scanBitmap);
    }

    /**
     * 直接识别二维码位图
     *
     * @param bitmap 二维码位图
     * @return 二维码包含的内容
     */
    public static String recognizeQrCode(Bitmap bitmap)
    {
        if (bitmap == null)
            return null;
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        //获取图片的像素存入到数组
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try
        {
            Result result = reader.decode(bitmap1, hints);
            if (reader == null)
            {
                return null;
            } else
            {
                String str = result.toString();
                boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
                if (ISO)
                    str = new String(str.getBytes("ISO-8859-1"), "GB2312");
                return str;
            }
        } catch (Exception e)
        {
            Log.e("QrCodeHelper", "recognizeQrCode fail:" + e.toString());
        }
        return null;
    }

}
