package com.ssymon.export.holiday.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author symon
 * @since 14.01.2021
 */
public class DateUtil {

    public static final String SIMPLE_FMT = "yyyy-MM-dd HH:mm:ss";
    public static final String SIMPLE_YMD = "yyyy-MM-dd";
    public static final String SIMPLE_FMT_ZH = "yyyy年MM月dd日";
    public static final String SIMPLE_FMT_M = "yyyy年MM月";
    public static final String SIMPLE_DATE_YMD = "yyyyMMdd";
    public static final String SIMPLE_MDY = "MM/dd/yyyy";
    public static final String SIMPLE_FMT_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String SIMPLE_FMT_MONTH = "yyyyMM";
    public static final String SIMPLE_YM = "yyyy-MM";
    public static final String SIMPLE_YMD_SPLIT_VIRGULE = "yyyy/MM/dd";

    private DateUtil() {

    }

    /**
     * @param date date
     * @return true-双休日，false-工作日
     */
    public static boolean isWeekday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w != 0 && w != 6;
    }

    /**
     * 时间转换String to Date
     *
     * @param sDate   源字符串
     * @param sFormat 对应日期格式
     * @return Date类型
     */
    public static Date stringToDate(String sDate, String sFormat) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(sFormat);
            return dateFormat.parse(sDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 时间转换Date to String
     *
     * @param date    日期
     * @param sFormat 对应日期格式
     * @return String类型
     */
    public static String dateToString(Date date, String sFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(sFormat);
        return dateFormat.format(date);
    }

    /**
     * @param start 开始时间
     * @param end   结束时间
     * @return 获取时间段内的日期
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        tempEnd.add(Calendar.DAY_OF_YEAR, 1);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }
}
