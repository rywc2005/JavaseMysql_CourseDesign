package com.PFM.CD.dao.constants;

/**
 * DAO层常量类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DaoConstants {

    // 配置文件
    public static final String DB_CONFIG_FILE = "database.properties";

    // 配置属性键
    public static final String PROP_JDBC_URL = "jdbc.url";
    public static final String PROP_DB_USERNAME = "db.username";
    public static final String PROP_DB_PASSWORD = "db.password";

    // 用户表
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_CREATED_AT = "created_at";

    // 账户表
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String COLUMN_ACCOUNT_ID = "account_id";
    public static final String COLUMN_ACCOUNT_NAME = "account_name";
    public static final String COLUMN_BALANCE = "balance";
    public static final String COLUMN_STATUS = "status";

    // 分类表
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_CATEGORY_TYPE = "category_type";

    // 交易表
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_TRANSACTION_ID = "transaction_id";
    public static final String COLUMN_SOURCE_ACCOUNT_ID = "source_account_id";
    public static final String COLUMN_DESTINATION_ACCOUNT_ID = "destination_account_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TRANSACTION_TYPE = "transaction_type";
    public static final String COLUMN_TRANSACTION_DATE = "transaction_date";
    public static final String COLUMN_DESCRIPTION = "description";

    // 预算表
    public static final String TABLE_BUDGETS = "budgets";
    public static final String COLUMN_BUDGET_ID = "budget_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PERIOD_TYPE = "period_type";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_TOTAL_AMOUNT = "total_amount";

    // 预算分类表
    public static final String TABLE_BUDGET_CATEGORIES = "budget_categories";
    public static final String COLUMN_BUDGET_CATEGORY_ID = "budget_category_id";
    public static final String COLUMN_ALLOCATED_AMOUNT = "allocated_amount";
    public static final String COLUMN_SPENT_AMOUNT = "spent_amount";

    // 报表表
    public static final String TABLE_REPORTS = "reports";
    public static final String COLUMN_REPORT_ID = "report_id";
    public static final String COLUMN_REPORT_TYPE = "report_type";
    public static final String COLUMN_GENERATED_DATE = "generated_date";
    public static final String COLUMN_PARAMETERS = "parameters";

    // SQL常量
    public static final int BATCH_SIZE = 100;
    public static final int DEFAULT_QUERY_LIMIT = 1000;

    /**
     * 私有构造函数，防止实例化
     */
    private DaoConstants() {
        throw new IllegalStateException("常量类不应被实例化");
    }
}