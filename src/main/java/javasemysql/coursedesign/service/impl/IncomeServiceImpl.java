package javasemysql.coursedesign.service.impl;

import javasemysql.coursedesign.dao.IncomeDao;
import javasemysql.coursedesign.dao.impl.IncomeDaoImpl;
import javasemysql.coursedesign.dto.IncomeQueryParam;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.model.Income;
import javasemysql.coursedesign.service.AccountService;
import javasemysql.coursedesign.service.IncomeService;
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
 * 收入服务实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class IncomeServiceImpl implements IncomeService {

    private static final Logger logger = Logger.getLogger(IncomeServiceImpl.class.getName());

    private IncomeDao incomeDao;
    private AccountService accountService;

    /**
     * 构造函数
     */
    public IncomeServiceImpl() {
        this.incomeDao = new IncomeDaoImpl();
        this.accountService = new AccountServiceImpl();
    }

    @Override
    public List<Income> getIncomesByUserId(int userId) {
        Connection conn = null;
        List<Income> incomes = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            incomes = incomeDao.findByUserId(conn, userId);
        } catch (SQLException e) {
            LogUtils.error("获取用户收入列表失败，用户ID: " + userId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return incomes;
    }

    @Override
    public Income getIncomeById(int incomeId) {
        Connection conn = null;
        Income income = null;

        try {
            conn = DBUtils.getConnection();
            income = incomeDao.findById(conn, incomeId);
        } catch (SQLException e) {
            LogUtils.error("获取收入信息失败，收入ID: " + incomeId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return income;
    }

    @Override
    public boolean addIncome(Income income) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 检查账户是否存在
            if (income.getAccountId() > 0) {
                Account account = accountService.getAccountById(income.getAccountId());
                if (account == null) {
                    LogUtils.error("添加收入失败，账户不存在: " + income.getAccountId());
                    return false;
                }

                // 设置账户名称
                income.setAccountName(account.getName());

                // 更新账户余额
                boolean balanceUpdated = accountService.updateAccountBalance(income.getAccountId(), income.getAmount());
                if (!balanceUpdated) {
                    LogUtils.error("添加收入失败，更新账户余额失败: " + income.getAccountId());
                    return false;
                }
            }

            // 执行添加操作
            success = incomeDao.insert(conn, income);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("添加收入失败", e);
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
    public boolean updateIncome(Income income) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取原始收入信息
            Income originalIncome = incomeDao.findById(conn, income.getId());
            if (originalIncome == null) {
                LogUtils.error("更新收入失败，收入不存在: " + income.getId());
                return false;
            }

            // 检查账户是否已更改
            if (income.getAccountId() != originalIncome.getAccountId()) {
                // 恢复原账户余额
                if (originalIncome.getAccountId() > 0) {
                    boolean originalBalanceUpdated = accountService.updateAccountBalance(
                            originalIncome.getAccountId(), -originalIncome.getAmount());
                    if (!originalBalanceUpdated) {
                        LogUtils.error("更新收入失败，恢复原账户余额失败: " + originalIncome.getAccountId());
                        return false;
                    }
                }

                // 检查新账户是否存在
                if (income.getAccountId() > 0) {
                    Account account = accountService.getAccountById(income.getAccountId());
                    if (account == null) {
                        LogUtils.error("更新收入失败，账户不存在: " + income.getAccountId());
                        return false;
                    }

                    // 更新账户名称
                    income.setAccountName(account.getName());

                    // 更新新账户余额
                    boolean newBalanceUpdated = accountService.updateAccountBalance(
                            income.getAccountId(), income.getAmount());
                    if (!newBalanceUpdated) {
                        LogUtils.error("更新收入失败，更新新账户余额失败: " + income.getAccountId());
                        return false;
                    }
                }
            } else if (income.getAmount() != originalIncome.getAmount()) {
                // 账户相同但金额不同，更新余额差额
                if (income.getAccountId() > 0) {
                    // 计算差额
                    double difference = income.getAmount() - originalIncome.getAmount();

                    // 更新账户余额
                    boolean balanceUpdated = accountService.updateAccountBalance(income.getAccountId(), difference);
                    if (!balanceUpdated) {
                        LogUtils.error("更新收入失败，更新账户余额失败: " + income.getAccountId());
                        return false;
                    }
                }
            }

            // 执行更新操作
            success = incomeDao.update(conn, income);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("更新收入失败", e);
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
    public boolean deleteIncome(int incomeId) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取收入信息
            Income income = incomeDao.findById(conn, incomeId);
            if (income == null) {
                LogUtils.error("删除收入失败，收入不存在: " + incomeId);
                return false;
            }

            // 恢复账户余额
            if (income.getAccountId() > 0) {
                boolean balanceUpdated = accountService.updateAccountBalance(
                        income.getAccountId(), -income.getAmount());
                if (!balanceUpdated) {
                    LogUtils.error("删除收入失败，恢复账户余额失败: " + income.getAccountId());
                    return false;
                }
            }

            // 执行删除操作
            success = incomeDao.delete(conn, incomeId);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("删除收入失败", e);
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
    public List<Income> queryIncomes(IncomeQueryParam param) {
        Connection conn = null;
        List<Income> incomes = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            incomes = incomeDao.findByParam(conn, param);
        } catch (SQLException e) {
            LogUtils.error("查询收入失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return incomes;
    }

    @Override
    public double getTotalIncomeByDateRange(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        double totalIncome = 0.0;

        try {
            conn = DBUtils.getConnection();
            totalIncome = incomeDao.getTotalIncomeByDateRange(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取总收入失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return totalIncome;
    }

    @Override
    public double getTotalIncomeByDateRange(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        double totalIncome = 0.0;

        try {
            conn = DBUtils.getConnection();
            totalIncome = incomeDao.getTotalIncomeByDateRange(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取总收入（按账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return totalIncome;
    }

    @Override
    public double getTotalIncomeByDateRangeAndCategory(int userId, Date startDate, Date endDate, String category) {
        Connection conn = null;
        double totalIncome = 0.0;

        try {
            conn = DBUtils.getConnection();
            totalIncome = incomeDao.getTotalIncomeByDateRangeAndCategory(conn, userId, startDate, endDate, category);
        } catch (SQLException e) {
            LogUtils.error("获取总收入（按类别）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return totalIncome;
    }

    @Override
    public double getAvgIncomeByDateRange(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        double avgIncome = 0.0;

        try {
            conn = DBUtils.getConnection();
            avgIncome = incomeDao.getAvgIncomeByDateRange(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取平均收入失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return avgIncome;
    }

    @Override
    public double getAvgIncomeByDateRange(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        double avgIncome = 0.0;

        try {
            conn = DBUtils.getConnection();
            avgIncome = incomeDao.getAvgIncomeByDateRange(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取平均收入（按账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return avgIncome;
    }

    @Override
    public double getMaxIncomeByDateRange(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        double maxIncome = 0.0;

        try {
            conn = DBUtils.getConnection();
            maxIncome = incomeDao.getMaxIncomeByDateRange(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取最大收入失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return maxIncome;
    }

    @Override
    public double getMaxIncomeByDateRange(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        double maxIncome = 0.0;

        try {
            conn = DBUtils.getConnection();
            maxIncome = incomeDao.getMaxIncomeByDateRange(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取最大收入（按账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return maxIncome;
    }

    @Override
    public Map<String, Double> getIncomeByCategory(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        Map<String, Double> categoryIncomes = new HashMap<>();

        try {
            conn = DBUtils.getConnection();
            categoryIncomes = incomeDao.getIncomeByCategory(conn, userId, startDate, endDate);
        } catch (SQLException e) {
            LogUtils.error("获取收入（按类别）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return categoryIncomes;
    }

    @Override
    public Map<String, Double> getIncomeByCategory(int userId, Date startDate, Date endDate, int accountId) {
        Connection conn = null;
        Map<String, Double> categoryIncomes = new HashMap<>();

        try {
            conn = DBUtils.getConnection();
            categoryIncomes = incomeDao.getIncomeByCategory(conn, userId, startDate, endDate, accountId);
        } catch (SQLException e) {
            LogUtils.error("获取收入（按类别和账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return categoryIncomes;
    }

    @Override
    public Map<String, Double> getIncomeByTime(int userId, Date startDate, Date endDate, String groupBy) {
        Connection conn = null;
        Map<String, Double> timeIncomes = new TreeMap<>();

        try {
            conn = DBUtils.getConnection();

            // 获取收入数据
            List<Object[]> rawData = incomeDao.getIncomeByTime(conn, userId, startDate, endDate, groupBy);

            // 处理日期格式
            for (Object[] row : rawData) {
                String dateStr = formatDateByGroupType(row[0], groupBy);
                Double amount = (Double) row[1];
                timeIncomes.put(dateStr, amount);
            }

            // 填充缺失的日期
            fillMissingDates(timeIncomes, startDate, endDate, groupBy);

        } catch (SQLException e) {
            LogUtils.error("获取收入（按时间）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return timeIncomes;
    }

    @Override
    public Map<String, Double> getIncomeByTime(int userId, Date startDate, Date endDate, String groupBy, int accountId) {
        Connection conn = null;
        Map<String, Double> timeIncomes = new TreeMap<>();

        try {
            conn = DBUtils.getConnection();

            // 获取收入数据
            List<Object[]> rawData = incomeDao.getIncomeByTime(conn, userId, startDate, endDate, groupBy, accountId);

            // 处理日期格式
            for (Object[] row : rawData) {
                String dateStr = formatDateByGroupType(row[0], groupBy);
                Double amount = (Double) row[1];
                timeIncomes.put(dateStr, amount);
            }

            // 填充缺失的日期
            fillMissingDates(timeIncomes, startDate, endDate, groupBy);

        } catch (SQLException e) {
            LogUtils.error("获取收入（按时间和账户）失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return timeIncomes;
    }

    @Override
    public Map<Date, Double> getIncomeByDate(int id, Date startDate, Date endDate) {
        return null;
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