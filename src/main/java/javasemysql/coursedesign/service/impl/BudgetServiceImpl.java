package javasemysql.coursedesign.service.impl;

import javasemysql.coursedesign.dao.BudgetDao;
import javasemysql.coursedesign.dao.impl.BudgetDaoImpl;
import javasemysql.coursedesign.dto.BudgetQueryParam;
import javasemysql.coursedesign.model.Budget;
import javasemysql.coursedesign.service.BudgetService;
import javasemysql.coursedesign.service.ExpenseService;
import javasemysql.coursedesign.utils.DBUtils;
import javasemysql.coursedesign.utils.DateUtils;
import javasemysql.coursedesign.utils.LogUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 预算服务实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BudgetServiceImpl implements BudgetService {

    private static final Logger logger = Logger.getLogger(BudgetServiceImpl.class.getName());

    private BudgetDao budgetDao;
    private ExpenseService expenseService;

    /**
     * 构造函数
     */
    public BudgetServiceImpl() {
        this.budgetDao = new BudgetDaoImpl();
    }

    public void setExpenseService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Override
    public List<Budget> getBudgetsByUserId(int userId) {
        Connection conn = null;
        List<Budget> budgets = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            budgets = budgetDao.findByUserId(conn, userId);

            // 更新预算使用情况
            for (Budget budget : budgets) {
                updateBudgetUsageData(conn, budget);
            }
        } catch (SQLException e) {
            LogUtils.error("获取用户预算列表失败，用户ID: " + userId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return budgets;
    }

    @Override
    public Budget getBudgetById(int budgetId) {
        Connection conn = null;
        Budget budget = null;

        try {
            conn = DBUtils.getConnection();
            budget = budgetDao.findById(conn, budgetId);

            // 更新预算使用情况
            if (budget != null) {
                updateBudgetUsageData(conn, budget);
            }
        } catch (SQLException e) {
            LogUtils.error("获取预算信息失败，预算ID: " + budgetId, e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return budget;
    }

    @Override
    public boolean addBudget(Budget budget) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 验证预算有效性
            if (!isValidBudget(budget)) {
                LogUtils.error("添加预算失败，预算数据无效");
                return false;
            }

            // 检查是否已存在相同类别和时间范围的预算
            if (hasDuplicateBudget(conn, budget)) {
                LogUtils.error("添加预算失败，已存在相同类别和时间范围的预算");
                return false;
            }

            // 执行添加操作
            success = budgetDao.insert(conn, budget);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("添加预算失败", e);
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
    public boolean updateBudget(Budget budget) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 获取原始预算信息
            Budget originalBudget = budgetDao.findById(conn, budget.getId());
            if (originalBudget == null) {
                LogUtils.error("更新预算失败，预算不存在: " + budget.getId());
                return false;
            }

            // 验证预算有效性
            if (!isValidBudget(budget)) {
                LogUtils.error("更新预算失败，预算数据无效");
                return false;
            }

            // 检查是否已存在相同类别和时间范围的其他预算
            if (hasDuplicateBudget(conn, budget)) {
                LogUtils.error("更新预算失败，已存在相同类别和时间范围的预算");
                return false;
            }

            // 执行更新操作
            success = budgetDao.update(conn, budget);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("更新预算失败", e);
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
    public boolean deleteBudget(int budgetId) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 检查预算是否存在
            Budget budget = budgetDao.findById(conn, budgetId);
            if (budget == null) {
                LogUtils.error("删除预算失败，预算不存在: " + budgetId);
                return false;
            }

            // 执行删除操作
            success = budgetDao.delete(conn, budgetId);

            // 提交事务
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            LogUtils.error("删除预算失败", e);
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
    public List<Budget> queryBudgets(BudgetQueryParam param) {
        Connection conn = null;
        List<Budget> budgets = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            budgets = budgetDao.findByParam(conn, param);

            // 更新预算使用情况
            for (Budget budget : budgets) {
                updateBudgetUsageData(conn, budget);
            }
        } catch (SQLException e) {
            LogUtils.error("查询预算失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return budgets;
    }

    @Override
    public List<Budget> getActiveBudgets(int userId) {
        Connection conn = null;
        List<Budget> budgets = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();

            // 获取当前日期
            Date today = new Date();

            // 获取活跃预算
            budgets = budgetDao.findActiveBudgets(conn, userId, today);

            // 更新预算使用情况
            for (Budget budget : budgets) {
                updateBudgetUsageData(conn, budget);
            }
        } catch (SQLException e) {
            LogUtils.error("获取活跃预算失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return budgets;
    }

    @Override
    public List<Budget> getCurrentMonthBudgets(int userId) {
        Connection conn = null;
        List<Budget> budgets = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();

            // 获取当前月份的日期范围
            Date[] monthRange = DateUtils.getCurrentMonthRange();
            Date startDate = monthRange[0];
            Date endDate = monthRange[1];

            // 创建查询参数
            BudgetQueryParam param = new BudgetQueryParam(userId);
            param.setStartDate(startDate);
            param.setEndDate(endDate);

            // 获取当前月份的预算
            budgets = budgetDao.findByParam(conn, param);

            // 更新预算使用情况
            for (Budget budget : budgets) {
                updateBudgetUsageData(conn, budget);
            }
        } catch (SQLException e) {
            LogUtils.error("获取当前月份预算失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return budgets;
    }

    @Override
    public boolean updateBudgetUsage(int budgetId) {
        Connection conn = null;
        boolean success = false;

        try {
            conn = DBUtils.getConnection();

            // 获取预算信息
            Budget budget = budgetDao.findById(conn, budgetId);
            if (budget == null) {
                LogUtils.error("更新预算使用情况失败，预算不存在: " + budgetId);
                return false;
            }

            // 更新预算使用情况
            success = updateBudgetUsageData(conn, budget);

        } catch (SQLException e) {
            LogUtils.error("更新预算使用情况失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return success;
    }

    /**
     * 更新预算使用情况数据
     *
     * @param conn 数据库连接
     * @param budget 预算对象
     * @return 是否更新成功
     * @throws SQLException 如果数据库操作失败
     */
    private boolean updateBudgetUsageData(Connection conn, Budget budget) throws SQLException {
        // 获取预算时间范围内的支出总额
        double usedAmount = expenseService.getTotalExpenseByDateRangeAndCategory(
                budget.getUserId(),
                budget.getStartDate(),
                budget.getEndDate(),
                budget.getCategory()
        );

        // 设置已使用金额
        budget.setUsedAmount(usedAmount);

        return true;
    }

    @Override
    public Map<String, double[]> getBudgetStatistics(int userId, Date startDate, Date endDate) {
        Connection conn = null;
        Map<String, double[]> statistics = new HashMap<>();

        try {
            conn = DBUtils.getConnection();

            // 创建查询参数
            BudgetQueryParam param = new BudgetQueryParam(userId);
            param.setStartDate(startDate);
            param.setEndDate(endDate);

            // 获取时间范围内的预算
            List<Budget> budgets = budgetDao.findByParam(conn, param);

            // 计算统计数据
            for (Budget budget : budgets) {
                // 更新预算使用情况
                updateBudgetUsageData(conn, budget);

                // 统计数据：[预算金额, 已使用金额]
                statistics.put(budget.getCategory(), new double[]{budget.getAmount(), budget.getUsedAmount()});
            }
        } catch (SQLException e) {
            LogUtils.error("获取预算统计数据失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return statistics;
    }

    @Override
    public boolean isOverBudget(int userId, String category, double amount) {
        Connection conn = null;
        boolean isOver = false;

        try {
            conn = DBUtils.getConnection();

            // 获取当前日期
            Date today = new Date();

            // 获取该类别的活跃预算
            List<Budget> budgets = budgetDao.findActiveBudgetsByCategory(conn, userId, category, today);

            if (!budgets.isEmpty()) {
                // 使用第一个找到的预算进行检查
                Budget budget = budgets.get(0);

                // 更新预算使用情况
                updateBudgetUsageData(conn, budget);

                // 检查是否超出预算
                double newUsedAmount = budget.getUsedAmount() + amount;
                isOver = newUsedAmount > budget.getAmount();
            }
        } catch (SQLException e) {
            LogUtils.error("检查是否超出预算失败", e);
        } finally {
            DBUtils.closeConnection(conn);
        }

        return isOver;
    }

    /**
     * 验证预算有效性
     *
     * @param budget 预算对象
     * @return 是否有效
     */
    private boolean isValidBudget(Budget budget) {
        // 检查必填字段
        if (budget.getUserId() <= 0 ||
                budget.getCategory() == null ||
                budget.getCategory().isEmpty() ||
                budget.getAmount() <= 0 ||
                budget.getStartDate() == null ||
                budget.getEndDate() == null) {
            return false;
        }

        // 检查开始日期是否早于结束日期
        if (budget.getStartDate().after(budget.getEndDate())) {
            return false;
        }

        return true;
    }

    /**
     * 检查是否存在重复预算
     *
     * @param conn 数据库连接
     * @param budget 预算对象
     * @return 是否存在重复
     * @throws SQLException 如果数据库操作失败
     */
    private boolean hasDuplicateBudget(Connection conn, Budget budget) throws SQLException {
        // 查找相同用户、类别和时间范围有重叠的预算
        List<Budget> overlappingBudgets = budgetDao.findOverlappingBudgets(
                conn,
                budget.getUserId(),
                budget.getCategory(),
                budget.getStartDate(),
                budget.getEndDate()
        );

        // 排除当前预算本身
        for (int i = 0; i < overlappingBudgets.size(); i++) {
            if (overlappingBudgets.get(i).getId() == budget.getId()) {
                overlappingBudgets.remove(i);
                break;
            }
        }

        return !overlappingBudgets.isEmpty();
    }
}