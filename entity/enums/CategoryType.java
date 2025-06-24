package com.PFM.CD.entity.enums;

/**
 * 分类类型枚举
 * 对应数据库categories表的category_type字段
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public enum CategoryType {

    INCOME("收入"),
    EXPENSE("支出");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 获取类型的显示名称
     * @return 中文显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 判断是否为支出类型
     * @return 如果是支出类型返回true，否则返回false
     */
    public boolean isExpenseType() {
        return this == EXPENSE;
    }

    /**
     * 判断是否为收入类型
     * @return 如果是收入类型返回true，否则返回false
     */
    public boolean isIncomeType() {
        return this == INCOME;
    }

    /**
     * 根据名称获取枚举值（不区分大小写）
     * @param name 枚举名称
     * @return 对应的枚举值，若不存在则返回null
     */
    public static CategoryType fromString(String name) {
        if (name == null) return null;

        try {
            return CategoryType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}