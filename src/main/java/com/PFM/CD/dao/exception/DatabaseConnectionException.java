package com.PFM.CD.dao.exception;

/**
 * 数据库连接异常，当无法建立数据库连接时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DatabaseConnectionException extends DaoException {

    private static final long serialVersionUID = 1L;

    /**
     * 默认构造函数
     */
    public DatabaseConnectionException() {
        super("无法连接到数据库");
    }

    /**
     * 构造一个带有错误消息的数据库连接异常
     *
     * @param message 错误消息
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }

    /**
     * 构造一个带有错误消息和原因的数据库连接异常
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个带有原因的数据库连接异常
     *
     * @param cause 原始异常
     */
    public DatabaseConnectionException(Throwable cause) {
        super("无法连接到数据库", cause);
    }
}