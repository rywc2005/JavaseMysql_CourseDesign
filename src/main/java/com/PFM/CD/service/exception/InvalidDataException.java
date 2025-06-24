package com.PFM.CD.service.exception;

/**
 * 无效数据异常，当输入数据不符合业务要求时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class InvalidDataException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final String fieldName;
    private final Object fieldValue;

    /**
     * 构造一个带有错误消息的无效数据异常
     *
     * @param message 错误消息
     */
    public InvalidDataException(String message) {
        super(message);
        this.fieldName = null;
        this.fieldValue = null;
    }

    /**
     * 构造一个带有字段名称和字段值的无效数据异常
     *
     * @param message 错误消息
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     */
    public InvalidDataException(String message, String fieldName, Object fieldValue) {
        super(message);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * 获取字段名称
     *
     * @return 字段名称
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 获取字段值
     *
     * @return 字段值
     */
    public Object getFieldValue() {
        return fieldValue;
    }

    /**
     * 创建一个金额必须为正数的无效数据异常
     *
     * @param fieldName 字段名称
     * @param value 字段值
     * @return 无效数据异常实例
     */
    public static InvalidDataException negativeAmount(String fieldName, Object value) {
        return new InvalidDataException(fieldName + "必须为正数", fieldName, value);
    }

    /**
     * 创建一个金额为零的无效数据异常
     *
     * @param fieldName 字段名称
     * @return 无效数据异常实例
     */
    public static InvalidDataException zeroAmount(String fieldName) {
        return new InvalidDataException(fieldName + "不能为零", fieldName, 0);
    }

    /**
     * 创建一个相同账户的无效数据异常
     *
     * @param fromAccountId 源账户ID
     * @param toAccountId 目标账户ID
     * @return 无效数据异常实例
     */
    public static InvalidDataException sameAccount(int fromAccountId, int toAccountId) {
        return new InvalidDataException("源账户和目标账户不能相同", "accountId", fromAccountId);
    }

    /**
     * 创建一个日期范围无效的无效数据异常
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 无效数据异常实例
     */
    public static InvalidDataException invalidDateRange(Object startDate, Object endDate) {
        return new InvalidDataException("结束日期必须晚于开始日期", "dateRange", startDate + " - " + endDate);
    }

    /**
     * 创建一个枚举值无效的无效数据异常
     *
     * @param fieldName 字段名称
     * @param value 字段值
     * @param validValues 有效值列表
     * @return 无效数据异常实例
     */
    public static InvalidDataException invalidEnumValue(String fieldName, Object value, String validValues) {
        return new InvalidDataException(fieldName + "的值无效，有效值: " + validValues, fieldName, value);
    }
}