package com.PFM.CD.service.dto;

import com.PFM.CD.entity.enums.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TransactionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int transactionId;
    private int userId;
    private Integer sourceAccountId;
    private String sourceAccountName;
    private Integer destinationAccountId;
    private String destinationAccountName;
    private int categoryId;
    private String categoryName;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDate transactionDate;
    private String description;

    /**
     * 默认构造函数
     */
    public TransactionDto() {
    }

    /**
     * 获取交易ID
     *
     * @return 交易ID
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * 设置交易ID
     *
     * @param transactionId 交易ID
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 获取源账户ID
     *
     * @return 源账户ID
     */
    public Integer getSourceAccountId() {
        return sourceAccountId;
    }

    /**
     * 设置源账户ID
     *
     * @param sourceAccountId 源账户ID
     */
    public void setSourceAccountId(Integer sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    /**
     * 获取源账户名称
     *
     * @return 源账户名称
     */
    public String getSourceAccountName() {
        return sourceAccountName;
    }

    /**
     * 设置源账户名称
     *
     * @param sourceAccountName 源账户名称
     */
    public void setSourceAccountName(String sourceAccountName) {
        this.sourceAccountName = sourceAccountName;
    }

    /**
     * 获取目标账户ID
     *
     * @return 目标账户ID
     */
    public Integer getDestinationAccountId() {
        return destinationAccountId;
    }

    /**
     * 设置目标账户ID
     *
     * @param destinationAccountId 目标账户ID
     */
    public void setDestinationAccountId(Integer destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    /**
     * 获取目标账户名称
     *
     * @return 目标账户名称
     */
    public String getDestinationAccountName() {
        return destinationAccountName;
    }

    /**
     * 设置目标账户名称
     *
     * @param destinationAccountName 目标账户名称
     */
    public void setDestinationAccountName(String destinationAccountName) {
        this.destinationAccountName = destinationAccountName;
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
     * 获取交易类型
     *
     * @return 交易类型
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * 设置交易类型
     *
     * @param transactionType 交易类型
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * 获取交易日期
     *
     * @return 交易日期
     */
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    /**
     * 设置交易日期
     *
     * @param transactionDate 交易日期
     */
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 检查是否为收入交易
     *
     * @return 如果是收入交易返回true，否则返回false
     */
    public boolean isIncome() {
        return transactionType == TransactionType.INCOME;
    }

    /**
     * 检查是否为支出交易
     *
     * @return 如果是支出交易返回true，否则返回false
     */
    public boolean isExpense() {
        return transactionType == TransactionType.EXPENSE;
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", sourceAccountId=" + sourceAccountId +
                ", destinationAccountId=" + destinationAccountId +
                ", categoryId=" + categoryId +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", transactionDate=" + transactionDate +
                ", description='" + description + '\'' +
                '}';
    }
}