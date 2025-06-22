package javasemysql.coursedesign.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类，提供日期相关的操作方法
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class DateUtils {

    // 日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";

    /**
     * 字符串转日期，使用默认格式yyyy-MM-dd
     *
     * @param dateStr 日期字符串
     * @return 日期对象，如果转换失败则返回null
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, DATE_FORMAT);
    }

    /**
     * 字符串转日期，指定格式
     *
     * @param dateStr 日期字符串
     * @param format 日期格式
     * @return 日期对象，如果转换失败则返回null
     */
    public static Date parseDate(String dateStr, String format) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);  // 不允许宽松解析
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 日期转字符串，使用默认格式yyyy-MM-dd
     *
     * @param date 日期对象
     * @return 日期字符串，如果日期为null则返回空字符串
     */
    public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT);
    }

    /**
     * 日期转字符串，指定格式
     *
     * @param date 日期对象
     * @param format 日期格式
     * @return 日期字符串，如果日期为null则返回空字符串
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前日期，时间部分为00:00:00
     *
     * @return 当前日期对象
     */
    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间对象
     */
    public static Date getCurrentDateTime() {
        return new Date();
    }

    /**
     * 获取当前月份的起始日期
     *
     * @return 当月第一天的日期对象
     */
    public static Date getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前月份的结束日期
     *
     * @return 当月最后一天的日期对象
     */
    public static Date getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 获取当前月份的日期范围
     *
     * @return 包含月初和月末日期的数组
     */
    public static Date[] getCurrentMonthRange() {
        Date[] range = new Date[2];
        range[0] = getMonthStart();
        range[1] = getMonthEnd();
        return range;
    }

    /**
     * 获取指定月份的日期范围
     *
     * @param year 年
     * @param month 月（1-12）
     * @return 包含月初和月末日期的数组
     */
    public static Date[] getMonthRange(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);  // 月份从0开始，所以要减1
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date[] range = new Date[2];
        range[0] = cal.getTime();  // 月初

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        range[1] = cal.getTime();  // 月末

        return range;
    }

    /**
     * 获取当前年份的日期范围
     *
     * @return 包含年初和年末日期的数组
     */
    public static Date[] getCurrentYearRange() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        // 年初
        cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        // 年末
        cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        return new Date[]{start, end};
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 两个日期之间的天数
     */
    public static int daysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }

        long diff = Math.abs(date1.getTime() - date2.getTime());
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /**
     * 日期加减天数
     *
     * @param date 日期
     * @param days 天数，正数为加，负数为减
     * @return 计算后的日期
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 日期加减月数
     *
     * @param date 日期
     * @param months 月数，正数为加，负数为减
     * @return 计算后的日期
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * 获取日期是星期几
     *
     * @param date 日期
     * @return 星期几（1-7，对应星期一到星期日）
     */
    public static int getDayOfWeek(Date date) {
        if (date == null) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 转换为星期一到星期日
        if (dayOfWeek == 0) {
            dayOfWeek = 7; // 周日为7
        }
        return dayOfWeek;
    }

    /**
     * 获取两个日期之间的所有日期
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期列表
     */
    public static Date[] getDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            return new Date[0];
        }

        // 将开始日期和结束日期设置为同一天的00:00:00
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        // 计算天数
        int days = (int) ((endCal.getTimeInMillis() - startCal.getTimeInMillis()) / (24 * 60 * 60 * 1000)) + 1;
        Date[] result = new Date[days];

        // 生成日期数组
        for (int i = 0; i < days; i++) {
            result[i] = startCal.getTime();
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return result;
    }

    public static Date[] getLastMonthRange() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1); // 上个月
        cal.set(Calendar.DAY_OF_MONTH, 1); // 月初
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // 月末
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        return new Date[]{start, end};
    }

    public static Date[] getTodayRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        return new Date[]{start, end};
    }

    public static Date[] getCurrentWeekRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 设置为本周一
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        cal.add(Calendar.DAY_OF_WEEK, 6); // 设置为本周日
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        return new Date[]{start, end};
    }

    public static Date[] getCurrentQuarterRange() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int startMonth, endMonth;

        // 确定季度的起始和结束月份
        if (month < Calendar.APRIL) {
            startMonth = Calendar.JANUARY;
            endMonth = Calendar.MARCH;
        } else if (month < Calendar.JULY) {
            startMonth = Calendar.APRIL;
            endMonth = Calendar.JUNE;
        } else if (month < Calendar.OCTOBER) {
            startMonth = Calendar.JULY;
            endMonth = Calendar.SEPTEMBER;
        } else {
            startMonth = Calendar.OCTOBER;
            endMonth = Calendar.DECEMBER;
        }

        // 设置季度起始日期
        cal.set(Calendar.MONTH, startMonth);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();

        // 设置季度结束日期
        cal.set(Calendar.MONTH, endMonth);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date end = cal.getTime();

        return new Date[]{start, end};
    }

    public static boolean isValidDateFormat(String startDateStr) {
        if (startDateStr == null || startDateStr.isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);  // 不允许宽松解析
            sdf.parse(startDateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}