package javasemysql.coursedesign.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 配置工具类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class ConfigUtils {

    private static final Logger logger = Logger.getLogger(ConfigUtils.class.getName());

    private static final String CONFIG_FILE = "src/main/resources/config.properties";// 配置文件路径
    private static Properties properties = new Properties();

    // 默认配置
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/finance_db?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "password";
    private static final String DEFAULT_LOG_LEVEL = "INFO";

    /**
     * 加载配置文件
     *
     * @throws IOException 如果读取配置文件失败
     */
    public static void loadConfig() throws IOException {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            logger.info("配置文件加载成功");
        } catch (IOException e) {
            logger.log(Level.WARNING, "未找到配置文件，将使用默认配置", e);
            setDefaultConfig();
            throw e;
        }
    }

    /**
     * 设置默认配置
     */
    private static void setDefaultConfig() {
        properties.setProperty("db.url", DEFAULT_DB_URL);
        properties.setProperty("db.user", DEFAULT_DB_USER);
        properties.setProperty("db.password", DEFAULT_DB_PASSWORD);
        properties.setProperty("log.level", DEFAULT_LOG_LEVEL);
    }

    /**
     * 获取数据库URL
     *
     * @return 数据库URL
     */
    public static String getDbUrl() {
        return properties.getProperty("db.url", DEFAULT_DB_URL);
    }

    /**
     * 获取数据库用户名
     *
     * @return 数据库用户名
     */
    public static String getDbUser() {
        return properties.getProperty("db.user", DEFAULT_DB_USER);
    }

    /**
     * 获取数据库密码
     *
     * @return 数据库密码
     */
    public static String getDbPassword() {
        return properties.getProperty("db.password", DEFAULT_DB_PASSWORD);
    }

    /**
     * 获取日志级别
     *
     * @return 日志级别
     */
    public static String getLogLevel() {
        return properties.getProperty("log.level", DEFAULT_LOG_LEVEL);
    }

    /**
     * 获取指定键的属性值
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 属性值，如果不存在则返回默认值
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}