package com.PFM.CD.service.interfaces;

import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 交易服务接口，提供交易相关的业务逻辑
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface TransactionService {

    /**
     * 记录收入交易
     *
     * @param userId 用户ID
     * @param accountId 收款账户ID
     * @param categoryId 分类ID
     * @param amount 金额
     * @param transactionDate 交易日期
     * @param description 描述
     * @return 创建的交易
     * @throws ServiceException 如果记录过程中发生错误
     */
    Transaction recordIncome(int userId, int accountId, int categoryId, BigDecimal amount,
                             LocalDate transactionDate, String description) throws ServiceException;

    /**
     * 记录支出交易
     *
     * @param userId 用户ID
     * @param accountId 付款账户ID
     * @param categoryId 分类ID
     * @param amount 金额
     * @param transactionDate 交易日期
     * @param description 描述
     * @return 创建的交易
     * @throws InsufficientBalanceException 如果账户余额不足
     * @throws ServiceException 如果记录过程中发生其他错误
     */
    Transaction recordExpense(int userId, int accountId, int categoryId, BigDecimal amount,
                              LocalDate transactionDate, String description)
            throws InsufficientBalanceException, ServiceException;

    /**
     * 记录转账交易
     *
     * @param userId 用户ID
     * @param fromAccountId 源账户ID
     * @param toAccountId 目标账户ID
     * @param amount 金额
     * @param transactionDate 交易日期
     * @param description 描述
     * @return 创建的交易
     * @throws InsufficientBalanceException 如果源账户余额不足
     * @throws ServiceException 如果记录过程中发生其他错误

    Transaction recordTransfer(int userId, int fromAccountId, int toAccountId, BigDecimal amount,
                               LocalDate transactionDate, String description)
            throws InsufficientBalanceException, ServiceException;
 */
    /**
     * 获取交易信息
     *
     * @param transactionId 交易ID
     * @return 交易信息
     * @throws ServiceException 如果获取过程中发生错误
     */
    Transaction getTransactionById(int transactionId) throws ServiceException;

    /**
     * 更新交易信息
     *
     * @param transaction 需要更新的交易信息
     * @return 更新后的交易
     * @throws ServiceException 如果更新过程中发生错误
     */
    Transaction updateTransaction(Transaction transaction) throws ServiceException;

    /**
     * 删除交易
     *
     * @param transactionId 交易ID
     * @param reverseAccountBalances 是否恢复账户余额
     * @return 是否删除成功
     * @throws ServiceException 如果删除过程中发生错误
     */
    boolean deleteTransaction(int transactionId, boolean reverseAccountBalances) throws ServiceException;

    /**
     * 获取用户的所有交易
     *
     * @param userId 用户ID
     * @return 交易列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Transaction> getUserTransactions(int userId) throws ServiceException;

    /**
     * 按日期范围获取用户交易
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Transaction> getTransactionsByDateRange(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException;

    /**
     * 按类型获取用户交易
     *
     * @param userId 用户ID
     * @param type 交易类型
     * @return 交易列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Transaction> getTransactionsByType(int userId, TransactionType type) throws ServiceException;

    /**
     * 按分类获取用户交易
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 交易列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Transaction> getTransactionsByCategory(int userId, int categoryId) throws ServiceException;

    /**
     * 按账户获取用户交易
     *
     * @param userId 用户ID
     * @param accountId 账户ID
     * @return 交易列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Transaction> getTransactionsByAccount(int userId, int accountId) throws ServiceException;

    /**
     * 获取用户最近交易
     *
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 交易列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Transaction> getRecentTransactions(int userId, int limit) throws ServiceException;

    /**
     * 计算指定日期范围内的总收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总收入
     * @throws ServiceException 如果计算过程中发生错误
     */
    BigDecimal calculateTotalIncome(int userId, LocalDate startDate, LocalDate endDate) throws ServiceException;

    /**
     * 计算指定日期范围内的总支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总支出
     * @throws ServiceException 如果计算过程中发生错误
     */
    BigDecimal calculateTotalExpense(int userId, LocalDate startDate, LocalDate endDate) throws ServiceException;

    /**
     * 按分类统计支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分类ID到金额的映射
     * @throws ServiceException 如果统计过程中发生错误
     */
    Map<Integer, BigDecimal> getExpenseByCategory(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException;

    /**
     * 按分类统计收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分类ID到金额的映射
     * @throws ServiceException 如果统计过程中发生错误
     */
    Map<Integer, BigDecimal> getIncomeByCategory(int userId, LocalDate startDate, LocalDate endDate)
            throws ServiceException;

    /**
     * 搜索用户交易
     *
     * @param userId 用户ID
     * @param keyword 关键词
     * @return 交易列表
     * @throws ServiceException 如果搜索过程中发生错误
     */
    List<Transaction> searchTransactions(int userId, String keyword) throws ServiceException;

    /**
     * 批量导入交易
     *
     * @param transactions 交易列表
     * @return 成功导入的数量
     * @throws ServiceException 如果导入过程中发生错误
     */
    int importTransactions(List<Transaction> transactions) throws ServiceException;

    List<Transaction> getTransactionsWithPagination(int currentUserId, LocalDate startDate, LocalDate endDate, TransactionType type, int accountId, int offset, int pageSize);

    int countByUserId(int currentUserId, LocalDate startDate, LocalDate endDate, TransactionType type);

    Map<Integer, BigDecimal> calculateIncomeByCategory(int currentUserId, LocalDate startDate, LocalDate endDate);

    Map<Integer, BigDecimal> calculateExpenseByCategory(int currentUserId, LocalDate startDate, LocalDate endDate);
}