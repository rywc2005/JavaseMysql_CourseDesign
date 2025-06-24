package com.PFM.CD.service.dto;

import java.time.LocalDate;

/**
 * 交易频率数据传输对象
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TransactionFrequency {

    private LocalDate date;
    private int count;

    /**
     * 构造函数
     *
     * @param date 日期
     * @param count 交易次数
     */
    public TransactionFrequency(LocalDate date, int count) {
        this.date = date;
        this.count = count;
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
     * 获取交易次数
     *
     * @return 交易次数
     */
    public int getCount() {
        return count;
    }

    /**
     * 设置交易次数
     *
     * @param count 交易次数
     */
    public void setCount(int count) {
        this.count = count;
    }
}