package com.baibutao.app.waibao.yun.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author lsb
 * @date 2012-5-29 下午11:31:09
 */
public class DateUtil {
    public static final String DEFAULT_DATE_FMT = "yyyy-MM-dd HH:mm";

    public static final String DATE_FMT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FMT_YMD = "MM-dd";

    public static final String DATE_MMddHHmm = "MM-dd HH:mm";

    public static final String DATE_FMT_DM = "MM/dd";

    public static final String DATE_HHmm = "HH:mm";

    public static final String DATE_HHmmss = "HH:mm:ss";

    /**
     * date1比date2更新（更迟，更晚），返回true，否则返回false
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isNewer(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        long d1 = date1.getTime();
        long d2 = date2.getTime();
        return d1 > d2;
    }

    public static Date parseNoException(String input) {
        try {
            return parse(input, DATE_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parse(String input, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        try {
            return sdf.parse(input);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parse(String input) {
        return parse(input, DEFAULT_DATE_FMT);
    }

    public static Date parseDefault(String input) {
        return parse(input, DATE_FMT);
    }


    public static String format(Date date, String fmt) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(fmt);
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String format(Date date) {
        return format(date, DEFAULT_DATE_FMT);
    }

    public static String formatDefault(Date date) {
        return format(date, DATE_FMT);
    }


    /**
     * 计算天
     *
     * @param date
     * @param changeValue 正数为增加，负数为减去
     * @return
     */
    public static Date changeDay(Date date, int changeValue) {
        if (date == null || changeValue == 0) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, changeValue);
        return c.getTime();
    }

    public static Date initDate(int year, int month, int day, int hour, int min, int second) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, hour, min, second);
        return c.getTime();
    }

    public static boolean after(Date d1, Date d2) {

        if (d1 == null || d2 == null
                ) {

            return false
                    ;
        }

        return d1.getTime() > d2
                .getTime();
    }


    public static int distanceDay(Date d1, Date d2) {

        if (d1 == null || d2 == null
                ) {

            return
                    0;
        }

        return (int) Math.abs((d1.getTime() - d2
                .getTime()) / (1000 * 3600 * 24));
    }

    /**
     *  修改分钟
     * @Title: changeMin
     * @param date
     * @param changeValue
     * @return Date
     * @throws
     */
    public static Date changeMin(Date date, int changeValue) {
        if (date == null || changeValue == 0) {
            return date;
        }
        long flag = changeValue * 60 * 1000;
        return new Date(date.getTime() + flag);
    }

    /**
     *
     * @Title: changeSecond
     * @param date
     * @param changeValue
     * @return Date
     * @throws
     */
    public static Date changeSecond(Date date, int changeValue) {
        if (date == null || changeValue == 0) {
            return date;
        }
        long flag = changeValue * 1000;
        return new Date(date.getTime() + flag);
    }


}
