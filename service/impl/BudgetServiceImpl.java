package com.PFM.CD.service.impl;

import com.PFM.CD.dao.interfaces.BudgetDao;
import com.PFM.CD.dao.interfaces.BudgetCategoryDao;
import com.PFM.CD.dao.interfaces.CategoryDao;
import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.BudgetCategory;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.enums.PeriodType;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.BudgetService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预算服务实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class BudgetServiceImpl implements BudgetService {

    private final BudgetDao budgetDao;
    private final BudgetCategoryDao budgetCategoryDao;
    private final CategoryDao categoryDao;

    /**
     * 构造函数
     *
     * @param budgetDao 预算DAO接口
     * @param budgetCategoryDao 预算分类DAO接口
     * @param categoryDao 分类DAO接口
     */
    public BudgetServiceImpl(BudgetDao budgetDao, BudgetCategoryDao budgetCategoryDao, CategoryDao categoryDao) {
        this.budgetDao = budgetDao;
        this.budgetCategoryDao = budgetCategoryDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public Budget createBudget(int userId, String name, PeriodType periodType,
                               LocalDate startDate, BigDecimal totalAmount) throws ServiceException {
        try {
            // 检查预算名称是否已存在
            if (budgetDao.isBudgetNameExists(userId, name)) {
                throw new ServiceException("预算名称已存在: " + name);
            }

            // 验证总金额
            if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("预算金额必须为正数");
            }

            // 计算结束日期
            LocalDate endDate = periodType.calculateEndDate(startDate);

            // 创建新预算对象
            Budget budget = new Budget();
            budget.setUserId(userId);
            budget.setName(name);
            budget.setPeriodType(periodType);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);
            budget.setTotalAmount(totalAmount);

            // 保存预算
            boolean success = budgetDao.save(budget);
            if (!success) {
                throw new ServiceException("创建预算失败");
            }

            return budget;
        } catch (SQLException e) {
            throw new ServiceException("创建预算过程中发生数据库错误", e);
        }
    }

    @Override
    public Budget getBudgetById(int budgetId) throws ServiceException {
        try {
            Budget budget = budgetDao.findById(budgetId);
            if (budget == null) {
                throw new ServiceException("预算不存在: " + budgetId);
            }
            return budget;
        } catch (SQLException e) {
            throw new ServiceException("获取预算信息过程中发生数据库错误", e);
        }
    }

    @Override
    public Budget updateBudget(Budget budget) throws ServiceException {
        try {
            // 检查预算是否存在
            Budget existingBudget = budgetDao.findById(budget.getBudgetId());
            if (existingBudget == null) {
                throw new ServiceException("预算不存在: " + budget.getBudgetId());
            }

            // 检查预算名称是否已被同一用户的其他预算使用
            if (!existingBudget.getName().equals(budget.getName()) &&
                    budgetDao.isBudgetNameExists(budget.getUserId(), budget.getName())) {
                throw new ServiceException("预算名称已存在: " + budget.getName());
            }

            // 验证总金额
            if (budget.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("预算金额必须为正数");
            }

            // 如果周期类型或开始日期发生变化，重新计算结束日期
            if (existingBudget.getPeriodType() != budget.getPeriodType() ||
                    !existingBudget.getStartDate().equals(budget.getStartDate())) {
                LocalDate endDate = budget.getPeriodType().calculateEndDate(budget.getStartDate());
                budget.setEndDate(endDate);
            }

            // 更新预算信息
            boolean success = budgetDao.update(budget);
            if (!success) {
                throw new ServiceException("更新预算信息失败");
            }

            return budget;
        } catch (SQLException e) {
            throw new ServiceException("更新预算信息过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean deleteBudget(int budgetId) throws ServiceException {
        try {
            // 检查预算是否存在
            Budget budget = budgetDao.findById(budgetId);
            if (budget == null) {
                throw new ServiceException("预算不存在: " + budgetId);
            }

            Connection conn = null;
            try {
                conn = budgetDao.getConnection();
                budgetDao.beginTransaction(conn);

                // 删除预算分类
                budgetCategoryDao.deleteByBudgetId(budgetId);

                // 删除预算
                boolean deleted = budgetDao.delete(budgetId);

                budgetDao.commitTransaction(conn);
                return deleted;
            } catch (SQLException e) {
                if (conn != null) {
                    budgetDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                budgetDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("删除预算过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Budget> getUserBudgets(int userId) throws ServiceException {
        try {
            return budgetDao.findByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("获取用户预算过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Budget> getActiveBudgets(int userId) throws ServiceException {
        try {
            return budgetDao.findActiveByUserIdAndDate(userId, LocalDate.now());
        } catch (SQLException e) {
            throw new ServiceException("获取用户活跃预算过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Budget> getBudgetsByPeriodType(int userId, PeriodType periodType) throws ServiceException {
        try {
            return budgetDao.findByUserIdAndPeriodType(userId, periodType);
        } catch (SQLException e) {
            throw new ServiceException("按周期类型获取预算过程中发生数据库错误", e);
        }
    }

    @Override
    public BudgetCategory addBudgetCategory(int budgetId, int categoryId, BigDecimal allocatedAmount)
            throws ServiceException {
        try {
            // 检查预算是否存在
            Budget budget = budgetDao.findById(budgetId);
            if (budget == null) {
                throw new ServiceException("预算不存在: " + budgetId);
            }

            // 检查分类是否存在
            Category category = categoryDao.findById(categoryId);
            if (category == null) {
                throw new ServiceException("分类不存在: " + categoryId);
            }

            // 只能为支出分类分配预算
            if (category.getCategoryType() != CategoryType.EXPENSE) {
                throw new ServiceException("只能为支出分类分配预算");
            }

            // 检查该分类是否已经在此预算中
            BudgetCategory existingBudgetCategory = budgetCategoryDao.findByBudgetIdAndCategoryId(budgetId, categoryId);
            if (existingBudgetCategory != null) {
                throw new ServiceException("该分类已在此预算中");
            }

            // 验证分配金额
            if (allocatedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("分配金额必须为正数");
            }

            // 获取所有已分配金额
            List<BudgetCategory> budgetCategories = budgetCategoryDao.findByBudgetId(budgetId);
            BigDecimal totalAllocated = BigDecimal.ZERO;
            for (BudgetCategory bc : budgetCategories) {
                totalAllocated = totalAllocated.add(bc.getAllocatedAmount());
            }

            // 检查分配金额是否超过预算总额
            if (totalAllocated.add(allocatedAmount).compareTo(budget.getTotalAmount()) > 0) {
                throw new ServiceException("分配金额超过预算剩余可分配金额");
            }

            // 创建新预算分类对象
            BudgetCategory budgetCategory = new BudgetCategory();
            budgetCategory.setBudgetId(budgetId);
            budgetCategory.setCategoryId(categoryId);
            budgetCategory.setAllocatedAmount(allocatedAmount);
            budgetCategory.setSpentAmount(BigDecimal.ZERO);

            // 保存预算分类
            boolean success = budgetCategoryDao.save(budgetCategory);
            if (!success) {
                throw new ServiceException("添加预算分类失败");
            }

            // 设置分类名称（非持久化字段）
            budgetCategory.setCategoryName(category.getCategoryName());

            return budgetCategory;
        } catch (SQLException e) {
            throw new ServiceException("添加预算分类过程中发生数据库错误", e);
        }
    }

    @Override
    public BudgetCategory updateBudgetCategory(int budgetCategoryId, BigDecimal allocatedAmount) throws ServiceException {
        try {
            // 检查预算分类是否存在
            BudgetCategory budgetCategory = budgetCategoryDao.findById(budgetCategoryId);
            if (budgetCategory == null) {
                throw new ServiceException("预算分类不存在: " + budgetCategoryId);
            }

            // 验证分配金额
            if (allocatedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("分配金额必须为正数");
            }

            // 获取预算
            Budget budget = budgetDao.findById(budgetCategory.getBudgetId());
            if (budget == null) {
                throw new ServiceException("预算不存在: " + budgetCategory.getBudgetId());
            }

            // 获取所有已分配金额（不包括当前要更新的）
            List<BudgetCategory> budgetCategories = budgetCategoryDao.findByBudgetId(budgetCategory.getBudgetId());
            BigDecimal totalAllocated = BigDecimal.ZERO;
            for (BudgetCategory bc : budgetCategories) {
                if (bc.getBudgetCategoryId() != budgetCategoryId) {
                    totalAllocated = totalAllocated.add(bc.getAllocatedAmount());
                }
            }

            // 检查分配金额是否超过预算总额
            if (totalAllocated.add(allocatedAmount).compareTo(budget.getTotalAmount()) > 0) {
                throw new ServiceException("分配金额超过预算剩余可分配金额");
            }

            // 更新分配金额
            budgetCategory.setAllocatedAmount(allocatedAmount);
            boolean success = budgetCategoryDao.update(budgetCategory);
            if (!success) {
                throw new ServiceException("更新预算分类失败");
            }

            return budgetCategory;
        } catch (SQLException e) {
            throw new ServiceException("更新预算分类过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean deleteBudgetCategory(int budgetCategoryId) throws ServiceException {
        try {
            // 检查预算分类是否存在
            BudgetCategory budgetCategory = budgetCategoryDao.findById(budgetCategoryId);
            if (budgetCategory == null) {
                throw new ServiceException("预算分类不存在: " + budgetCategoryId);
            }

            return budgetCategoryDao.delete(budgetCategoryId);
        } catch (SQLException e) {
            throw new ServiceException("删除预算分类过程中发生数据库错误", e);
        }
    }

    @Override
    public List<BudgetCategory> getBudgetCategories(int budgetId) throws ServiceException {
        try {
            return budgetCategoryDao.findByBudgetId(budgetId);
        } catch (SQLException e) {
            throw new ServiceException("获取预算分类过程中发生数据库错误", e);
        }
    }

    @Override
    public Budget getBudgetWithCategories(int budgetId) throws ServiceException {
        try {
            return budgetDao.findBudgetWithCategories(budgetId);
        } catch (SQLException e) {
            throw new ServiceException("获取完整预算信息过程中发生数据库错误", e);
        }
    }

    @Override
    public BigDecimal calculateBudgetUsage(int budgetId) throws ServiceException {
        try {
            // 获取预算分类
            List<BudgetCategory> budgetCategories = budgetCategoryDao.findByBudgetId(budgetId);

            // 计算总支出
            BigDecimal totalSpent = BigDecimal.ZERO;
            for (BudgetCategory bc : budgetCategories) {
                if (bc.getSpentAmount() != null) {
                    totalSpent = totalSpent.add(bc.getSpentAmount());
                }
            }

            return totalSpent;
        } catch (SQLException e) {
            throw new ServiceException("计算预算使用情况过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean isBudgetOverspent(int budgetId) throws ServiceException {
        try {
            // 获取预算
            Budget budget = budgetDao.findById(budgetId);
            if (budget == null) {
                throw new ServiceException("预算不存在: " + budgetId);
            }

            // 计算总支出
            BigDecimal totalSpent = calculateBudgetUsage(budgetId);

            // 比较总支出和预算总额
            return totalSpent.compareTo(budget.getTotalAmount()) > 0;
        } catch (SQLException e) {
            throw new ServiceException("检查预算是否超支过程中发生数据库错误", e);
        }
    }

    @Override
    public Map<Integer, Double> getBudgetCategoryUsagePercentage(int budgetId) throws ServiceException {
        try {
            // 获取预算分类
            List<BudgetCategory> budgetCategories = budgetCategoryDao.findByBudgetId(budgetId);

            Map<Integer, Double> usagePercentages = new HashMap<>();

            for (BudgetCategory bc : budgetCategories) {
                if (bc.getAllocatedAmount().compareTo(BigDecimal.ZERO) > 0) {
                    // 计算使用百分比
                    BigDecimal spentAmount = bc.getSpentAmount() != null ? bc.getSpentAmount() : BigDecimal.ZERO;
                    double percentage = spentAmount.divide(bc.getAllocatedAmount(), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .doubleValue();
                    usagePercentages.put(bc.getCategoryId(), percentage);
                } else {
                    usagePercentages.put(bc.getCategoryId(), 0.0);
                }
            }

            return usagePercentages;
        } catch (SQLException e) {
            throw new ServiceException("获取预算分类使用百分比过程中发生数据库错误", e);
        }
    }

    @Override
    public Budget copyBudget(int budgetId, String newName, LocalDate newStartDate) throws ServiceException {
        try {
            // 检查预算是否存在
            Budget originalBudget = budgetDao.findById(budgetId);
            if (originalBudget == null) {
                throw new ServiceException("预算不存在: " + budgetId);
            }

            // 检查新预算名称是否已存在
            if (budgetDao.isBudgetNameExists(originalBudget.getUserId(), newName)) {
                throw new ServiceException("预算名称已存在: " + newName);
            }

            // 复制预算
            int newBudgetId = budgetDao.copyBudget(budgetId, newName, newStartDate);
            if (newBudgetId == -1) {
                throw new ServiceException("复制预算失败");
            }

            // 获取新预算信息
            Budget newBudget = budgetDao.findById(newBudgetId);
            if (newBudget == null) {
                throw new ServiceException("获取新预算信息失败");
            }

            return newBudget;
        } catch (SQLException e) {
            throw new ServiceException("复制预算过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean isBudgetNameAvailable(int userId, String budgetName) throws ServiceException {
        try {
            return !budgetDao.isBudgetNameExists(userId, budgetName);
        } catch (SQLException e) {
            throw new ServiceException("检查预算名称可用性过程中发生数据库错误", e);
        }
    }

    @Override
    public int allocateBudgetCategories(int budgetId, Map<Integer, BigDecimal> categoryAllocations)
            throws ServiceException {
        try {
            // 检查预算是否存在
            Budget budget = budgetDao.findById(budgetId);
            if (budget == null) {
                throw new ServiceException("预算不存在: " + budgetId);
            }

            // 验证总分配金额不超过预算总额
            BigDecimal totalAllocated = BigDecimal.ZERO;
            for (BigDecimal amount : categoryAllocations.values()) {
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ServiceException("分配金额必须为正数");
                }
                totalAllocated = totalAllocated.add(amount);
            }

            if (totalAllocated.compareTo(budget.getTotalAmount()) > 0) {
                throw new ServiceException("总分配金额超过预算总额");
            }

            // 获取所有分类ID
            List<Integer> categoryIds = new ArrayList<>(categoryAllocations.keySet());
            List<Category> categories = categoryDao.findByIds(categoryIds);

            // 检查所有分类是否都是支出类型
            for (Category category : categories) {
                if (category.getCategoryType() != CategoryType.EXPENSE) {
                    throw new ServiceException("只能为支出分类分配预算: " + category.getCategoryName());
                }
            }

            // 创建预算分类对象列表
            List<BudgetCategory> budgetCategories = new ArrayList<>();
            for (Map.Entry<Integer, BigDecimal> entry : categoryAllocations.entrySet()) {
                BudgetCategory budgetCategory = new BudgetCategory();
                budgetCategory.setBudgetId(budgetId);
                budgetCategory.setCategoryId(entry.getKey());
                budgetCategory.setAllocatedAmount(entry.getValue());
                budgetCategory.setSpentAmount(BigDecimal.ZERO);
                budgetCategories.add(budgetCategory);
            }

            Connection conn = null;
            try {
                conn = budgetCategoryDao.getConnection();
                budgetCategoryDao.beginTransaction(conn);

                // 删除现有预算分类
                budgetCategoryDao.deleteByBudgetId(budgetId);

                // 批量保存新预算分类
                int count = budgetCategoryDao.batchSave(budgetCategories);

                budgetCategoryDao.commitTransaction(conn);
                return count;
            } catch (SQLException e) {
                if (conn != null) {
                    budgetCategoryDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                budgetCategoryDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("批量分配预算过程中发生数据库错误", e);
        }
    }
}