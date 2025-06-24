package com.PFM.CD.dao.factory;

import com.PFM.CD.dao.constants.DaoConstants;
import com.PFM.CD.dao.impl.*;
import com.PFM.CD.dao.interfaces.*;
import com.PFM.CD.dao.util.DatabaseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DAO工厂类，用于创建DAO实例
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DaoFactory {

    private static DaoFactory instance;
    private static final Object LOCK = new Object();

    private final ConnectionManager connectionManager;

    private UserDao userDao;
    private AccountDao accountDao;
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;
    private BudgetDao budgetDao;
    private BudgetCategoryDao budgetCategoryDao;
    private ReportDao reportDao;

    /**
     * 私有构造函数
     */
    private DaoFactory() {
        Properties properties = loadDatabaseProperties();
        this.connectionManager = ConnectionManagerImpl.fromProperties(properties);
    }

    /**
     * 获取DaoFactory实例
     *
     * @return DaoFactory实例
     */
    public static DaoFactory getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DaoFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 获取UserDao实例
     *
     * @return UserDao实例
     */
    public UserDao getUserDao() {
        if (userDao == null) {
            synchronized (LOCK) {
                if (userDao == null) {
                    userDao = new UserDaoImpl(connectionManager);
                }
            }
        }
        return userDao;
    }

    /**
     * 获取AccountDao实例
     *
     * @return AccountDao实例
     */
    public AccountDao getAccountDao() {
        if (accountDao == null) {
            synchronized (LOCK) {
                if (accountDao == null) {
                    accountDao = new AccountDaoImpl(connectionManager);
                }
            }
        }
        return accountDao;
    }

    /**
     * 获取CategoryDao实例
     *
     * @return CategoryDao实例
     */
    public CategoryDao getCategoryDao() {
        if (categoryDao == null) {
            synchronized (LOCK) {
                if (categoryDao == null) {
                    categoryDao = new CategoryDaoImpl(connectionManager);
                }
            }
        }
        return categoryDao;
    }

    /**
     * 获取TransactionDao实例
     *
     * @return TransactionDao实例
     */
    public TransactionDao getTransactionDao() {
        if (transactionDao == null) {
            synchronized (LOCK) {
                if (transactionDao == null) {
                    transactionDao = new TransactionDaoImpl(connectionManager);
                }
            }
        }
        return transactionDao;
    }

    /**
     * 获取BudgetDao实例
     *
     * @return BudgetDao实例
     */
    public BudgetDao getBudgetDao() {
        if (budgetDao == null) {
            synchronized (LOCK) {
                if (budgetDao == null) {
                    budgetDao = new BudgetDaoImpl(connectionManager);
                }
            }
        }
        return budgetDao;
    }

    /**
     * 获取BudgetCategoryDao实例
     *
     * @return BudgetCategoryDao实例
     */
    public BudgetCategoryDao getBudgetCategoryDao() {
        if (budgetCategoryDao == null) {
            synchronized (LOCK) {
                if (budgetCategoryDao == null) {
                    budgetCategoryDao = new BudgetCategoryDaoImpl(connectionManager);
                }
            }
        }
        return budgetCategoryDao;
    }

    /**
     * 获取ReportDao实例
     *
     * @return ReportDao实例
     */
    public ReportDao getReportDao() {
        if (reportDao == null) {
            synchronized (LOCK) {
                if (reportDao == null) {
                    reportDao = new ReportDaoImpl(connectionManager);
                }
            }
        }
        return reportDao;
    }

    /**
     * 关闭连接池
     */
    public void closeConnectionPool() {
        try {
            connectionManager.closeConnectionPool();
        } catch (Exception e) {
            System.err.println("关闭连接池失败: " + e.getMessage());
        }
    }

    /**
     * 加载数据库配置属性
     *
     * @return 配置属性
     */
    private Properties loadDatabaseProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DaoConstants.DB_CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                // 使用默认配置
                properties.setProperty(DaoConstants.PROP_JDBC_URL, "jdbc:mysql://localhost:3306/learn?useSSL=false&serverTimezone=UTC");
                properties.setProperty(DaoConstants.PROP_DB_USERNAME, "root");
                properties.setProperty(DaoConstants.PROP_DB_PASSWORD, "password");
            }
        } catch (IOException e) {
            System.err.println("无法加载数据库配置文件，使用默认配置: " + e.getMessage());
            // 使用默认配置
            properties.setProperty(DaoConstants.PROP_JDBC_URL, "jdbc:mysql://localhost:3306/learn?useSSL=false&serverTimezone=UTC");
            properties.setProperty(DaoConstants.PROP_DB_USERNAME, "root");
            properties.setProperty(DaoConstants.PROP_DB_PASSWORD, "password");
        }
        return properties;
    }
}