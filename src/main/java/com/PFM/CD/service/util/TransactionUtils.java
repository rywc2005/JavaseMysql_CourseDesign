package com.PFM.CD.service.util;

import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.enums.TransactionType;
import com.PFM.CD.service.dto.TransactionDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TransactionUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private TransactionUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 将Transaction实体转换为TransactionDto
     *
     * @param transaction 交易实体
     * @return 交易DTO
     */
    public static TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setUserId(transaction.getUserId());
        dto.setSourceAccountId(transaction.getSourceAccountId());
        dto.setDestinationAccountId(transaction.getDestinationAccountId());
        dto.setCategoryId(transaction.getCategoryId());
        dto.setAmount(transaction.getAmount());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setDescription(transaction.getDescription());

        return dto;
    }

    /**
     * 将多个Transaction实体转换为TransactionDto列表
     *
     * @param transactions 交易实体列表
     * @return 交易DTO列表
     */
    public static List<TransactionDto> toDtoList(List<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }

        List<TransactionDto> dtoList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            dtoList.add(toDto(transaction));
        }

        return dtoList;
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数
     */
    public static long getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * 根据交易类型和日期范围对交易进行分组
     *
     * @param transactions 交易列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分组结果
     */
    public static Map<String, Map<LocalDate, BigDecimal>> groupTransactionsByTypeAndDate(
            List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {

        Map<String, Map<LocalDate, BigDecimal>> result = new HashMap<>();
        Map<LocalDate, BigDecimal> incomeByDate = new HashMap<>();
        Map<LocalDate, BigDecimal> expenseByDate = new HashMap<>();

        // 初始化所有日期
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            incomeByDate.put(currentDate, BigDecimal.ZERO);
            expenseByDate.put(currentDate, BigDecimal.ZERO);
            currentDate = currentDate.plusDays(1);
        }

        // 汇总交易
        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getTransactionDate();
            if (date.isBefore(startDate) || date.isAfter(endDate)) {
                continue;
            }

            if (transaction.getTransactionType() == TransactionType.INCOME) {
                BigDecimal currentAmount = incomeByDate.getOrDefault(date, BigDecimal.ZERO);
                incomeByDate.put(date, currentAmount.add(transaction.getAmount()));
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                BigDecimal currentAmount = expenseByDate.getOrDefault(date, BigDecimal.ZERO);
                expenseByDate.put(date, currentAmount.add(transaction.getAmount()));
            }
        }

        result.put("income", incomeByDate);
        result.put("expense", expenseByDate);

        return result;
    }

    /**
     * 计算净流入（收入减支出）
     *
     * @param transactions 交易列表
     * @return 净流入
     */
    public static BigDecimal calculateNetFlow(List<Transaction> transactions) {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == TransactionType.INCOME) {
                income = income.add(transaction.getAmount());
            } else if (transaction.getTransactionType() == TransactionType.EXPENSE) {
                expense = expense.add(transaction.getAmount());
            }
        }

        return income.subtract(expense);
    }
}