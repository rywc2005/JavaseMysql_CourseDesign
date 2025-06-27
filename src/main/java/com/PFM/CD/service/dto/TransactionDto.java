package com.PFM.CD.service.dto;

import com.PFM.CD.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDto {
    private int transactionId;
    private int userId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Integer sourceAccountId;
    private Integer destinationAccountId;
    private Integer categoryId;
    private String categoryName; // 增加分类名称，用于显示
    private LocalDate transactionDate;
    private String description;
    private String sourceAccountName; // 增加账户名称，用于显示
    private String destinationAccountName;

    public TransactionDto() {}

    public TransactionDto(int transactionId, int userId, TransactionType transactionType,
                          BigDecimal amount, Integer sourceAccountId, Integer destinationAccountId,
                          Integer categoryId, LocalDate transactionDate, String description) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.categoryId = categoryId;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getSourceAccountId() { return sourceAccountId; }
    public void setSourceAccountId(Integer sourceAccountId) { this.sourceAccountId = sourceAccountId; }

    public Integer getDestinationAccountId() { return destinationAccountId; }
    public void setDestinationAccountId(Integer destinationAccountId) { this.destinationAccountId = destinationAccountId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceAccountName() { return sourceAccountName; }
    public void setSourceAccountName(String sourceAccountName) { this.sourceAccountName = sourceAccountName; }

    public String getDestinationAccountName() { return destinationAccountName; }
    public void setDestinationAccountName(String destinationAccountName) { this.destinationAccountName = destinationAccountName; }
}