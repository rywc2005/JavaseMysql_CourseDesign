package javasemysql.coursedesign.service;

import javasemysql.coursedesign.dto.BudgetQueryParam;
import javasemysql.coursedesign.model.Budget;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预算服务接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface BudgetService {

    /**
     * 根据用户ID获取预算列表
     *
     * @param userId 用户ID
     * @return 预算列表
     */
    List<Budget> getBudgetsByUserId(int userId);

    /**
     * 根据预算ID获取预算信息
     *
     * @param budgetId 预算ID
     * @return 预算对象，如果不存在则返回null
     */
    Budget getBudgetById(int budgetId);

    /**
     * 添加预算
     *
     * @param budget 预算对象
     * @return 是否添加成功
     */
    boolean addBudget(Budget budget);

    /**
     * 更新预算
     *
     * @param budget 预算对象
     * @return 是否更新成功
     */
    boolean updateBudget(Budget budget);

    /**
     * 删除预算
     *
     * @param budgetId 预算ID
     * @return 是否删除成功
     */
    boolean deleteBudget(int budgetId);

    /**
     * 根据条件查询预算
     *
     * @param param 查询参数
     * @return 预算列表
     */
    List<Budget> queryBudgets(BudgetQueryParam param);

    /**
     * 获取活跃预算（当前日期在预算时间范围内）
     *
     * @param userId 用户ID
     * @return 预算列表
     */
    List<Budget> getActiveBudgets(int userId);

    /**
     * 获取当前月份的预算
     *
     * @param userId 用户ID
     * @return 预算列表
     */
    List<Budget> getCurrentMonthBudgets(int userId);

    /**
     * 更新预算使用情况
     *
     * @param budgetId 预算ID
     * @return 是否更新成功
     */
    boolean updateBudgetUsage(int budgetId);

    /**
     * 获取预算统计数据
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据，键为类别，值为[预算金额, 已使用金额]
     */
    Map<String, double[]> getBudgetStatistics(int userId, Date startDate, Date endDate);

    /**
     * 检查是否超出预算
     *
     * @param userId 用户ID
     * @param category 类别
     * @param amount 金额
     * @return 是否超出预算
     */
    boolean isOverBudget(int userId, String category, double amount);
}