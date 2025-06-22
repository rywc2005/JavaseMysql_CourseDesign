package javasemysql.coursedesign.service;

import javasemysql.coursedesign.dto.ExpenseQueryParam;
import javasemysql.coursedesign.model.Expense;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支出服务接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface ExpenseService {

    /**
     * 根据用户ID获取支出列表
     *
     * @param userId 用户ID
     * @return 支出列表
     */
    List<Expense> getExpensesByUserId(int userId);

    /**
     * 根据支出ID获取支出信息
     *
     * @param expenseId 支出ID
     * @return 支出对象，如果不存在则返回null
     */
    Expense getExpenseById(int expenseId);

    /**
     * 添加支出
     *
     * @param expense 支出对象
     * @return 是否添加成功
     */
    boolean addExpense(Expense expense);

    /**
     * 更新支出
     *
     * @param expense 支出对象
     * @return 是否更新成功
     */
    boolean updateExpense(Expense expense);

    /**
     * 删除支出
     *
     * @param expenseId 支出ID
     * @return 是否删除成功
     */
    boolean deleteExpense(int expenseId);

    /**
     * 根据条件查询支出
     *
     * @param param 查询参数
     * @return 支出列表
     */
    List<Expense> queryExpenses(ExpenseQueryParam param);

    /**
     * 获取用户在指定日期范围内的总支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总支出金额
     */
    double getTotalExpenseByDateRange(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的总支出（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 总支出金额
     */
    double getTotalExpenseByDateRange(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内的总支出（按类别过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param category 类别
     * @return 总支出金额
     */
    double getTotalExpenseByDateRangeAndCategory(int userId, Date startDate, Date endDate, String category);

    /**
     * 获取用户在指定日期范围内的平均支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 平均支出金额
     */
    double getAvgExpenseByDateRange(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的平均支出（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 平均支出金额
     */
    double getAvgExpenseByDateRange(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内的最大支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 最大支出金额
     */
    double getMaxExpenseByDateRange(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的最大支出（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 最大支出金额
     */
    double getMaxExpenseByDateRange(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内按类别统计的支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按类别统计的支出，键为类别，值为金额
     */
    Map<String, Double> getExpenseByCategory(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内按类别统计的支出（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 按类别统计的支出，键为类别，值为金额
     */
    Map<String, Double> getExpenseByCategory(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内按时间统计的支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @return 按时间统计的支出，键为时间，值为金额
     */
    Map<String, Double> getExpenseByTime(int userId, Date startDate, Date endDate, String groupBy);

    /**
     * 获取用户在指定日期范围内按时间统计的支出（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @param accountId 账户ID
     * @return 按时间统计的支出，键为时间，值为金额
     */
    Map<String, Double> getExpenseByTime(int userId, Date startDate, Date endDate, String groupBy, int accountId);
}