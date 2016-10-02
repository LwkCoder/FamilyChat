package com.lwk.familycontact.utils.other;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LWK
 * TODO 文本高亮显示工具类
 * 2016/8/24
 */
public class TextLightUtils
{

    /**
     * 关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text, String keyword)
    {
        SpannableString s = new SpannableString(text);
        if (keyword.contains("+"))
            keyword = Pattern.quote("+");
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find())
        {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

}
