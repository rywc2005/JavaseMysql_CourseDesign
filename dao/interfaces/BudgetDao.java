package com.PFM.CD.dao.interfaces;

import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.enums.PeriodType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * 预算数据访问接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface BudgetDao extends BaseDao<Budget, Integer> {

    /**
     * 查找用户的所有预算
     *
     * @param userId 用户ID
     * @return 预算列表
     */
    List<Budget> findByUserId(int userId) throws SQLException;

    /**
     * 查找用户在指定日期活跃的预算
     *
     * @param userId 用户ID
     * @param date 日期
     * @return 预算列表
     */
    List<Budget> findActiveByUserIdAndDate(int userId, LocalDate date) throws SQLException;

    /**
     * 按周期类型查找用户预算
     *
     * @param userId 用户ID
     * @param periodType 周期类型
     * @return 预算列表
     */
    List<Budget> findByUserIdAndPeriodType(int userId, PeriodType periodType) throws SQLException;

    /**
     * 按日期范围查找用户预算
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预算列表
     */
    List<Budget> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * 获取带有预算分类的完整预算
     *
     * @param budgetId 预算ID
     * @return 完整预算，如果不存在返回null
     */
    Budget findBudgetWithCategories(int budgetId) throws SQLException;

    /**
     * 获取用户最近创建的预算
     *
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 预算列表
     */
    List<Budget> findRecentByUserId(int userId, int limit) throws SQLException;

    /**
     * 检查预算名称在用户范围内是否已存在
     *
     * @param userId 用户ID
     * @param name 预算名称
     * @return 如果存在返回true，否则返回false
     */
    boolean isBudgetNameExists(int userId, String name) throws SQLException;

    /**
     * 复制预算
     *
     * @param budgetId 要复制的预算ID
     * @param newName 新预算名称
     * @param newStartDate 新开始日期
     * @return 新预算ID，失败返回-1
     */
    int copyBudget(int budgetId, String newName, LocalDate newStartDate) throws SQLException;
}