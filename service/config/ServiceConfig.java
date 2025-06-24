package com.PFM.CD.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服务配置类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ServiceConfig {

    private final Properties properties;

    private static final String CONFIG_FILE = "service-config.properties";

    /**
     * 构造函数
     */
    public ServiceConfig() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * 加载配置文件
     */
    private void loadProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            System.err.println("无法加载服务配置文件: " + e.getMessage());
        }
    }

    /**
     * 获取字符串属性
     *
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 获取整数属性
     *
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 获取布尔属性
     *
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
}