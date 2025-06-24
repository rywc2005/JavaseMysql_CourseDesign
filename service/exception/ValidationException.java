package com.PFM.CD.service.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据验证异常，当数据不满足验证规则时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ValidationException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final Map<String, String> validationErrors;

    /**
     * 构造一个带有单个验证错误的数据验证异常
     *
     * @param fieldName 字段名称
     * @param errorMessage 错误消息
     */
    public ValidationException(String fieldName, String errorMessage) {
        super("数据验证失败: " + fieldName + " - " + errorMessage);

        Map<String, String> errors = new HashMap<>();
        errors.put(fieldName, errorMessage);
        this.validationErrors = Collections.unmodifiableMap(errors);
    }

    /**
     * 构造一个带有多个验证错误的数据验证异常
     *
     * @param validationErrors 验证错误映射
     */
    public ValidationException(Map<String, String> validationErrors) {
        super("数据验证失败: " + validationErrors.size() + "个错误");
        this.validationErrors = Collections.unmodifiableMap(new HashMap<>(validationErrors));
    }

    /**
     * 获取验证错误映射
     *
     * @return 字段名到错误消息的映射
     */
    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * 获取格式化的验证错误消息
     *
     * @return 格式化的错误消息
     */
    public String getFormattedValidationErrors() {
        StringBuilder sb = new StringBuilder();
        sb.append("数据验证错误:\n");

        validationErrors.forEach((field, error) ->
                sb.append(" - ").append(field).append(": ").append(error).append("\n")
        );

        return sb.toString();
    }

    /**
     * 创建一个字段为空的验证异常
     *
     * @param fieldName 字段名称
     * @return 验证异常实例
     */
    public static ValidationException fieldIsRequired(String fieldName) {
        return new ValidationException(fieldName, "不能为空");
    }

    /**
     * 创建一个字段长度不符合要求的验证异常
     *
     * @param fieldName 字段名称
     * @param min 最小长度
     * @param max 最大长度
     * @return 验证异常实例
     */
    public static ValidationException invalidLength(String fieldName, int min, int max) {
        return new ValidationException(fieldName, "长度必须在" + min + "到" + max + "之间");
    }

    /**
     * 创建一个字段格式不正确的验证异常
     *
     * @param fieldName 字段名称
     * @param pattern 正确格式的描述
     * @return 验证异常实例
     */
    public static ValidationException invalidFormat(String fieldName, String pattern) {
        return new ValidationException(fieldName, "格式不正确，应为" + pattern);
    }

    /**
     * 创建一个数字范围不正确的验证异常
     *
     * @param fieldName 字段名称
     * @param min 最小值
     * @param max 最大值
     * @return 验证异常实例
     */
    public static ValidationException numberOutOfRange(String fieldName, Number min, Number max) {
        return new ValidationException(fieldName, "必须在" + min + "到" + max + "之间");
    }

    /**
     * 创建一个日期范围不正确的验证异常
     *
     * @param fieldName 字段名称
     * @param min 最早日期的描述
     * @param max 最晚日期的描述
     * @return 验证异常实例
     */
    public static ValidationException dateOutOfRange(String fieldName, String min, String max) {
        return new ValidationException(fieldName, "必须在" + min + "到" + max + "之间");
    }
}