package com.PFM.CD.dao.exception;

/**
 * DAO层基础异常类，所有DAO相关异常的父类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造一个带有错误消息的DAO异常
     *
     * @param message 错误消息
     */
    public DaoException(String message) {
        super(message);
    }

    /**
     * 构造一个带有错误消息和原因的DAO异常
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个带有原因的DAO异常
     *
     * @param cause 原始异常
     */
    public DaoException(Throwable cause) {
        super(cause);
    }
}