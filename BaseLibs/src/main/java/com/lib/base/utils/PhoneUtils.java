package com.lib.base.utils;

/**
 * 手机号文本的工具类
 */
public class PhoneUtils
{

    /**
     * 将手机号格式化（去除分隔符）：将 153-1111-2222 或者 153 1111 2222 变为 15311112222
     */
    public static String formartPhoneRemoveSep(String phone)
    {
        if (StringUtil.isEmpty(phone))
            return "";
        if (phone.indexOf('-') > 0)
        {
            String[] strs = phone.split("-");
            String result = "";
            for (int i = 0; i < strs.length; i++)
                result += strs[i];
            return result;
        }
        if (phone.indexOf(' ') > 0)
        {
            String[] strs = phone.split(" ");
            String result = "";
            for (int i = 0; i < strs.length; i++)
                result += strs[i];
            return result;
        }
        return phone;
    }

    /**
     * 去除国字区号：+86
     */
    public static String cutCharacterCode(String phone)
    {
        if (StringUtil.isEmpty(phone))
            return "";
        if (phone.contains("+86"))
        {
            return phone.substring(phone.indexOf("+86"));
        } else
        {
            return phone;
        }
    }

    /**
     * 判断是否为纯数字
     * */
    public static boolean isNumber(String phone){
        if (StringUtil.isEmpty(phone))
            return false;
        return phone.matches("[0-9]+");
    }


    /**
     * 将手机号格式化（添加分隔符）：将 15311112222，变为 153-1111-2222
     */
    public static String formartPhoneAddSep(String phone)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(phone.substring(0, 3));
        sb.append('-');
        sb.append(phone.subSequence(3, 7));
        sb.append('-');
        sb.append(phone.subSequence(7, 11));
        return sb.toString();
    }

    /**
     * 手机号加密处理： 将 15311112222 变为 153****2222;
     */
    public static String hiddenPhone(String phone)
    {
        return phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
    }

    /**
     * 检验传入的mobiles是否为手机号
     */
    public static boolean isMobileNO(String mobiles)
    {
        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (StringUtil.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }
}
