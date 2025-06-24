package com.PFM.CD.entity;

import com.PFM.CD.entity.enums.AccountStatus;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 账户实体类
 * 对应数据库accounts表
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class Account {

    private int accountId;
    private int userId;
    private String accountName;
    private BigDecimal balance;
    private AccountStatus status;

    /**
     * 默认构造函数
     */
    public Account() {
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }

    /**
     * 带参数构造函数
     */
    public Account(int userId, String accountName) {
        this.userId = userId;
        this.accountName = accountName;
        this.balance = BigDecimal.ZERO;
        this.status = AccountStatus.ACTIVE;
    }

    /**
     * 完整构造函数
     */
    public Account(int accountId, int userId, String accountName, BigDecimal balance, AccountStatus status) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountName = accountName;
        this.balance = balance;
        this.status = status;
    }

    // Getters and Setters

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * 判断账户是否可用于交易
     * @return 如果账户状态为ACTIVE返回true，否则返回false
     */
    public boolean isAvailableForTransaction() {
        return status == AccountStatus.ACTIVE;
    }

    /**
     * 账户余额增加
     * @param amount 增加金额
     */
    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("信用金额不能为负");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * 账户余额减少
     * @param amount 减少金额
     */
    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("借记金额不能为负");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("余额不足");
        }
        this.balance = this.balance.subtract(amount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId &&
                userId == account.userId &&
                Objects.equals(accountName, account.accountName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, accountName);
    }

    /**
     * 用于在ComboBox等UI控件中显示
     */
    public String getDisplayString() {
        return accountName + " (" + balance + " 元)";
    }
}