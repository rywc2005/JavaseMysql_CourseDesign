package javasemysql.coursedesign.service;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-22
 * @Description:
 * @Version: 1.0
 */


public interface StatisticsService {
    /**
     * 获取总收入
     *
     * @return 总收入
     */
    double getTotalIncome();

    /**
     * 获取总支出
     *
     * @return 总支出
     */
    double getTotalExpenditure();

    /**
     * 获取总余额
     *
     * @return 总余额
     */
    double getTotalBalance();

    /**
     * 获取每月收入统计
     *
     * @return 每月收入统计数组
     */
    double[] getMonthlyIncomeStatistics();

    /**
     * 获取每月支出统计
     *
     * @return 每月支出统计数组
     */
    double[] getMonthlyExpenditureStatistics();
}
