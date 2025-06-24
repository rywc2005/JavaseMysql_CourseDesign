package com.PFM.CD.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账户余额趋势数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class AccountBalanceTrend {

    private LocalDate date;
    private int accountId;
    private String accountName;
    private BigDecimal balance;

    /**
     * 构造函数
     *
     * @param date 日期
     * @param accountId 账户ID
     * @param accountName 账户名称
     * @param balance 余额
     */
    public AccountBalanceTrend(LocalDate date, int accountId, String accountName, BigDecimal balance) {
        this.date = date;
        this.accountId = accountId;
        this.accountName = accountName;
        this.balance = balance;
    }

    /**
     * 获取日期
     *
     * @return 日期
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * 设置日期
     *
     * @param date 日期
     */
    public void setDate(LocalDate date) {
        this.date = date;
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
}