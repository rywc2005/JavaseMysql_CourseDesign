package com.PFM.CD.service.dto;

import com.PFM.CD.entity.enums.AccountStatus;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 账户数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class AccountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int accountId;
    private int userId;
    private String accountName;
    private BigDecimal balance;
    private AccountStatus status;

    /**
     * 默认构造函数
     */
    public AccountDto() {
    }

    /**
     * 完整构造函数
     *
     * @param accountId 账户ID
     * @param userId 用户ID
     * @param accountName 账户名称
     * @param balance 余额
     * @param status 状态
     */
    public AccountDto(int accountId, int userId, String accountName, BigDecimal balance, AccountStatus status) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountName = accountName;
        this.balance = balance;
        this.status = status;
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
     * 检查账户是否活跃
     *
     * @return 如果账户活跃返回true，否则返回false
     */
    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", status=" + status +
                '}';
    }
}