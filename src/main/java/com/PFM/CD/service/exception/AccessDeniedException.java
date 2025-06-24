package com.PFM.CD.service.exception;

/**
 * 访问拒绝异常，当用户尝试访问无权限的资源时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class AccessDeniedException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final int userId;
    private final String resourceType;
    private final Object resourceId;

    /**
     * 构造一个带有用户ID、资源类型和资源ID的访问拒绝异常
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public AccessDeniedException(int userId, String resourceType, Object resourceId) {
        super(String.format("用户(ID=%d)无权访问%s(ID=%s)", userId, resourceType, resourceId));
        this.userId = userId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * 构造一个带有自定义错误消息的访问拒绝异常
     *
     * @param message 错误消息
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public AccessDeniedException(String message, int userId, String resourceType, Object resourceId) {
        super(message);
        this.userId = userId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public int getUserId() {
        return userId;
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
     * 创建一个账户所有权错误的访问拒绝异常
     *
     * @param userId 用户ID
     * @param accountId 账户ID
     * @return 访问拒绝异常实例
     */
    public static AccessDeniedException accountOwnership(int userId, int accountId) {
        return new AccessDeniedException("账户不属于当前用户", userId, "账户", accountId);
    }

    /**
     * 创建一个交易所有权错误的访问拒绝异常
     *
     * @param userId 用户ID
     * @param transactionId 交易ID
     * @return 访问拒绝异常实例
     */
    public static AccessDeniedException transactionOwnership(int userId, int transactionId) {
        return new AccessDeniedException("交易不属于当前用户", userId, "交易", transactionId);
    }

    /**
     * 创建一个预算所有权错误的访问拒绝异常
     *
     * @param userId 用户ID
     * @param budgetId 预算ID
     * @return 访问拒绝异常实例
     */
    public static AccessDeniedException budgetOwnership(int userId, int budgetId) {
        return new AccessDeniedException("预算不属于当前用户", userId, "预算", budgetId);
    }

    /**
     * 创建一个报表所有权错误的访问拒绝异常
     *
     * @param userId 用户ID
     * @param reportId 报表ID
     * @return 访问拒绝异常实例
     */
    public static AccessDeniedException reportOwnership(int userId, int reportId) {
        return new AccessDeniedException("报表不属于当前用户", userId, "报表", reportId);
    }
}