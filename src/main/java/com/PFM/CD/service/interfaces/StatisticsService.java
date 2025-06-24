package com.PFM.CD.service.interfaces;

import com.PFM.CD.service.dto.AccountBalanceTrend;
import com.PFM.CD.service.dto.CategoryDistribution;
import com.PFM.CD.service.dto.IncomeExpenseTrend;
import com.PFM.CD.service.dto.TransactionFrequency;
import com.PFM.CD.service.exception.ServiceException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计服务接口，提供财务统计相关的业务逻辑
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface StatisticsService {

    /**
     * 获取收支趋势数据
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param intervalType 间隔类型（日/周/月/年）
     * @return 收支趋势数据
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<IncomeExpenseTrend> getIncomeExpenseTrend(int userId, LocalDate startDate,
                                                   LocalDate endDate, String intervalType)
            throws ServiceException;

    /**
     * 获取账户余额趋势数据
     *
     * @param userId 用户ID
     * @param accountIds 账户ID列表（为空表示所有账户）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 账户余额趋势数据
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<AccountBalanceTrend> getAccountBalanceTrend(int userId, List<Integer> accountIds,
                                                     LocalDate startDate, LocalDate endDate)
            throws ServiceException;

    /**
     * 获取分类支出分布
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分类支出分布
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<CategoryDistribution> getExpenseCategoryDistribution(int userId, LocalDate startDate,
                                                              LocalDate endDate)
            throws ServiceException;

    /**
     * 获取分类收入分布
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分类收入分布
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<CategoryDistribution> getIncomeCategoryDistribution(int userId, LocalDate startDate,
                                                             LocalDate endDate)
            throws ServiceException;

    /**
     * 获取交易频率数据
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易频率数据
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<TransactionFrequency> getTransactionFrequency(int userId, LocalDate startDate,
                                                       LocalDate endDate)
            throws ServiceException;

    /**
     * 获取月度收支汇总
     *
     * @param userId 用户ID
     * @param year 年份
     * @return 月份到收支数据的映射
     * @throws ServiceException 如果获取过程中发生错误
     */
    Map<Integer, Map<String, BigDecimal>> getMonthlyIncomeExpenseSummary(int userId, int year)
            throws ServiceException;

    /**
     * 获取日均支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日均支出
     * @throws ServiceException 如果获取过程中发生错误
     */
    BigDecimal getDailyAverageExpense(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException;

    /**
     * 获取月均支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月均支出
     * @throws ServiceException 如果获取过程中发生错误
     */
    BigDecimal getMonthlyAverageExpense(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException;

    /**
     * 获取净资产变化
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 开始日期和结束日期的净资产，以及变化金额和百分比
     * @throws ServiceException 如果获取过程中发生错误
     */
    Map<String, Object> getNetWorthChange(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException;

    /**
     * 获取预算执行情况统计
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预算执行情况统计
     * @throws ServiceException 如果获取过程中发生错误
     */
    Map<String, Object> getBudgetExecutionStatistics(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException;
}