package com.PFM.CD.service.dto;

import com.PFM.CD.entity.enums.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账户摘要数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class AccountSummaryDto {

    private int accountId;
    private String accountName;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDate lastActivityDate;
    private int transactionCount;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    /**
     * 默认构造函数
     */
    public AccountSummaryDto() {
    }

    /**
     * 获取账户ID
     *
     * @return 账户ID
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * 设置账户ID
     *
     * @param accountId 账户ID
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    /**
     * 获取账户名称
     *
     * @return 账户名称
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * 设置账户名称
     *
     * @param accountName 账户名称
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * 获取余额
     *
     * @return 余额
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * 设置余额
     *
     * @param balance 余额
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public AccountStatus getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * 获取最后活动日期
     *
     * @return 最后活动日期
     */
    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    /**
     * 设置最后活动日期
     *
     * @param lastActivityDate 最后活动日期
     */
    public void setLastActivityDate(LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
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
     * 获取总收入
     *
     * @return 总收入
     */
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    /**
     * 设置总收入
     *
     * @param totalIncome 总收入
     */
    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    /**
     * 获取总支出
     *
     * @return 总支出
     */
    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    /**
     * 设置总支出
     *
     * @param totalExpense 总支出
     */
    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    /**
     * 获取净流入（总收入减总支出）
     *
     * @return 净流入
     */
    public BigDecimal getNetFlow() {
        if (totalIncome != null && totalExpense != null) {
            return totalIncome.subtract(totalExpense);
        }
        return BigDecimal.ZERO;
    }
}