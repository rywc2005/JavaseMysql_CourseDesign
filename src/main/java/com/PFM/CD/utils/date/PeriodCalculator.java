package com.PFM.CD.utils.date;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 周期计算工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class PeriodCalculator {

    /**
     * 私有构造函数，防止实例化
     */
    private PeriodCalculator() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 计算两个日期之间的周期
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周期
     */
    public static Period getPeriod(LocalDate startDate, LocalDate endDate) {
        return Period.between(startDate, endDate);
    }

    /**
     * 获取人性化的周期描述
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周期描述
     */
    public static String getPeriodDescription(LocalDate startDate, LocalDate endDate) {
        Period period = Period.between(startDate, endDate);

        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        StringBuilder sb = new StringBuilder();

        if (years > 0) {
            sb.append(years).append("年");
        }

        if (months > 0) {
            sb.append(months).append("个月");
        }

        if (days > 0 || (years == 0 && months == 0)) {
            sb.append(days).append("天");
        }

        return sb.toString();
    }

    /**
     * 根据指定的周期类型，计算下一个周期的日期范围
     *
     * @param currentStartDate 当前周期开始日期
     * @param currentEndDate 当前周期结束日期
     * @param periodType 周期类型（daily, weekly, monthly, quarterly, yearly）
     * @return 下一个周期的日期范围
     */
    public static Map<String, LocalDate> getNextPeriod(LocalDate currentStartDate,
                                                       LocalDate currentEndDate,
                                                       String periodType) {
        Map<String, LocalDate> result = new HashMap<>();

        switch (periodType.toLowerCase()) {
            case "daily":
                result.put("startDate", currentStartDate.plusDays(1));
                result.put("endDate", currentEndDate.plusDays(1));
                break;

            case "weekly":
                result.put("startDate", currentStartDate.plusWeeks(1));
                result.put("endDate", currentEndDate.plusWeeks(1));
                break;

            case "monthly":
                LocalDate nextMonthStart = currentStartDate.plusMonths(1);
                LocalDate nextMonthEnd = YearMonth.from(nextMonthStart).atEndOfMonth();

                result.put("startDate", nextMonthStart);
                result.put("endDate", nextMonthEnd);
                break;

            case "quarterly":
                LocalDate nextQuarterStart = currentStartDate.plusMonths(3);
                int month = nextQuarterStart.getMonthValue();
                int quarterEndMonth = month + (3 - (month % 3)) % 3;
                LocalDate nextQuarterEnd = YearMonth.of(nextQuarterStart.getYear(), quarterEndMonth).atEndOfMonth();

                result.put("startDate", nextQuarterStart);
                result.put("endDate", nextQuarterEnd);
                break;

            case "yearly":
                LocalDate nextYearStart = currentStartDate.plusYears(1);
                LocalDate nextYearEnd = LocalDate.of(nextYearStart.getYear(), 12, 31);

                result.put("startDate", nextYearStart);
                result.put("endDate", nextYearEnd);
                break;

            default:
                throw new IllegalArgumentException("不支持的周期类型: " + periodType);
        }

        return result;
    }

    /**
     * 根据指定的周期类型，计算上一个周期的日期范围
     *
     * @param currentStartDate 当前周期开始日期
     * @param currentEndDate 当前周期结束日期
     * @param periodType 周期类型（daily, weekly, monthly, quarterly, yearly）
     * @return 上一个周期的日期范围
     */
    public static Map<String, LocalDate> getPreviousPeriod(LocalDate currentStartDate,
                                                           LocalDate currentEndDate,
                                                           String periodType) {
        Map<String, LocalDate> result = new HashMap<>();

        switch (periodType.toLowerCase()) {
            case "daily":
                result.put("startDate", currentStartDate.minusDays(1));
                result.put("endDate", currentEndDate.minusDays(1));
                break;

            case "weekly":
                result.put("startDate", currentStartDate.minusWeeks(1));
                result.put("endDate", currentEndDate.minusWeeks(1));
                break;

            case "monthly":
                LocalDate prevMonthStart = currentStartDate.minusMonths(1).withDayOfMonth(1);
                LocalDate prevMonthEnd = YearMonth.from(prevMonthStart).atEndOfMonth();

                result.put("startDate", prevMonthStart);
                result.put("endDate", prevMonthEnd);
                break;

            case "quarterly":
                LocalDate prevQuarterStart = currentStartDate.minusMonths(3);
                int month = prevQuarterStart.getMonthValue();
                int quarterStartMonth = month - ((month - 1) % 3);
                int quarterEndMonth = quarterStartMonth + 2;

                LocalDate adjPrevQuarterStart = LocalDate.of(prevQuarterStart.getYear(), quarterStartMonth, 1);
                LocalDate prevQuarterEnd = YearMonth.of(prevQuarterStart.getYear(), quarterEndMonth).atEndOfMonth();

                result.put("startDate", adjPrevQuarterStart);
                result.put("endDate", prevQuarterEnd);
                break;

            case "yearly":
                int prevYear = currentStartDate.getYear() - 1;
                LocalDate prevYearStart = LocalDate.of(prevYear, 1, 1);
                LocalDate prevYearEnd = LocalDate.of(prevYear, 12, 31);

                result.put("startDate", prevYearStart);
                result.put("endDate", prevYearEnd);
                break;

            default:
                throw new IllegalArgumentException("不支持的周期类型: " + periodType);
        }

        return result;
    }

    /**
     * 计算两个日期之间有多少个工作日
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 工作日数量
     */
    public static long countWeekdays(LocalDate startDate, LocalDate endDate) {
        long count = 0;
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            if (DateUtils.isWeekday(date)) {
                count++;
            }
            date = date.plusDays(1);
        }

        return count;
    }

    /**
     * 计算两个日期之间的周数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 周数
     */
    public static long countWeeks(LocalDate startDate, LocalDate endDate) {
        LocalDate start = DateUtils.firstDayOfWeek(startDate);
        LocalDate end = DateUtils.lastDayOfWeek(endDate);

        return ChronoUnit.WEEKS.between(start, end) + 1;
    }

    /**
     * 计算两个日期之间的月数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月数
     */
    public static long countMonths(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate.withDayOfMonth(1);
        LocalDate end = endDate.withDayOfMonth(1);

        return ChronoUnit.MONTHS.between(start, end) + 1;
    }

    /**
     * 计算两个日期之间的季度数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 季度数
     */
    public static long countQuarters(LocalDate startDate, LocalDate endDate) {
        int startQuarter = (startDate.getMonthValue() - 1) / 3 + 1;
        int endQuarter = (endDate.getMonthValue() - 1) / 3 + 1;

        return (endDate.getYear() - startDate.getYear()) * 4 + (endQuarter - startQuarter) + 1;
    }
}