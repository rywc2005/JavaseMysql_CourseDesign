package com.PFM.CD.dao.util;

import com.PFM.CD.dao.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;
import java.util.function.Supplier;

/**
 * 数据库工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DatabaseUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private DatabaseUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 关闭数据库资源
     *
     * @param resources 数据库资源，如Connection、Statement、ResultSet等
     */
    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // 忽略关闭资源时的异常
                }
            }
        }
    }

    /**
     * 在事务中执行操作
     *
     * @param <T> 返回值类型
     * @param connectionSupplier 连接提供者
     * @param operation 操作
     * @return 操作结果
     * @throws SQLException 如果操作失败
     */
    public static <T> T executeInTransaction(Supplier<Connection> connectionSupplier,
                                             TransactionOperation<T> operation) throws SQLException {
        Connection conn = null;
        try {
            conn = connectionSupplier.get();
            conn.setAutoCommit(false);

            T result = operation.execute(conn);

            conn.commit();
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseConnectionException("回滚事务失败", ex);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // 忽略关闭连接时的异常
                }
            }
        }
    }

    /**
     * 事务操作接口
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface TransactionOperation<T> {
        /**
         * 执行事务操作
         *
         * @param connection 数据库连接
         * @return 操作结果
         * @throws SQLException 如果操作失败
         */
        T execute(Connection connection) throws SQLException;
    }

    /**
     * 设置日期参数
     *
     * @param statement 预处理语句
     * @param parameterIndex 参数索引
     * @param date 日期
     * @throws SQLException 如果设置参数失败
     */
    public static void setLocalDate(PreparedStatement statement, int parameterIndex, LocalDate date)
            throws SQLException {
        if (date == null) {
            statement.setNull(parameterIndex, java.sql.Types.DATE);
        } else {
            statement.setDate(parameterIndex, Date.valueOf(date));
        }
    }

    /**
     * 获取日期结果
     *
     * @param resultSet 结果集
     * @param columnLabel 列标签
     * @return 日期，如果为NULL则返回null
     * @throws SQLException 如果获取结果失败
     */
    public static LocalDate getLocalDate(ResultSet resultSet, String columnLabel) throws SQLException {
        Date date = resultSet.getDate(columnLabel);
        return date != null ? date.toLocalDate() : null;
    }

    /**
     * 获取整数结果，如果为NULL则返回默认值
     *
     * @param resultSet 结果集
     * @param columnLabel 列标签
     * @param defaultValue 默认值
     * @return 整数，如果为NULL则返回默认值
     * @throws SQLException 如果获取结果失败
     */
    public static int getIntOrDefault(ResultSet resultSet, String columnLabel, int defaultValue)
            throws SQLException {
        int value = resultSet.getInt(columnLabel);
        return resultSet.wasNull() ? defaultValue : value;
    }

    /**
     * 检查是否是唯一性约束违反
     *
     * @param e SQL异常
     * @return 如果是唯一性约束违反则返回true，否则返回false
     */
    public static boolean isUniqueConstraintViolation(SQLException e) {
        return e.getSQLState() != null &&
                (e.getSQLState().startsWith("23") || // SQL标准
                        e.getErrorCode() == 1062); // MySQL
    }
}