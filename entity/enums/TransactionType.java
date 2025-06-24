package com.PFM.CD.entity.enums;

/**
 * 交易类型枚举
 * 对应数据库transactions表的transaction_type字段
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public enum TransactionType {

    INCOME("收入"),
    EXPENSE("支出");

    private final String displayName;

    TransactionType(String displayName) {
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
     * 获取对应的分类类型
     * @return 对应的CategoryType枚举值
     */
    public CategoryType toCategoryType() {
        return this == INCOME ? CategoryType.INCOME : CategoryType.EXPENSE;
    }

    /**
     * 判断是否需要源账户
     * @return 如果是支出类型返回true，否则返回false
     */
    public boolean requiresSourceAccount() {
        return this == EXPENSE;
    }

    /**
     * 判断是否需要目标账户
     * @return 如果是收入类型返回true，否则返回false
     */
    public boolean requiresDestinationAccount() {
        return this == INCOME;
    }

    /**
     * 根据名称获取枚举值（不区分大小写）
     * @param name 枚举名称
     * @return 对应的枚举值，若不存在则返回null
     */
    public static TransactionType fromString(String name) {
        if (name == null) return null;

        try {
            return TransactionType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}