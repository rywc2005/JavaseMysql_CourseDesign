package com.PFM.CD.utils.report;

import com.PFM.CD.entity.enums.ReportType;
import com.PFM.CD.service.dto.CategoryDistribution;
import com.PFM.CD.service.dto.IncomeExpenseTrend;
import com.PFM.CD.utils.format.CurrencyFormatter;
import com.PFM.CD.utils.format.DateFormatter;
import com.PFM.CD.utils.format.NumberFormatter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 报表生成工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ReportGenerator {

    /**
     * 生成收支报表内容
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param incomeExpenseTrends 收支趋势数据
     * @param expenseDistributions 支出分布数据
     * @param incomeDistributions 收入分布数据
     * @param summaryData 汇总数据
     * @return 报表内容
     */
    public String generateIncomeExpenseReport(int userId, LocalDate startDate, LocalDate endDate,
                                              List<IncomeExpenseTrend> incomeExpenseTrends,
                                              List<CategoryDistribution> expenseDistributions,
                                              List<CategoryDistribution> incomeDistributions,
                                              Map<String, Object> summaryData) {
        StringBuilder report = new StringBuilder();

        // 报表标题
        report.append("# 收支报表\n\n");
        report.append("报表生成日期: ").append(DateFormatter.format(LocalDate.now())).append("\n");
        report.append("报表周期: ").append(DateFormatter.format(startDate))
                .append(" 至 ").append(DateFormatter.format(endDate)).append("\n\n");

        // 摘要部分
        report.append("## 收支摘要\n\n");
        report.append("| 项目 | 金额 |\n");
        report.append("|------|------|\n");

        BigDecimal totalIncome = (BigDecimal) summaryData.get("totalIncome");
        BigDecimal totalExpense = (BigDecimal) summaryData.get("totalExpense");
        BigDecimal balance = totalIncome.subtract(totalExpense);

        report.append("| 总收入 | ").append(CurrencyFormatter.format(totalIncome)).append(" |\n");
        report.append("| 总支出 | ").append(CurrencyFormatter.format(totalExpense)).append(" |\n");
        report.append("| 结余 | ").append(CurrencyFormatter.format(balance)).append(" |\n\n");

        // 收入分布
        report.append("## 收入分布\n\n");
        report.append("| 分类 | 金额 | 占比 |\n");
        report.append("|------|------|------|\n");

        for (CategoryDistribution income : incomeDistributions) {
            report.append("| ").append(income.getCategoryName())
                    .append(" | ").append(CurrencyFormatter.format(income.getAmount()))
                    .append(" | ").append(NumberFormatter.formatPercent(income.getPercentage() / 100))
                    .append(" |\n");
        }
        report.append("\n");

        // 支出分布
        report.append("## 支出分布\n\n");
        report.append("| 分类 | 金额 | 占比 |\n");
        report.append("|------|------|------|\n");

        for (CategoryDistribution expense : expenseDistributions) {
            report.append("| ").append(expense.getCategoryName())
                    .append(" | ").append(CurrencyFormatter.format(expense.getAmount()))
                    .append(" | ").append(NumberFormatter.formatPercent(expense.getPercentage() / 100))
                    .append(" |\n");
        }
        report.append("\n");

        // 收支趋势
        report.append("## 收支趋势\n\n");
        report.append("| 日期 | 收入 | 支出 | 结余 |\n");
        report.append("|------|------|------|------|\n");

        for (IncomeExpenseTrend trend : incomeExpenseTrends) {
            report.append("| ").append(DateFormatter.format(trend.getDate()))
                    .append(" | ").append(CurrencyFormatter.format(trend.getIncome()))
                    .append(" | ").append(CurrencyFormatter.format(trend.getExpense()))
                    .append(" | ").append(CurrencyFormatter.format(trend.getBalance()))
                    .append(" |\n");
        }
        report.append("\n");

        // 分析结论
        report.append("## 分析结论\n\n");

        // 收支比例分析
        double incomeExpenseRatio = 0;
        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            incomeExpenseRatio = totalIncome.divide(totalExpense, 2, java.math.RoundingMode.HALF_UP).doubleValue();
        }

        report.append("- 收入支出比: ").append(String.format("%.2f", incomeExpenseRatio)).append("\n");

        if (incomeExpenseRatio > 1.5) {
            report.append("- 收入远高于支出，财务状况良好，可考虑增加投资或储蓄。\n");
        } else if (incomeExpenseRatio > 1.0) {
            report.append("- 收入高于支出，财务状况稳定。\n");
        } else if (incomeExpenseRatio == 1.0) {
            report.append("- 收入与支出持平，需注意控制支出。\n");
        } else {
            report.append("- 支出超过收入，财务状况需要注意，应考虑增加收入或减少支出。\n");
        }

        // 最大支出分类分析
        if (!expenseDistributions.isEmpty()) {
            CategoryDistribution maxExpenseCategory = expenseDistributions.get(0);
            report.append("- 最大支出类别为\"").append(maxExpenseCategory.getCategoryName())
                    .append("\"，占总支出的").append(NumberFormatter.formatPercent(maxExpenseCategory.getPercentage() / 100))
                    .append("。\n");
        }

        return report.toString();
    }

    /**
     * 生成预算报表内容
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param budgetData 预算数据
     * @return 报表内容
     */
    public String generateBudgetReport(int userId, LocalDate startDate, LocalDate endDate,
                                       Map<String, Object> budgetData) {
        StringBuilder report = new StringBuilder();

        // 报表标题
        report.append("# 预算执行报表\n\n");
        report.append("报表生成日期: ").append(DateFormatter.format(LocalDate.now())).append("\n");
        report.append("报表周期: ").append(DateFormatter.format(startDate))
                .append(" 至 ").append(DateFormatter.format(endDate)).append("\n\n");

        // 预算摘要
        report.append("## 预算执行摘要\n\n");
        report.append("| 项目 | 数值 |\n");
        report.append("|------|------|\n");

        int totalBudgets = (int) budgetData.get("totalBudgets");
        int overBudgetCount = (int) budgetData.get("overBudgetCount");
        int nearLimitCount = (int) budgetData.get("nearLimitCount");
        int healthyBudgetCount = (int) budgetData.get("healthyBudgetCount");
        BigDecimal totalBudgetAmount = (BigDecimal) budgetData.get("totalBudgetAmount");
        BigDecimal totalSpentAmount = (BigDecimal) budgetData.get("totalSpentAmount");
        double overallUsagePercentage = (double) budgetData.get("overallUsagePercentage");

        report.append("| 预算总数 | ").append(totalBudgets).append(" |\n");
        report.append("| 预算总额 | ").append(CurrencyFormatter.format(totalBudgetAmount)).append(" |\n");
        report.append("| 已用金额 | ").append(CurrencyFormatter.format(totalSpentAmount)).append(" |\n");
        report.append("| 使用率 | ").append(NumberFormatter.formatPercent(overallUsagePercentage / 100)).append(" |\n");
        report.append("| 超支预算数 | ").append(overBudgetCount).append(" |\n");
        report.append("| 接近限额预算数 | ").append(nearLimitCount).append(" |\n");
        report.append("| 健康预算数 | ").append(healthyBudgetCount).append(" |\n\n");

        // 详细预算执行情况
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> budgetDetails = (List<Map<String, Object>>) budgetData.get("budgetDetails");

        if (budgetDetails != null && !budgetDetails.isEmpty()) {
            report.append("## 预算执行详情\n\n");
            report.append("| 预算名称 | 预算金额 | 已用金额 | 剩余金额 | 使用率 | 状态 |\n");
            report.append("|----------|----------|----------|----------|--------|------|\n");

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

                report.append("| ").append(name)
                        .append(" | ").append(CurrencyFormatter.format(amount))
                        .append(" | ").append(CurrencyFormatter.format(spent))
                        .append(" | ").append(CurrencyFormatter.format(remaining))
                        .append(" | ").append(NumberFormatter.formatPercent(usagePercentage / 100))
                        .append(" | ").append(status)
                        .append(" |\n");
            }
            report.append("\n");
        }

        // 分析结论
        report.append("## 分析结论\n\n");

        if (overallUsagePercentage > 100) {
            report.append("- 总体预算已超支，请注意控制支出。\n");
        } else if (overallUsagePercentage > 90) {
            report.append("- 总体预算使用率较高，接近限额，请谨慎支出。\n");
        } else if (overallUsagePercentage > 70) {
            report.append("- 总体预算使用率适中，请继续保持良好的财务习惯。\n");
        } else {
            report.append("- 总体预算使用率较低，资金利用效率有待提高。\n");
        }

        if (overBudgetCount > 0) {
            report.append("- 有").append(overBudgetCount).append("个预算已超支，请重点关注并调整。\n");
        }

        if (nearLimitCount > 0) {
            report.append("- 有").append(nearLimitCount).append("个预算接近限额，请注意控制相关支出。\n");
        }

        return report.toString();
    }
    /**
     * 生成账户报表内容
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountData 账户数据
     * @return 报表内容
     */
    public String generateAccountReport(int userId, LocalDate startDate, LocalDate endDate,
                                        Map<String, Object> accountData) {
        StringBuilder report = new StringBuilder();

        // 报表标题
        report.append("# 账户报表\n\n");
        report.append("报表生成日期: ").append(DateFormatter.format(LocalDate.now())).append("\n");
        report.append("报表周期: ").append(DateFormatter.format(startDate))
                .append(" 至 ").append(DateFormatter.format(endDate)).append("\n\n");

        // 账户摘要
        report.append("## 账户摘要\n\n");
        report.append("| 项目 | 数值 |\n");
        report.append("|------|------|\n");

        int totalAccounts = (int) accountData.get("totalAccounts");
        int activeAccounts = (int) accountData.get("activeAccounts");
        BigDecimal totalBalance = (BigDecimal) accountData.get("totalBalance");
        BigDecimal totalInflow = (BigDecimal) accountData.get("totalInflow");
        BigDecimal totalOutflow = (BigDecimal) accountData.get("totalOutflow");
        BigDecimal netFlow = totalInflow.subtract(totalOutflow);

        report.append("| 账户总数 | ").append(totalAccounts).append(" |\n");
        report.append("| 活跃账户数 | ").append(activeAccounts).append(" |\n");
        report.append("| 总资产 | ").append(CurrencyFormatter.format(totalBalance)).append(" |\n");
        report.append("| 总流入 | ").append(CurrencyFormatter.format(totalInflow)).append(" |\n");
        report.append("| 总流出 | ").append(CurrencyFormatter.format(totalOutflow)).append(" |\n");
        report.append("| 净流入 | ").append(CurrencyFormatter.format(netFlow)).append(" |\n\n");

        // 账户详情
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> accountDetails = (List<Map<String, Object>>) accountData.get("accountDetails");

        if (accountDetails != null && !accountDetails.isEmpty()) {
            report.append("## 账户详情\n\n");
            report.append("| 账户名称 | 当前余额 | 期初余额 | 变动金额 | 流入金额 | 流出金额 | 交易次数 |\n");
            report.append("|----------|----------|----------|----------|----------|----------|----------|\n");

            for (Map<String, Object> account : accountDetails) {
                String name = (String) account.get("name");
                BigDecimal balance = (BigDecimal) account.get("balance");
                BigDecimal initialBalance = (BigDecimal) account.get("initialBalance");
                BigDecimal change = balance.subtract(initialBalance);
                BigDecimal inflow = (BigDecimal) account.get("inflow");
                BigDecimal outflow = (BigDecimal) account.get("outflow");
                int transactionCount = (int) account.get("transactionCount");

                report.append("| ").append(name)
                        .append(" | ").append(CurrencyFormatter.format(balance))
                        .append(" | ").append(CurrencyFormatter.format(initialBalance))
                        .append(" | ").append(CurrencyFormatter.format(change))
                        .append(" | ").append(CurrencyFormatter.format(inflow))
                        .append(" | ").append(CurrencyFormatter.format(outflow))
                        .append(" | ").append(transactionCount)
                        .append(" |\n");
            }
            report.append("\n");
        }

        // 账户余额趋势
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> balanceTrends = (List<Map<String, Object>>) accountData.get("balanceTrends");

        if (balanceTrends != null && !balanceTrends.isEmpty()) {
            report.append("## 余额趋势\n\n");
            report.append("| 日期 | 总余额 | 变动金额 | 变动比例 |\n");
            report.append("|------|--------|----------|----------|\n");

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

                report.append("| ").append(DateFormatter.format(date))
                        .append(" | ").append(CurrencyFormatter.format(balance))
                        .append(" | ").append(CurrencyFormatter.format(change))
                        .append(" | ").append(changePercentage)
                        .append(" |\n");

                previousBalance = balance;
            }
            report.append("\n");
        }

        // 分析结论
        report.append("## 分析结论\n\n");

        if (netFlow.compareTo(BigDecimal.ZERO) > 0) {
            report.append("- 期间净流入为正，总资产增加").append(CurrencyFormatter.format(netFlow)).append("。\n");
        } else if (netFlow.compareTo(BigDecimal.ZERO) < 0) {
            report.append("- 期间净流入为负，总资产减少").append(CurrencyFormatter.format(netFlow.abs())).append("。\n");
        } else {
            report.append("- 期间净流入为零，总资产保持不变。\n");
        }

        // 最活跃的账户
        if (accountDetails != null && !accountDetails.isEmpty()) {
            Map<String, Object> mostActiveAccount = accountDetails.stream()
                    .max((a, b) -> Integer.compare(
                            (int) a.get("transactionCount"),
                            (int) b.get("transactionCount")))
                    .orElse(null);

            if (mostActiveAccount != null) {
                report.append("- 最活跃的账户为\"").append(mostActiveAccount.get("name"))
                        .append("\"，共有").append(mostActiveAccount.get("transactionCount"))
                        .append("笔交易。\n");
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

                report.append("- 余额变动最大的账户为\"").append(mostChangedAccount.get("name"))
                        .append("\"，");

                if (change.compareTo(BigDecimal.ZERO) > 0) {
                    report.append("增加了");
                } else {
                    report.append("减少了");
                }

                report.append(CurrencyFormatter.format(change.abs())).append("。\n");
            }
        }

        return report.toString();
    }

    /**
     * 生成报表内容
     *
     * @param reportType 报表类型,
     *     INCOME_EXPENSE("收支分析报表", "分析收入和支出的比例及趋势"),
     *     CATEGORY_ANALYSIS("分类分析报表", "按分类统计收支情况"),
     *     BUDGET_EVALUATION("预算评估报表", "评估预算执行情况"),
     *     BUDGET("预算报表", "展示指定期间的预算信息"),
     *     CATEGORY("分类报表", "展示各分类的收支统计"),
     *     ACCOUNT("账户报表", "展示账户相关的交易与余额");
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param reportData 报表数据
     * @return 报表内容
     */
    public String generateReport(ReportType reportType, int userId, LocalDate startDate, LocalDate endDate,
                                 Map<String, Object> reportData) {
        switch (reportType) {
            case INCOME_EXPENSE:
                @SuppressWarnings("unchecked")
                List<IncomeExpenseTrend> trends = (List<IncomeExpenseTrend>) reportData.get("trends");
                @SuppressWarnings("unchecked")
                List<CategoryDistribution> expenseDistributions =
                        (List<CategoryDistribution>) reportData.get("expenseDistributions");
                @SuppressWarnings("unchecked")
                List<CategoryDistribution> incomeDistributions =
                        (List<CategoryDistribution>) reportData.get("incomeDistributions");

                return generateIncomeExpenseReport(userId, startDate, endDate, trends,
                        expenseDistributions, incomeDistributions, reportData);

            case CATEGORY:
                return generateBudgetReport(userId, startDate, endDate, reportData);

            case ACCOUNT:
                return generateAccountReport(userId, startDate, endDate, reportData);

            case BUDGET:
                return generateBudgetReport(userId, startDate, endDate, reportData);

            case BUDGET_EVALUATION:
                return generateBudgetReport(userId, startDate, endDate, reportData);



            default:
                return "不支持的报表类型: " + reportType;
        }
    }

    public String generateIncomeExpenseReport(Map<String, Object> reportData) {
        int userId = (int) reportData.get("userId");
        LocalDate startDate = (LocalDate) reportData.get("startDate");
        LocalDate endDate = (LocalDate) reportData.get("endDate");
        List<IncomeExpenseTrend> incomeExpenseTrends = (List<IncomeExpenseTrend>) reportData.get("incomeExpenseTrends");
        List<CategoryDistribution> expenseDistributions = (List<CategoryDistribution>) reportData.get("expenseDistributions");
        List<CategoryDistribution> incomeDistributions = (List<CategoryDistribution>) reportData.get("incomeDistributions");
        Map<String, Object> summaryData = (Map<String, Object>) reportData.get("summaryData");

        return generateIncomeExpenseReport(userId, startDate, endDate, incomeExpenseTrends,
                expenseDistributions, incomeDistributions, summaryData);
    }

    public String generateBudgetReport(Map<String, Object> reportData) {
        int userId = (int) reportData.get("userId");
        LocalDate startDate = (LocalDate) reportData.get("startDate");
        LocalDate endDate = (LocalDate) reportData.get("endDate");
        Map<String, Object> budgetData = (Map<String, Object>) reportData.get("budgetData");

        return generateBudgetReport(userId, startDate, endDate, budgetData);
    }

    public String generateAccountReport(Map<String, Object> reportData) {
        int userId = (int) reportData.get("userId");
        LocalDate startDate = (LocalDate) reportData.get("startDate");
        LocalDate endDate = (LocalDate) reportData.get("endDate");
        Map<String, Object> accountData = (Map<String, Object>) reportData.get("accountData");

        return generateAccountReport(userId, startDate, endDate, accountData);
    }

    public String generateCategoryReport(Map<String, Object> reportData) {
        int userId = (int) reportData.get("userId");
        LocalDate startDate = (LocalDate) reportData.get("startDate");
        LocalDate endDate = (LocalDate) reportData.get("endDate");
        Map<String, Object> categoryData = (Map<String, Object>) reportData.get("categoryData");


        return generateIncomeExpenseReport(userId, startDate, endDate,
                (List<IncomeExpenseTrend>) categoryData.get("incomeExpenseTrends"),
                (List<CategoryDistribution>) categoryData.get("expenseDistributions"),
                (List<CategoryDistribution>) categoryData.get("incomeDistributions"),
                categoryData);
    }
}