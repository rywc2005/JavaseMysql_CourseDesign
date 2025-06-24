package com.PFM.CD.utils.report;

import com.PFM.CD.entity.Report;
import com.PFM.CD.service.dto.CategoryDistribution;
import com.PFM.CD.service.dto.IncomeExpenseTrend;
import com.PFM.CD.utils.file.TempFileManager;
import com.PFM.CD.utils.format.NumberFormatter;

import com.PFM.CD.utils.format.DateFormatter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Excel报表导出工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ExcelExporter {

    /**
     * 导出收支报表到Excel
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param incomeExpenseTrends 收支趋势数据
     * @param expenseDistributions 支出分布数据
     * @param incomeDistributions 收入分布数据
     * @param summaryData 汇总数据
     * @return 临时文件路径
     * @throws IOException 如果导出过程中发生IO错误
     */
    public Path exportIncomeExpenseReport(int userId, LocalDate startDate, LocalDate endDate,
                                          List<IncomeExpenseTrend> incomeExpenseTrends,
                                          List<CategoryDistribution> expenseDistributions,
                                          List<CategoryDistribution> incomeDistributions,
                                          Map<String, Object> summaryData) throws IOException {
        // 创建工作簿和工作表
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle amountStyle = createAmountStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);

            // 创建摘要工作表
            Sheet summarySheet = workbook.createSheet("收支摘要");
            createIncomeExpenseSummary(summarySheet, headerStyle, titleStyle, amountStyle,
                    startDate, endDate, summaryData);

            // 创建收入分布工作表
            Sheet incomeSheet = workbook.createSheet("收入分布");
            createDistributionSheet(incomeSheet, headerStyle, titleStyle, amountStyle, percentStyle,
                    "收入分布", incomeDistributions);

            // 创建支出分布工作表
            Sheet expenseSheet = workbook.createSheet("支出分布");
            createDistributionSheet(expenseSheet, headerStyle, titleStyle, amountStyle, percentStyle,
                    "支出分布", expenseDistributions);

            // 创建收支趋势工作表
            Sheet trendSheet = workbook.createSheet("收支趋势");
            createTrendSheet(trendSheet, headerStyle, titleStyle, dateStyle, amountStyle,
                    incomeExpenseTrends);

            // 自动调整列宽
            for (int i = 0; i < 5; i++) {
                summarySheet.autoSizeColumn(i);
                incomeSheet.autoSizeColumn(i);
                expenseSheet.autoSizeColumn(i);
                trendSheet.autoSizeColumn(i);
            }

            // 创建临时文件
            Path tempFile = TempFileManager.createTempFile("income_expense_report_", ".xlsx");

            // 写入文件
            try (FileOutputStream fileOut = new FileOutputStream(tempFile.toFile())) {
                workbook.write(fileOut);
            }

            return tempFile;
        }
    }

    /**
     * 导出预算报表到Excel
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param budgetData 预算数据
     * @return 临时文件路径
     * @throws IOException 如果导出过程中发生IO错误
     */
    public Path exportBudgetReport(int userId, LocalDate startDate, LocalDate endDate,
                                   Map<String, Object> budgetData) throws IOException {
        // 创建工作簿和工作表
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle amountStyle = createAmountStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);

            // 创建摘要工作表
            Sheet summarySheet = workbook.createSheet("预算摘要");
            createBudgetSummary(summarySheet, headerStyle, titleStyle, amountStyle, percentStyle,
                    startDate, endDate, budgetData);

            // 创建预算详情工作表
            Sheet detailsSheet = workbook.createSheet("预算详情");
            createBudgetDetails(detailsSheet, headerStyle, titleStyle, amountStyle, percentStyle,
                    budgetData);

            // 自动调整列宽
            for (int i = 0; i < 7; i++) {
                summarySheet.autoSizeColumn(i);
                detailsSheet.autoSizeColumn(i);
            }

            // 创建临时文件
            Path tempFile = TempFileManager.createTempFile("budget_report_", ".xlsx");

            // 写入文件
            try (FileOutputStream fileOut = new FileOutputStream(tempFile.toFile())) {
                workbook.write(fileOut);
            }

            return tempFile;
        }
    }

    /**
     * 导出账户报表到Excel
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountData 账户数据
     * @return 临时文件路径
     * @throws IOException 如果导出过程中发生IO错误
     */
    public Path exportAccountReport(int userId, LocalDate startDate, LocalDate endDate,
                                    Map<String, Object> accountData) throws IOException {
        // 创建工作簿和工作表
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle amountStyle = createAmountStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);

            // 创建摘要工作表
            Sheet summarySheet = workbook.createSheet("账户摘要");
            createAccountSummary(summarySheet, headerStyle, titleStyle, amountStyle,
                    startDate, endDate, accountData);

            // 创建账户详情工作表
            Sheet detailsSheet = workbook.createSheet("账户详情");
            createAccountDetails(detailsSheet, headerStyle, titleStyle, amountStyle,
                    accountData);

            // 创建余额趋势工作表
            Sheet trendSheet = workbook.createSheet("余额趋势");
            createBalanceTrends(trendSheet, headerStyle, titleStyle, dateStyle, amountStyle, percentStyle,
                    accountData);

            // 自动调整列宽
            for (int i = 0; i < 7; i++) {
                summarySheet.autoSizeColumn(i);
                detailsSheet.autoSizeColumn(i);
                trendSheet.autoSizeColumn(i);
            }

            // 创建临时文件
            Path tempFile = TempFileManager.createTempFile("account_report_", ".xlsx");

            // 写入文件
            try (FileOutputStream fileOut = new FileOutputStream(tempFile.toFile())) {
                workbook.write(fileOut);
            }

            return tempFile;
        }
    }

    /**
     * 创建收支摘要工作表
     */
    private void createIncomeExpenseSummary(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                            CellStyle amountStyle, LocalDate startDate, LocalDate endDate,
                                            Map<String, Object> summaryData) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("收支报表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // 日期行
        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("报表周期: " + DateFormatter.format(startDate) + " 至 " + DateFormatter.format(endDate));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        // 空行
        sheet.createRow(2);

        // 摘要标题
        Row summaryTitleRow = sheet.createRow(3);
        Cell summaryTitleCell = summaryTitleRow.createCell(0);
        summaryTitleCell.setCellValue("收支摘要");
        summaryTitleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));

        // 摘要表头
        Row headerRow = sheet.createRow(4);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("项目");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("金额");
        headerCell2.setCellStyle(headerStyle);

        // 摘要数据
        BigDecimal totalIncome = (BigDecimal) summaryData.get("totalIncome");
        BigDecimal totalExpense = (BigDecimal) summaryData.get("totalExpense");
        BigDecimal balance = totalIncome.subtract(totalExpense);

        // 总收入行
        Row incomeRow = sheet.createRow(5);
        Cell incomeCell1 = incomeRow.createCell(0);
        incomeCell1.setCellValue("总收入");

        Cell incomeCell2 = incomeRow.createCell(1);
        incomeCell2.setCellValue(totalIncome.doubleValue());
        incomeCell2.setCellStyle(amountStyle);

        // 总支出行
        Row expenseRow = sheet.createRow(6);
        Cell expenseCell1 = expenseRow.createCell(0);
        expenseCell1.setCellValue("总支出");

        Cell expenseCell2 = expenseRow.createCell(1);
        expenseCell2.setCellValue(totalExpense.doubleValue());
        expenseCell2.setCellStyle(amountStyle);

        // 结余行
        Row balanceRow = sheet.createRow(7);
        Cell balanceCell1 = balanceRow.createCell(0);
        balanceCell1.setCellValue("结余");

        Cell balanceCell2 = balanceRow.createCell(1);
        balanceCell2.setCellValue(balance.doubleValue());
        balanceCell2.setCellStyle(amountStyle);
    }

    /**
     * 创建分布工作表
     */
    private void createDistributionSheet(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                         CellStyle amountStyle, CellStyle percentStyle,
                                         String title, List<CategoryDistribution> distributions) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // 表头行
        Row headerRow = sheet.createRow(1);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("分类");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("金额");
        headerCell2.setCellStyle(headerStyle);

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("占比");
        headerCell3.setCellStyle(headerStyle);

        // 数据行
        int rowNum = 2;
        for (CategoryDistribution distribution : distributions) {
            Row dataRow = sheet.createRow(rowNum++);

            Cell dataCell1 = dataRow.createCell(0);
            dataCell1.setCellValue(distribution.getCategoryName());

            Cell dataCell2 = dataRow.createCell(1);
            dataCell2.setCellValue(distribution.getAmount().doubleValue());
            dataCell2.setCellStyle(amountStyle);

            Cell dataCell3 = dataRow.createCell(2);
            dataCell3.setCellValue(distribution.getPercentage() / 100.0);
            dataCell3.setCellStyle(percentStyle);
        }
    }

    /**
     * 创建趋势工作表
     */
    private void createTrendSheet(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                  CellStyle dateStyle, CellStyle amountStyle,
                                  List<IncomeExpenseTrend> trends) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("收支趋势");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // 表头行
        Row headerRow = sheet.createRow(1);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("日期");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("收入");
        headerCell2.setCellStyle(headerStyle);

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("支出");
        headerCell3.setCellStyle(headerStyle);

        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("结余");
        headerCell4.setCellStyle(headerStyle);

        // 数据行
        int rowNum = 2;
        for (IncomeExpenseTrend trend : trends) {
            Row dataRow = sheet.createRow(rowNum++);

            Cell dataCell1 = dataRow.createCell(0);
            dataCell1.setCellValue(java.sql.Date.valueOf(trend.getDate()));
            dataCell1.setCellStyle(dateStyle);

            Cell dataCell2 = dataRow.createCell(1);
            dataCell2.setCellValue(trend.getIncome().doubleValue());
            dataCell2.setCellStyle(amountStyle);

            Cell dataCell3 = dataRow.createCell(2);
            dataCell3.setCellValue(trend.getExpense().doubleValue());
            dataCell3.setCellStyle(amountStyle);

            Cell dataCell4 = dataRow.createCell(3);
            dataCell4.setCellValue(trend.getBalance().doubleValue());
            dataCell4.setCellStyle(amountStyle);
        }
    }

    /**
     * 创建预算摘要工作表
     */
    private void createBudgetSummary(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                     CellStyle amountStyle, CellStyle percentStyle,
                                     LocalDate startDate, LocalDate endDate,
                                     Map<String, Object> budgetData) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("预算执行报表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // 日期行
        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("报表周期: " + DateFormatter.format(startDate) + " 至 " + DateFormatter.format(endDate));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        // 空行
        sheet.createRow(2);

        // 摘要标题
        Row summaryTitleRow = sheet.createRow(3);
        Cell summaryTitleCell = summaryTitleRow.createCell(0);
        summaryTitleCell.setCellValue("预算执行摘要");
        summaryTitleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));

        // 摘要表头
        Row headerRow = sheet.createRow(4);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("项目");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("数值");
        headerCell2.setCellStyle(headerStyle);

        // 摘要数据
        int totalBudgets = (int) budgetData.get("totalBudgets");
        int overBudgetCount = (int) budgetData.get("overBudgetCount");
        int nearLimitCount = (int) budgetData.get("nearLimitCount");
        int healthyBudgetCount = (int) budgetData.get("healthyBudgetCount");
        BigDecimal totalBudgetAmount = (BigDecimal) budgetData.get("totalBudgetAmount");
        BigDecimal totalSpentAmount = (BigDecimal) budgetData.get("totalSpentAmount");
        double overallUsagePercentage = (double) budgetData.get("overallUsagePercentage");

        // 数据行
        int rowNum = 5;

        // 预算总数
        Row row1 = sheet.createRow(rowNum++);
        Cell cell1_1 = row1.createCell(0);
        cell1_1.setCellValue("预算总数");
        Cell cell1_2 = row1.createCell(1);
        cell1_2.setCellValue(totalBudgets);

        // 预算总额
        Row row2 = sheet.createRow(rowNum++);
        Cell cell2_1 = row2.createCell(0);
        cell2_1.setCellValue("预算总额");
        Cell cell2_2 = row2.createCell(1);
        cell2_2.setCellValue(totalBudgetAmount.doubleValue());
        cell2_2.setCellStyle(amountStyle);

        // 已用金额
        Row row3 = sheet.createRow(rowNum++);
        Cell cell3_1 = row3.createCell(0);
        cell3_1.setCellValue("已用金额");
        Cell cell3_2 = row3.createCell(1);
        cell3_2.setCellValue(totalSpentAmount.doubleValue());
        cell3_2.setCellStyle(amountStyle);

        // 使用率
        Row row4 = sheet.createRow(rowNum++);
        Cell cell4_1 = row4.createCell(0);
        cell4_1.setCellValue("使用率");
        Cell cell4_2 = row4.createCell(1);
        cell4_2.setCellValue(overallUsagePercentage / 100.0);
        cell4_2.setCellStyle(percentStyle);

        // 超支预算数
        Row row5 = sheet.createRow(rowNum++);
        Cell cell5_1 = row5.createCell(0);
        cell5_1.setCellValue("超支预算数");
        Cell cell5_2 = row5.createCell(1);
        cell5_2.setCellValue(overBudgetCount);

        // 接近限额预算数
        Row row6 = sheet.createRow(rowNum++);
        Cell cell6_1 = row6.createCell(0);
        cell6_1.setCellValue("接近限额预算数");
        Cell cell6_2 = row6.createCell(1);
        cell6_2.setCellValue(nearLimitCount);

        // 健康预算数
        Row row7 = sheet.createRow(rowNum++);
        Cell cell7_1 = row7.createCell(0);
        cell7_1.setCellValue("健康预算数");
        Cell cell7_2 = row7.createCell(1);
        cell7_2.setCellValue(healthyBudgetCount);
    }

    /**
     * 创建预算详情工作表
     */
    @SuppressWarnings("unchecked")
    private void createBudgetDetails(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                     CellStyle amountStyle, CellStyle percentStyle,
                                     Map<String, Object> budgetData) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("预算执行详情");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // 表头行
        Row headerRow = sheet.createRow(1);

        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("预算名称");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("预算金额");
        headerCell2.setCellStyle(headerStyle);

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("已用金额");
        headerCell3.setCellStyle(headerStyle);

        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("剩余金额");
        headerCell4.setCellStyle(headerStyle);

        Cell headerCell5 = headerRow.createCell(4);
        headerCell5.setCellValue("使用率");
        headerCell5.setCellStyle(headerStyle);

        Cell headerCell6 = headerRow.createCell(5);
        headerCell6.setCellValue("状态");
        headerCell6.setCellStyle(headerStyle);

        // 数据行
        List<Map<String, Object>> budgetDetails = (List<Map<String, Object>>) budgetData.get("budgetDetails");
        if (budgetDetails != null) {
            int rowNum = 2;

            for (Map<String, Object> budget : budgetDetails) {
                String name = (String) budget.get("name");
                BigDecimal amount = (BigDecimal) budget.get("amount");
                BigDecimal spent = (BigDecimal) budget.get("spent");
                BigDecimal remaining = amount.subtract(spent);
                double usagePercentage = spent.divide(amount, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();

                String status;
                if (usagePercentage > 100) {
                    status = "超支";
                } else if (usagePercentage > 80) {
                    status = "接近限额";
                } else {
                    status = "健康";
                }

                Row dataRow = sheet.createRow(rowNum++);

                Cell dataCell1 = dataRow.createCell(0);
                dataCell1.setCellValue(name);

                Cell dataCell2 = dataRow.createCell(1);
                dataCell2.setCellValue(amount.doubleValue());
                dataCell2.setCellStyle(amountStyle);

                Cell dataCell3 = dataRow.createCell(2);
                dataCell3.setCellValue(spent.doubleValue());
                dataCell3.setCellStyle(amountStyle);

                Cell dataCell4 = dataRow.createCell(3);
                dataCell4.setCellValue(remaining.doubleValue());
                dataCell4.setCellStyle(amountStyle);

                Cell dataCell5 = dataRow.createCell(4);
                dataCell5.setCellValue(usagePercentage / 100.0);
                dataCell5.setCellStyle(percentStyle);

                Cell dataCell6 = dataRow.createCell(5);
                dataCell6.setCellValue(status);
            }
        }
    }

    /**
     * 创建账户摘要工作表
     */
    private void createAccountSummary(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                      CellStyle amountStyle, LocalDate startDate, LocalDate endDate,
                                      Map<String, Object> accountData) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("账户报表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        // 日期行
        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("报表周期: " + DateFormatter.format(startDate) + " 至 " + DateFormatter.format(endDate));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));

        // 空行
        sheet.createRow(2);

        // 摘要标题
        Row summaryTitleRow = sheet.createRow(3);
        Cell summaryTitleCell = summaryTitleRow.createCell(0);
        summaryTitleCell.setCellValue("账户摘要");
        summaryTitleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 4));

        // 摘要表头
        Row headerRow = sheet.createRow(4);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("项目");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("数值");
        headerCell2.setCellStyle(headerStyle);

        // 摘要数据
        int totalAccounts = (int) accountData.get("totalAccounts");
        int activeAccounts = (int) accountData.get("activeAccounts");
        BigDecimal totalBalance = (BigDecimal) accountData.get("totalBalance");
        BigDecimal totalInflow = (BigDecimal) accountData.get("totalInflow");
        BigDecimal totalOutflow = (BigDecimal) accountData.get("totalOutflow");
        BigDecimal netFlow = totalInflow.subtract(totalOutflow);

        // 数据行
        int rowNum = 5;

        // 账户总数
        Row row1 = sheet.createRow(rowNum++);
        Cell cell1_1 = row1.createCell(0);
        cell1_1.setCellValue("账户总数");
        Cell cell1_2 = row1.createCell(1);
        cell1_2.setCellValue(totalAccounts);

        // 活跃账户数
        Row row2 = sheet.createRow(rowNum++);
        Cell cell2_1 = row2.createCell(0);
        cell2_1.setCellValue("活跃账户数");
        Cell cell2_2 = row2.createCell(1);
        cell2_2.setCellValue(activeAccounts);

        // 总资产
        Row row3 = sheet.createRow(rowNum++);
        Cell cell3_1 = row3.createCell(0);
        cell3_1.setCellValue("总资产");
        Cell cell3_2 = row3.createCell(1);
        cell3_2.setCellValue(totalBalance.doubleValue());
        cell3_2.setCellStyle(amountStyle);

        // 总流入
        Row row4 = sheet.createRow(rowNum++);
        Cell cell4_1 = row4.createCell(0);
        cell4_1.setCellValue("总流入");
        Cell cell4_2 = row4.createCell(1);
        cell4_2.setCellValue(totalInflow.doubleValue());
        cell4_2.setCellStyle(amountStyle);

        // 总流出
        Row row5 = sheet.createRow(rowNum++);
        Cell cell5_1 = row5.createCell(0);
        cell5_1.setCellValue("总流出");
        Cell cell5_2 = row5.createCell(1);
        cell5_2.setCellValue(totalOutflow.doubleValue());
        cell5_2.setCellStyle(amountStyle);

        // 净流入
        Row row6 = sheet.createRow(rowNum++);
        Cell cell6_1 = row6.createCell(0);
        cell6_1.setCellValue("净流入");
        Cell cell6_2 = row6.createCell(1);
        cell6_2.setCellValue(netFlow.doubleValue());
        cell6_2.setCellStyle(amountStyle);
    }

    /**
     * 创建账户详情工作表
     */
    @SuppressWarnings("unchecked")
    private void createAccountDetails(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                      CellStyle amountStyle, Map<String, Object> accountData) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("账户详情");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // 表头行
        Row headerRow = sheet.createRow(1);

        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("账户名称");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("当前余额");
        headerCell2.setCellStyle(headerStyle);

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("期初余额");
        headerCell3.setCellStyle(headerStyle);

        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("变动金额");
        headerCell4.setCellStyle(headerStyle);

        Cell headerCell5 = headerRow.createCell(4);
        headerCell5.setCellValue("流入金额");
        headerCell5.setCellStyle(headerStyle);

        Cell headerCell6 = headerRow.createCell(5);
        headerCell6.setCellValue("流出金额");
        headerCell6.setCellStyle(headerStyle);

        Cell headerCell7 = headerRow.createCell(6);
        headerCell7.setCellValue("交易次数");
        headerCell7.setCellStyle(headerStyle);

        // 数据行
        List<Map<String, Object>> accountDetails = (List<Map<String, Object>>) accountData.get("accountDetails");
        if (accountDetails != null) {
            int rowNum = 2;

            for (Map<String, Object> account : accountDetails) {
                String name = (String) account.get("name");
                BigDecimal balance = (BigDecimal) account.get("balance");
                BigDecimal initialBalance = (BigDecimal) account.get("initialBalance");
                BigDecimal change = balance.subtract(initialBalance);
                BigDecimal inflow = (BigDecimal) account.get("inflow");
                BigDecimal outflow = (BigDecimal) account.get("outflow");
                int transactionCount = (int) account.get("transactionCount");

                Row dataRow = sheet.createRow(rowNum++);

                Cell dataCell1 = dataRow.createCell(0);
                dataCell1.setCellValue(name);

                Cell dataCell2 = dataRow.createCell(1);
                dataCell2.setCellValue(balance.doubleValue());
                dataCell2.setCellStyle(amountStyle);

                Cell dataCell3 = dataRow.createCell(2);
                dataCell3.setCellValue(initialBalance.doubleValue());
                dataCell3.setCellStyle(amountStyle);

                Cell dataCell4 = dataRow.createCell(3);
                dataCell4.setCellValue(change.doubleValue());
                dataCell4.setCellStyle(amountStyle);

                Cell dataCell5 = dataRow.createCell(4);
                dataCell5.setCellValue(inflow.doubleValue());
                dataCell5.setCellStyle(amountStyle);

                Cell dataCell6 = dataRow.createCell(5);
                dataCell6.setCellValue(outflow.doubleValue());
                dataCell6.setCellStyle(amountStyle);

                Cell dataCell7 = dataRow.createCell(6);
                dataCell7.setCellValue(transactionCount);
            }
        }
    }

    /**
     * 创建余额趋势工作表
     */
    @SuppressWarnings("unchecked")
    private void createBalanceTrends(Sheet sheet, CellStyle headerStyle, CellStyle titleStyle,
                                     CellStyle dateStyle, CellStyle amountStyle, CellStyle percentStyle,
                                     Map<String, Object> accountData) {
        // 标题行
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("余额趋势");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // 表头行
        Row headerRow = sheet.createRow(1);

        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("日期");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("总余额");
        headerCell2.setCellStyle(headerStyle);

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("变动金额");
        headerCell3.setCellStyle(headerStyle);

        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("变动比例");
        headerCell4.setCellStyle(headerStyle);

        // 数据行
        List<Map<String, Object>> balanceTrends = (List<Map<String, Object>>) accountData.get("balanceTrends");
        if (balanceTrends != null) {
            int rowNum = 2;
            BigDecimal previousBalance = null;

            for (Map<String, Object> trend : balanceTrends) {
                LocalDate date = (LocalDate) trend.get("date");
                BigDecimal balance = (BigDecimal) trend.get("balance");

                BigDecimal change = BigDecimal.ZERO;
                double changePercentage = 0;

                if (previousBalance != null) {
                    change = balance.subtract(previousBalance);

                    if (previousBalance.compareTo(BigDecimal.ZERO) != 0) {
                        changePercentage = change.divide(previousBalance, 4, java.math.RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .doubleValue();
                    }
                }

                Row dataRow = sheet.createRow(rowNum++);

                Cell dataCell1 = dataRow.createCell(0);
                dataCell1.setCellValue(java.sql.Date.valueOf(date));
                dataCell1.setCellStyle(dateStyle);

                Cell dataCell2 = dataRow.createCell(1);
                dataCell2.setCellValue(balance.doubleValue());
                dataCell2.setCellStyle(amountStyle);

                Cell dataCell3 = dataRow.createCell(2);
                dataCell3.setCellValue(change.doubleValue());
                dataCell3.setCellStyle(amountStyle);

                Cell dataCell4 = dataRow.createCell(3);
                if (previousBalance != null) {
                    dataCell4.setCellValue(changePercentage / 100.0);
                    dataCell4.setCellStyle(percentStyle);
                } else {
                    dataCell4.setCellValue("N/A");
                }

                previousBalance = balance;
            }
        }
    }

    /**
     * 创建标题样式
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建日期样式
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        return style;
    }

    /**
     * 创建金额样式
     */
    private CellStyle createAmountStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat((short) 4); // #,##0.00
        return style;
    }

    /**
     * 创建百分比样式
     */
    private CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat((short) 9); // 0.00%
        return style;
    }

    public void export(Report report, OutputStream outputStream) {
    }
}