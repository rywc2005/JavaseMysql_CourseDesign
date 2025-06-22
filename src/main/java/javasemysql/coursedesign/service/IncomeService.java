package javasemysql.coursedesign.service;

import javasemysql.coursedesign.dto.IncomeQueryParam;
import javasemysql.coursedesign.model.Income;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收入服务接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface IncomeService {

    /**
     * 根据用户ID获取收入列表
     *
     * @param userId 用户ID
     * @return 收入列表
     */
    List<Income> getIncomesByUserId(int userId);

    /**
     * 根据收入ID获取收入信息
     *
     * @param incomeId 收入ID
     * @return 收入对象，如果不存在则返回null
     */
    Income getIncomeById(int incomeId);

    /**
     * 添加收入
     *
     * @param income 收入对象
     * @return 是否添加成功
     */
    boolean addIncome(Income income);

    /**
     * 更新收入
     *
     * @param income 收入对象
     * @return 是否更新成功
     */
    boolean updateIncome(Income income);

    /**
     * 删除收入
     *
     * @param incomeId 收入ID
     * @return 是否删除成功
     */
    boolean deleteIncome(int incomeId);

    /**
     * 根据条件查询收入
     *
     * @param param 查询参数
     * @return 收入列表
     */
    List<Income> queryIncomes(IncomeQueryParam param);

    /**
     * 获取用户在指定日期范围内的总收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总收入金额
     */
    double getTotalIncomeByDateRange(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的总收入（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 总收入金额
     */
    double getTotalIncomeByDateRange(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内的总收入（按类别过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param category 类别
     * @return 总收入金额
     */
    double getTotalIncomeByDateRangeAndCategory(int userId, Date startDate, Date endDate, String category);

    /**
     * 获取用户在指定日期范围内的平均收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 平均收入金额
     */
    double getAvgIncomeByDateRange(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的平均收入（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 平均收入金额
     */
    double getAvgIncomeByDateRange(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内的最大收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 最大收入金额
     */
    double getMaxIncomeByDateRange(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内的最大收入（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 最大收入金额
     */
    double getMaxIncomeByDateRange(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内按类别统计的收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按类别统计的收入，键为类别，值为金额
     */
    Map<String, Double> getIncomeByCategory(int userId, Date startDate, Date endDate);

    /**
     * 获取用户在指定日期范围内按类别统计的收入（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param accountId 账户ID
     * @return 按类别统计的收入，键为类别，值为金额
     */
    Map<String, Double> getIncomeByCategory(int userId, Date startDate, Date endDate, int accountId);

    /**
     * 获取用户在指定日期范围内按时间统计的收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @return 按时间统计的收入，键为时间，值为金额
     */
    Map<String, Double> getIncomeByTime(int userId, Date startDate, Date endDate, String groupBy);

    /**
     * 获取用户在指定日期范围内按时间统计的收入（按账户过滤）
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param groupBy 分组方式（day, week, month, quarter, year）
     * @param accountId 账户ID
     * @return 按时间统计的收入，键为时间，值为金额
     */
    Map<String, Double> getIncomeByTime(int userId, Date startDate, Date endDate, String groupBy, int accountId);

    Map<Date, Double> getIncomeByDate(int id, Date startDate, Date endDate);
}