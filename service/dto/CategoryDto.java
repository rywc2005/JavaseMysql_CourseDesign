package com.PFM.CD.service.dto;

import com.PFM.CD.entity.enums.CategoryType;

import java.io.Serializable;

/**
 * 分类数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class CategoryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int categoryId;
    private String categoryName;
    private CategoryType categoryType;
    private int transactionCount;

    /**
     * 默认构造函数
     */
    public CategoryDto() {
    }

    /**
     * 完整构造函数
     *
     * @param categoryId 分类ID
     * @param categoryName 分类名称
     * @param categoryType 分类类型
     */
    public CategoryDto(int categoryId, String categoryName, CategoryType categoryType) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    /**
     * 带交易数量的构造函数
     *
     * @param categoryId 分类ID
     * @param categoryName 分类名称
     * @param categoryType 分类类型
     * @param transactionCount 交易数量
     */
    public CategoryDto(int categoryId, String categoryName, CategoryType categoryType, int transactionCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.transactionCount = transactionCount;
    }

    /**
     * 获取分类ID
     *
     * @return 分类ID
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * 设置分类ID
     *
     * @param categoryId 分类ID
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获取分类名称
     *
     * @return 分类名称
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 设置分类名称
     *
     * @param categoryName 分类名称
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 获取分类类型
     *
     * @return 分类类型
     */
    public CategoryType getCategoryType() {
        return categoryType;
    }

    /**
     * 设置分类类型
     *
     * @param categoryType 分类类型
     */
    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    /**
     * 获取交易数量
     *
     * @return 交易数量
     */
    public int getTransactionCount() {
        return transactionCount;
    }

    /**
     * 设置交易数量
     *
     * @param transactionCount 交易数量
     */
    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    /**
     * 检查是否为收入分类
     *
     * @return 如果是收入分类返回true，否则返回false
     */
    public boolean isIncomeCategory() {
        return categoryType == CategoryType.INCOME;
    }

    /**
     * 检查是否为支出分类
     *
     * @return 如果是支出分类返回true，否则返回false
     */
    public boolean isExpenseCategory() {
        return categoryType == CategoryType.EXPENSE;
    }

    @Override
    public String toString() {
        return "CategoryDto{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryType=" + categoryType +
                ", transactionCount=" + transactionCount +
                '}';
    }
}