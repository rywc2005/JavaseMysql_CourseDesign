package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.interfaces.BaseDao;
import com.PFM.CD.utils.db.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO基础实现类，提供通用数据访问功能
 *
 * @param <T> 实体类型
 * @param <K> 主键类型
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public abstract class BaseDaoImpl<T, K> implements BaseDao<T, K> {

    @Override
    public Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection();
    }

    @Override
    public void beginTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }

    @Override
    public void commitTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    @Override
    public void rollbackTransaction(Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
            conn.setAutoCommit(true);
        }
    }

    @Override
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("关闭数据库连接失败: " + e.getMessage());
            }
        }
    }

    /**
     * 执行带有事务的操作
     *
     * @param action 要执行的操作
     * @return 操作结果
     * @throws SQLException 如果发生SQL错误
     */
    protected boolean executeWithTransaction(TransactionAction action) throws SQLException {
        Connection conn = null;
        boolean result = false;

        try {
            conn = getConnection();
            beginTransaction(conn);

            result = action.execute(conn);

            commitTransaction(conn);
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                rollbackTransaction(conn);
            }
            throw e;
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 函数式接口，用于执行事务操作
     */
    @FunctionalInterface
    protected interface TransactionAction {
        boolean execute(Connection conn) throws SQLException;
    }
}