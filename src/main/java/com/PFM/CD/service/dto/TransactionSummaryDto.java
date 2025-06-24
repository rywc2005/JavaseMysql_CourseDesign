package com.PFM.CD.service.dto;

import com.PFM.CD.entity.enums.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 交易摘要数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TransactionSummaryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private TransactionType transactionType;
    private BigDecimal totalAmount;
    private int transactionCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal averageAmount;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
    private LocalDate lastTransactionDate;

    /**
     * 默认构造函数
     */
    public TransactionSummaryDto() {
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
     * 获取总金额
     *
     * @return 总金额
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置总金额
     *
     * @param totalAmount 总金额
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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
     * 获取开始日期
     *
     * @return 开始日期
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * 设置开始日期
     *
     * @param startDate 开始日期
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取结束日期
     *
     * @return 结束日期
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * 设置结束日期
     *
     * @param endDate 结束日期
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * 获取平均金额
     *
     * @return 平均金额
     */
    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    /**
     * 设置平均金额
     *
     * @param averageAmount 平均金额
     */
    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }

    /**
     * 获取最大金额
     *
     * @return 最大金额
     */
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    /**
     * 设置最大金额
     *
     * @param maxAmount 最大金额
     */
    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    /**
     * 获取最小金额
     *
     * @return 最小金额
     */
    public BigDecimal getMinAmount() {
        return minAmount;
    }

    /**
     * 设置最小金额
     *
     * @param minAmount 最小金额
     */
    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * 获取最后交易日期
     *
     * @return 最后交易日期
     */
    public LocalDate getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * 设置最后交易日期
     *
     * @param lastTransactionDate 最后交易日期
     */
    public void setLastTransactionDate(LocalDate lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    @Override
    public String toString() {
        return "TransactionSummaryDto{" +
                "transactionType=" + transactionType +
                ", totalAmount=" + totalAmount +
                ", transactionCount=" + transactionCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", averageAmount=" + averageAmount +
                ", maxAmount=" + maxAmount +
                ", minAmount=" + minAmount +
                ", lastTransactionDate=" + lastTransactionDate +
                '}';
    }
}