package com.lib.base.date;

import android.content.Context;

import com.lib.base.utils.StringUtil;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Function:日期时间帮助类
 */
public class DateUtils
{
    //默认日期格式
    public static final String sDEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    //默认日期格式
    public static final String sCHINA_DATE_FORMAT = "yyyy年MM月dd日";
    //默认时间格式
    public static final String sDEFAULT_TIME_FORMAT = "HH:mm:ss";
    //判断两条消息之间是否该显示时间戳的最小时间间隔
    private static final long INTERVAL_IN_MILLISECONDS = 15 * 60 * 1000;

    /**
     * 根据服务器的时间戳转为格式化Date字符串【服务器时间戳是秒级的】
     *
     * @param serverStamp 服务器时间戳【秒级】
     * @return 格式化的时间字符串
     */
    public static String getDateStrByServerStamp(String serverStamp)
    {
        if (StringUtil.isEmpty(serverStamp))
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(sDEFAULT_DATE_FORMAT);
        return sdf.format(new Date(Long.valueOf(serverStamp + "000")));
    }

    /**
     * 根据服务器的时间戳转为格式化Date字符串【服务器时间戳是秒级的】
     *
     * @param serverStamp 服务器时间戳【秒级】
     * @return 格式化的时间字符串
     */
    public static String getDateStrByServerStamp(String serverStamp, String format)
    {
        if (StringUtil.isEmpty(serverStamp))
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(serverStamp + "000")));
    }

    /**
     * 根据服务器的时间戳转为格式化Date字符串【服务器时间戳是秒级的】
     *
     * @param serverStamp 服务器时间戳【秒级】
     * @return 格式化的时间字符串
     */
    public static String getDateStrByServerStamp(long serverStamp)
    {
        if (serverStamp == -1)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(sDEFAULT_DATE_FORMAT);
        return sdf.format(new Date(Long.valueOf(serverStamp * 1000)));
    }

    /**
     * 将本地时间戳转为格式化Date字符串
     *
     * @param stamp 本地时间戳【秒级】
     * @return 格式化的时间字符串
     */
    public static String getDateStrByLocalStamp(long stamp)
    {
        if (stamp == -1)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(sCHINA_DATE_FORMAT);
        return sdf.format(new Date(Long.valueOf(stamp)));
    }

    /**
     * 将格式化Date字符串转为服务器时间戳
     *
     * @param dateStr 格式化时间字符串
     * @return 服务器时间戳【秒级】
     */
    public static String getServerStampByDateStr(String dateStr)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(sDEFAULT_DATE_FORMAT);
            return String.valueOf(sdf.parse(dateStr).getTime() / 1000);
        } catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 将本地时间戳转为服务器时间戳【即转为秒级的】
     */
    public static String getServerStampByLocal()
    {
        long curStamp = System.currentTimeMillis();
        return String.valueOf(curStamp / 1000);
    }

    /**
     * 将格式化日期字符串转为Calendar对象
     *
     * @param dateStr 格式化日期字符串【格式："yyyy-MM-dd"】
     * @return Calendar对象
     */
    public static Calendar getCalendarByDateStr(String dateStr)
    {
        if (StringUtil.isEmpty(dateStr))
            dateStr = "1990-01-01";

        Calendar calendar = Calendar.getInstance();

        String time[] = dateStr.split("-");
        int year = Integer.valueOf(time[0]);
        int month = Integer.valueOf(time[1]) - 1;
        int day = Integer.valueOf(time[2]);

        calendar.set(year, month, day);
        return calendar;
    }

    /**
     * 将Date转为时间描述
     *
     * @param context     上下文
     * @param messageDate Date对象
     * @return 时间描述
     */
    public static String getTimeDescribe(Context context, java.util.Date messageDate)
    {
        String format;
        boolean isChinese = context.getResources().getConfiguration().locale.getCountry().equals("CN");

        long messageTime = messageDate.getTime();
        if (isSameDay(messageTime))
        {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(messageDate);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            format = "HH:mm";

            if (hour > 17)
            {
                if (isChinese)
                    format = "晚上 hh:mm";
            } else if (hour >= 0 && hour <= 6)
            {
                if (isChinese)
                    format = "凌晨 hh:mm";
            } else if (hour > 11 && hour <= 17)
            {
                if (isChinese)
                    format = "下午 hh:mm";
            } else
            {
                if (isChinese)
                    format = "上午 hh:mm";
            }
        } else if (isYesterday(messageTime))
        {
            if (isChinese)
                format = "昨天 HH:mm";
            else
                format = "MM-dd HH:mm";
        } else
        {
            if (isChinese)
                format = "M月d日 HH:mm";
            else
                format = "MM-dd HH:mm";
        }

        if (isChinese)
            return new SimpleDateFormat(format, Locale.CHINA).format(messageDate);
        else
            return new SimpleDateFormat(format, Locale.US).format(messageDate);
    }

    /**
     * 判断两条消息的时间间隔是否大于最小时间间隔，以便判断是否需要显示两消息间的间隔描述
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isCloseEnough(long time1, long time2)
    {
        long delta = time1 - time2;
        if (delta < 0)
            delta = -delta;
        return delta > INTERVAL_IN_MILLISECONDS;
    }

    /**
     * 判断时间是否在今天
     *
     * @param inputTime
     * @return
     */
    public static boolean isSameDay(long inputTime)
    {
        TimeInfo tStartAndEndTime = getTodayStartAndEndTime();
        if (inputTime > tStartAndEndTime.getStartTime() && inputTime < tStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    /**
     * 判断时间是否在昨天
     *
     * @param inputTime
     * @return
     */
    public static boolean isYesterday(long inputTime)
    {
        TimeInfo yStartAndEndTime = getYesterdayStartAndEndTime();
        if (inputTime > yStartAndEndTime.getStartTime() && inputTime < yStartAndEndTime.getEndTime())
            return true;
        return false;
    }

    /**
     * 获取今天时间的始点和终点
     *
     * @return
     */
    public static TimeInfo getTodayStartAndEndTime()
    {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        java.util.Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        java.util.Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    /**
     * 获取昨天时间的始点和终点
     *
     * @return
     */
    public static TimeInfo getYesterdayStartAndEndTime()
    {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DATE, -1);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        java.util.Date startDate = calendar1.getTime();
        long startTime = startDate.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -1);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        java.util.Date endDate = calendar2.getTime();
        long endTime = endDate.getTime();
        TimeInfo info = new TimeInfo();
        info.setStartTime(startTime);
        info.setEndTime(endTime);
        return info;
    }

    /**
     * 根据小时点和分钟点获取格式化的时间String
     *
     * @param hour 小时点
     * @param min  分组点
     * @return 时间String
     */
    public static String formatTimeText(int hour, int min)
    {
        String format = "HH:mm";
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, hour);
        calendar1.set(Calendar.MINUTE, min);
        java.util.Date date = calendar1.getTime();
        return new SimpleDateFormat(format).format(date);
    }
}
