package com.PFM.CD.service.impl;

import com.PFM.CD.dao.interfaces.AccountDao;
import com.PFM.CD.dao.interfaces.BudgetDao;
import com.PFM.CD.dao.interfaces.CategoryDao;
import com.PFM.CD.dao.interfaces.TransactionDao;
import com.PFM.CD.entity.*;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.service.dto.AccountBalanceTrend;
import com.PFM.CD.service.dto.CategoryDistribution;
import com.PFM.CD.service.dto.IncomeExpenseTrend;
import com.PFM.CD.service.dto.TransactionFrequency;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.StatisticsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class StatisticsServiceImpl implements StatisticsService {

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;
    private final CategoryDao categoryDao;
    private final BudgetDao budgetDao;

    /**
     * 构造函数
     *
     * @param transactionDao 交易DAO接口
     * @param accountDao 账户DAO接口
     * @param categoryDao 分类DAO接口
     * @param budgetDao 预算DAO接口
     */
    public StatisticsServiceImpl(TransactionDao transactionDao, AccountDao accountDao,
                                 CategoryDao categoryDao, BudgetDao budgetDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.categoryDao = categoryDao;
        this.budgetDao = budgetDao;
    }

    @Override
    public List<IncomeExpenseTrend> getIncomeExpenseTrend(int userId, LocalDate startDate,
                                                          LocalDate endDate, String intervalType)
            throws ServiceException {
        try {
            // 验证日期范围
            if (endDate.isBefore(startDate)) {
                throw new ServiceException("结束日期不能早于开始日期");
            }

            // 获取指定日期范围内的交易
            List<Transaction> transactions = transactionDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 根据间隔类型分组
            Map<LocalDate, IncomeExpenseTrend> trendMap = new TreeMap<>();

            for (Transaction transaction : transactions) {
                LocalDate trendDate = getTrendDate(transaction.getTransactionDate(), intervalType);

                IncomeExpenseTrend trend = trendMap.computeIfAbsent(trendDate,
                        k -> new IncomeExpenseTrend(trendDate, BigDecimal.ZERO, BigDecimal.ZERO));

                if (transaction.getTransactionType() == TransactionType.INCOME) {
                    trend.setIncome(trend.getIncome().add(transaction.getAmount()));
                } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                    trend.setExpense(trend.getExpense().add(transaction.getAmount()));
                }
            }

            // 确保所有间隔都有数据点
            fillMissingDatePoints(trendMap, startDate, endDate, intervalType);

            return new ArrayList<>(trendMap.values());
        } catch (SQLException e) {
            throw new ServiceException("获取收支趋势数据过程中发生数据库错误", e);
        }
    }

    @Override
    public List<AccountBalanceTrend> getAccountBalanceTrend(int userId, List<Integer> accountIds,
                                                            LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            // 验证日期范围
            if (endDate.isBefore(startDate)) {
                throw new ServiceException("结束日期不能早于开始日期");
            }

            // 获取用户的所有账户或指定账户
            List<Account> accounts;
            if (accountIds == null || accountIds.isEmpty()) {
                accounts = accountDao.findByUserId(userId);
            } else {
                accounts = new ArrayList<>();
                for (Integer accountId : accountIds) {
                    Account account = accountDao.findById(accountId);
                    if (account != null && account.getUserId() == userId) {
                        accounts.add(account);
                    }
                }
            }

            if (accounts.isEmpty()) {
                return new ArrayList<>();
            }

            // 获取指定日期范围内的交易
            List<Transaction> transactions = transactionDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 计算每个账户在每天的余额
            Map<Integer, Map<LocalDate, BigDecimal>> accountBalanceMap = new HashMap<>();

            // 初始化每个账户的初始余额（开始日期的余额）
            for (Account account : accounts) {
                accountBalanceMap.put(account.getAccountId(), new TreeMap<>());
                // 计算账户在开始日期的余额
                BigDecimal initialBalance = calculateAccountBalanceAt(account, transactions, startDate);
                accountBalanceMap.get(account.getAccountId()).put(startDate, initialBalance);
            }

            // 计算每个账户每天的余额变化
            for (Transaction transaction : transactions) {
                LocalDate transactionDate = transaction.getTransactionDate();

                // 处理收入
                if (transaction.getTransactionType() == TransactionType.INCOME &&
                        accountBalanceMap.containsKey(transaction.getDestinationAccountId())) {
                    updateAccountBalanceAfterDate(accountBalanceMap.get(transaction.getDestinationAccountId()),
                            transactionDate, transaction.getAmount());
                }

                // 处理支出
                if (transaction.getTransactionType() == TransactionType.EXPENSE &&
                        accountBalanceMap.containsKey(transaction.getSourceAccountId())) {
                    updateAccountBalanceAfterDate(accountBalanceMap.get(transaction.getSourceAccountId()),
                            transactionDate, transaction.getAmount().negate());
                }

                /*
                // 处理转账
                if (transaction.getTransactionType() == TransactionType.TRANSFER) {
                    if (accountBalanceMap.containsKey(transaction.getSourceAccountId())) {
                        updateAccountBalanceAfterDate(accountBalanceMap.get(transaction.getSourceAccountId()),
                                transactionDate, transaction.getAmount().negate());
                    }

                    if (accountBalanceMap.containsKey(transaction.getDestinationAccountId())) {
                        updateAccountBalanceAfterDate(accountBalanceMap.get(transaction.getDestinationAccountId()),
                                transactionDate, transaction.getAmount());
                    }
                }
                */
            }

            // 填充所有日期
            for (Account account : accounts) {
                Map<LocalDate, BigDecimal> balanceMap = accountBalanceMap.get(account.getAccountId());
                fillMissingDateBalancePoints(balanceMap, startDate, endDate);
            }

            // 构建结果
            List<AccountBalanceTrend> result = new ArrayList<>();
            for (Account account : accounts) {
                Map<LocalDate, BigDecimal> balanceMap = accountBalanceMap.get(account.getAccountId());
                for (Map.Entry<LocalDate, BigDecimal> entry : balanceMap.entrySet()) {
                    result.add(new AccountBalanceTrend(entry.getKey(), account.getAccountId(),
                            account.getAccountName(), entry.getValue()));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new ServiceException("获取账户余额趋势数据过程中发生数据库错误", e);
        }
    }

    @Override
    public List<CategoryDistribution> getExpenseCategoryDistribution(int userId, LocalDate startDate,
                                                                     LocalDate endDate)
            throws ServiceException {
        try {
            // 获取按分类统计的支出
            Map<Integer, BigDecimal> expenseByCategory = transactionDao.calculateExpenseByCategory(userId, startDate, endDate);

            // 获取分类信息
            List<Category> categories = new ArrayList<>();
            for (Integer categoryId : expenseByCategory.keySet()) {
                Category category = categoryDao.findById(categoryId);
                if (category != null) {
                    categories.add(category);
                }
            }

            // 计算总支出
            BigDecimal totalExpense = BigDecimal.ZERO;
            for (BigDecimal amount : expenseByCategory.values()) {
                totalExpense = totalExpense.add(amount);
            }

            // 构建结果
            List<CategoryDistribution> result = new ArrayList<>();
            for (Category category : categories) {
                BigDecimal amount = expenseByCategory.get(category.getCategoryId());
                if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                    // 计算百分比
                    double percentage = 0;
                    if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = amount.divide(totalExpense, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .doubleValue();
                    }

                    result.add(new CategoryDistribution(category.getCategoryId(), category.getCategoryName(),
                            amount, percentage));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new ServiceException("获取分类支出分布过程中发生数据库错误", e);
        }
    }

    @Override
    public List<CategoryDistribution> getIncomeCategoryDistribution(int userId, LocalDate startDate,
                                                                    LocalDate endDate)
            throws ServiceException {
        try {
            // 获取按分类统计的收入
            Map<Integer, BigDecimal> incomeByCategory = transactionDao.calculateIncomeByCategory(userId, startDate, endDate);

            // 获取分类信息
            List<Category> categories = new ArrayList<>();
            for (Integer categoryId : incomeByCategory.keySet()) {
                Category category = categoryDao.findById(categoryId);
                if (category != null) {
                    categories.add(category);
                }
            }

            // 计算总收入
            BigDecimal totalIncome = BigDecimal.ZERO;
            for (BigDecimal amount : incomeByCategory.values()) {
                totalIncome = totalIncome.add(amount);
            }

            // 构建结果
            List<CategoryDistribution> result = new ArrayList<>();
            for (Category category : categories) {
                BigDecimal amount = incomeByCategory.get(category.getCategoryId());
                if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                    // 计算百分比
                    double percentage = 0;
                    if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
                        percentage = amount.divide(totalIncome, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .doubleValue();
                    }

                    result.add(new CategoryDistribution(category.getCategoryId(), category.getCategoryName(),
                            amount, percentage));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new ServiceException("获取分类收入分布过程中发生数据库错误", e);
        }
    }

    @Override
    public List<TransactionFrequency> getTransactionFrequency(int userId, LocalDate startDate,
                                                              LocalDate endDate)
            throws ServiceException {
        try {
            // 获取指定日期范围内的交易
            List<Transaction> transactions = transactionDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 按日期统计交易频率
            Map<LocalDate, Integer> frequencyMap = new TreeMap<>();

            for (Transaction transaction : transactions) {
                LocalDate date = transaction.getTransactionDate();
                frequencyMap.put(date, frequencyMap.getOrDefault(date, 0) + 1);
            }

            // 确保所有日期都有数据点
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                frequencyMap.putIfAbsent(currentDate, 0);
                currentDate = currentDate.plusDays(1);
            }

            // 构建结果
            List<TransactionFrequency> result = new ArrayList<>();
            for (Map.Entry<LocalDate, Integer> entry : frequencyMap.entrySet()) {
                result.add(new TransactionFrequency(entry.getKey(), entry.getValue()));
            }

            return result;
        } catch (SQLException e) {
            throw new ServiceException("获取交易频率数据过程中发生数据库错误", e);
        }
    }

    @Override
    public Map<Integer, Map<String, BigDecimal>> getMonthlyIncomeExpenseSummary(int userId, int year)
            throws ServiceException {
        try {
            // 设置日期范围
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year, 12, 31);

            // 获取指定日期范围内的交易
            List<Transaction> transactions = transactionDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 按月统计收入和支出
            Map<Integer, Map<String, BigDecimal>> result = new HashMap<>();

            // 初始化每个月的数据
            for (int month = 1; month <= 12; month++) {
                Map<String, BigDecimal> monthData = new HashMap<>();
                monthData.put("income", BigDecimal.ZERO);
                monthData.put("expense", BigDecimal.ZERO);
                monthData.put("balance", BigDecimal.ZERO);
                result.put(month, monthData);
            }

            // 统计每笔交易
            for (Transaction transaction : transactions) {
                int month = transaction.getTransactionDate().getMonthValue();
                Map<String, BigDecimal> monthData = result.get(month);

                if (transaction.getTransactionType() == TransactionType.INCOME) {
                    BigDecimal income = monthData.get("income").add(transaction.getAmount());
                    monthData.put("income", income);
                } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                    BigDecimal expense = monthData.get("expense").add(transaction.getAmount());
                    monthData.put("expense", expense);
                }

                // 更新余额
                BigDecimal balance = monthData.get("income").subtract(monthData.get("expense"));
                monthData.put("balance", balance);
            }

            return result;
        } catch (SQLException e) {
            throw new ServiceException("获取月度收支汇总过程中发生数据库错误", e);
        }
    }

    @Override
    public BigDecimal getDailyAverageExpense(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            // 验证日期范围
            if (endDate.isBefore(startDate)) {
                throw new ServiceException("结束日期不能早于开始日期");
            }

            // 计算总支出
            BigDecimal totalExpense = transactionDao.calculateTotalExpense(userId, startDate, endDate);

            // 计算天数
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

            // 计算日均支出
            if (days > 0 && totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                return totalExpense.divide(new BigDecimal(days), 2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new ServiceException("计算日均支出过程中发生数据库错误", e);
        }
    }

    @Override
    public BigDecimal getMonthlyAverageExpense(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            // 验证日期范围
            if (endDate.isBefore(startDate)) {
                throw new ServiceException("结束日期不能早于开始日期");
            }

            // 计算总支出
            BigDecimal totalExpense = transactionDao.calculateTotalExpense(userId, startDate, endDate);

            // 计算月数
            YearMonth startYearMonth = YearMonth.from(startDate);
            YearMonth endYearMonth = YearMonth.from(endDate);
            long months = ChronoUnit.MONTHS.between(startYearMonth, endYearMonth) + 1;

            // 计算月均支出
            if (months > 0 && totalExpense.compareTo(BigDecimal.ZERO) > 0) {
                return totalExpense.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            throw new ServiceException("计算月均支出过程中发生数据库错误", e);
        }
    }

    @Override
    public Map<String, Object> getNetWorthChange(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            // 验证日期范围
            if (endDate.isBefore(startDate)) {
                throw new ServiceException("结束日期不能早于开始日期");
            }

            // 获取用户账户
            List<Account> accounts = accountDao.findByUserId(userId);

            // 获取指定日期范围内的交易
            List<Transaction> transactions = transactionDao.findByUserIdAndDateRange(userId, startDate, endDate.plusDays(1));

            // 计算开始日期的净资产
            BigDecimal startNetWorth = calculateNetWorthAt(accounts, transactions, startDate);

            // 计算结束日期的净资产
            BigDecimal endNetWorth = calculateNetWorthAt(accounts, transactions, endDate.plusDays(1));

            // 计算变化
            BigDecimal change = endNetWorth.subtract(startNetWorth);

            // 计算变化百分比
            double changePercentage = 0;
            if (startNetWorth.compareTo(BigDecimal.ZERO) > 0) {
                changePercentage = change.divide(startNetWorth, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();
            }

            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("startNetWorth", startNetWorth);
            result.put("endNetWorth", endNetWorth);
            result.put("change", change);
            result.put("changePercentage", changePercentage);

            return result;
        } catch (SQLException e) {
            throw new ServiceException("计算净资产变化过程中发生数据库错误", e);
        }
    }

    @Override
    public Map<String, Object> getBudgetExecutionStatistics(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            // 获取指定日期范围内的预算
            List<Budget> budgets = budgetDao.findByUserIdAndDateRange(userId, startDate, endDate);

            // 统计数据
            int totalBudgets = budgets.size();
            int overBudgetCount = 0;
            int nearLimitCount = 0;
            int healthyBudgetCount = 0;
            BigDecimal totalBudgetAmount = BigDecimal.ZERO;
            BigDecimal totalSpentAmount = BigDecimal.ZERO;

            for (Budget budget : budgets) {
                Budget fullBudget = budgetDao.findBudgetWithCategories(budget.getBudgetId());
                if (fullBudget == null) continue;

                // 计算预算使用情况
                BigDecimal budgetAmount = fullBudget.getTotalAmount();
                BigDecimal spentAmount = BigDecimal.ZERO;

                if (fullBudget.getBudgetCategories() != null) {
                    for (BudgetCategory bc : fullBudget.getBudgetCategories()) {
                        if (bc.getSpentAmount() != null) {
                            spentAmount = spentAmount.add(bc.getSpentAmount());
                        }
                    }
                }

                // 累加总金额
                totalBudgetAmount = totalBudgetAmount.add(budgetAmount);
                totalSpentAmount = totalSpentAmount.add(spentAmount);

                // 计算使用百分比
                double usagePercentage = 0;
                if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
                    usagePercentage = spentAmount.divide(budgetAmount, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .doubleValue();
                }

                // 分类预算状态
                if (usagePercentage > 100) {
                    overBudgetCount++;
                } else if (usagePercentage > 80) {
                    nearLimitCount++;
                } else {
                    healthyBudgetCount++;
                }
            }

            // 计算总体使用百分比
            double overallUsagePercentage = 0;
            if (totalBudgetAmount.compareTo(BigDecimal.ZERO) > 0) {
                overallUsagePercentage = totalSpentAmount.divide(totalBudgetAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();
            }

            // 构建结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalBudgets", totalBudgets);
            result.put("overBudgetCount", overBudgetCount);
            result.put("nearLimitCount", nearLimitCount);
            result.put("healthyBudgetCount", healthyBudgetCount);
            result.put("totalBudgetAmount", totalBudgetAmount);
            result.put("totalSpentAmount", totalSpentAmount);
            result.put("overallUsagePercentage", overallUsagePercentage);

            return result;
        } catch (SQLException e) {
            throw new ServiceException("获取预算执行情况统计过程中发生数据库错误", e);
        }
    }

    /**
     * 根据间隔类型获取趋势日期
     *
     * @param date 原始日期
     * @param intervalType 间隔类型（日/周/月/年）
     * @return 趋势日期
     */
    private LocalDate getTrendDate(LocalDate date, String intervalType) {
        if ("day".equalsIgnoreCase(intervalType)) {
            return date;
        } else if ("week".equalsIgnoreCase(intervalType)) {
            // 返回所在周的周一
            return date.minusDays(date.getDayOfWeek().getValue() - 1);
        } else if ("month".equalsIgnoreCase(intervalType)) {
            // 返回所在月的第一天
            return date.withDayOfMonth(1);
        } else if ("year".equalsIgnoreCase(intervalType)) {
            // 返回所在年的第一天
            return date.withDayOfYear(1);
        } else {
            // 默认按日
            return date;
        }
    }

    /**
     * 填充缺失的日期点
     *
     * @param trendMap 趋势图数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param intervalType 间隔类型
     */
    private void fillMissingDatePoints(Map<LocalDate, IncomeExpenseTrend> trendMap,
                                       LocalDate startDate, LocalDate endDate, String intervalType) {
        LocalDate currentDate = getTrendDate(startDate, intervalType);
        LocalDate lastDate = getTrendDate(endDate, intervalType);

        while (!currentDate.isAfter(lastDate)) {
            trendMap.putIfAbsent(currentDate, new IncomeExpenseTrend(currentDate, BigDecimal.ZERO, BigDecimal.ZERO));

            // 增加间隔
            if ("day".equalsIgnoreCase(intervalType)) {
                currentDate = currentDate.plusDays(1);
            } else if ("week".equalsIgnoreCase(intervalType)) {
                currentDate = currentDate.plusWeeks(1);
            } else if ("month".equalsIgnoreCase(intervalType)) {
                currentDate = currentDate.plusMonths(1);
            } else if ("year".equalsIgnoreCase(intervalType)) {
                currentDate = currentDate.plusYears(1);
            } else {
                currentDate = currentDate.plusDays(1);
            }
        }
    }

    /**
     * 计算账户在指定日期的余额
     *
     * @param account 账户
     * @param transactions 交易列表
     * @param date 日期
     * @return 账户余额
     */
    private BigDecimal calculateAccountBalanceAt(Account account, List<Transaction> transactions, LocalDate date) {
        BigDecimal balance = account.getBalance();

        // 筛选出指定日期之后的交易
        List<Transaction> laterTransactions = transactions.stream()
                .filter(t -> t.getTransactionDate().isAfter(date))
                .collect(Collectors.toList());

        // 对于每笔晚于指定日期的交易，从当前余额中减去或加上相应金额
        for (Transaction transaction : laterTransactions) {
            if (transaction.getTransactionType() == TransactionType.INCOME &&
                    transaction.getDestinationAccountId() == account.getAccountId()) {
                balance = balance.subtract(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE &&
                    transaction.getSourceAccountId() == account.getAccountId()) {
                balance = balance.add(transaction.getAmount());
            }
            /*
            else if (transaction.getTransactionType() == TransactionType.TRANSFER) {
                if (transaction.getSourceAccountId() == account.getAccountId()) {
                    balance = balance.add(transaction.getAmount());
                }
                if (transaction.getDestinationAccountId() == account.getAccountId()) {
                    balance = balance.subtract(transaction.getAmount());
                }
            }
            */
        }

        return balance;
    }

    /**
     * 更新账户余额在指定日期后的所有记录
     *
     * @param balanceMap 余额映射
     * @param date 日期
     * @param amount 金额变化
     */
    private void updateAccountBalanceAfterDate(Map<LocalDate, BigDecimal> balanceMap,
                                               LocalDate date, BigDecimal amount) {
        // 为指定日期添加余额记录（如果不存在）
        balanceMap.putIfAbsent(date, BigDecimal.ZERO);

        // 获取所有在此日期之后的记录，并更新余额
        for (Map.Entry<LocalDate, BigDecimal> entry : balanceMap.entrySet()) {
            if (!entry.getKey().isBefore(date)) {
                entry.setValue(entry.getValue().add(amount));
            }
        }
    }

    /**
     * 填充缺失的日期余额点
     *
     * @param balanceMap 余额映射
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    private void fillMissingDateBalancePoints(Map<LocalDate, BigDecimal> balanceMap,
                                              LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        BigDecimal lastBalance = balanceMap.get(startDate);

        while (currentDate.isBefore(endDate)) {
            currentDate = currentDate.plusDays(1);

            if (!balanceMap.containsKey(currentDate)) {
                balanceMap.put(currentDate, lastBalance);
            } else {
                lastBalance = balanceMap.get(currentDate);
            }
        }
    }

    /**
     * 计算指定日期的净资产
     *
     * @param accounts 账户列表
     * @param transactions 交易列表
     * @param date 日期
     * @return 净资产
     */
    private BigDecimal calculateNetWorthAt(List<Account> accounts, List<Transaction> transactions, LocalDate date) {
        BigDecimal netWorth = BigDecimal.ZERO;

        for (Account account : accounts) {
            BigDecimal accountBalance = calculateAccountBalanceAt(account, transactions, date);
            netWorth = netWorth.add(accountBalance);
        }

        return netWorth;
    }
}