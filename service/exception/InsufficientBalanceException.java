package com.PFM.CD.service.exception;

import java.math.BigDecimal;

/**
 * 余额不足异常，当账户余额不足以完成交易时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class InsufficientBalanceException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final BigDecimal available;
    private final BigDecimal required;
    private final BigDecimal shortage;

    /**
     * 构造一个带有错误消息的余额不足异常
     *
     * @param message 错误消息
     */
    public InsufficientBalanceException(String message) {
        super(message);
        this.available = BigDecimal.ZERO;
        this.required = BigDecimal.ZERO;
        this.shortage = BigDecimal.ZERO;
    }

    /**
     * 构造一个带有可用余额和需求金额的余额不足异常
     *
     * @param message 错误消息
     * @param available 可用余额
     * @param required 需求金额
     */
    public InsufficientBalanceException(String message, BigDecimal available, BigDecimal required) {
        super(message);
        this.available = available;
        this.required = required;
        this.shortage = required.subtract(available);
    }

    /**
     * 获取可用余额
     *
     * @return 可用余额
     */
    public BigDecimal getAvailable() {
        return available;
    }

    /**
     * 获取需求金额
     *
     * @return 需求金额
     */
    public BigDecimal getRequired() {
        return required;
    }

    /**
     * 获取金额差额（缺口）
     *
     * @return 金额差额
     */
    public BigDecimal getShortage() {
        return shortage;
    }

    /**
     * 获取格式化的错误信息
     *
     * @return 格式化的错误信息
     */
    @Override
    public String getMessage() {
        return super.getMessage() +
                " [可用余额: " + available +
                ", 需求金额: " + required +
                ", 差额: " + shortage + "]";
    }

    /**
     * 创建一个带有账户ID的余额不足异常
     *
     * @param accountId 账户ID
     * @param available 可用余额
     * @param required 需求金额
     * @return 余额不足异常实例
     */
    public static InsufficientBalanceException forAccount(int accountId, BigDecimal available, BigDecimal required) {
        return new InsufficientBalanceException("账户(ID=" + accountId + ")余额不足", available, required);
    }
}