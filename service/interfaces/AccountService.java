package com.PFM.CD.service.interfaces;

import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.enums.AccountStatus;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务接口，提供账户相关的业务逻辑
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface AccountService {

    /**
     * 创建新账户
     *
     * @param userId 用户ID
     * @param accountName 账户名称
     * @param initialBalance 初始余额
     * @return 创建的账户
     * @throws ServiceException 如果创建过程中发生错误
     */
    Account createAccount(int userId, String accountName, BigDecimal initialBalance) throws ServiceException;

    /**
     * 获取账户信息
     *
     * @param accountId 账户ID
     * @return 账户信息
     * @throws ServiceException 如果获取过程中发生错误
     */
    Account getAccountById(int accountId) throws ServiceException;

    /**
     * 更新账户信息
     *
     * @param account 需要更新的账户信息
     * @return 更新后的账户
     * @throws ServiceException 如果更新过程中发生错误
     */
    Account updateAccount(Account account) throws ServiceException;

    /**
     * 更新账户状态
     *
     * @param accountId 账户ID
     * @param status 新状态
     * @return 是否更新成功
     * @throws ServiceException 如果更新过程中发生错误
     */
    boolean updateAccountStatus(int accountId, AccountStatus status) throws ServiceException;

    /**
     * 删除账户
     *
     * @param accountId 账户ID
     * @param transferAccountId 转移资金的目标账户ID（如果有余额）
     * @return 是否删除成功
     * @throws ServiceException 如果删除过程中发生错误
     */
    boolean deleteAccount(int accountId, Integer transferAccountId) throws ServiceException;

    /**
     * 获取用户的所有账户
     *
     * @param userId 用户ID
     * @return 账户列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Account> getUserAccounts(int userId) throws ServiceException;

    /**
     * 获取用户的活跃账户
     *
     * @param userId 用户ID
     * @return 活跃账户列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Account> getActiveAccounts(int userId) throws ServiceException;

    /**
     * 调整账户余额（直接设置）
     *
     * @param accountId 账户ID
     * @param newBalance 新余额
     * @return 更新后的账户
     * @throws ServiceException 如果调整过程中发生错误
     */
    Account adjustBalance(int accountId, BigDecimal newBalance) throws ServiceException;

    /**
     * 账户充值（增加余额）
     *
     * @param accountId 账户ID
     * @param amount 增加金额
     * @return 更新后的账户
     * @throws ServiceException 如果充值过程中发生错误
     */
    Account deposit(int accountId, BigDecimal amount) throws ServiceException;

    /**
     * 账户提款（减少余额）
     *
     * @param accountId 账户ID
     * @param amount 减少金额
     * @return 更新后的账户
     * @throws InsufficientBalanceException 如果余额不足
     * @throws ServiceException 如果提款过程中发生其他错误
     */
    Account withdraw(int accountId, BigDecimal amount)
            throws InsufficientBalanceException, ServiceException;

    /**
     * 账户间转账
     *
     * @param fromAccountId 源账户ID
     * @param toAccountId 目标账户ID
     * @param amount 转账金额
     * @return 是否转账成功
     * @throws InsufficientBalanceException 如果源账户余额不足
     * @throws ServiceException 如果转账过程中发生其他错误
     */
    boolean transfer(int fromAccountId, int toAccountId, BigDecimal amount)
            throws InsufficientBalanceException, ServiceException;

    /**
     * 计算用户总资产
     *
     * @param userId 用户ID
     * @return 总资产
     * @throws ServiceException 如果计算过程中发生错误
     */
    BigDecimal calculateTotalBalance(int userId) throws ServiceException;

    /**
     * 检查账户名称是否可用
     *
     * @param userId 用户ID
     * @param accountName 账户名称
     * @return 如果可用返回true，否则返回false
     * @throws ServiceException 如果检查过程中发生错误
     */
    boolean isAccountNameAvailable(int userId, String accountName) throws ServiceException;
}