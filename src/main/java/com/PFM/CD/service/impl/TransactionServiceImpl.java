package com.PFM.CD.service.impl;

import com.PFM.CD.dao.interfaces.AccountDao;
import com.PFM.CD.dao.interfaces.BudgetCategoryDao;
import com.PFM.CD.dao.interfaces.CategoryDao;
import com.PFM.CD.dao.interfaces.TransactionDao;
import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.enums.AccountStatus;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.service.exception.InsufficientBalanceException;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.TransactionService;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 交易服务实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;
    private final AccountDao accountDao;
    private final CategoryDao categoryDao;
    private final BudgetCategoryDao budgetCategoryDao;

    /**
     * 构造函数
     *
     * @param transactionDao 交易DAO接口
     * @param accountDao 账户DAO接口
     * @param categoryDao 分类DAO接口
     * @param budgetCategoryDao 预算分类DAO接口
     */
    public TransactionServiceImpl(TransactionDao transactionDao, AccountDao accountDao,
                                  CategoryDao categoryDao, BudgetCategoryDao budgetCategoryDao) {
        this.transactionDao = transactionDao;
        this.accountDao = accountDao;
        this.categoryDao = categoryDao;
        this.budgetCategoryDao = budgetCategoryDao;
    }

    @Override
    public Transaction recordIncome(int userId, int accountId, int categoryId, BigDecimal amount,
                                    LocalDate transactionDate, String description) throws ServiceException {
        try {
            // 验证账户
            Account account = accountDao.findById(accountId);
            if (account == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }

            if (account.getUserId() != userId) {
                throw new ServiceException("账户不属于当前用户");
            }

            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能向活跃账户存入收入");
            }

            // 验证分类
            Category category = categoryDao.findById(categoryId);
            if (category == null) {
                throw new ServiceException("分类不存在: " + categoryId);
            }

            if (category.getCategoryType() != TransactionType.INCOME.toCategoryType()) {
                throw new ServiceException("必须选择收入类型的分类");
            }

            // 验证金额
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("收入金额必须为正数");
            }

            // 验证日期
            if (transactionDate == null) {
                transactionDate = LocalDate.now();
            }

            if (transactionDate.isAfter(LocalDate.now())) {
                throw new ServiceException("交易日期不能是未来日期");
            }

            // 创建交易记录
            Transaction transaction = new Transaction();
            transaction.setUserId(userId);
            transaction.setDestinationAccountId(accountId);
            transaction.setCategoryId(categoryId);
            transaction.setAmount(amount);
            transaction.setTransactionType(TransactionType.INCOME);
            transaction.setTransactionDate(transactionDate);
            transaction.setDescription(description);

            Connection conn = null;
            try {
                conn = transactionDao.getConnection();
                transactionDao.beginTransaction(conn);

                // 保存交易记录
                boolean saved = transactionDao.save(transaction);
                if (!saved) {
                    throw new ServiceException("保存交易记录失败");
                }

                // 更新账户余额
                BigDecimal newBalance = account.getBalance().add(amount);
                accountDao.updateBalance(accountId, newBalance);

                transactionDao.commitTransaction(conn);
                return transaction;
            } catch (SQLException e) {
                if (conn != null) {
                    transactionDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                transactionDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("记录收入过程中发生数据库错误", e);
        }
    }

    @Override
    public Transaction recordExpense(int userId, int accountId, int categoryId, BigDecimal amount,
                                     LocalDate transactionDate, String description)
            throws InsufficientBalanceException, ServiceException {
        try {
            // 验证账户
            Account account = accountDao.findById(accountId);
            if (account == null) {
                throw new ServiceException("账户不存在: " + accountId);
            }

            if (account.getUserId() != userId) {
                throw new ServiceException("账户不属于当前用户");
            }

            if (account.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能从活跃账户支出");
            }

            // 验证分类
            Category category = categoryDao.findById(categoryId);
            if (category == null) {
                throw new ServiceException("分类不存在: " + categoryId);
            }

            if (category.getCategoryType() != TransactionType.EXPENSE.toCategoryType()) {
                throw new ServiceException("必须选择支出类型的分类");
            }

            // 验证金额
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("支出金额必须为正数");
            }

            // 检查余额是否充足
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("余额不足", account.getBalance(), amount);
            }

            // 验证日期
            if (transactionDate == null) {
                transactionDate = LocalDate.now();
            }

            if (transactionDate.isAfter(LocalDate.now())) {
                throw new ServiceException("交易日期不能是未来日期");
            }

            // 创建交易记录
            Transaction transaction = new Transaction();
            transaction.setUserId(userId);
            transaction.setSourceAccountId(accountId);
            transaction.setCategoryId(categoryId);
            transaction.setAmount(amount);
            transaction.setTransactionType(TransactionType.EXPENSE);
            transaction.setTransactionDate(transactionDate);
            transaction.setDescription(description);

            Connection conn = null;
            try {
                conn = transactionDao.getConnection();
                transactionDao.beginTransaction(conn);

                // 保存交易记录
                boolean saved = transactionDao.save(transaction);
                if (!saved) {
                    throw new ServiceException("保存交易记录失败");
                }

                // 更新账户余额
                BigDecimal newBalance = account.getBalance().subtract(amount);
                accountDao.updateBalance(accountId, newBalance);

                // 更新预算使用情况
                budgetCategoryDao.updateBudgetCategorySpentAmountByTransaction(
                        userId, categoryId, amount, transactionDate);

                transactionDao.commitTransaction(conn);
                return transaction;
            } catch (SQLException e) {
                if (conn != null) {
                    transactionDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                transactionDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("记录支出过程中发生数据库错误", e);
        }
    }
/*
    @Override
    public Transaction recordTransfer(int userId, int fromAccountId, int toAccountId, BigDecimal amount,
                                      LocalDate transactionDate, String description)
            throws InsufficientBalanceException, ServiceException {
        try {
            // 验证源账户
            Account fromAccount = accountDao.findById(fromAccountId);
            if (fromAccount == null) {
                throw new ServiceException("源账户不存在: " + fromAccountId);
            }

            if (fromAccount.getUserId() != userId) {
                throw new ServiceException("源账户不属于当前用户");
            }

            if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能从活跃账户转出");
            }

            // 验证目标账户
            Account toAccount = accountDao.findById(toAccountId);
            if (toAccount == null) {
                throw new ServiceException("目标账户不存在: " + toAccountId);
            }

            if (toAccount.getUserId() != userId) {
                throw new ServiceException("目标账户不属于当前用户");
            }

            if (toAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new ServiceException("只能向活跃账户转入");
            }

            // 源账户和目标账户不能相同
            if (fromAccountId == toAccountId) {
                throw new ServiceException("源账户和目标账户不能相同");
            }

            // 验证金额
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("转账金额必须为正数");
            }

            // 检查余额是否充足
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("余额不足", fromAccount.getBalance(), amount);
            }

            // 验证日期
            if (transactionDate == null) {
                transactionDate = LocalDate.now();
            }

            if (transactionDate.isAfter(LocalDate.now())) {
                throw new ServiceException("交易日期不能是未来日期");
            }

            // 创建交易记录
            Transaction transaction = new Transaction();
            transaction.setUserId(userId);
            transaction.setSourceAccountId(fromAccountId);
            transaction.setDestinationAccountId(toAccountId);
            transaction.setCategoryId(2); // 假设2是"账户转账"分类
            transaction.setAmount(amount);
            transaction.setTransactionType(TransactionType.TRANSFER);
            transaction.setTransactionDate(transactionDate);
            transaction.setDescription(description);

            Connection conn = null;
            try {
                conn = transactionDao.getConnection();
                transactionDao.beginTransaction(conn);

                // 保存交易记录
                boolean saved = transactionDao.save(transaction);
                if (!saved) {
                    throw new ServiceException("保存交易记录失败");
                }

                // 更新源账户余额
                BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
                accountDao.updateBalance(fromAccountId, newFromBalance);

                // 更新目标账户余额
                BigDecimal newToBalance = toAccount.getBalance().add(amount);
                accountDao.updateBalance(toAccountId, newToBalance);

                transactionDao.commitTransaction(conn);
                return transaction;
            } catch (SQLException e) {
                if (conn != null) {
                    transactionDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                transactionDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("记录转账过程中发生数据库错误", e);
        }
    }
*/
    @Override
    public Transaction getTransactionById(int transactionId) throws ServiceException {
        try {
            Transaction transaction = transactionDao.findById(transactionId);
            if (transaction == null) {
                throw new ServiceException("交易记录不存在: " + transactionId);
            }
            return transaction;
        } catch (SQLException e) {
            throw new ServiceException("获取交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) throws ServiceException {
        try {
            // 检查交易记录是否存在
            Transaction existingTransaction = transactionDao.findById(transaction.getTransactionId());
            if (existingTransaction == null) {
                throw new ServiceException("交易记录不存在: " + transaction.getTransactionId());
            }

            // 交易类型不能修改
            if (existingTransaction.getTransactionType() != transaction.getTransactionType()) {
                throw new ServiceException("不能修改交易类型");
            }

            // 检查分类是否与交易类型匹配
            Category category = categoryDao.findById(transaction.getCategoryId());
            if (category == null) {
                throw new ServiceException("分类不存在: " + transaction.getCategoryId());
            }
/*
            if (category.getCategoryType() != transaction.getTransactionType().toCategoryType() &&
                    transaction.getTransactionType() != TransactionType.TRANSFER) {
                throw new ServiceException("分类类型必须与交易类型匹配");
            }

 */

            // 更新交易记录
            boolean success = transactionDao.update(transaction);
            if (!success) {
                throw new ServiceException("更新交易记录失败");
            }

            return transaction;
        } catch (SQLException e) {
            throw new ServiceException("更新交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean deleteTransaction(int transactionId, boolean reverseAccountBalances) throws ServiceException {
        try {
            // 检查交易记录是否存在
            Transaction transaction = transactionDao.findById(transactionId);
            if (transaction == null) {
                throw new ServiceException("交易记录不存在: " + transactionId);
            }

            Connection conn = null;
            try {
                conn = transactionDao.getConnection();
                transactionDao.beginTransaction(conn);

                // 如果需要恢复账户余额
                if (reverseAccountBalances) {
                    switch (transaction.getTransactionType()) {
                        case INCOME:
                            // 减少目标账户余额
                            Account destAccount = accountDao.findById(transaction.getDestinationAccountId());
                            if (destAccount != null && destAccount.getStatus() == AccountStatus.ACTIVE) {
                                BigDecimal newBalance = destAccount.getBalance().subtract(transaction.getAmount());
                                if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                                    throw new ServiceException("恢复账户余额后将导致余额为负数");
                                }
                                accountDao.updateBalance(transaction.getDestinationAccountId(), newBalance);
                            }
                            break;

                        case EXPENSE:
                            // 增加源账户余额
                            Account sourceAccount = accountDao.findById(transaction.getSourceAccountId());
                            if (sourceAccount != null && sourceAccount.getStatus() == AccountStatus.ACTIVE) {
                                BigDecimal newBalance = sourceAccount.getBalance().add(transaction.getAmount());
                                accountDao.updateBalance(transaction.getSourceAccountId(), newBalance);

                                // 恢复预算使用情况
                                budgetCategoryDao.updateBudgetCategorySpentAmountByTransaction(
                                        transaction.getUserId(), transaction.getCategoryId(),
                                        transaction.getAmount().negate(), transaction.getTransactionDate());
                            }
                            break;
/*
                        case TRANSFER:
                            // 增加源账户余额
                            Account fromAccount = accountDao.findById(transaction.getSourceAccountId());
                            if (fromAccount != null && fromAccount.getStatus() == AccountStatus.ACTIVE) {
                                BigDecimal newFromBalance = fromAccount.getBalance().add(transaction.getAmount());
                                accountDao.updateBalance(transaction.getSourceAccountId(), newFromBalance);
                            }

                            // 减少目标账户余额
                            Account toAccount = accountDao.findById(transaction.getDestinationAccountId());
                            if (toAccount != null && toAccount.getStatus() == AccountStatus.ACTIVE) {
                                BigDecimal newToBalance = toAccount.getBalance().subtract(transaction.getAmount());
                                if (newToBalance.compareTo(BigDecimal.ZERO) < 0) {
                                    throw new ServiceException("恢复账户余额后将导致目标账户余额为负数");
                                }
                                accountDao.updateBalance(transaction.getDestinationAccountId(), newToBalance);
                            }
                            break;
                            */
                    }
                }

                // 删除交易记录
                boolean deleted = transactionDao.delete(transactionId);

                transactionDao.commitTransaction(conn);
                return deleted;
            } catch (SQLException e) {
                if (conn != null) {
                    transactionDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                transactionDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("删除交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Transaction> getUserTransactions(int userId) throws ServiceException {
        try {
            return transactionDao.findByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("获取用户交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByDateRange(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            return transactionDao.findByUserIdAndDateRange(userId, startDate, endDate);
        } catch (SQLException e) {
            throw new ServiceException("按日期范围获取交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByType(int userId, TransactionType type) throws ServiceException {
        try {
            return transactionDao.findByUserIdAndType(userId, type);
        } catch (SQLException e) {
            throw new ServiceException("按类型获取交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByCategory(int userId, int categoryId) throws ServiceException {
        try {
            return transactionDao.findByUserIdAndCategory(userId, categoryId);
        } catch (SQLException e) {
            throw new ServiceException("按分类获取交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Transaction> getTransactionsByAccount(int userId, int accountId) throws ServiceException {
        try {
            return transactionDao.findByUserIdAndAccount(userId, accountId);
        } catch (SQLException e) {
            throw new ServiceException("按账户获取交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Transaction> getRecentTransactions(int userId, int limit) throws ServiceException {
        try {
            return transactionDao.findRecentByUserId(userId, limit);
        } catch (SQLException e) {
            throw new ServiceException("获取最近交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public BigDecimal calculateTotalIncome(int userId, LocalDate startDate, LocalDate endDate) throws ServiceException {
        try {
            return transactionDao.calculateTotalIncome(userId, startDate, endDate);
        } catch (SQLException e) {
            throw new ServiceException("计算总收入过程中发生数据库错误", e);
        }
    }

    @Override
    public BigDecimal calculateTotalExpense(int userId, LocalDate startDate, LocalDate endDate) throws ServiceException {
        try {
            return transactionDao.calculateTotalExpense(userId, startDate, endDate);
        } catch (SQLException e) {
            throw new ServiceException("计算总支出过程中发生数据库错误", e);
        }
    }

    @Override
    public Map<Integer, BigDecimal> getExpenseByCategory(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            return transactionDao.calculateExpenseByCategory(userId, startDate, endDate);
        } catch (SQLException e) {
            throw new ServiceException("按分类统计支出过程中发生数据库错误", e);
        }
    }

    @Override
    public Map<Integer, BigDecimal> getIncomeByCategory(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException {
        try {
            return transactionDao.calculateIncomeByCategory(userId, startDate, endDate);
        } catch (SQLException e) {
            throw new ServiceException("按分类统计收入过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Transaction> searchTransactions(int userId, String keyword) throws ServiceException {
        try {
            return transactionDao.searchTransactions(userId, keyword);
        } catch (SQLException e) {
            throw new ServiceException("搜索交易记录过程中发生数据库错误", e);
        }
    }

    @Override
    public int importTransactions(List<Transaction> transactions) throws ServiceException {
        try {
            return transactionDao.batchSave(transactions);
        } catch (SQLException e) {
            throw new ServiceException("批量导入交易记录过程中发生数据库错误", e);
        }
    }
}