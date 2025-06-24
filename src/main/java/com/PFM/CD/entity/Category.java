package com.PFM.CD.entity;

import com.PFM.CD.entity.enums.CategoryType;

import java.util.Objects;

/**
 * 分类实体类
 * 对应数据库categories表
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class Category {

    private int categoryId;
    private String categoryName;
    private CategoryType categoryType;

    /**
     * 默认构造函数
     */
    public Category() {
    }

    /**
     * 带参数构造函数
     */
    public Category(String categoryName, CategoryType categoryType) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    /**
     * 完整构造函数
     */
    public Category(int categoryId, String categoryName, CategoryType categoryType) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    // Getters and Setters

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    /**
     * 判断是否为支出类别
     * @return 如果是支出类别返回true，否则返回false
     */
    public boolean isExpenseCategory() {
        return categoryType == CategoryType.EXPENSE;
    }

    /**
     * 判断是否为收入类别
     * @return 如果是收入类别返回true，否则返回false
     */
    public boolean isIncomeCategory() {
        return categoryType == CategoryType.INCOME;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryType=" + categoryType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return categoryId == category.categoryId &&
                Objects.equals(categoryName, category.categoryName) &&
                categoryType == category.categoryType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryName, categoryType);
    }

    /**
     * 用于在ComboBox等UI控件中显示
     */
    public String getDisplayString() {
        return categoryName + " (" + categoryType.getDisplayName() + ")";
    }
}