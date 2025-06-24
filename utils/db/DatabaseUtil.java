package com.PFM.CD.utils.db;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-24
 * @Description:
 * @Version: 17.0
 */

import org.apache.commons.dbutils.QueryRunner;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final Properties properties = new Properties();
    private static String driverClassName;
    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("无法找到 db.properties 文件!");
                throw new RuntimeException("无法找到数据库配置文件");
            }

            // 加载配置文件
            properties.load(input);

            // 初始化数据库连接属性
            driverClassName = properties.getProperty("jdbc.driver");
            url = properties.getProperty("jdbc.url");
            username = properties.getProperty("jdbc.username");
            password = properties.getProperty("jdbc.password");

            // 加载驱动
            Class.forName(driverClassName);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("初始化数据库连接失败: " + e.getMessage());
            throw new RuntimeException("数据库配置错误", e);
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 创建QueryRunner实例
     */
    public static QueryRunner createQueryRunner() {
        return new QueryRunner();
    }

    /**
     * 关闭连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("关闭数据库连接失败: " + e.getMessage());
            }
        }
    }
    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUser() {
        return properties.getProperty("app.current_user");
    }

    /**
     * 获取最后登录时间
     */
    public static String getLastLoginTime() {
        return properties.getProperty("app.last_login");
    }
}
