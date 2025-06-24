package com.PFM.CD.entity.enums;

/**
 * 账户状态枚举
 * 对应数据库accounts表的status字段
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public enum AccountStatus {

    ACTIVE("活跃"),
    INACTIVE("不活跃"),
    CLOSED("已关闭");

    /**
     * 根据名称获取枚举值（不区分大小写）
     * @param name 枚举名称
     * @return 对应的枚举值，若不存在则返回null
     */
    public static AccountStatus fromString(String name) {
        if (name == null) return null;

        try {
            return AccountStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private final String displayName;

    AccountStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 获取状态的显示名称
     * @return 中文显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 判断账户是否可用（可进行交易）
     * @return 如果账户状态为ACTIVE返回true，否则返回false
     */
    public boolean isAvailable() {
        return this == ACTIVE;
    }
}