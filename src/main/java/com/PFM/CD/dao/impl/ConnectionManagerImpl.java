package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.constants.DaoConstants;
import com.PFM.CD.dao.exception.DatabaseConnectionException;
import com.PFM.CD.dao.interfaces.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 数据库连接管理实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ConnectionManagerImpl implements ConnectionManager {

    private static final int MAX_CONNECTIONS = 10;
    private static final int INITIAL_CONNECTIONS = 0;

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final ConcurrentLinkedQueue<Connection> connectionPool;

    /**
     * 构造函数
     *
     * @param jdbcUrl 数据库URL
     * @param username 用户名
     * @param password 密码
     */
    public ConnectionManagerImpl(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.connectionPool = new ConcurrentLinkedQueue<>();

        try {
            initConnectionPool();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("初始化连接池失败", e);
        }
    }

    /**
     * 从配置文件创建ConnectionManager
     *
     * @param properties 配置属性
     * @return ConnectionManager实例
     */
    public static ConnectionManager fromProperties(Properties properties) {
        String jdbcUrl = properties.getProperty(DaoConstants.PROP_JDBC_URL);
        String username = properties.getProperty(DaoConstants.PROP_DB_USERNAME);
        String password = properties.getProperty(DaoConstants.PROP_DB_PASSWORD);

        if (jdbcUrl == null || username == null || password == null) {
            throw new IllegalArgumentException("数据库配置不完整");
        }

        return new ConnectionManagerImpl(jdbcUrl, username, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = connectionPool.poll();

        if (connection == null) {
            connection = createConnection();
        } else if (connection.isClosed()) {
            connection = createConnection();
        }

        return connection;
    }

    @Override
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }

                if (!connection.isClosed() && connectionPool.size() < MAX_CONNECTIONS) {
                    connectionPool.offer(connection);
                } else {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        // 忽略关闭连接时的异常
                    }
                }
            } catch (SQLException e) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    // 忽略关闭连接时的异常
                }
            }
        }
    }

    @Override
    public void beginTransaction(Connection connection) throws SQLException {
        if (connection != null && connection.getAutoCommit()) {
            connection.setAutoCommit(false);
        }
    }

    @Override
    public void commitTransaction(Connection connection) throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void rollbackTransaction(Connection connection) throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void initConnectionPool() throws SQLException {
        for (int i = 0; i < INITIAL_CONNECTIONS; i++) {
            connectionPool.offer(createConnection());
        }
    }

    @Override
    public void closeConnectionPool() throws SQLException {
        Connection connection;
        while ((connection = connectionPool.poll()) != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // 忽略关闭连接时的异常
            }
        }
    }

    /**
     * 创建新的数据库连接
     *
     * @return 数据库连接
     * @throws SQLException 如果创建连接失败
     */
    private Connection createConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            connection.setAutoCommit(true);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC驱动未找到", e);
        }
    }
}