package com.PFM.CD.utils.format;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 日期格式化工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DateFormatter {

    private static final Locale CHINA_LOCALE = Locale.CHINA;
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new HashMap<>();

    /**
     * 私有构造函数，防止实例化
     */
    private DateFormatter() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 使用默认格式（yyyy-MM-dd）格式化日期
     *
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return "";
        }

        return getFormatter("yyyy-MM-dd").format(date);
    }

    /**
     * 使用指定格式格式化日期
     *
     * @param date 日期
     * @param pattern 格式模式
     * @return 格式化后的日期字符串
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null) {
            return "";
        }

        return getFormatter(pattern).format(date);
    }

    /**
     * 使用默认格式（yyyy-MM-dd HH:mm:ss）格式化日期时间
     *
     * @param dateTime 日期时间
     * @return 格式化后的日期时间字符串
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        return getFormatter("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }

    /**
     * 使用指定格式格式化日期时间
     *
     * @param dateTime 日期时间
     * @param pattern 格式模式
     * @return 格式化后的日期时间字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }

        return getFormatter(pattern).format(dateTime);
    }

    /**
     * 使用中文格式格式化日期
     *
     * @param date 日期
     * @return 格式化后的中文日期字符串（如：2025年06月24日）
     */
    public static String formatChinese(LocalDate date) {
        if (date == null) {
            return "";
        }

        return getFormatter("yyyy年MM月dd日").format(date);
    }

    /**
     * 使用短中文格式格式化日期
     *
     * @param date 日期
     * @return 格式化后的短中文日期字符串（如：25年6月24日）
     */
    public static String formatShortChinese(LocalDate date) {
        if (date == null) {
            return "";
        }

        String year = String.valueOf(date.getYear());
        year = year.substring(2); // 只取年份的后两位

        return year + "年" + date.getMonthValue() + "月" + date.getDayOfMonth() + "日";
    }

    /**
     * 格式化为友好的日期描述
     *
     * @param date 日期
     * @return 友好的日期描述
     */
    public static String formatFriendly(LocalDate date) {
        if (date == null) {
            return "";
        }

        LocalDate today = LocalDate.now();

        if (date.isEqual(today)) {
            return "今天";
        } else if (date.isEqual(today.minusDays(1))) {
            return "昨天";
        } else if (date.isEqual(today.plusDays(1))) {
            return "明天";
        } else if (date.isEqual(today.minusDays(2))) {
            return "前天";
        } else if (date.isEqual(today.plusDays(2))) {
            return "后天";
        } else if (date.isAfter(today.minusDays(7)) && date.isBefore(today)) {
            // 一周内的过去日期
            return date.getDayOfWeek().getDisplayName(TextStyle.FULL, CHINA_LOCALE);
        } else if (date.isAfter(today) && date.isBefore(today.plusDays(7))) {
            // 一周内的未来日期
            return "下" + date.getDayOfWeek().getDisplayName(TextStyle.FULL, CHINA_LOCALE);
        } else if (date.getYear() == today.getYear()) {
            // 同年不同月
            return (date.getMonthValue()) + "月" + date.getDayOfMonth() + "日";
        } else {
            // 不同年
            return date.getYear() + "年" + date.getMonthValue() + "月" + date.getDayOfMonth() + "日";
        }
    }

    /**
     * 格式化为带星期的日期
     *
     * @param date 日期
     * @return 带星期的日期字符串
     */
    public static String formatWithWeekday(LocalDate date) {
        if (date == null) {
            return "";
        }

        String dateStr = format(date);
        String weekday = date.getDayOfWeek().getDisplayName(TextStyle.FULL, CHINA_LOCALE);

        return dateStr + " " + weekday;
    }

    /**
     * 解析日期字符串为LocalDate对象
     *
     * @param dateString 日期字符串
     * @param pattern 格式模式
     * @return 解析后的LocalDate对象，如果解析失败返回null
     */
    public static LocalDate parseDate(String dateString, String pattern) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString, getFormatter(pattern));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析日期字符串为LocalDateTime对象
     *
     * @param dateTimeString 日期时间字符串
     * @param pattern 格式模式
     * @return 解析后的LocalDateTime对象，如果解析失败返回null
     */
    public static LocalDateTime parseDateTime(String dateTimeString, String pattern) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateTimeString, getFormatter(pattern));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取格式化器（使用缓存提高性能）
     *
     * @param pattern 格式模式
     * @return 日期时间格式化器
     */
    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    /**
     * 获取月份的中文名称
     *
     * @param month 月份（1-12）
     * @return 月份的中文名称
     */
    public static String getMonthChineseName(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("月份必须在1到12之间");
        }

        String[] monthNames = {
                "一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"
        };

        return monthNames[month - 1];
    }

    /**
     * 获取星期的中文名称
     *
     * @param dayOfWeek 星期几（1-7，周一到周日）
     * @return 星期的中文名称
     */
    public static String getWeekdayChineseName(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("星期几必须在1到7之间");
        }

        String[] weekdayNames = {
                "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"
        };

        return weekdayNames[dayOfWeek - 1];
    }

    /**
     * 获取星期的中文名称
     *
     * @param dayOfWeek 星期几枚举
     * @return 星期的中文名称
     */
    public static String getWeekdayChineseName(DayOfWeek dayOfWeek) {
        return getWeekdayChineseName(dayOfWeek.getValue());
    }
}