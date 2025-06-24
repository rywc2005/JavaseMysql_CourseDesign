package com.PFM.CD.entity.enums;

import java.time.LocalDate;

/**
 * 预算周期类型枚举
 * 对应数据库budgets表的period_type字段
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public enum PeriodType {

    MONTHLY("月度", 1),
    QUARTERLY("季度", 3),
    YEARLY("年度", 12);

    private final String displayName;
    private final int monthCount;

    PeriodType(String displayName, int monthCount) {
        this.displayName = displayName;
        this.monthCount = monthCount;
    }

    /**
     * 获取周期类型的显示名称
     * @return 中文显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取周期包含的月份数
     * @return 月份数
     */
    public int getMonthCount() {
        return monthCount;
    }

    /**
     * 计算给定日期的周期结束日期
     * @param startDate 开始日期
     * @return 结束日期
     */
    public LocalDate calculateEndDate(LocalDate startDate) {
        if (startDate == null) {
            return null;
        }

        switch (this) {
            case MONTHLY:
                return startDate.plusMonths(1).minusDays(1);
            case QUARTERLY:
                return startDate.plusMonths(3).minusDays(1);
            case YEARLY:
                return startDate.plusYears(1).minusDays(1);
            default:
                return startDate; // 不应该发生
        }
    }

    /**
     * 获取周期的默认名称
     * @param startDate 开始日期
     * @return 默认名称，例如"2025年6月预算"
     */
    public String getDefaultName(LocalDate startDate) {
        if (startDate == null) {
            return "";
        }

        int year = startDate.getYear();

        switch (this) {
            case MONTHLY:
                return year + "年" + startDate.getMonthValue() + "月预算";
            case QUARTERLY:
                int quarter = (startDate.getMonthValue() - 1) / 3 + 1;
                return year + "年第" + quarter + "季度预算";
            case YEARLY:
                return year + "年度预算";
            default:
                return ""; // 不应该发生
        }
    }

    /**
     * 根据名称获取枚举值（不区分大小写）
     * @param name 枚举名称
     * @return 对应的枚举值，若不存在则返回null
     */
    public static PeriodType fromString(String name) {
        if (name == null) return null;

        try {
            return PeriodType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}