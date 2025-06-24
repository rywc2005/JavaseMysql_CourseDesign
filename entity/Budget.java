package com.PFM.CD.entity;

import com.PFM.CD.entity.enums.PeriodType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 预算实体类
 * 对应数据库budgets表
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class Budget {

    private int budgetId;
    private int userId;
    private String name;
    private PeriodType periodType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;

    // 非数据库字段，关联数据
    private List<BudgetCategory> budgetCategories;

    /**
     * 默认构造函数
     */
    public Budget() {
        this.totalAmount = BigDecimal.ZERO;
        this.budgetCategories = new ArrayList<>();
    }

    /**
     * 带参数构造函数
     */
    public Budget(int userId, String name, PeriodType periodType,
                  LocalDate startDate, BigDecimal totalAmount) {
        this.userId = userId;
        this.name = name;
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = periodType.calculateEndDate(startDate);
        this.totalAmount = totalAmount;
        this.budgetCategories = new ArrayList<>();
    }

    /**
     * 完整构造函数
     */
    public Budget(int budgetId, int userId, String name, PeriodType periodType,
                  LocalDate startDate, LocalDate endDate, BigDecimal totalAmount) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.name = name;
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.budgetCategories = new ArrayList<>();
    }

    // Getters and Setters

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
        // 自动更新结束日期
        if (startDate != null) {
            this.endDate = periodType.calculateEndDate(startDate);
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        // 自动更新结束日期
        if (periodType != null) {
            this.endDate = periodType.calculateEndDate(startDate);
        }
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<BudgetCategory> getBudgetCategories() {
        return budgetCategories;
    }

    public void setBudgetCategories(List<BudgetCategory> budgetCategories) {
        this.budgetCategories = budgetCategories;
    }

    /**
     * 添加预算分类
     * @param budgetCategory 预算分类
     */
    public void addBudgetCategory(BudgetCategory budgetCategory) {
        if (budgetCategory != null) {
            budgetCategories.add(budgetCategory);
        }
    }

    /**
     * 计算已分配预算总额
     * @return 已分配预算总额
     */
    public BigDecimal getAllocatedTotal() {
        return budgetCategories.stream()
                .map(BudgetCategory::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算剩余可分配预算
     * @return 剩余可分配预算
     */
    public BigDecimal getRemainingAllocatable() {
        BigDecimal allocated = getAllocatedTotal();
        return totalAmount.subtract(allocated);
    }

    /**
     * 计算已使用预算总额
     * @return 已使用预算总额
     */
    public BigDecimal getSpentTotal() {
        return budgetCategories.stream()
                .map(BudgetCategory::getSpentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算预算使用百分比
     * @return 预算使用百分比
     */
    public double getUsagePercentage() {
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getSpentTotal().doubleValue() / totalAmount.doubleValue() * 100.0;
    }

    /**
     * 判断预算是否处于活跃状态
     * @return 如果当前日期在预算有效期内返回true，否则返回false
     */
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "budgetId=" + budgetId +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", periodType=" + periodType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalAmount=" + totalAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return budgetId == budget.budgetId &&
                userId == budget.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetId, userId);
    }
}