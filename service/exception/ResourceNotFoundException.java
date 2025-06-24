package com.PFM.CD.service.exception;

/**
 * 资源不存在异常，当请求的资源不存在时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ResourceNotFoundException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final Object resourceId;

    /**
     * 构造一个带有资源类型和ID的资源不存在异常
     *
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s不存在: ID=%s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * 构造一个带有自定义错误消息的资源不存在异常
     *
     * @param message 错误消息
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public ResourceNotFoundException(String message, String resourceType, Object resourceId) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * 获取资源类型
     *
     * @return 资源类型
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * 获取资源ID
     *
     * @return 资源ID
     */
    public Object getResourceId() {
        return resourceId;
    }

    /**
     * 创建一个用户不存在异常
     *
     * @param userId 用户ID
     * @return 资源不存在异常实例
     */
    public static ResourceNotFoundException userNotFound(int userId) {
        return new ResourceNotFoundException("用户", userId);
    }

    /**
     * 创建一个账户不存在异常
     *
     * @param accountId 账户ID
     * @return 资源不存在异常实例
     */
    public static ResourceNotFoundException accountNotFound(int accountId) {
        return new ResourceNotFoundException("账户", accountId);
    }

    /**
     * 创建一个分类不存在异常
     *
     * @param categoryId 分类ID
     * @return 资源不存在异常实例
     */
    public static ResourceNotFoundException categoryNotFound(int categoryId) {
        return new ResourceNotFoundException("分类", categoryId);
    }

    /**
     * 创建一个交易不存在异常
     *
     * @param transactionId 交易ID
     * @return 资源不存在异常实例
     */
    public static ResourceNotFoundException transactionNotFound(int transactionId) {
        return new ResourceNotFoundException("交易", transactionId);
    }

    /**
     * 创建一个预算不存在异常
     *
     * @param budgetId 预算ID
     * @return 资源不存在异常实例
     */
    public static ResourceNotFoundException budgetNotFound(int budgetId) {
        return new ResourceNotFoundException("预算", budgetId);
    }

    /**
     * 创建一个报表不存在异常
     *
     * @param reportId 报表ID
     * @return 资源不存在异常实例
     */
    public static ResourceNotFoundException reportNotFound(int reportId) {
        return new ResourceNotFoundException("报表", reportId);
    }
}