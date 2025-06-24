package com.PFM.CD.service.exception;

/**
 * Service层基础异常类，所有Service相关异常的父类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 构造一个带有错误消息的Service异常
     *
     * @param message 错误消息
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * 构造一个带有错误消息和原因的Service异常
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个带有原因的Service异常
     *
     * @param cause 原始异常
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }
}