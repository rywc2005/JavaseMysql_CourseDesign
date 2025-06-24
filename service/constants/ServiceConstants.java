package com.PFM.CD.service.constants;

import java.math.BigDecimal;

/**
 * 服务层常量类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ServiceConstants {

    // 用户相关常量
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 64;

    // 账户相关常量
    public static final int MAX_ACCOUNTS_PER_USER = 20;
    public static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000");

    // 预算相关常量
    public static final int MAX_BUDGETS_PER_USER = 10;
    public static final int MAX_CATEGORIES_PER_BUDGET = 30;

    // 交易相关常量
    public static final int DEFAULT_RECENT_TRANSACTIONS_LIMIT = 10;
    public static final int MAX_TRANSACTION_DESCRIPTION_LENGTH = 255;

    // 分类相关常量
    public static final int MAX_CATEGORY_NAME_LENGTH = 50;

    // 报表相关常量
    public static final int MAX_REPORTS_TO_KEEP = 50;
    public static final int DEFAULT_REPORTS_EXPIRY_DAYS = 90;

    // 统计相关常量
    public static final String INTERVAL_TYPE_DAY = "day";
    public static final String INTERVAL_TYPE_WEEK = "week";
    public static final String INTERVAL_TYPE_MONTH = "month";
    public static final String INTERVAL_TYPE_YEAR = "year";

    // 默认分页大小
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 私有构造函数，防止实例化
     */
    private ServiceConstants() {
        throw new IllegalStateException("常量类不应被实例化");
    }
}