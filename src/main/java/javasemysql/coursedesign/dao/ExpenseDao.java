package javasemysql.coursedesign.dao;

import javasemysql.coursedesign.dto.ExpenseQueryParam;
import javasemysql.coursedesign.model.Expense;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支出数据访问接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface ExpenseDao {

    /**
     * 根据ID查找支出
     *
     * @param conn 数据库连接
     * @param id 支出ID
     * @return 支出对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    Expense findById(Connection conn, int id) throws SQLException;

    /**
     * 根据用户ID查找支出列表
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @return 支出列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Expense> findByUserId(Connection conn, int userId) throws SQLException;

    /**
     * 根据查询参数查找支出
     *
     * @param conn 数据库连接
     * @param param 查询参数
     * @return 支出列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Expense> findByParam(Connection conn, ExpenseQueryParam param) throws SQLException;

    /**
     * 插入支出
     *
     * @param conn 数据库连接
     * @param expense 支出对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean insert(Connection conn, Expense expense) throws SQLException;

    /**
     * 更新支出
     *
     * @param conn 数据库连接
     * @param expense 支出对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean update(Connection conn, Expense expense) throws SQLException;

    /**
     * 删除支出
     *
     * @param conn 数据库连接
     * @param id 支出ID
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean delete(Connection conn, int id) throws SQLException;

    /**
     * 获取指定日期范围内的总支出
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总支出金额
     * @throws SQLException 如果数据库操作失败
     */
    double getTotalExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内的总支出（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 总支出金额
     * @throws SQLException 如果数据库操作失败
     */
    double getTotalExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内的总支出（按类别过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param category 类别
     * @return 总支出金额
     * @throws SQLException 如果数据库操作失败
     */
    double getTotalExpenseByDateRangeAndCategory(Connection conn, int userId, Date startDate, Date endDate, String category) throws SQLException;

    /**
     * 获取指定日期范围内的平均支出
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 平均支出金额
     * @throws SQLException 如果数据库操作失败
     */
    double getAvgExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内的平均支出（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 平均支出金额
     * @throws SQLException 如果数据库操作失败
     */
    double getAvgExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内的最大支出
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 最大支出金额
     * @throws SQLException 如果数据库操作失败
     */
    double getMaxExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内的最大支出（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 最大支出金额
     * @throws SQLException 如果数据库操作失败
     */
    double getMaxExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内按类别统计的支出
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按类别统计的支出，键为类别，值为金额
     * @throws SQLException 如果数据库操作失败
     */
    Map<String, Double> getExpenseByCategory(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内按类别统计的支出（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 按类别统计的支出，键为类别，值为金额
     * @throws SQLException 如果数据库操作失败
     */
    Map<String, Double> getExpenseByCategory(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内按时间统计的支出
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @return 按时间统计的支出，格式为[日期, 金额]的列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Object[]> getExpenseByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy) throws SQLException;

    /**
     * 获取指定日期范围内按时间统计的支出（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @param accountId 账户ID
     * @return 按时间统计的支出，格式为[日期, 金额]的列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Object[]> getExpenseByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy, int accountId) throws SQLException;
}