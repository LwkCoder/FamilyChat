package com.lib.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil
{

    public static final String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？≧▽≦]";

    /**
     * 替换sqlite特殊字符转义符
     *
     * @param keyWord
     * @return
     */
    public static String sqliteEscape(String keyWord)
    {
        if (StringUtil.isNotEmpty(keyWord))
        {
//            keyWord = keyWord.replace("/", "//");
            keyWord = keyWord.replace("'", "''");
            keyWord = keyWord.replace("[", "/[");
            keyWord = keyWord.replace("]", "/]");
            keyWord = keyWord.replace("%", "/%");
            keyWord = keyWord.replace("&", "/&");
            keyWord = keyWord.replace("_", "/_");
            keyWord = keyWord.replace("(", "/(");
            keyWord = keyWord.replace(")", "/)");
        }
        return keyWord;
    }

    /**
     * 判断字符串是否为空?
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
    }

    /**
     * 判断字符串是否为空?
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str)
    {
        return (str != null && str.length() != 0);
    }

    /**
     * 判断文本是否相同
     *
     * @param actual
     * @param expected
     * @return
     */
    public static boolean isEquals(String actual, String expected)
    {
        return ObjectUtils.isEquals(actual, expected);
    }

    /**
     * 去除 所有特殊字符
     * `~!@#$%^&*()+=|{}':;',\[\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？
     * */
    public static String removeSpecialChar(String str){
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
