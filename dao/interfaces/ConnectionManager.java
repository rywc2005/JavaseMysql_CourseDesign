package com.PFM.CD.dao.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接管理接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface ConnectionManager {

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     * @throws SQLException 如果获取连接失败
     */
    Connection getConnection() throws SQLException;

    /**
     * 关闭数据库连接
     *
     * @param connection 数据库连接
     */
    void closeConnection(Connection connection);

    /**
     * 开始事务
     *
     * @param connection 数据库连接
     * @throws SQLException 如果操作失败
     */
    void beginTransaction(Connection connection) throws SQLException;

    /**
     * 提交事务
     *
     * @param connection 数据库连接
     * @throws SQLException 如果操作失败
     */
    void commitTransaction(Connection connection) throws SQLException;

    /**
     * 回滚事务
     *
     * @param connection 数据库连接
     * @throws SQLException 如果操作失败
     */
    void rollbackTransaction(Connection connection) throws SQLException;

    /**
     * 初始化连接池
     *
     * @throws SQLException 如果初始化失败
     */
    void initConnectionPool() throws SQLException;

    /**
     * 关闭连接池
     *
     * @throws SQLException 如果关闭失败
     */
    void closeConnectionPool() throws SQLException;
}