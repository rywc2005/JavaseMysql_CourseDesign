package com.PFM.CD.service.interfaces;

import com.PFM.CD.entity.Report;
import com.PFM.CD.entity.enums.ReportType;
import com.PFM.CD.service.exception.ServiceException;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表服务接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface ReportService {

    Report generateIncomeExpenseReport(int userId, LocalDate startDate, LocalDate endDate,
                                       Map<String, String> parameters) throws ServiceException;

    Report generateBudgetReport(int userId, LocalDate startDate, LocalDate endDate,
                                Map<String, String> parameters) throws ServiceException;

    Report generateCategoryReport(int userId, LocalDate startDate, LocalDate endDate,
                                  Map<String, String> parameters) throws ServiceException;

    Report generateAccountReport(int userId, LocalDate startDate, LocalDate endDate,
                                 Map<String, String> parameters) throws ServiceException;

    /**
     * 根据报表类型生成自定义报表
     */
    Report generateCustomReport(int userId, ReportType reportType, LocalDate startDate,
                                LocalDate endDate, Map<String, String> parameters) throws ServiceException;

    /**
     * 获取指定ID的报表
     */
    Report getReportById(int reportId) throws ServiceException;

    /**
     * 删除指定ID的报表
     */
    boolean deleteReport(int reportId) throws ServiceException;

    /**
     * 获取用户的所有报表
     */
    List<Report> getUserReports(int userId) throws ServiceException;

    /**
     * 获取用户指定类型的报表
     */
    List<Report> getReportsByType(int userId, ReportType reportType) throws ServiceException;

    /**
     * 获取用户最近生成的报表
     */
    List<Report> getRecentReports(int userId, int limit) throws ServiceException;

    /**
     * 导出报表为PDF
     * @param reportId 报表ID
     * @param outputStream 输出流
     * @throws ServiceException 导出失败时抛出
     */
    void exportReportToPdf(int reportId, OutputStream outputStream) throws ServiceException;

    /**
     * 导出报表为Excel
     * @param reportId 报表ID
     * @param outputStream 输出流
     * @throws ServiceException 导出失败时抛出
     */
    void exportReportToExcel(int reportId, OutputStream outputStream) throws ServiceException;

    /**
     * 删除某用户指定日期之前的所有报表
     * @param userId 用户ID
     * @param olderThan 日期
     * @return 删除的报表数量
     */
    int deleteOldReports(int userId, LocalDate olderThan) throws ServiceException;
}