package com.PFM.CD.service.exception;

/**
 * 重复资源异常，当尝试创建已存在的资源时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DuplicateResourceException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final String resourceType;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * 构造一个带有资源类型、字段名称和字段值的重复资源异常
     *
     * @param resourceType 资源类型
     * @param fieldName 冲突字段名称
     * @param fieldValue 冲突字段值
     */
    public DuplicateResourceException(String resourceType, String fieldName, Object fieldValue) {
        super(String.format("%s已存在: %s=%s", resourceType, fieldName, fieldValue));
        this.resourceType = resourceType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * 构造一个带有自定义错误消息的重复资源异常
     *
     * @param message 错误消息
     * @param resourceType 资源类型
     * @param fieldName 冲突字段名称
     * @param fieldValue 冲突字段值
     */
    public DuplicateResourceException(String message, String resourceType, String fieldName, Object fieldValue) {
        super(message);
        this.resourceType = resourceType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
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
     * 获取冲突字段名称
     *
     * @return 字段名称
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 获取冲突字段值
     *
     * @return 字段值
     */
    public Object getFieldValue() {
        return fieldValue;
    }

    /**
     * 创建一个用户名已存在异常
     *
     * @param username 用户名
     * @return 重复资源异常实例
     */
    public static DuplicateResourceException usernameExists(String username) {
        return new DuplicateResourceException("用户", "username", username);
    }

    /**
     * 创建一个邮箱已存在异常
     *
     * @param email 邮箱
     * @return 重复资源异常实例
     */
    public static DuplicateResourceException emailExists(String email) {
        return new DuplicateResourceException("用户", "email", email);
    }

    /**
     * 创建一个账户名称已存在异常
     *
     * @param accountName 账户名称
     * @return 重复资源异常实例
     */
    public static DuplicateResourceException accountNameExists(String accountName) {
        return new DuplicateResourceException("账户", "accountName", accountName);
    }

    /**
     * 创建一个分类名称已存在异常
     *
     * @param categoryName 分类名称
     * @return 重复资源异常实例
     */
    public static DuplicateResourceException categoryNameExists(String categoryName) {
        return new DuplicateResourceException("分类", "categoryName", categoryName);
    }

    /**
     * 创建一个预算名称已存在异常
     *
     * @param budgetName 预算名称
     * @return 重复资源异常实例
     */
    public static DuplicateResourceException budgetNameExists(String budgetName) {
        return new DuplicateResourceException("预算", "name", budgetName);
    }
}