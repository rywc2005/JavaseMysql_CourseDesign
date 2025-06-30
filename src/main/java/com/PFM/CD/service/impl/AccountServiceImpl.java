package com.PFM.CD.service.impl;

import com.PFM.CD.dao.impl.CategoryDaoImpl;
import com.PFM.CD.dao.interfaces.AccountDao;
import com.PFM.CD.dao.interfaces.TransactionDao;
import com.PFM.CD.dao.interfaces.CategoryDao;
import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.AccountStatus;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.service.exception.InsufficientBalanceException;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.AccountService;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * 账户服务实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    /**
     * 构造函数
     *
     * @param accountDao 账户DAO接口
     * @param transactionDao 交易DAO接口
     */

    public AccountServiceImpl(AccountDao accountDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public Account createAccount(int userId, String accountName, BigDecimal initialBalance) throws ServiceException {
        try {
            // 检查账户名称是否已存在
            if (accountDao.isAccountNameExists(userId, accountName)) {
                throw new ServiceException("账户名称已存在: " + accountName);
            }

            // 初始余额不能为负数
            if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("初始余额不能为负数");
            }

            // 创建新账户对象
            Account account = new Account();
            account.setUserId(userId);
            account.setAccountName(accountName);
            account.setBalance(initialBalance);
            account.setStatus(AccountStatus.ACTIVE);

            // 保存账户
            Connection conn = null;
            try {
                conn = accountDao.getConnection();
                accountDao.beginTransaction(conn);

                boolean success = accountDao.save(account);
                if (!success) {
                    throw new ServiceException("创建账户失败");
                }

//                // 如果初始余额大于0，创建一个初始存款交易
//                if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
//                    Transaction transaction = new Transaction();
//                    transaction.setUserId(userId);
//                    transaction.setDestinationAccountId(account.getAccountId());
//                    transaction.setCategoryId(1); // 假设1是"初始余额"分类
//                    transaction.setAmount(initialBalance);
//                    transaction.setTransactionType(TransactionType.INCOME);
//                    transaction.setTransactionDate(LocalDate.now());
//                    transaction.setDescription("初始余额");
//
//                    transactionDao.save(transaction);
//                }

                accountDao.commitTransaction(conn);
                return account;
            } catch (SQLException e) {
                if (conn != null) {
                    accountDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                accountDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("创建账户过程中发生数据库错误", e);
        }
    }

    @Override
    public Account getAccountById(int accountId) throws ServiceException {
        try {
            Account account = accountDao.findById(accountId);
            if (account == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }
            return account;
        } catch (SQLException e) {
            throw new ServiceException("获取账户信息过程中发生数据库错误", e);
        }
    }

    @Override
    public Account updateAccount(Account account) throws ServiceException {
        try {
            // 检查账户是否存在
            Account existingAccount = accountDao.findById(account.getAccountId());
            if (existingAccount == null) {
                throw new ServiceException("账户不存在: " + account.getAccountId());
            }

            // 检查账户名称是否已被同一用户的其他账户使用
            Account accountWithSameName = accountDao.findByUserIdAndName(account.getUserId(), account.getAccountName());
            if (accountWithSameName != null && accountWithSameName.getAccountId() != account.getAccountId()) {
                throw new ServiceException("账户名称已存在: " + account.getAccountName());
            }

            // 保留原始余额
            account.setBalance(existingAccount.getBalance());

            // 更新账户信息
            boolean success = accountDao.update(account);
            if (!success) {
                throw new ServiceException("更新账户信息失败");
            }

            return account;
        } catch (SQLException e) {
            throw new ServiceException("更新账户信息过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean updateAccountStatus(int accountId, AccountStatus status) throws ServiceException {
        try {
            // 检查账户是否存在
            Account existingAccount = accountDao.findById(accountId);
            if (existingAccount == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }

            // 如果要停用账户，但余额不为零，则不允许
            if (status == AccountStatus.INACTIVE && existingAccount.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new ServiceException("账户余额不为零，无法停用");
            }

            return accountDao.updateStatus(accountId, status);
        } catch (SQLException e) {
            throw new ServiceException("更新账户状态过程中发生数据库错误", e);
        }
    }
    /**
     * 删除账户
     *
     * @param accountId 账户ID
     * @param transferAccountId 转移资金的目标账户ID（如果有余额）
     * @return 是否删除成功
     * @throws ServiceException 如果删除过程中发生错误
     */
    @Override
    public boolean deleteAccount(int accountId, Integer transferAccountId) throws ServiceException {
        try {
            // 检查账户是否存在
            Account account = accountDao.findById(accountId);
            if (account == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }

            // 如果账户余额不为零，需要转移资金
            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                if (transferAccountId == null) {
                    throw new ServiceException("账户余额不为零，需要指定转移资金的目标账户");
                }

                // 检查目标账户是否存在
                Account targetAccount = accountDao.findById(transferAccountId);
                if (targetAccount == null) {
                    throw new ServiceException("目标账户不存在: " + transferAccountId);
                }

                // 检查目标账户是否属于同一用户
                if (targetAccount.getUserId() != account.getUserId()) {
                    throw new ServiceException("目标账户不属于同一用户");
                }

                // 检查目标账户是否活跃
                if (targetAccount.getStatus() != AccountStatus.ACTIVE) {
                    throw new ServiceException("目标账户不是活跃状态");
                }

                // 转移资金 - 使用两步操作而非转账
                Connection conn = null;
                try {
                    conn = accountDao.getConnection();
                    accountDao.beginTransaction(conn);

                    BigDecimal transferAmount = account.getBalance();

                    // 步骤1：从源账户支出资金
                    Transaction expenseTransaction = new Transaction();
                    expenseTransaction.setUserId(account.getUserId());
                    expenseTransaction.setSourceAccountId(accountId);
                    expenseTransaction.setCategoryId(findOrCreateClosureCategory(account.getUserId())); // 使用"账户关闭"分类
                    expenseTransaction.setAmount(transferAmount);
                    expenseTransaction.setTransactionType(TransactionType.EXPENSE);
                    expenseTransaction.setTransactionDate(LocalDate.now());
                    expenseTransaction.setDescription("账户关闭 - 资金转出");

                    transactionDao.save(expenseTransaction);

                    // 步骤2：向目标账户存入资金
                    Transaction incomeTransaction = new Transaction();
                    incomeTransaction.setUserId(account.getUserId());
                    incomeTransaction.setDestinationAccountId(transferAccountId);
                    incomeTransaction.setCategoryId(findOrCreateClosureCategory(account.getUserId())); // 使用"账户关闭"分类
                    incomeTransaction.setAmount(transferAmount);
                    incomeTransaction.setTransactionType(TransactionType.INCOME);
                    incomeTransaction.setTransactionDate(LocalDate.now());
                    incomeTransaction.setDescription("账户关闭 - 资金转入");

                    transactionDao.save(incomeTransaction);

                    // 注意：触发器会自动更新两个账户的余额，不需要手动更新

                    // 删除账户
                    boolean deleted = accountDao.delete(accountId);

                    accountDao.commitTransaction(conn);
                    return deleted;
                } catch (SQLException e) {
                    if (conn != null) {
                        accountDao.rollbackTransaction(conn);
                    }
                    throw e;
                } finally {
                    accountDao.closeConnection(conn);
                }
            } else {
                // 账户余额为零，直接删除
                return accountDao.delete(accountId);
            }
        } catch (SQLException e) {
            throw new ServiceException("删除账户过程中发生数据库错误", e);
        }
    }

    /**
     * 查找或创建账户关闭分类
     *
     * @param userId 用户ID
     * @return 分类ID
     * @throws SQLException 如果数据库操作失败
     */
    private int findOrCreateClosureCategory(int userId) throws SQLException {
        // 尝试查找"账户关闭"分类（收入和支出都可以用同一个分类）
        CategoryDao categoryDao = new CategoryDaoImpl();
        Category closureCategory = categoryDao.findByNameAndType("账户关闭", CategoryType.EXPENSE);

        // 如果不存在，创建新分类
        if (closureCategory == null) {
            closureCategory = new Category();
            closureCategory.setCategoryName("账户关闭");
            closureCategory.setCategoryType(CategoryType.EXPENSE);
            categoryDao.save(closureCategory);

            // 创建对应的收入分类
            Category incomeClosureCategory = new Category();
            incomeClosureCategory.setCategoryName("账户关闭");
            incomeClosureCategory.setCategoryType(CategoryType.INCOME);
            categoryDao.save(incomeClosureCategory);

            return closureCategory.getCategoryId();
        }

        return closureCategory.getCategoryId();
    }

    @Override
    public List<Account> getUserAccounts(int userId) throws ServiceException {
        try {
            return accountDao.findByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("获取用户账户过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Account> getActiveAccounts(int userId) throws ServiceException {
        try {
            return accountDao.findByUserIdAndStatus(userId, AccountStatus.ACTIVE);
        } catch (SQLException e) {
            throw new ServiceException("获取用户活跃账户过程中发生数据库错误", e);
        }
    }
    @Override
    public BigDecimal calculateTotalBalance(int userId) throws ServiceException {
        try {
            return accountDao.calculateTotalBalance(userId);
        } catch (SQLException e) {
            throw new ServiceException("计算总资产过程中发生数据库错误", e);
        }
    }
    @Override
    public boolean isAccountNameAvailable(int userId, String accountName) throws ServiceException {
        try {
            return !accountDao.isAccountNameExists(userId, accountName);
        } catch (SQLException e) {
            throw new ServiceException("检查账户名称可用性过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Account> getUserActiveAccounts(int currentUserId) {
        try {
            return accountDao.findByUserIdAndStatus(currentUserId, AccountStatus.ACTIVE);
        } catch (SQLException e) {
            try {
                throw new ServiceException("获取用户活跃账户过程中发生数据库错误", e);
            } catch (ServiceException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}