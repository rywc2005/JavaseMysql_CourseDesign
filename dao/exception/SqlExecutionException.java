package com.PFM.CD.dao.exception;

/**
 * SQL执行异常，当SQL语句执行失败时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class SqlExecutionException extends DaoException {

    private static final long serialVersionUID = 1L;

    private final String sql;
    private final String sqlState;
    private final int errorCode;

    /**
     * 构造一个带有SQL语句和原始SQL异常的SQL执行异常
     *
     * @param sql 执行失败的SQL语句
     * @param cause 原始SQL异常
     */
    public SqlExecutionException(String sql, Throwable cause) {
        super("SQL执行失败", cause);
        this.sql = sql;
        this.sqlState = extractSqlState(cause);
        this.errorCode = extractErrorCode(cause);
    }

    /**
     * 构造一个带有错误消息、SQL语句和原始SQL异常的SQL执行异常
     *
     * @param message 错误消息
     * @param sql 执行失败的SQL语句
     * @param cause 原始SQL异常
     */
    public SqlExecutionException(String message, String sql, Throwable cause) {
        super(message, cause);
        this.sql = sql;
        this.sqlState = extractSqlState(cause);
        this.errorCode = extractErrorCode(cause);
    }

    /**
     * 从原始异常中提取SQL状态
     */
    private String extractSqlState(Throwable cause) {
        if (cause instanceof java.sql.SQLException) {
            return ((java.sql.SQLException) cause).getSQLState();
        }
        return "UNKNOWN";
    }

    /**
     * 从原始异常中提取错误代码
     */
    private int extractErrorCode(Throwable cause) {
        if (cause instanceof java.sql.SQLException) {
            return ((java.sql.SQLException) cause).getErrorCode();
        }
        return -1;
    }

    /**
     * 获取执行失败的SQL语句
     *
     * @return SQL语句
     */
    public String getSql() {
        return sql;
    }

    /**
     * 获取SQL状态
     *
     * @return SQL状态
     */
    public String getSqlState() {
        return sqlState;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 判断是否为唯一性约束违反
     *
     * @return 如果是唯一性约束违反返回true，否则返回false
     */
    public boolean isUniqueConstraintViolation() {
        // MySQL: 1062, SQLState: 23000
        // PostgreSQL: SQLState: 23505
        // Oracle: ORA-00001, SQLState: 23000
        // SQL Server: 2627 or 2601, SQLState: 23000
        return (errorCode == 1062 || errorCode == 2627 || errorCode == 2601 ||
                "23000".equals(sqlState) || "23505".equals(sqlState));
    }

    /**
     * 判断是否为外键约束违反
     *
     * @return 如果是外键约束违反返回true，否则返回false
     */
    public boolean isForeignKeyConstraintViolation() {
        // MySQL: 1451 (delete) or 1452 (insert/update), SQLState: 23000
        // PostgreSQL: SQLState: 23503
        // Oracle: ORA-02291 or ORA-02292, SQLState: 23000
        // SQL Server: 547, SQLState: 23000
        return (errorCode == 1451 || errorCode == 1452 || errorCode == 547 ||
                "23503".equals(sqlState) ||
                (getCause() != null && getCause().getMessage() != null &&
                        (getCause().getMessage().contains("ORA-02291") ||
                                getCause().getMessage().contains("ORA-02292"))));
    }
}