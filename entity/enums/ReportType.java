package com.PFM.CD.entity.enums;

/**
 * 报表类型枚举
 * 对应数据库reports表的report_type字段
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public enum ReportType {

    INCOME_EXPENSE("收支分析报表", "分析收入和支出的比例及趋势"),
    CATEGORY_ANALYSIS("分类分析报表", "按分类统计收支情况"),
    BUDGET_EVALUATION("预算评估报表", "评估预算执行情况"),

    BUDGET("预算报表", "展示指定期间的预算信息"),
    CATEGORY("分类报表", "展示各分类的收支统计"),
    ACCOUNT("账户报表", "展示账户相关的交易与余额");


    private final String displayName;
    private final String description;

    ReportType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 获取报表类型的显示名称
     * @return 中文显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 获取报表类型的描述
     * @return 报表类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取报表的默认文件名前缀
     * @return 文件名前缀
     */
    public String getFileNamePrefix() {
        switch (this) {
            case INCOME_EXPENSE:
                return "income_expense_report";
            case CATEGORY_ANALYSIS:
                return "category_analysis_report";
            case BUDGET_EVALUATION:
                return "budget_evaluation_report";
            case BUDGET:
                return "budget_report";
            case CATEGORY:
                return "category_report";
            case ACCOUNT:
                return "account_report";
            default:
                return "report"; // 不应该发生
        }
    }

    /**
     * 检查指定报表类型是否需要预算数据
     * @return 如果需要预算数据返回true，否则返回false
     */
    public boolean requiresBudgetData() {
        return this == BUDGET_EVALUATION || this == BUDGET;
    }

    /**
     * 根据名称获取枚举值（不区分大小写）
     * @param name 枚举名称
     * @return 对应的枚举值，若不存在则返回null
     */
    public static ReportType fromString(String name) {
        if (name == null) return null;

        try {
            return ReportType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}