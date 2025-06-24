package com.PFM.CD.dao.interfaces;

import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.enums.AccountStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * 账户数据访问接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface AccountDao extends BaseDao<Account, Integer> {

    /**
     * 查找用户的所有账户
     *
     * @param userId 用户ID
     * @return 账户列表
     */
    List<Account> findByUserId(int userId) throws SQLException;

    /**
     * 查找指定状态的用户账户
     *
     * @param userId 用户ID
     * @param status 账户状态
     * @return 账户列表
     */
    List<Account> findByUserIdAndStatus(int userId, AccountStatus status) throws SQLException;

    /**
     * 通过账户名查找用户账户
     *
     * @param userId 用户ID
     * @param accountName 账户名
     * @return 找到的账户，如果不存在返回null
     */
    Account findByUserIdAndName(int userId, String accountName) throws SQLException;

    /**
     * 更新账户余额
     *
     * @param accountId 账户ID
     * @param newBalance 新余额
     * @return 是否成功
     */
    boolean updateBalance(int accountId, BigDecimal newBalance) throws SQLException;

    /**
     * 更新账户状态
     *
     * @param accountId 账户ID
     * @param newStatus 新状态
     * @return 是否成功
     */
    boolean updateStatus(int accountId, AccountStatus newStatus) throws SQLException;

    /**
     * 计算用户总资产
     *
     * @param userId 用户ID
     * @return 总资产
     */
    BigDecimal calculateTotalBalance(int userId) throws SQLException;

    /**
     * 检查账户名在用户范围内是否已存在
     *
     * @param userId 用户ID
     * @param accountName 账户名
     * @return 如果存在返回true，否则返回false
     */
    boolean isAccountNameExists(int userId, String accountName) throws SQLException;
}