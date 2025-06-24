package com.PFM.CD.dao.interfaces;

import com.PFM.CD.entity.BudgetCategory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * 预算分类数据访问接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface BudgetCategoryDao extends BaseDao<BudgetCategory, Integer> {

    /**
     * 查找预算的所有分类预算
     *
     * @param budgetId 预算ID
     * @return 预算分类列表
     */
    List<BudgetCategory> findByBudgetId(int budgetId) throws SQLException;

    /**
     * 查找分类的所有预算分配
     *
     * @param categoryId 分类ID
     * @return 预算分类列表
     */
    List<BudgetCategory> findByCategoryId(int categoryId) throws SQLException;

    /**
     * 查找预算中特定分类的预算分配
     *
     * @param budgetId 预算ID
     * @param categoryId 分类ID
     * @return 预算分类，如果不存在返回null
     */
    BudgetCategory findByBudgetIdAndCategoryId(int budgetId, int categoryId) throws SQLException;

    /**
     * 更新预算分类的已用金额
     *
     * @param budgetCategoryId 预算分类ID
     * @param spentAmount 已用金额
     * @return 是否成功
     */
    boolean updateSpentAmount(int budgetCategoryId, BigDecimal spentAmount) throws SQLException;

    /**
     * 增加预算分类的已用金额
     *
     * @param budgetCategoryId 预算分类ID
     * @param amount 要增加的金额
     * @return 是否成功
     */
    boolean increaseSpentAmount(int budgetCategoryId, BigDecimal amount) throws SQLException;

    /**
     * 减少预算分类的已用金额
     *
     * @param budgetCategoryId 预算分类ID
     * @param amount 要减少的金额
     * @return 是否成功
     */
    boolean decreaseSpentAmount(int budgetCategoryId, BigDecimal amount) throws SQLException;

    /**
     * 批量保存预算分类
     *
     * @param budgetCategories 预算分类列表
     * @return 成功保存的数量
     */
    int batchSave(List<BudgetCategory> budgetCategories) throws SQLException;

    /**
     * 删除预算的所有分类预算
     *
     * @param budgetId 预算ID
     * @return 是否成功
     */
    boolean deleteByBudgetId(int budgetId) throws SQLException;

    /**
     * 根据交易更新相关预算类别的已用金额
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @param amount 交易金额
     * @param transactionDate 交易日期
     * @return 是否成功
     */
    boolean updateBudgetCategorySpentAmountByTransaction(int userId, int categoryId,
                                                         BigDecimal amount,
                                                         java.time.LocalDate transactionDate) throws SQLException;
}