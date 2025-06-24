package com.PFM.CD.service.impl;

import com.PFM.CD.dao.interfaces.*;
import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.Report;
import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.ReportType;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.ReportService;
import com.PFM.CD.utils.report.ExcelExporter;
import com.PFM.CD.utils.report.PdfExporter;
import com.PFM.CD.utils.report.ReportGenerator;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;
    private final TransactionDao transactionDao;
    private final CategoryDao categoryDao;
    private final AccountDao accountDao;
    private final BudgetDao budgetDao;
    private final ReportGenerator reportGenerator;
    private final ExcelExporter excelExporter;
    private final PdfExporter pdfExporter;

    /**
     * 构造函数
     *
     * @param reportDao 报表DAO接口
     * @param transactionDao 交易DAO接口
     * @param categoryDao 分类DAO接口
     * @param accountDao 账户DAO接口
     * @param budgetDao 预算DAO接口
     * @param reportGenerator 报表生成器
     * @param excelExporter Excel导出器
     * @param pdfExporter PDF导出器
     */
    public ReportServiceImpl(ReportDao reportDao, TransactionDao transactionDao,
                             CategoryDao categoryDao, AccountDao accountDao,
                             BudgetDao budgetDao, ReportGenerator reportGenerator,
                             ExcelExporter excelExporter, PdfExporter pdfExporter) {
        this.reportDao = reportDao;
        this.transactionDao = transactionDao;
        this.categoryDao = categoryDao;
        this.accountDao = accountDao;
        this.budgetDao = budgetDao;
        this.reportGenerator = reportGenerator;
        this.excelExporter = excelExporter;
        this.pdfExporter = pdfExporter;
    }

    @Override
    public Report generateIncomeExpenseReport(int userId, LocalDate startDate, LocalDate endDate,
                                              Map<String, String> parameters) throws ServiceException {
        try {
            // 获取指定日期范围内的交易
            List<Transaction> transactions = transactionDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 计算总收入和总支出
            BigDecimal totalIncome = transactionDao.calculateTotalIncome(userId, startDate, endDate);
            BigDecimal totalExpense = transactionDao.calculateTotalExpense(userId, startDate, endDate);

            // 按分类统计收入和支出
            Map<Integer, BigDecimal> incomeByCategory = transactionDao.calculateIncomeByCategory(userId, startDate, endDate);
            Map<Integer, BigDecimal> expenseByCategory = transactionDao.calculateExpenseByCategory(userId, startDate, endDate);

            // 准备报表数据
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("transactions", transactions);
            reportData.put("totalIncome", totalIncome);
            reportData.put("totalExpense", totalExpense);
            reportData.put("incomeByCategory", incomeByCategory);
            reportData.put("expenseByCategory", expenseByCategory);
            reportData.put("startDate", startDate);
            reportData.put("endDate", endDate);

            // 生成报表内容
            String reportContent = reportGenerator.generateIncomeExpenseReport(reportData);

            // 创建报表记录
            Report report = new Report();
            report.setUserId(userId);
            report.setReportType(ReportType.INCOME_EXPENSE);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setGeneratedDate(LocalDate.now());
            report.setParameters(mapToJson(parameters));
            report.setContent(reportContent);

            // 保存报表
            boolean success = reportDao.save(report);
            if (!success) {
                throw new ServiceException("保存报表失败");
            }

            return report;
        } catch (SQLException e) {
            throw new ServiceException("生成收支报表过程中发生数据库错误", e);
        } catch (Exception e) {
            throw new ServiceException("生成收支报表失败", e);
        }
    }

    @Override
    public Report generateBudgetReport(int userId, LocalDate startDate, LocalDate endDate,
                                       Map<String, String> parameters) throws ServiceException {
        try {
            // 获取指定日期范围内的预算
            List<Budget> budgets = budgetDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 准备报表数据
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("budgets", budgets);
            reportData.put("startDate", startDate);
            reportData.put("endDate", endDate);

            // 生成报表内容
            String reportContent = reportGenerator.generateBudgetReport(reportData);

            // 创建报表记录
            Report report = new Report();
            report.setUserId(userId);
            report.setReportType(ReportType.BUDGET_EVALUATION);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setGeneratedDate(LocalDate.now());
            report.setParameters(mapToJson(parameters));
            report.setContent(reportContent);

            // 保存报表
            boolean success = reportDao.save(report);
            if (!success) {
                throw new ServiceException("保存报表失败");
            }

            return report;
        } catch (SQLException e) {
            throw new ServiceException("生成预算报表过程中发生数据库错误", e);
        } catch (Exception e) {
            throw new ServiceException("生成预算报表失败", e);
        }
    }

    @Override
    public Report generateCategoryReport(int userId, LocalDate startDate, LocalDate endDate,
                                         Map<String, String> parameters) throws ServiceException {
        try {
            // 按分类统计收入和支出
            Map<Integer, BigDecimal> incomeByCategory = transactionDao.calculateIncomeByCategory(userId, startDate, endDate);
            Map<Integer, BigDecimal> expenseByCategory = transactionDao.calculateExpenseByCategory(userId, startDate, endDate);

            // 获取所有分类信息
            List<Category> categories = categoryDao.findAll();

            // 准备报表数据
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("incomeByCategory", incomeByCategory);
            reportData.put("expenseByCategory", expenseByCategory);
            reportData.put("categories", categories);
            reportData.put("startDate", startDate);
            reportData.put("endDate", endDate);

            // 生成报表内容
            String reportContent = reportGenerator.generateCategoryReport(reportData);

            // 创建报表记录
            Report report = new Report();
            report.setUserId(userId);
            report.setReportType(ReportType.CATEGORY);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setGeneratedDate(LocalDate.now());
            report.setParameters(mapToJson(parameters));
            report.setContent(reportContent);

            // 保存报表
            boolean success = reportDao.save(report);
            if (!success) {
                throw new ServiceException("保存报表失败");
            }

            return report;
        } catch (SQLException e) {
            throw new ServiceException("生成分类报表过程中发生数据库错误", e);
        } catch (Exception e) {
            throw new ServiceException("生成分类报表失败", e);
        }
    }

    @Override
    public Report generateAccountReport(int userId, LocalDate startDate, LocalDate endDate,
                                        Map<String, String> parameters) throws ServiceException {
        try {
            // 获取用户的所有账户
            List<Account> accounts = accountDao.findByUserId(userId);

            // 获取指定日期范围内的交易
            List<Transaction> transactions = transactionDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 准备报表数据
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("accounts", accounts);
            reportData.put("transactions", transactions);
            reportData.put("startDate", startDate);
            reportData.put("endDate", endDate);

            // 生成报表内容
            String reportContent = reportGenerator.generateAccountReport(reportData);

            // 创建报表记录
            Report report = new Report();
            report.setUserId(userId);
            report.setReportType(ReportType.ACCOUNT);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setGeneratedDate(LocalDate.now());
            report.setParameters(mapToJson(parameters));
            report.setContent(reportContent);

            // 保存报表
            boolean success = reportDao.save(report);
            if (!success) {
                throw new ServiceException("保存报表失败");
            }

            return report;
        } catch (SQLException e) {
            throw new ServiceException("生成账户报表过程中发生数据库错误", e);
        } catch (Exception e) {
            throw new ServiceException("生成账户报表失败", e);
        }
    }

    @Override
    public Report generateCustomReport(int userId, ReportType reportType, LocalDate startDate,
                                       LocalDate endDate, Map<String, String> parameters) throws ServiceException {
        try {
            // 根据报表类型选择不同的生成方法
            switch (reportType) {
                case INCOME_EXPENSE:
                    return generateIncomeExpenseReport(userId, startDate, endDate, parameters);
                case BUDGET:
                    return generateBudgetReport(userId, startDate, endDate, parameters);
                case CATEGORY:
                    return generateCategoryReport(userId, startDate, endDate, parameters);
                case ACCOUNT:
                    return generateAccountReport(userId, startDate, endDate, parameters);
                default:
                    throw new ServiceException("不支持的报表类型: " + reportType);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("生成自定义报表失败", e);
        }
    }

    @Override
    public Report getReportById(int reportId) throws ServiceException {
        try {
            Report report = reportDao.findById(reportId);
            if (report == null) {
                throw new ServiceException("报表不存在: " + reportId);
            }
            return report;
        } catch (SQLException e) {
            throw new ServiceException("获取报表信息过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean deleteReport(int reportId) throws ServiceException {
        try {
            // 检查报表是否存在
            Report report = reportDao.findById(reportId);
            if (report == null) {
                throw new ServiceException("报表不存在: " + reportId);
            }

            return reportDao.delete(reportId);
        } catch (SQLException e) {
            throw new ServiceException("删除报表过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Report> getUserReports(int userId) throws ServiceException {
        try {
            return reportDao.findByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("获取用户报表过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Report> getReportsByType(int userId, ReportType reportType) throws ServiceException {
        try {
            return reportDao.findByUserIdAndType(userId, reportType);
        } catch (SQLException e) {
            throw new ServiceException("按类型获取报表过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Report> getRecentReports(int userId, int limit) throws ServiceException {
        try {
            return reportDao.findRecentByUserId(userId, limit);
        } catch (SQLException e) {
            throw new ServiceException("获取最近报表过程中发生数据库错误", e);
        }
    }

    @Override
    public void exportReportToPdf(int reportId, OutputStream outputStream) throws ServiceException {
        try {
            // 获取报表信息
            Report report = reportDao.findById(reportId);
            if (report == null) {
                throw new ServiceException("报表不存在: " + reportId);
            }

            // 导出为PDF
            pdfExporter.export(report, outputStream);
        } catch (SQLException e) {
            throw new ServiceException("导出报表过程中发生数据库错误", e);
        } catch (Exception e) {
            throw new ServiceException("导出报表为PDF失败", e);
        }
    }

    @Override
    public void exportReportToExcel(int reportId, OutputStream outputStream) throws ServiceException {
        try {
            // 获取报表信息
            Report report = reportDao.findById(reportId);
            if (report == null) {
                throw new ServiceException("报表不存在: " + reportId);
            }

            // 导出为Excel
            excelExporter.export(report, outputStream);
        } catch (SQLException e) {
            throw new ServiceException("导出报表过程中发生数据库错误", e);
        } catch (Exception e) {
            throw new ServiceException("导出报表为Excel失败", e);
        }
    }

    @Override
    public int deleteOldReports(int userId, LocalDate olderThan) throws ServiceException {
        try {
            return reportDao.deleteOlderThan(userId, olderThan);
        } catch (SQLException e) {
            throw new ServiceException("删除旧报表过程中发生数据库错误", e);
        }
    }

    /**
     * 将Map转换为JSON字符串
     */
    private String mapToJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }

        sb.append("}");
        return sb.toString();
    }
}