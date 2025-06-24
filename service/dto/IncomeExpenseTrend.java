package com.PFM.CD.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收支趋势数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class IncomeExpenseTrend {

    private LocalDate date;
    private BigDecimal income;
    private BigDecimal expense;

    /**
     * 构造函数
     *
     * @param date 日期
     * @param income 收入
     * @param expense 支出
     */
    public IncomeExpenseTrend(LocalDate date, BigDecimal income, BigDecimal expense) {
        this.date = date;
        this.income = income;
        this.expense = expense;
    }

    /**
     * 获取日期
     *
     * @return 日期
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * 设置日期
     *
     * @param date 日期
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * 获取收入
     *
     * @return 收入
     */
    public BigDecimal getIncome() {
        return income;
    }

    /**
     * 设置收入
     *
     * @param income 收入
     */
    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    /**
     * 获取支出
     *
     * @return 支出
     */
    public BigDecimal getExpense() {
        return expense;
    }

    /**
     * 设置支出
     *
     * @param expense 支出
     */
    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    /**
     * 获取余额（收入减支出）
     *
     * @return 余额
     */
    public BigDecimal getBalance() {
        return income.subtract(expense);
    }
}