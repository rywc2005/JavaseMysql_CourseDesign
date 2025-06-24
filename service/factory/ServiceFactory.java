package com.PFM.CD.service.factory;

import com.PFM.CD.dao.factory.DaoFactory;
import com.PFM.CD.service.config.ServiceConfig;
import com.PFM.CD.service.impl.*;
import com.PFM.CD.service.interfaces.*;
import com.PFM.CD.utils.report.ExcelExporter;
import com.PFM.CD.utils.report.PdfExporter;
import com.PFM.CD.utils.report.ReportGenerator;

/**
 * 服务工厂类，用于创建Service实例
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ServiceFactory {

    private static ServiceFactory instance;
    private static final Object LOCK = new Object();

    private final DaoFactory daoFactory;
    private final ServiceConfig serviceConfig;

    private UserService userService;
    private AccountService accountService;
    private CategoryService categoryService;
    private TransactionService transactionService;
    private BudgetService budgetService;
    private ReportService reportService;
    private StatisticsService statisticsService;

    /**
     * 私有构造函数
     */
    private ServiceFactory() {
        this.daoFactory = DaoFactory.getInstance();
        this.serviceConfig = new ServiceConfig();
    }

    /**
     * 获取ServiceFactory实例
     *
     * @return ServiceFactory实例
     */
    public static ServiceFactory getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 获取UserService实例
     *
     * @return UserService实例
     */
    public UserService getUserService() {
        if (userService == null) {
            synchronized (LOCK) {
                if (userService == null) {
                    userService = new UserServiceImpl(daoFactory.getUserDao());
                }
            }
        }
        return userService;
    }

    /**
     * 获取AccountService实例
     *
     * @return AccountService实例
     */
    public AccountService getAccountService() {
        if (accountService == null) {
            synchronized (LOCK) {
                if (accountService == null) {
                    accountService = new AccountServiceImpl(
                            daoFactory.getAccountDao(),
                            daoFactory.getTransactionDao());
                }
            }
        }
        return accountService;
    }

    /**
     * 获取CategoryService实例
     *
     * @return CategoryService实例
     */
    public CategoryService getCategoryService() {
        if (categoryService == null) {
            synchronized (LOCK) {
                if (categoryService == null) {
                    categoryService = new CategoryServiceImpl(daoFactory.getCategoryDao());
                }
            }
        }
        return categoryService;
    }

    /**
     * 获取TransactionService实例
     *
     * @return TransactionService实例
     */
    public TransactionService getTransactionService() {
        if (transactionService == null) {
            synchronized (LOCK) {
                if (transactionService == null) {
                    transactionService = new TransactionServiceImpl(
                            daoFactory.getTransactionDao(),
                            daoFactory.getAccountDao(),
                            daoFactory.getCategoryDao(),
                            daoFactory.getBudgetCategoryDao());
                }
            }
        }
        return transactionService;
    }

    /**
     * 获取BudgetService实例
     *
     * @return BudgetService实例
     */
    public BudgetService getBudgetService() {
        if (budgetService == null) {
            synchronized (LOCK) {
                if (budgetService == null) {
                    budgetService = new BudgetServiceImpl(
                            daoFactory.getBudgetDao(),
                            daoFactory.getBudgetCategoryDao(),
                            daoFactory.getCategoryDao());
                }
            }
        }
        return budgetService;
    }

    /**
     * 获取ReportService实例
     *
     * @return ReportService实例
     */
    public ReportService getReportService() {
        if (reportService == null) {
            synchronized (LOCK) {
                if (reportService == null) {
                    reportService = new ReportServiceImpl(
                            daoFactory.getReportDao(),
                            daoFactory.getTransactionDao(),
                            daoFactory.getCategoryDao(),
                            daoFactory.getAccountDao(),
                            daoFactory.getBudgetDao(),
                            new ReportGenerator(),
                            new ExcelExporter(),
                            new PdfExporter());
                }
            }
        }
        return reportService;
    }

    /**
     * 获取StatisticsService实例
     *
     * @return StatisticsService实例
     */
    public StatisticsService getStatisticsService() {
        if (statisticsService == null) {
            synchronized (LOCK) {
                if (statisticsService == null) {
                    statisticsService = new StatisticsServiceImpl(
                            daoFactory.getTransactionDao(),
                            daoFactory.getAccountDao(),
                            daoFactory.getCategoryDao(),
                            daoFactory.getBudgetDao());
                }
            }
        }
        return statisticsService;
    }

    /**
     * 重置所有服务
     */
    public void reset() {
        userService = null;
        accountService = null;
        categoryService = null;
        transactionService = null;
        budgetService = null;
        reportService = null;
        statisticsService = null;
    }
}