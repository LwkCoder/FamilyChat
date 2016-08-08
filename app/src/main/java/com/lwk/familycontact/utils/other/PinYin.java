package com.lwk.familycontact.utils.other;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.regex.Pattern;

/**
 * 汉字的拼音处理
 */
public class PinYin
{
    private PinYin()
    {
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变(均返回大写)
     *
     * @return 汉语拼音首字母
     */
    public static String getSimple(String str)
    {
        String convert = "";
        for (int j = 0; j < str.length(); j++)
        {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);// 这个得到的是一个汉字的多个读音的数组且带数字声调的，如张zhang1
            if (pinyinArray != null)
            {
                convert += pinyinArray[0].charAt(0);
            } else
            {
                convert += word;
            }
        }
        return convert.toUpperCase();
    }

    /**
     * 获取汉字串拼音，英文字符不变(均返回大写)
     *
     * @return 汉语拼音
     */
    public static String getFull(String src)
    {
        char[] t1;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try
        {
            for (int i = 0; i < t0; i++)
            {
                // 判断是否为汉字字符
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+"))
                {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else
                {
                    t4 += Character.toString(t1[i]);
                }
            }
            return t4.toUpperCase();
        } catch (Exception e1)
        {
            e1.printStackTrace();
            t4 = "";
        }
        return t4.toUpperCase();
    }

    /**
     * 判断是否为拼音
     *
     * @param str
     * @return
     */
    public static boolean isPinYin(String str)
    {
        Pattern pattern = Pattern.compile("[ a-zA-Z]*");
        return pattern.matcher(str).matches();
    }
}
