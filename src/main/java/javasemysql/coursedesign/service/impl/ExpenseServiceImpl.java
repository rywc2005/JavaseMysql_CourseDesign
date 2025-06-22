package javasemysql.coursedesign.service.impl;

import javasemysql.coursedesign.dao.ExpenseDao;
import javasemysql.coursedesign.dao.impl.ExpenseDaoImpl;
import javasemysql.coursedesign.dto.ExpenseQueryParam;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Expense;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.BudgetService;
import javasemysql.coursedesign.service.ExpenseService;
import javasemysql.coursedesign.utils.DBUtils;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 支出服务实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger logger = Logger.getLogger(ExpenseServiceImpl.class.getName());

    private ExpenseDao expenseDao;
    private AccountService accountService;
    private BudgetService budgetService;

    /**
     * 构造函数
     */
    public ExpenseServiceImpl() {
        this.expenseDao = new ExpenseDaoImpl();
    }

    public void setBudgetService(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @Override
    public List<Expense> getExpensesByUserId(int userId) {
        Connection conn = null;
        List<Expense> expenses = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            expenses = expenseDao.findByUserId(conn, userId);
        } catch (SQLException e) {
            LogUtils.error("获取用户支出列表失败，用户ID: " + userId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return expenses;
    }

    @Override
    public Expense getExpenseById(int expenseId) {
        Connection conn = null;
        Expense expense = null;

        try {
            conn = DBUtils.getConnection();
            expense = expenseDao.findById(conn, expenseId);
        } catch (SQLException e) {
            LogUtils.error("获取支出信息失败，支出ID: " + expenseId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return expense;
    }

    @Override
    public boolean addExpense(Expense expense) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 检查账户是否存在
            if (expense.getAccountId() > 0) {
                Account account = accountService.getAccountById(expense.getAccountId());
                if (account == null) {
                    LogUtils.error("添加支出失败，账户不存在: " + expense.getAccountId());
                    return false;
                }

                // 设置账户名称
                expense.setAccountName(account.getName());

                // 检查余额是否足够
                if (account.getBalance() < expense.getAmount()) {
                    LogUtils.error("添加支出失败，账户余额不足: " + account.getBalance() + " < " + expense.getAmount());
                    return false;
                }

                // 检查是否超出预算
                if (budgetService.isOverBudget(expense.getUserId(), expense.getCategory(), expense.getAmount())) {
                    // 仅记录警告，不阻止添加
                    LogUtils.warn("添加支出可能超出预算，类别: " + expense.getCategory() + ", 金额: " + expense.getAmount());
                }

                // 更新账户余额
                boolean balanceUpdated = accountService.updateAccountBalance(expense.getAccountId(), -expense.getAmount());
                if (!balanceUpdated) {
                    LogUtils.error("添加支出失败，更新账户余额失败: " + expense.getAccountId());
                    return false;
                }
            }

            // 执行添加操作
            success = expenseDao.insert(conn, expense);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("添加支出失败", e);
            // 回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                LogUtils.error("恢复自动提交失败", e);
            }
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    @Override
    public boolean updateExpense(Expense expense) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取原始支出信息
            Expense originalExpense = expenseDao.findById(conn, expense.getId());
            if (originalExpense == null) {
                LogUtils.error("更新支出失败，支出不存在: " + expense.getId());
                return false;
            }

            // 检查账户是否已更改
            if (expense.getAccountId() != originalExpense.getAccountId()) {
                // 恢复原账户余额
                if (originalExpense.getAccountId() > 0) {
                    boolean originalBalanceUpdated = accountService.updateAccountBalance(
                            originalExpense.getAccountId(), originalExpense.getAmount());
                    if (!originalBalanceUpdated) {
                        LogUtils.error("更新支出失败，恢复原账户余额失败: " + originalExpense.getAccountId());
                        return false;
                    }
                }

                // 检查新账户是否存在
                if (expense.getAccountId() > 0) {
                    Account account = accountService.getAccountById(expense.getAccountId());
                    if (account == null) {
                        LogUtils.error("更新支出失败，账户不存在: " + expense.getAccountId());
                        return false;
                    }

                    // 更新账户名称
                    expense.setAccountName(account.getName());

                    // 检查余额是否足够
                    if (account.getBalance() < expense.getAmount()) {
                        LogUtils.error("更新支出失败，账户余额不足: " + account.getBalance() + " < " + expense.getAmount());
                        return false;
                    }

                    // 更新新账户余额
                    boolean newBalanceUpdated = accountService.updateAccountBalance(
                            expense.getAccountId(), -expense.getAmount());
                    if (!newBalanceUpdated) {
                        LogUtils.error("更新支出失败，更新新账户余额失败: " + expense.getAccountId());
                        return false;
                    }
                }
            } else if (expense.getAmount() != originalExpense.getAmount()) {
                // 账户相同但金额不同，更新余额差额
                if (expense.getAccountId() > 0) {
                    // 计算差额
                    double difference = originalExpense.getAmount() - expense.getAmount();

                    // 更新账户余额
                    boolean balanceUpdated = accountService.updateAccountBalance(expense.getAccountId(), difference);
                    if (!balanceUpdated) {
                        LogUtils.error("更新支出失败，更新账户余额失败: " + expense.getAccountId());
                        return false;
                    }
                }
            }

            // 执行更新操作
            success = expenseDao.update(conn, expense);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("更新支出失败", e);
            // 回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                LogUtils.error("恢复自动提交失败", e);
            }
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    @Override
    public boolean deleteExpense(int expenseId) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取支出信息
            Expense expense = expenseDao.findById(conn, expenseId);
            if (expense == null) {
                LogUtils.error("删除支出失败，支出不存在: " + expenseId);
                return false;
            }

            // 恢复账户余额
            if (expense.getAccountId() > 0) {
                boolean balanceUpdated = accountService.updateAccountBalance(
                        expense.getAccountId(), expense.getAmount());
                if (!balanceUpdated) {
                    LogUtils.error("删除支出失败，恢复账户余额失败: " + expense.getAccountId());
                    return false;
                }
            }

            // 执行删除操作
            success = expenseDao.delete(conn, expenseId);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("删除支出失败", e);
            // 回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LogUtils.error("回滚事务失败", ex);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                LogUtils.error("恢复自动提交失败", e);
            }
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    @Override
    public List<Expense> queryExpenses(ExpenseQueryParam param) {
        Connection conn = null;
        List<Expense> expenses = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            expenses = expenseDao.findByParam(conn, param);
        } catch (SQLException e) {
            LogUtils.error("查询支出失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return expenses;
    }

    @Override
    public double getTotalExpenseByDateRange(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        double totalExpense = 0.0;

        try {
            conn = DBUtils.getConnection();
            totalExpense = expenseDao.getTotalExpenseByDateRange(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取总支出失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return totalExpense;
    }

    @Override
    public double getTotalExpenseByDateRange(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        double totalExpense = 0.0;

        try {
            conn = DBUtils.getConnection();
            totalExpense = expenseDao.getTotalExpenseByDateRange(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取总支出（按账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return totalExpense;
    }

    @Override
    public double getTotalExpenseByDateRangeAndCategory(int userId, Date startDate, Date endDate, String category) {
        Connection conn = null;
        double totalExpense = 0.0;

        try {
            conn = DBUtils.getConnection();
            totalExpense = expenseDao.getTotalExpenseByDateRangeAndCategory(conn, userId, startDate, endDate, category);
        } catch (SQLException e) {
            LogUtils.error("获取总支出（按类别）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return totalExpense;
    }

    @Override
    public double getAvgExpenseByDateRange(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        double avgExpense = 0.0;

        try {
            conn = DBUtils.getConnection();
            avgExpense = expenseDao.getAvgExpenseByDateRange(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取平均支出失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return avgExpense;
    }

    @Override
    public double getAvgExpenseByDateRange(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        double avgExpense = 0.0;

        try {
            conn = DBUtils.getConnection();
            avgExpense = expenseDao.getAvgExpenseByDateRange(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取平均支出（按账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return avgExpense;
    }

    @Override
    public double getMaxExpenseByDateRange(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        double maxExpense = 0.0;

        try {
            conn = DBUtils.getConnection();
            maxExpense = expenseDao.getMaxExpenseByDateRange(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取最大支出失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return maxExpense;
    }

    @Override
    public double getMaxExpenseByDateRange(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        double maxExpense = 0.0;

        try {
            conn = DBUtils.getConnection();
            maxExpense = expenseDao.getMaxExpenseByDateRange(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取最大支出（按账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return maxExpense;
    }

    @Override
    public Map<String, Double> getExpenseByCategory(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        Map<String, Double> categoryExpenses = new HashMap<>();

        try {
            conn = DBUtils.getConnection();
            categoryExpenses = expenseDao.getExpenseByCategory(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取支出（按类别）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return categoryExpenses;
    }

    @Override
    public Map<String, Double> getExpenseByCategory(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        Map<String, Double> categoryExpenses = new HashMap<>();

        try {
            conn = DBUtils.getConnection();
            categoryExpenses = expenseDao.getExpenseByCategory(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取支出（按类别和账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return categoryExpenses;
    }

    @Override
    public Map<String, Double> getExpenseByTime(int userId, Date startDate, Date endDate, String groupBy) {
        Connection conn = null;
        Map<String, Double> timeExpenses = new TreeMap<>();

        try {
            conn = DBUtils.getConnection();

            // 获取支出数据
            List<Object[]> rawData = expenseDao.getExpenseByTime(conn, userId, startDate, endDate, groupBy);

            // 处理日期格式
            for (Object[] row : rawData) {
                String dateStr = formatDateByGroupType(row[0], groupBy);
                Double amount = (Double) row[1];
                timeExpenses.put(dateStr, amount);
            }

            // 填充缺失的日期
            fillMissingDates(timeExpenses, startDate, endDate, groupBy);

        } catch (SQLException e) {
            LogUtils.error("获取支出（按时间）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return timeExpenses;
    }

    @Override
    public Map<String, Double> getExpenseByTime(int userId, Date startDate, Date endDate, String groupBy, int accountId) {
        Connection conn = null;
        Map<String, Double> timeExpenses = new TreeMap<>();

        try {
            conn = DBUtils.getConnection();

            // 获取支出数据
            List<Object[]> rawData = expenseDao.getExpenseByTime(conn, userId, startDate, endDate, groupBy, accountId);

            // 处理日期格式
            for (Object[] row : rawData) {
                String dateStr = formatDateByGroupType(row[0], groupBy);
                Double amount = (Double) row[1];
                timeExpenses.put(dateStr, amount);
            }

            // 填充缺失的日期
            fillMissingDates(timeExpenses, startDate, endDate, groupBy);

        } catch (SQLException e) {
            LogUtils.error("获取支出（按时间和账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return timeExpenses;
    }

    /**
     * 根据分组类型格式化日期
     *
     * @param dateObj 日期对象
     * @param groupBy 分组方式
     * @return 格式化后的日期字符串
     */
    private String formatDateByGroupType(Object dateObj, String groupBy) {
        if (dateObj == null) {
            return "";
        }

        if (dateObj instanceof Date) {
            Date date = (Date) dateObj;
            SimpleDateFormat sdf = null;

            switch (groupBy.toLowerCase()) {
                case "day":
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    break;
                case "week":
                    // 格式化为 yyyy-ww（年-周数）
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int year = calendar.get(Calendar.YEAR);
                    int week = calendar.get(Calendar.WEEK_OF_YEAR);
                    return String.format("%d-W%02d", year, week);
                case "month":
                    sdf = new SimpleDateFormat("yyyy-MM");
                    break;
                case "quarter":
                    // 格式化为 yyyy-Q（年-季度）
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int y = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int quarter = (month / 3) + 1;
                    return String.format("%d-Q%d", y, quarter);
                case "year":
                    sdf = new SimpleDateFormat("yyyy");
                    break;
                default:
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
            }

            return sdf.format(date);
        } else if (dateObj instanceof String) {
            return (String) dateObj;
        } else {
            return dateObj.toString();
        }
    }

    /**
     * 填充缺失的日期
     *
     * @param data 数据映射
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式
     */
    private void fillMissingDates(Map<String, Double> data, Date startDate, Date endDate, String groupBy) {
        if (startDate == null || endDate == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // 根据分组类型设置日期增量
        int calendarField;
        int increment;

        switch (groupBy.toLowerCase()) {
            case "day":
                calendarField = Calendar.DAY_OF_MONTH;
                increment = 1;
                break;
            case "week":
                calendarField = Calendar.WEEK_OF_YEAR;
                increment = 1;
                break;
            case "month":
                calendarField = Calendar.MONTH;
                increment = 1;
                break;
            case "quarter":
                calendarField = Calendar.MONTH;
                increment = 3;
                break;
            case "year":
                calendarField = Calendar.YEAR;
                increment = 1;
                break;
            default:
                return; // 未知分组类型，不填充
        }

        // 循环填充缺失的日期
        while (!calendar.getTime().after(endDate)) {
            String dateStr = formatDateByGroupType(calendar.getTime(), groupBy);

            // 如果不存在该日期的数据，则添加0值
            if (!data.containsKey(dateStr)) {
                data.put(dateStr, 0.0);
            }

            // 增加日期
            calendar.add(calendarField, increment);
        }
    }
}