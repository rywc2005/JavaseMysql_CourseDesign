package com.PFM.CD.utils.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * 日期处理工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DateUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private DateUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取昨天的日期
     *
     * @return 昨天的日期
     */
    public static LocalDate yesterday() {
        return LocalDate.now().minusDays(1);
    }

    /**
     * 获取明天的日期
     *
     * @return 明天的日期
     */
    public static LocalDate tomorrow() {
        return LocalDate.now().plusDays(1);
    }

    /**
     * 获取指定日期所在月份的第一天
     *
     * @param date 日期
     * @return 当月第一天
     */
    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /**
     * 获取指定日期所在月份的最后一天
     *
     * @param date 日期
     * @return 当月最后一天
     */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取指定日期所在周的周一
     *
     * @param date 日期
     * @return 当周周一
     */
    public static LocalDate firstDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取指定日期所在周的周日
     *
     * @param date 日期
     * @return 当周周日
     */
    public static LocalDate lastDayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    /**
     * 获取指定日期所在年份的第一天
     *
     * @param date 日期
     * @return 当年第一天
     */
    public static LocalDate firstDayOfYear(LocalDate date) {
        return date.withDayOfYear(1);
    }

    /**
     * 获取指定日期所在年份的最后一天
     *
     * @param date 日期
     * @return 当年最后一天
     */
    public static LocalDate lastDayOfYear(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 获取指定月份的天数
     *
     * @param year 年份
     * @param month 月份（1-12）
     * @return 该月天数
     */
    public static int getDaysInMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 计算两个日期之间的月数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月数
     */
    public static long monthsBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.MONTHS.between(startDate, endDate);
    }

    /**
     * 检查日期是否在指定范围内
     *
     * @param date 待检查日期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 如果在范围内返回true，否则返回false
     */
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * 获取指定日期的开始时间（00:00:00）
     *
     * @param date 日期
     * @return 该日期的开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 获取指定日期的结束时间（23:59:59.999999999）
     *
     * @param date 日期
     * @return 该日期的结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    /**
     * 获取指定日期范围内的所有日期
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期列表
     */
    public static List<LocalDate> getDateRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

    /**
     * 获取指定日期所在月份的所有日期
     *
     * @param year 年份
     * @param month 月份（1-12）
     * @return 日期列表
     */
    public static List<LocalDate> getDatesInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return getDateRange(startDate, endDate);
    }

    /**
     * 检查日期是否为周末
     *
     * @param date 日期
     * @return 如果是周末返回true，否则返回false
     */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * 检查日期是否为工作日
     *
     * @param date 日期
     * @return 如果是工作日返回true，否则返回false
     */
    public static boolean isWeekday(LocalDate date) {
        return !isWeekend(date);
    }

    /**
     * 获取两个日期中较早的一个
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 较早的日期
     */
    public static LocalDate min(LocalDate date1, LocalDate date2) {
        return date1.isBefore(date2) ? date1 : date2;
    }

    /**
     * 获取两个日期中较晚的一个
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 较晚的日期
     */
    public static LocalDate max(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2) ? date1 : date2;
    }
}