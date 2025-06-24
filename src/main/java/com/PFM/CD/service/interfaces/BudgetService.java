package com.PFM.CD.service.interfaces;

import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.BudgetCategory;
import com.PFM.CD.entity.enums.PeriodType;
import com.PFM.CD.service.exception.ServiceException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预算服务接口，提供预算相关的业务逻辑
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface BudgetService {

    /**
     * 创建新预算
     *
     * @param userId 用户ID
     * @param name 预算名称
     * @param periodType 周期类型
     * @param startDate 开始日期
     * @param totalAmount 总金额
     * @return 创建的预算
     * @throws ServiceException 如果创建过程中发生错误
     */
    Budget createBudget(int userId, String name, PeriodType periodType,
                        LocalDate startDate, BigDecimal totalAmount) throws ServiceException;

    /**
     * 获取预算信息
     *
     * @param budgetId 预算ID
     * @return 预算信息
     * @throws ServiceException 如果获取过程中发生错误
     */
    Budget getBudgetById(int budgetId) throws ServiceException;

    /**
     * 更新预算信息
     *
     * @param budget 需要更新的预算信息
     * @return 更新后的预算
     * @throws ServiceException 如果更新过程中发生错误
     */
    Budget updateBudget(Budget budget) throws ServiceException;

    /**
     * 删除预算
     *
     * @param budgetId 预算ID
     * @return 是否删除成功
     * @throws ServiceException 如果删除过程中发生错误
     */
    boolean deleteBudget(int budgetId) throws ServiceException;

    /**
     * 获取用户的所有预算
     *
     * @param userId 用户ID
     * @return 预算列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Budget> getUserBudgets(int userId) throws ServiceException;

    /**
     * 获取用户当前活跃的预算
     *
     * @param userId 用户ID
     * @return 预算列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Budget> getActiveBudgets(int userId) throws ServiceException;

    /**
     * 按周期类型获取用户预算
     *
     * @param userId 用户ID
     * @param periodType 周期类型
     * @return 预算列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Budget> getBudgetsByPeriodType(int userId, PeriodType periodType) throws ServiceException;

    /**
     * 添加预算分类
     *
     * @param budgetId 预算ID
     * @param categoryId 分类ID
     * @param allocatedAmount 分配金额
     * @return 创建的预算分类
     * @throws ServiceException 如果添加过程中发生错误
     */
    BudgetCategory addBudgetCategory(int budgetId, int categoryId, BigDecimal allocatedAmount)
            throws ServiceException;

    /**
     * 更新预算分类
     *
     * @param budgetCategoryId 预算分类ID
     * @param allocatedAmount 新的分配金额
     * @return 更新后的预算分类
     * @throws ServiceException 如果更新过程中发生错误
     */
    BudgetCategory updateBudgetCategory(int budgetCategoryId, BigDecimal allocatedAmount) throws ServiceException;

    /**
     * 删除预算分类
     *
     * @param budgetCategoryId 预算分类ID
     * @return 是否删除成功
     * @throws ServiceException 如果删除过程中发生错误
     */
    boolean deleteBudgetCategory(int budgetCategoryId) throws ServiceException;

    /**
     * 获取预算的所有分类
     *
     * @param budgetId 预算ID
     * @return 预算分类列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<BudgetCategory> getBudgetCategories(int budgetId) throws ServiceException;

    /**
     * 获取完整预算（包含分类详情）
     *
     * @param budgetId 预算ID
     * @return 完整预算
     * @throws ServiceException 如果获取过程中发生错误
     */
    Budget getBudgetWithCategories(int budgetId) throws ServiceException;

    /**
     * 计算预算使用情况
     *
     * @param budgetId 预算ID
     * @return 已使用金额
     * @throws ServiceException 如果计算过程中发生错误
     */
    BigDecimal calculateBudgetUsage(int budgetId) throws ServiceException;

    /**
     * 检查预算是否超支
     *
     * @param budgetId 预算ID
     * @return 如果超支返回true，否则返回false
     * @throws ServiceException 如果检查过程中发生错误
     */
    boolean isBudgetOverspent(int budgetId) throws ServiceException;

    /**
     * 获取预算分类使用情况
     *
     * @param budgetId 预算ID
     * @return 分类ID到使用百分比的映射
     * @throws ServiceException 如果获取过程中发生错误
     */
    Map<Integer, Double> getBudgetCategoryUsagePercentage(int budgetId) throws ServiceException;

    /**
     * 复制预算
     *
     * @param budgetId 要复制的预算ID
     * @param newName 新预算名称
     * @param newStartDate 新开始日期
     * @return 创建的新预算
     * @throws ServiceException 如果复制过程中发生错误
     */
    Budget copyBudget(int budgetId, String newName, LocalDate newStartDate) throws ServiceException;

    /**
     * 检查预算名称是否可用
     *
     * @param userId 用户ID
     * @param budgetName 预算名称
     * @return 如果可用返回true，否则返回false
     * @throws ServiceException 如果检查过程中发生错误
     */
    boolean isBudgetNameAvailable(int userId, String budgetName) throws ServiceException;

    /**
     * 批量分配预算
     *
     * @param budgetId 预算ID
     * @param categoryAllocations 分类ID到分配金额的映射
     * @return 成功分配的数量
     * @throws ServiceException 如果分配过程中发生错误
     */
    int allocateBudgetCategories(int budgetId, Map<Integer, BigDecimal> categoryAllocations)
            throws ServiceException;
}