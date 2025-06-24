package com.PFM.CD.service.dto;

import java.time.LocalDate;

/**
 * 用户数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class UserDto {

    private int userId;
    private String username;
    private String email;
    private LocalDate createdAt;
    private int accountCount;
    private int transactionCount;

    /**
     * 默认构造函数
     */
    public UserDto() {
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
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取创建日期
     *
     * @return 创建日期
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建日期
     *
     * @param createdAt 创建日期
     */
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取账户数量
     *
     * @return 账户数量
     */
    public int getAccountCount() {
        return accountCount;
    }

    /**
     * 设置账户数量
     *
     * @param accountCount 账户数量
     */
    public void setAccountCount(int accountCount) {
        this.accountCount = accountCount;
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
}