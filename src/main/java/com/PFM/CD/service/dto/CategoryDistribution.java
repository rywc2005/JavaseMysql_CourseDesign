package com.PFM.CD.service.dto;

import java.math.BigDecimal;

/**
 * 分类分布数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class CategoryDistribution {

    private int categoryId;
    private String categoryName;
    private BigDecimal amount;
    private double percentage;

    /**
     * 构造函数
     *
     * @param categoryId 分类ID
     * @param categoryName 分类名称
     * @param amount 金额
     * @param percentage 百分比
     */
    public CategoryDistribution(int categoryId, String categoryName, BigDecimal amount, double percentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.percentage = percentage;
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
     * 获取金额
     *
     * @return 金额
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置金额
     *
     * @param amount 金额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取百分比
     *
     * @return 百分比
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * 设置百分比
     *
     * @param percentage 百分比
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}