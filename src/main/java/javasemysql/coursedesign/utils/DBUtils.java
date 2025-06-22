package javasemysql.coursedesign.utils;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javasemysql.coursedesign.utils.ConfigUtils;

/**
 * 数据库工具类，提供数据库连接和操作的方法
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class DBUtils {

    private static final Logger logger = Logger.getLogger(DBUtils.class.getName());

    // 数据库连接信息
    private static final String URL=ConfigUtils.getDbUrl();
    private static final String USERNAME = ConfigUtils.getDbUser();
    private static final String PASSWORD = ConfigUtils.getDbPassword();

    // 初始化驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.log(Level.INFO, "MySQL JDBC Driver registered");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接对象
     * @throws SQLException 如果获取连接失败
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 关闭数据库资源
     *
     * @param conn 数据库连接
     * @param stmt 语句对象
     * @param rs 结果集
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error closing database resources", e);
        }
    }

    /**
     * 执行更新操作（INSERT, UPDATE, DELETE）
     *
     * @param sql SQL语句
     * @param params 参数数组
     * @return 受影响的行数
     * @throws SQLException 如果执行失败
     */
    public static int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate();
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    /**
     * 执行查询操作
     *
     * @param sql SQL语句
     * @param params 参数数组
     * @return 结果集
     * @throws SQLException 如果执行失败
     */
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeQuery();
        } catch (SQLException e) {
            closeResources(conn, stmt, null);
            throw e;
        }
        // 注意：此处不关闭资源，由调用者负责关闭
    }

    /**
     * 执行事务操作
     *
     * @param callback 事务回调接口
     * @return 事务执行是否成功
     */
    public static boolean executeTransaction(TransactionCallback callback) {
        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            boolean result = callback.execute(conn);

            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }

            return result;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error rolling back transaction", ex);
            }
            logger.log(Level.SEVERE, "Error executing transaction", e);
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }

    public static Properties getDBProperties() {
        Properties properties = new Properties();
        properties.setProperty("user", USERNAME);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("useSSL", "false");
        properties.setProperty("serverTimezone", "UTC");
        return properties;
    }

    /**
     * 事务回调接口
     */
    public interface TransactionCallback {
        /**
         * 执行事务操作
         *
         * @param conn 数据库连接
         * @return 操作是否成功
         * @throws SQLException 如果执行失败
         */
        boolean execute(Connection conn) throws SQLException;
    }

    /**
     * 获取自动生成的主键
     *
     * @param stmt PreparedStatement对象
     * @return 自动生成的主键值，如果没有则返回-1
     */
    public static int getGeneratedKey(PreparedStatement stmt) {
        try {
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error getting generated key", e);
        }
        return -1;
    }
}