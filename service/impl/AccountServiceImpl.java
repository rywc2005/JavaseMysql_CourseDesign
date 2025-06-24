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

                // 如果初始余额大于0，创建一个初始存款交易
                if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
                    Transaction transaction = new Transaction();
                    transaction.setUserId(userId);
                    transaction.setDestinationAccountId(account.getAccountId());
                    transaction.setCategoryId(1); // 假设1是"初始余额"分类
                    transaction.setAmount(initialBalance);
                    transaction.setTransactionType(TransactionType.INCOME);
                    transaction.setTransactionDate(LocalDate.now());
                    transaction.setDescription("初始余额");

                    transactionDao.save(transaction);
                }

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
    public Account adjustBalance(int accountId, BigDecimal newBalance) throws ServiceException {
        try {
            // 检查账户是否存在
            Account account = accountDao.findById(accountId);
            if (account == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }

            // 检查账户是否活跃
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能调整活跃账户的余额");
            }

            // 余额不能为负数
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("余额不能为负数");
            }

            // 计算差额
            BigDecimal difference = newBalance.subtract(account.getBalance());

            Connection conn = null;
            try {
                conn = accountDao.getConnection();
                accountDao.beginTransaction(conn);

                // 更新账户余额
                boolean success = accountDao.updateBalance(accountId, newBalance);
                if (!success) {
                    throw new ServiceException("调整余额失败");
                }

                // 创建调整交易记录
                Transaction transaction = new Transaction();
                transaction.setUserId(account.getUserId());

                if (difference.compareTo(BigDecimal.ZERO) > 0) {
                    // 增加余额
                    transaction.setDestinationAccountId(accountId);
                    transaction.setCategoryId(3); // 假设3是"余额调整"分类
                    transaction.setAmount(difference);
                    transaction.setTransactionType(TransactionType.INCOME);
                } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
                    // 减少余额
                    transaction.setSourceAccountId(accountId);
                    transaction.setCategoryId(3); // 假设3是"余额调整"分类
                    transaction.setAmount(difference.abs());
                    transaction.setTransactionType(TransactionType.EXPENSE);
                } else {
                    // 余额没有变化，不创建交易记录
                    accountDao.commitTransaction(conn);
                    account.setBalance(newBalance);
                    return account;
                }

                transaction.setTransactionDate(LocalDate.now());
                transaction.setDescription("余额调整");

                transactionDao.save(transaction);

                accountDao.commitTransaction(conn);

                account.setBalance(newBalance);
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
            throw new ServiceException("调整余额过程中发生数据库错误", e);
        }
    }

    @Override
    public Account deposit(int accountId, BigDecimal amount) throws ServiceException {
        try {
            // 检查账户是否存在
            Account account = accountDao.findById(accountId);
            if (account == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }

            // 检查账户是否活跃
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能向活跃账户存款");
            }

            // 金额必须为正数
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("存款金额必须为正数");
            }

            // 计算新余额
            BigDecimal newBalance = account.getBalance().add(amount);

            // 更新账户余额
            boolean success = accountDao.updateBalance(accountId, newBalance);
            if (!success) {
                throw new ServiceException("存款失败");
            }

            account.setBalance(newBalance);
            return account;
        } catch (SQLException e) {
            throw new ServiceException("存款过程中发生数据库错误", e);
        }
    }

    @Override
    public Account withdraw(int accountId, BigDecimal amount)
            throws InsufficientBalanceException, ServiceException {
        try {
            // 检查账户是否存在
            Account account = accountDao.findById(accountId);
            if (account == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }

            // 检查账户是否活跃
            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能从活跃账户提款");
            }

            // 金额必须为正数
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("提款金额必须为正数");
            }

            // 检查余额是否充足
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("余额不足", account.getBalance(), amount);
            }

            // 计算新余额
            BigDecimal newBalance = account.getBalance().subtract(amount);

            // 更新账户余额
            boolean success = accountDao.updateBalance(accountId, newBalance);
            if (!success) {
                throw new ServiceException("提款失败");
            }

            account.setBalance(newBalance);
            return account;
        } catch (SQLException e) {
            throw new ServiceException("提款过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean transfer(int fromAccountId, int toAccountId, BigDecimal amount)
            throws InsufficientBalanceException, ServiceException {
        try {
            // 检查源账户是否存在
            Account fromAccount = accountDao.findById(fromAccountId);
            if (fromAccount == null) {
                throw new ServiceException("源账户不存在: " + fromAccountId);
            }

            // 检查目标账户是否存在
            Account toAccount = accountDao.findById(toAccountId);
            if (toAccount == null) {
                throw new ServiceException("目标账户不存在: " + toAccountId);
            }

            // 检查两个账户是否都活跃
            if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能从活跃账户转出");
            }

            if (toAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能向活跃账户转入");
            }

            // 金额必须为正数
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("转账金额必须为正数");
            }

            // 检查余额是否充足
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("余额不足", fromAccount.getBalance(), amount);
            }

            // 计算新余额
            BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
            BigDecimal newToBalance = toAccount.getBalance().add(amount);

            Connection conn = null;
            try {
                conn = accountDao.getConnection();
                accountDao.beginTransaction(conn);

                // 更新源账户余额
                accountDao.updateBalance(fromAccountId, newFromBalance);

                // 更新目标账户余额
                accountDao.updateBalance(toAccountId, newToBalance);

                accountDao.commitTransaction(conn);
                return true;
            } catch (SQLException e) {
                if (conn != null) {
                    accountDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                accountDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("转账过程中发生数据库错误", e);
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
}