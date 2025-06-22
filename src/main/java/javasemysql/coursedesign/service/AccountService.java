package javasemysql.coursedesign.service;

import javasemysql.coursedesign.dto.AccountQueryParam;
import javasemysql.coursedesign.model.Account;

import java.util.List;

/**
 * 账户服务接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface AccountService {

    /**
     * 根据用户ID获取账户列表
     *
     * @param userId 用户ID
     * @return 账户列表
     */
    List<Account> getAccountsByUserId(int userId);

    /**
     * 根据账户ID获取账户信息
     *
     * @param accountId 账户ID
     * @return 账户对象，如果不存在则返回null
     */
    Account getAccountById(int accountId);

    /**
     * 添加账户
     *
     * @param account 账户对象
     * @return 是否添加成功
     */
    boolean addAccount(Account account);

    /**
     * 更新账户
     *
     * @param account 账户对象
     * @return 是否更新成功
     */
    boolean updateAccount(Account account);

    /**
     * 删除账户
     *
     * @param accountId 账户ID
     * @return 是否删除成功
     */
    boolean deleteAccount(int accountId);

    /**
     * 获取用户的总资产
     *
     * @param userId 用户ID
     * @return 总资产金额
     */
    double getTotalBalance(int userId);

    /**
     * 根据条件查询账户
     *
     * @param param 查询参数
     * @return 账户列表
     */
    List<Account> queryAccounts(AccountQueryParam param);

    /**
     * 更新账户余额
     *
     * @param accountId 账户ID
     * @param amount 变动金额（正数为增加，负数为减少）
     * @return 是否更新成功
     */
    boolean updateAccountBalance(int accountId, double amount);

    /**
     * 检查账户是否存在
     *
     * @param accountId 账户ID
     * @return 是否存在
     */
    boolean accountExists(int accountId);

    /**
     * 获取默认账户（如果存在）
     *
     * @param userId 用户ID
     * @return 默认账户对象，如果不存在则返回null
     */
    Account getDefaultAccount(int userId);
}