package com.PFM.CD.dao.interfaces;

import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 交易数据访问接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface TransactionDao extends BaseDao<Transaction, Integer> {

    /**
     * 查找用户的所有交易
     *
     * @param userId 用户ID
     * @return 交易列表
     */
    List<Transaction> findByUserId(int userId) throws SQLException;

    /**
     * 按日期范围查找用户交易
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易列表
     */
    List<Transaction> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * 按类型查找用户交易
     *
     * @param userId 用户ID
     * @param type 交易类型
     * @return 交易列表
     */
    List<Transaction> findByUserIdAndType(int userId, TransactionType type) throws SQLException;

    /**
     * 按分类查找用户交易
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 交易列表
     */
    List<Transaction> findByUserIdAndCategory(int userId, int categoryId) throws SQLException;

    /**
     * 按账户查找用户交易
     *
     * @param userId 用户ID
     * @param accountId 账户ID
     * @return 交易列表
     */
    List<Transaction> findByUserIdAndAccount(int userId, int accountId) throws SQLException;

    /**
     * 查找用户最近交易
     *
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 交易列表
     */
    List<Transaction> findRecentByUserId(int userId, int limit) throws SQLException;

    /**
     * 计算用户在指定日期范围内的总收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总收入
     */
    BigDecimal calculateTotalIncome(int userId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * 计算用户在指定日期范围内的总支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总支出
     */
    BigDecimal calculateTotalExpense(int userId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * 按分类统计用户在指定日期范围内的支出
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分类ID到金额的映射
     */
    Map<Integer, BigDecimal> calculateExpenseByCategory(int userId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * 按分类统计用户在指定日期范围内的收入
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分类ID到金额的映射
     */
    Map<Integer, BigDecimal> calculateIncomeByCategory(int userId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * 搜索用户交易
     *
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 交易列表
     */
    List<Transaction> searchTransactions(int userId, String keyword) throws SQLException;

    /**
     * 批量保存交易
     *
     * @param transactions 交易列表
     * @return 成功保存的数量
     */
    int batchSave(List<Transaction> transactions) throws SQLException;
}