package com.PFM.CD.service.exception;

/**
 * 认证异常，当用户认证失败时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class AuthenticationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造一个带有错误消息的认证异常
     *
     * @param message 错误消息
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * 构造一个带有错误消息和原因的认证异常
     *
     * @param message 错误消息
     * @param cause 原始异常
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个带有认证失败原因的认证异常
     *
     * @param reason 认证失败原因
     * @return 认证异常实例
     */
    public static AuthenticationException invalidCredentials(String reason) {
        return new AuthenticationException("认证失败: " + reason);
    }

    /**
     * 创建一个用户名或密码不正确的认证异常
     *
     * @return 认证异常实例
     */
    public static AuthenticationException invalidUsernameOrPassword() {
        return new AuthenticationException("用户名或密码不正确");
    }

    /**
     * 创建一个账户已锁定的认证异常
     *
     * @return 认证异常实例
     */
    public static AuthenticationException accountLocked() {
        return new AuthenticationException("账户已锁定，请联系管理员");
    }

    /**
     * 创建一个会话已过期的认证异常
     *
     * @return 认证异常实例
     */
    public static AuthenticationException sessionExpired() {
        return new AuthenticationException("会话已过期，请重新登录");
    }
}