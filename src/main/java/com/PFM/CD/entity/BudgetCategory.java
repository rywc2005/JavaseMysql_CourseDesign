package com.PFM.CD.entity;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 预算分类实体类
 * 对应数据库budget_categories表
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class BudgetCategory {

    private int budgetCategoryId;
    private int budgetId;
    private int categoryId;
    private BigDecimal allocatedAmount;
    private BigDecimal spentAmount;

    // 非数据库字段，关联数据
    private String categoryName;

    /**
     * 默认构造函数
     */
    public BudgetCategory() {
        this.allocatedAmount = BigDecimal.ZERO;
        this.spentAmount = BigDecimal.ZERO;
    }

    /**
     * 带参数构造函数
     */
    public BudgetCategory(int budgetId, int categoryId, BigDecimal allocatedAmount) {
        this.budgetId = budgetId;
        this.categoryId = categoryId;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = BigDecimal.ZERO;
    }

    /**
     * 完整构造函数
     */
    public BudgetCategory(int budgetCategoryId, int budgetId, int categoryId,
                          BigDecimal allocatedAmount, BigDecimal spentAmount) {
        this.budgetCategoryId = budgetCategoryId;
        this.budgetId = budgetId;
        this.categoryId = categoryId;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = spentAmount;
    }

    // Getters and Setters

    public int getBudgetCategoryId() {
        return budgetCategoryId;
    }

    public void setBudgetCategoryId(int budgetCategoryId) {
        this.budgetCategoryId = budgetCategoryId;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 计算剩余预算
     * @return 剩余预算金额
     */
    public BigDecimal getRemainingAmount() {
        return allocatedAmount.subtract(spentAmount);
    }

    /**
     * 计算预算使用百分比
     * @return 预算使用百分比
     */
    public double getUsagePercentage() {
        if (allocatedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return spentAmount.doubleValue() / allocatedAmount.doubleValue() * 100.0;
    }

    /**
     * 判断预算是否超支
     * @return 如果已使用金额超过分配金额返回true，否则返回false
     */
    public boolean isOverBudget() {
        return spentAmount.compareTo(allocatedAmount) > 0;
    }

    @Override
    public String toString() {
        return "BudgetCategory{" +
                "budgetCategoryId=" + budgetCategoryId +
                ", budgetId=" + budgetId +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", allocatedAmount=" + allocatedAmount +
                ", spentAmount=" + spentAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BudgetCategory that = (BudgetCategory) o;
        return budgetCategoryId == that.budgetCategoryId &&
                budgetId == that.budgetId &&
                categoryId == that.categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetCategoryId, budgetId, categoryId);
    }
}