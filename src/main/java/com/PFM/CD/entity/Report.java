package com.PFM.CD.entity;

import com.PFM.CD.entity.enums.ReportType;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 报表实体类
 * 对应数据库reports表
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class Report {

    private int reportId;
    private int userId;
    private ReportType reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate generatedDate;
    private String parameters;  // JSON格式参数

    /**
     * 默认构造函数
     */
    public Report() {
        this.generatedDate = LocalDate.now();
    }

    /**
     * 带参数构造函数
     */
    public Report(int userId, ReportType reportType, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedDate = LocalDate.now();
    }

    /**
     * 完整构造函数
     */
    public Report(int reportId, int userId, ReportType reportType, LocalDate startDate,
                  LocalDate endDate, LocalDate generatedDate, String parameters) {
        this.reportId = reportId;
        this.userId = userId;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedDate = generatedDate;
        this.parameters = parameters;
    }

    // Getters and Setters

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    /**
     * 获取报表周期名称
     * @return 报表周期名称，例如"2025年6月"
     */
    public String getPeriodName() {
        int startYear = startDate.getYear();
        int startMonth = startDate.getMonthValue();
        int endYear = endDate.getYear();
        int endMonth = endDate.getMonthValue();

        if (startYear == endYear && startMonth == endMonth) {
            return startYear + "年" + startMonth + "月";
        } else if (startYear == endYear) {
            return startYear + "年" + startMonth + "月至" + endMonth + "月";
        } else {
            return startYear + "年" + startMonth + "月至" +
                    endYear + "年" + endMonth + "月";
        }
    }

    /**
     * 获取报表的默认文件名
     * @return 默认文件名
     */
    public String getDefaultFileName() {
        return reportType.getFileNamePrefix() + "_" +
                startDate.toString() + "_to_" +
                endDate.toString() + ".pdf";
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", userId=" + userId +
                ", reportType=" + reportType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedDate=" + generatedDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return reportId == report.reportId &&
                userId == report.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, userId);
    }
}