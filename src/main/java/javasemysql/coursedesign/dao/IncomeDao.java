package javasemysql.coursedesign.dao;

import javasemysql.coursedesign.dto.IncomeQueryParam;
import javasemysql.coursedesign.model.Income;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收入数据访问接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface IncomeDao {

    /**
     * 根据ID查找收入
     *
     * @param conn 数据库连接
     * @param id 收入ID
     * @return 收入对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    Income findById(Connection conn, int id) throws SQLException;

    /**
     * 根据用户ID查找收入列表
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @return 收入列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Income> findByUserId(Connection conn, int userId) throws SQLException;

    /**
     * 根据查询参数查找收入
     *
     * @param conn 数据库连接
     * @param param 查询参数
     * @return 收入列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Income> findByParam(Connection conn, IncomeQueryParam param) throws SQLException;

    /**
     * 插入收入
     *
     * @param conn 数据库连接
     * @param income 收入对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean insert(Connection conn, Income income) throws SQLException;

    /**
     * 更新收入
     *
     * @param conn 数据库连接
     * @param income 收入对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean update(Connection conn, Income income) throws SQLException;

    /**
     * 删除收入
     *
     * @param conn 数据库连接
     * @param id 收入ID
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean delete(Connection conn, int id) throws SQLException;

    /**
     * 获取指定日期范围内的总收入
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总收入金额
     * @throws SQLException 如果数据库操作失败
     */
    double getTotalIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内的总收入（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 总收入金额
     * @throws SQLException 如果数据库操作失败
     */
    double getTotalIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内的总收入（按类别过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param category 类别
     * @return 总收入金额
     * @throws SQLException 如果数据库操作失败
     */
    double getTotalIncomeByDateRangeAndCategory(Connection conn, int userId, Date startDate, Date endDate, String category) throws SQLException;

    /**
     * 获取指定日期范围内的平均收入
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 平均收入金额
     * @throws SQLException 如果数据库操作失败
     */
    double getAvgIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内的平均收入（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 平均收入金额
     * @throws SQLException 如果数据库操作失败
     */
    double getAvgIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内的最大收入
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 最大收入金额
     * @throws SQLException 如果数据库操作失败
     */
    double getMaxIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内的最大收入（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 最大收入金额
     * @throws SQLException 如果数据库操作失败
     */
    double getMaxIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内按类别统计的收入
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按类别统计的收入，键为类别，值为金额
     * @throws SQLException 如果数据库操作失败
     */
    Map<String, Double> getIncomeByCategory(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 获取指定日期范围内按类别统计的收入（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 按类别统计的收入，键为类别，值为金额
     * @throws SQLException 如果数据库操作失败
     */
    Map<String, Double> getIncomeByCategory(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException;

    /**
     * 获取指定日期范围内按时间统计的收入
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @return 按时间统计的收入，格式为[日期, 金额]的列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Object[]> getIncomeByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy) throws SQLException;

    /**
     * 获取指定日期范围内按时间统计的收入（按账户过滤）
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @param accountId 账户ID
     * @return 按时间统计的收入，格式为[日期, 金额]的列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Object[]> getIncomeByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy, int accountId) throws SQLException;
}