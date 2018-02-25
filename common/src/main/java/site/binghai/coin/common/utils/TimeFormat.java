package site.binghai.coin.common.utils;

/**
 * Created by binghai on 2017/12/20.
 *
 * @ huobi
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeFormat {
    public static String format(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String format(Long date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.format(date);
        }catch (Exception e){e.printStackTrace();}

        return "NULL";
    }

    public static String format2yyyy_MM_dd(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static String format2yyyyMMdd(Long date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(date);
    }

    public static Long dataTime2Timestamp(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static Long data2Timestamp(String yyyy_MM_dd) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(yyyy_MM_dd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static Long data2Timestamp(int year, int month, int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = year + "-" + month + "-" + day;
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static int getThisYear(Long null_ts) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return Integer.parseInt(df.format(null_ts == null ? System.currentTimeMillis() : null_ts));
    }

    public static int getThisMonth(Long null_ts) {
        SimpleDateFormat df = new SimpleDateFormat("MM");
        return Integer.parseInt(df.format(null_ts == null ? System.currentTimeMillis() : null_ts));
    }

    public static int getThisDay(Long null_ts) {
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return Integer.parseInt(df.format(null_ts == null ? System.currentTimeMillis() : null_ts));
    }

    //获得当天0点时间
    public static Long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //获得当天24点时间
    public static Long getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    //获得本周一0点时间
    public static Long getTimesWeekmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTimeInMillis();
    }

    //获得本周日24点时间
    public static Long getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime().getTime() + (7 * 24 * 60 * 60 * 1000);
    }

    //获得本月第一天0点时间
    public static Long getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    //获得本月最后一天24点时间
    public static Long getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当前分钟的时间戳开始和结束
     * yyyy-MM-dd HH:mm:ss
     */
    public static Long[] getThisMinute(long ts) {
        String now = format(ts);
        int idx = now.lastIndexOf(":");
        long start = dataTime2Timestamp(now.substring(0, idx) + ":00");
        long end = dataTime2Timestamp(now.substring(0, idx) + ":59");
        return new Long[]{start, end};
    }
}
