package com.PFM.CD.dao.exception;

/**
 * 事务异常，当事务操作失败时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TransactionException extends DaoException {

    private static final long serialVersionUID = 1L;

    private final TransactionOperation operation;

    /**
     * 事务操作枚举
     */
    public enum TransactionOperation {
        BEGIN("开始"),
        COMMIT("提交"),
        ROLLBACK("回滚");

        private final String displayName;

        TransactionOperation(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 构造一个带有事务操作的事务异常
     *
     * @param operation 事务操作
     */
    public TransactionException(TransactionOperation operation) {
        super(String.format("事务%s失败", operation.getDisplayName()));
        this.operation = operation;
    }

    /**
     * 构造一个带有错误消息和事务操作的事务异常
     *
     * @param message 错误消息
     * @param operation 事务操作
     */
    public TransactionException(String message, TransactionOperation operation) {
        super(message);
        this.operation = operation;
    }

    /**
     * 构造一个带有错误消息、事务操作和原因的事务异常
     *
     * @param message 错误消息
     * @param operation 事务操作
     * @param cause 原始异常
     */
    public TransactionException(String message, TransactionOperation operation, Throwable cause) {
        super(message, cause);
        this.operation = operation;
    }

    /**
     * 获取事务操作
     *
     * @return 事务操作
     */
    public TransactionOperation getOperation() {
        return operation;
    }
}