package com.lib.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

/**
 * 手机号文本的工具类
 */
public class PhoneUtils
{

    /**
     * 检验传入的字符串是否为手机号
     */
    public static boolean isMobileNO(String mobiles)
    {
        // "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][34578]\\d{9}";
        if (StringUtil.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    /**
     * 格式化手机号码，将所包含的所有空格字符去掉
     */
    public static String formatPhoneNumAsNoSpace(String phoneNum)
    {
        if (StringUtil.isEmpty(phoneNum))
            return phoneNum;

        //替换所有空格字符
        return phoneNum.replaceAll("\\s*", "");
    }


    /**
     * 将传入的手机号格式化为规定的分割形式
     * [示例：将13000000000转为130-0000-0000]
     * [非正规手机号将直接返回原样]
     *
     * @param phoneNum 手机号
     * @param regular  特定的分隔符
     * @return 格式化手机号
     */
    public static String formatPhoneNumAsRegular(String phoneNum, String regular)
    {
        String formatNum = formatPhoneNumAsNoSpace(phoneNum);
        if (isMobileNO(formatNum))
        {
            return new StringBuffer()
                    .append(formatNum.substring(0, 3)).append(regular)
                    .append(formatNum.substring(3, 7)).append(regular)
                    .append(formatNum.substring(7, 11))
                    .toString();
        } else
        {
            return phoneNum;
        }
    }

    /**
     * 直接拨打电话
     * [sdk23需要检查权限]
     *
     * @param context  环境上下文Activity
     * @param phoneNum 号码
     */
    public static void callPhone(Activity context, String phoneNum)
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + phoneNum);
        intent.setData(uri);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
        {
            context.startActivity(intent);
            return;
        }
    }

    /**
     * 跳转到拨号界面
     *
     * @param context  环境上下文
     * @param phoneNum 号码
     */
    public static void dialPhone(Activity context, String phoneNum)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri uri = Uri.parse("tel:" + phoneNum);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
