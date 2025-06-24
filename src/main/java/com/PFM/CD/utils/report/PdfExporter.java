package com.PFM.CD.utils.report;

import com.PFM.CD.entity.Report;
import com.PFM.CD.service.dto.CategoryDistribution;
import com.PFM.CD.service.dto.IncomeExpenseTrend;

import com.PFM.CD.utils.file.TempFileManager;
import com.PFM.CD.utils.format.CurrencyFormatter;
import com.PFM.CD.utils.format.DateFormatter;
import com.PFM.CD.utils.format.NumberFormatter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * PDF报表导出工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class PdfExporter {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    /**
     * 导出收支报表到PDF
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
        // 创建临时文件
        Path tempFile = TempFileManager.createTempFile("income_expense_report_", ".pdf");

        try {
            // 创建文档
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(tempFile.toFile()));
            document.open();

            // 添加标题
            addReportTitle(document, "收支报表", startDate, endDate);

            // 添加摘要
            addIncomeExpenseSummary(document, summaryData);

            // 添加收入分布
            addDistributionTable(document, "收入分布", incomeDistributions);

            // 添加支出分布
            addDistributionTable(document, "支出分布", expenseDistributions);

            // 添加收支趋势
            addTrendTable(document, incomeExpenseTrends);

            // 添加分析结论
            addIncomeExpenseAnalysis(document, summaryData, expenseDistributions);

            document.close();

            return tempFile;
        } catch (DocumentException e) {
            throw new IOException("创建PDF文档失败", e);
        }
    }

    /**
     * 导出预算报表到PDF
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
        // 创建临时文件
        Path tempFile = TempFileManager.createTempFile("budget_report_", ".pdf");

        try {
            // 创建文档
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(tempFile.toFile()));
            document.open();

            // 添加标题
            addReportTitle(document, "预算执行报表", startDate, endDate);

            // 添加预算摘要
            addBudgetSummary(document, budgetData);

            // 添加预算详情
            addBudgetDetails(document, budgetData);

            // 添加分析结论
            addBudgetAnalysis(document, budgetData);

            document.close();

            return tempFile;
        } catch (DocumentException e) {
            throw new IOException("创建PDF文档失败", e);
        }
    }

    /**
     * 导出账户报表到PDF
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
        // 创建临时文件
        Path tempFile = TempFileManager.createTempFile("account_report_", ".pdf");

        try {
            // 创建文档
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(tempFile.toFile()));
            document.open();

            // 添加标题
            addReportTitle(document, "账户报表", startDate, endDate);

            // 添加账户摘要
            addAccountSummary(document, accountData);

            // 添加账户详情
            addAccountDetails(document, accountData);

            // 添加余额趋势
            addBalanceTrends(document, accountData);

            // 添加分析结论
            addAccountAnalysis(document, accountData);

            document.close();

            return tempFile;
        } catch (DocumentException e) {
            throw new IOException("创建PDF文档失败", e);
        }
    }

    /**
     * 添加报表标题
     */
    private void addReportTitle(Document document, String title, LocalDate startDate, LocalDate endDate)
            throws DocumentException {
        Paragraph titleParagraph = new Paragraph(title, TITLE_FONT);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);

        Paragraph dateParagraph = new Paragraph(
                "报表周期: " + DateFormatter.format(startDate) + " 至 " + DateFormatter.format(endDate), NORMAL_FONT);
        dateParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(dateParagraph);

        Paragraph generatedParagraph = new Paragraph(
                "生成日期: " + DateFormatter.format(LocalDate.now()), NORMAL_FONT);
        generatedParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(generatedParagraph);

        document.add(Chunk.NEWLINE);
    }

    /**
     * 添加收支摘要
     */
    private void addIncomeExpenseSummary(Document document, Map<String, Object> summaryData)
            throws DocumentException {
        Paragraph subtitle = new Paragraph("收支摘要", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setSpacingBefore(10);

        // 表头
        PdfPCell cell1 = new PdfPCell(new Phrase("项目", HEADER_FONT));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("金额", HEADER_FONT));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        // 数据
        BigDecimal totalIncome = (BigDecimal) summaryData.get("totalIncome");
        BigDecimal totalExpense = (BigDecimal) summaryData.get("totalExpense");
        BigDecimal balance = totalIncome.subtract(totalExpense);

        table.addCell(new Phrase("总收入", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(totalIncome), NORMAL_FONT));

        table.addCell(new Phrase("总支出", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(totalExpense), NORMAL_FONT));

        table.addCell(new Phrase("结余", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(balance), NORMAL_FONT));

        document.add(table);
    }

    /**
     * 添加分布表
     */
    private void addDistributionTable(Document document, String title, List<CategoryDistribution> distributions)
            throws DocumentException {
        Paragraph subtitle = new Paragraph(title, SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(90);
        table.setSpacingBefore(10);

        // 表头
        PdfPCell cell1 = new PdfPCell(new Phrase("分类", HEADER_FONT));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("金额", HEADER_FONT));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase("占比", HEADER_FONT));
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell3);

        // 数据
        for (CategoryDistribution distribution : distributions) {
            table.addCell(new Phrase(distribution.getCategoryName(), NORMAL_FONT));
            table.addCell(new Phrase(CurrencyFormatter.format(distribution.getAmount()), NORMAL_FONT));
            table.addCell(new Phrase(NumberFormatter.formatPercent(distribution.getPercentage() / 100), NORMAL_FONT));
        }

        document.add(table);
    }

    /**
     * 添加趋势表
     */
    private void addTrendTable(Document document, List<IncomeExpenseTrend> trends) throws DocumentException {
        Paragraph subtitle = new Paragraph("收支趋势", SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(95);
        table.setSpacingBefore(10);

        // 表头
        PdfPCell cell1 = new PdfPCell(new Phrase("日期", HEADER_FONT));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("收入", HEADER_FONT));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase("支出", HEADER_FONT));
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Phrase("结余", HEADER_FONT));
        cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell4);

        // 数据
        for (IncomeExpenseTrend trend : trends) {
            table.addCell(new Phrase(DateFormatter.format(trend.getDate()), NORMAL_FONT));
            table.addCell(new Phrase(CurrencyFormatter.format(trend.getIncome()), NORMAL_FONT));
            table.addCell(new Phrase(CurrencyFormatter.format(trend.getExpense()), NORMAL_FONT));
            table.addCell(new Phrase(CurrencyFormatter.format(trend.getBalance()), NORMAL_FONT));
        }

        document.add(table);
    }

    /**
     * 添加收支分析结论
     */
    private void addIncomeExpenseAnalysis(Document document, Map<String, Object> summaryData,
                                          List<CategoryDistribution> expenseDistributions) throws DocumentException {
        Paragraph subtitle = new Paragraph("分析结论", SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        // 收支比例分析
        BigDecimal totalIncome = (BigDecimal) summaryData.get("totalIncome");
        BigDecimal totalExpense = (BigDecimal) summaryData.get("totalExpense");

        double incomeExpenseRatio = 0;
        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            incomeExpenseRatio = totalIncome.divide(totalExpense, 2, java.math.RoundingMode.HALF_UP).doubleValue();
        }

        Paragraph ratioText = new Paragraph("收入支出比: " + String.format("%.2f", incomeExpenseRatio), NORMAL_FONT);
        document.add(ratioText);

        if (incomeExpenseRatio > 1.5) {
            document.add(new Paragraph("收入远高于支出，财务状况良好，可考虑增加投资或储蓄。", NORMAL_FONT));
        } else if (incomeExpenseRatio > 1.0) {
            document.add(new Paragraph("收入高于支出，财务状况稳定。", NORMAL_FONT));
        } else if (incomeExpenseRatio == 1.0) {
            document.add(new Paragraph("收入与支出持平，需注意控制支出。", NORMAL_FONT));
        } else {
            document.add(new Paragraph("支出超过收入，财务状况需要注意，应考虑增加收入或减少支出。", NORMAL_FONT));
        }

        // 最大支出分类分析
        if (!expenseDistributions.isEmpty()) {
            CategoryDistribution maxExpenseCategory = expenseDistributions.get(0);
            Paragraph categoryText = new Paragraph(
                    "最大支出类别为\"" + maxExpenseCategory.getCategoryName() + "\"，占总支出的" +
                            NumberFormatter.formatPercent(maxExpenseCategory.getPercentage() / 100) + "。",
                    NORMAL_FONT);
            document.add(categoryText);
        }
    }

    /**
     * 添加预算摘要
     */
    private void addBudgetSummary(Document document, Map<String, Object> budgetData) throws DocumentException {
        Paragraph subtitle = new Paragraph("预算执行摘要", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setSpacingBefore(10);

        // 表头
        PdfPCell cell1 = new PdfPCell(new Phrase("项目", HEADER_FONT));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("数值", HEADER_FONT));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        // 数据
        int totalBudgets = (int) budgetData.get("totalBudgets");
        int overBudgetCount = (int) budgetData.get("overBudgetCount");
        int nearLimitCount = (int) budgetData.get("nearLimitCount");
        int healthyBudgetCount = (int) budgetData.get("healthyBudgetCount");
        BigDecimal totalBudgetAmount = (BigDecimal) budgetData.get("totalBudgetAmount");
        BigDecimal totalSpentAmount = (BigDecimal) budgetData.get("totalSpentAmount");
        double overallUsagePercentage = (double) budgetData.get("overallUsagePercentage");

        table.addCell(new Phrase("预算总数", NORMAL_FONT));
        table.addCell(new Phrase(String.valueOf(totalBudgets), NORMAL_FONT));

        table.addCell(new Phrase("预算总额", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(totalBudgetAmount), NORMAL_FONT));

        table.addCell(new Phrase("已用金额", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(totalSpentAmount), NORMAL_FONT));

        table.addCell(new Phrase("使用率", NORMAL_FONT));
        table.addCell(new Phrase(NumberFormatter.formatPercent(overallUsagePercentage / 100), NORMAL_FONT));

        table.addCell(new Phrase("超支预算数", NORMAL_FONT));
        table.addCell(new Phrase(String.valueOf(overBudgetCount), NORMAL_FONT));

        table.addCell(new Phrase("接近限额预算数", NORMAL_FONT));
        table.addCell(new Phrase(String.valueOf(nearLimitCount), NORMAL_FONT));

        table.addCell(new Phrase("健康预算数", NORMAL_FONT));
        table.addCell(new Phrase(String.valueOf(healthyBudgetCount), NORMAL_FONT));

        document.add(table);
    }

    /**
     * 添加预算详情
     */
    @SuppressWarnings("unchecked")
    private void addBudgetDetails(Document document, Map<String, Object> budgetData) throws DocumentException {
        Paragraph subtitle = new Paragraph("预算执行详情", SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // 表头
        String[] headers = {"预算名称", "预算金额", "已用金额", "剩余金额", "使用率", "状态"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // 数据
        List<Map<String, Object>> budgetDetails = (List<Map<String, Object>>) budgetData.get("budgetDetails");
        if (budgetDetails != null) {
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

                table.addCell(new Phrase(name, NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(amount), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(spent), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(remaining), NORMAL_FONT));
                table.addCell(new Phrase(NumberFormatter.formatPercent(usagePercentage / 100), NORMAL_FONT));
                table.addCell(new Phrase(status, NORMAL_FONT));
            }
        }

        document.add(table);
    }

    /**
     * 添加预算分析结论
     */
    private void addBudgetAnalysis(Document document, Map<String, Object> budgetData) throws DocumentException {
        Paragraph subtitle = new Paragraph("分析结论", SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        double overallUsagePercentage = (double) budgetData.get("overallUsagePercentage");
        int overBudgetCount = (int) budgetData.get("overBudgetCount");
        int nearLimitCount = (int) budgetData.get("nearLimitCount");

        if (overallUsagePercentage > 100) {
            document.add(new Paragraph("总体预算已超支，请注意控制支出。", NORMAL_FONT));
        } else if (overallUsagePercentage > 90) {
            document.add(new Paragraph("总体预算使用率较高，接近限额，请谨慎支出。", NORMAL_FONT));
        } else if (overallUsagePercentage > 70) {
            document.add(new Paragraph("总体预算使用率适中，请继续保持良好的财务习惯。", NORMAL_FONT));
        } else {
            document.add(new Paragraph("总体预算使用率较低，资金利用效率有待提高。", NORMAL_FONT));
        }

        if (overBudgetCount > 0) {
            document.add(new Paragraph("有" + overBudgetCount + "个预算已超支，请重点关注并调整。", NORMAL_FONT));
        }

        if (nearLimitCount > 0) {
            document.add(new Paragraph("有" + nearLimitCount + "个预算接近限额，请注意控制相关支出。", NORMAL_FONT));
        }
    }

    /**
     * 添加账户摘要
     */
    private void addAccountSummary(Document document, Map<String, Object> accountData) throws DocumentException {
        Paragraph subtitle = new Paragraph("账户摘要", SUBTITLE_FONT);
        subtitle.setSpacingBefore(10);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setSpacingBefore(10);

        // 表头
        PdfPCell cell1 = new PdfPCell(new Phrase("项目", HEADER_FONT));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase("数值", HEADER_FONT));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        // 数据
        int totalAccounts = (int) accountData.get("totalAccounts");
        int activeAccounts = (int) accountData.get("activeAccounts");
        BigDecimal totalBalance = (BigDecimal) accountData.get("totalBalance");
        BigDecimal totalInflow = (BigDecimal) accountData.get("totalInflow");
        BigDecimal totalOutflow = (BigDecimal) accountData.get("totalOutflow");
        BigDecimal netFlow = totalInflow.subtract(totalOutflow);

        table.addCell(new Phrase("账户总数", NORMAL_FONT));
        table.addCell(new Phrase(String.valueOf(totalAccounts), NORMAL_FONT));

        table.addCell(new Phrase("活跃账户数", NORMAL_FONT));
        table.addCell(new Phrase(String.valueOf(activeAccounts), NORMAL_FONT));

        table.addCell(new Phrase("总资产", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(totalBalance), NORMAL_FONT));

        table.addCell(new Phrase("总流入", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(totalInflow), NORMAL_FONT));

        table.addCell(new Phrase("总流出", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(totalOutflow), NORMAL_FONT));

        table.addCell(new Phrase("净流入", NORMAL_FONT));
        table.addCell(new Phrase(CurrencyFormatter.format(netFlow), NORMAL_FONT));

        document.add(table);
    }

    /**
     * 添加账户详情
     */
    @SuppressWarnings("unchecked")
    private void addAccountDetails(Document document, Map<String, Object> accountData) throws DocumentException {
        Paragraph subtitle = new Paragraph("账户详情", SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // 表头
        String[] headers = {"账户名称", "当前余额", "期初余额", "变动金额", "流入金额", "流出金额", "交易次数"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // 数据
        List<Map<String, Object>> accountDetails = (List<Map<String, Object>>) accountData.get("accountDetails");
        if (accountDetails != null) {
            for (Map<String, Object> account : accountDetails) {
                String name = (String) account.get("name");
                BigDecimal balance = (BigDecimal) account.get("balance");
                BigDecimal initialBalance = (BigDecimal) account.get("initialBalance");
                BigDecimal change = balance.subtract(initialBalance);
                BigDecimal inflow = (BigDecimal) account.get("inflow");
                BigDecimal outflow = (BigDecimal) account.get("outflow");
                int transactionCount = (int) account.get("transactionCount");

                table.addCell(new Phrase(name, NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(balance), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(initialBalance), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(change), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(inflow), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(outflow), NORMAL_FONT));
                table.addCell(new Phrase(String.valueOf(transactionCount), NORMAL_FONT));
            }
        }

        document.add(table);
    }

    /**
     * 添加余额趋势
     */
    @SuppressWarnings("unchecked")
    private void addBalanceTrends(Document document, Map<String, Object> accountData) throws DocumentException {
        Paragraph subtitle = new Paragraph("余额趋势", SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(90);
        table.setSpacingBefore(10);

        // 表头
        String[] headers = {"日期", "总余额", "变动金额", "变动比例"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // 数据
        List<Map<String, Object>> balanceTrends = (List<Map<String, Object>>) accountData.get("balanceTrends");
        if (balanceTrends != null) {
            BigDecimal previousBalance = null;

            for (Map<String, Object> trend : balanceTrends) {
                LocalDate date = (LocalDate) trend.get("date");
                BigDecimal balance = (BigDecimal) trend.get("balance");

                BigDecimal change = BigDecimal.ZERO;
                String changePercentage = "N/A";

                if (previousBalance != null) {
                    change = balance.subtract(previousBalance);

                    if (previousBalance.compareTo(BigDecimal.ZERO) != 0) {
                        double percentage = change.divide(previousBalance, 4, java.math.RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .doubleValue();
                        changePercentage = NumberFormatter.formatPercent(percentage / 100);
                    }
                }

                table.addCell(new Phrase(DateFormatter.format(date), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(balance), NORMAL_FONT));
                table.addCell(new Phrase(CurrencyFormatter.format(change), NORMAL_FONT));
                table.addCell(new Phrase(changePercentage, NORMAL_FONT));

                previousBalance = balance;
            }
        }

        document.add(table);
    }

    /**
     * 添加账户分析结论
     */
    private void addAccountAnalysis(Document document, Map<String, Object> accountData) throws DocumentException {
        Paragraph subtitle = new Paragraph("分析结论", SUBTITLE_FONT);
        subtitle.setSpacingBefore(15);
        document.add(subtitle);

        BigDecimal totalInflow = (BigDecimal) accountData.get("totalInflow");
        BigDecimal totalOutflow = (BigDecimal) accountData.get("totalOutflow");
        BigDecimal netFlow = totalInflow.subtract(totalOutflow);

        if (netFlow.compareTo(BigDecimal.ZERO) > 0) {
            document.add(new Paragraph("期间净流入为正，总资产增加" +
                    CurrencyFormatter.format(netFlow) + "。", NORMAL_FONT));
        } else if (netFlow.compareTo(BigDecimal.ZERO) < 0) {
            document.add(new Paragraph("期间净流入为负，总资产减少" +
                    CurrencyFormatter.format(netFlow.abs()) + "。", NORMAL_FONT));
        } else {
            document.add(new Paragraph("期间净流入为零，总资产保持不变。", NORMAL_FONT));
        }

        // 最活跃的账户
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> accountDetails = (List<Map<String, Object>>) accountData.get("accountDetails");
        if (accountDetails != null && !accountDetails.isEmpty()) {
            Map<String, Object> mostActiveAccount = accountDetails.stream()
                    .max((a, b) -> Integer.compare(
                            (int) a.get("transactionCount"),
                            (int) b.get("transactionCount")))
                    .orElse(null);

            if (mostActiveAccount != null) {
                document.add(new Paragraph("最活跃的账户为\"" + mostActiveAccount.get("name") +
                        "\"，共有" + mostActiveAccount.get("transactionCount") +
                        "笔交易。", NORMAL_FONT));
            }

            // 余额变动最大的账户
            Map<String, Object> mostChangedAccount = accountDetails.stream()
                    .max((a, b) -> {
                        BigDecimal changeA = ((BigDecimal) a.get("balance")).subtract((BigDecimal) a.get("initialBalance")).abs();
                        BigDecimal changeB = ((BigDecimal) b.get("balance")).subtract((BigDecimal) b.get("initialBalance")).abs();
                        return changeA.compareTo(changeB);
                    })
                    .orElse(null);

            if (mostChangedAccount != null) {
                BigDecimal initialBalance = (BigDecimal) mostChangedAccount.get("initialBalance");
                BigDecimal balance = (BigDecimal) mostChangedAccount.get("balance");
                BigDecimal change = balance.subtract(initialBalance);

                StringBuilder analysisText = new StringBuilder("余额变动最大的账户为\"")
                        .append(mostChangedAccount.get("name"))
                        .append("\"，");

                if (change.compareTo(BigDecimal.ZERO) > 0) {
                    analysisText.append("增加了");
                } else {
                    analysisText.append("减少了");
                }

                analysisText.append(CurrencyFormatter.format(change.abs())).append("。");

                document.add(new Paragraph(analysisText.toString(), NORMAL_FONT));
            }
        }
    }

    public void export(Report report, OutputStream outputStream) {

    }
}