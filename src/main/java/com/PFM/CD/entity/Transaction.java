package com.PFM.CD.entity;

import com.PFM.CD.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 交易实体类
 * 对应数据库transactions表
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class Transaction {

    private int transactionId;
    private int userId;
    private Integer sourceAccountId;  // 可为null
    private Integer destinationAccountId;  // 可为null
    private int categoryId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDate transactionDate;
    private String description;

    // 非数据库字段，用于展示
    private String categoryName;
    private String sourceAccountName;
    private String destinationAccountName;

    /**
     * 默认构造函数
     */
    public Transaction() {
        this.transactionDate = LocalDate.now();
        this.amount = BigDecimal.ZERO;
    }

    /**
     * 创建收入交易的便捷构造函数
     */
    public static Transaction createIncome(int userId, int destinationAccountId, int categoryId,
                                           BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setDestinationAccountId(destinationAccountId);
        transaction.setCategoryId(categoryId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription(description);
        return transaction;
    }

    /**
     * 创建支出交易的便捷构造函数
     */
    public static Transaction createExpense(int userId, int sourceAccountId, int categoryId,
                                            BigDecimal amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setSourceAccountId(sourceAccountId);
        transaction.setCategoryId(categoryId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription(description);
        return transaction;
    }

    /**
     * 完整构造函数
     */
    public Transaction(int transactionId, int userId, Integer sourceAccountId, Integer destinationAccountId,
                       int categoryId, BigDecimal amount, TransactionType transactionType,
                       LocalDate transactionDate, String description) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    // Getters and Setters

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Integer sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Integer getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(Integer destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSourceAccountName() {
        return sourceAccountName;
    }

    public void setSourceAccountName(String sourceAccountName) {
        this.sourceAccountName = sourceAccountName;
    }

    public String getDestinationAccountName() {
        return destinationAccountName;
    }

    public void setDestinationAccountName(String destinationAccountName) {
        this.destinationAccountName = destinationAccountName;
    }

    /**
     * 获取交易摘要信息
     * @return 交易摘要字符串
     */
    public String getSummary() {
        if (transactionType == TransactionType.INCOME) {
            return "收入: " + amount + " 元 - " + (categoryName != null ? categoryName : "未知类别");
        } else {
            return "支出: " + amount + " 元 - " + (categoryName != null ? categoryName : "未知类别");
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId &&
                userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, userId);
    }
}