package com.PFM.CD.dao.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据验证异常，当数据不满足验证规则时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DataValidationException extends DaoException {

    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final Map<String, String> validationErrors;

    /**
     * 构造一个带有实体名称和单个验证错误的数据验证异常
     *
     * @param entityName 实体名称
     * @param fieldName 字段名称
     * @param errorMessage 错误消息
     */
    public DataValidationException(String entityName, String fieldName, String errorMessage) {
        super(String.format("%s验证失败: %s - %s", entityName, fieldName, errorMessage));
        this.entityName = entityName;

        Map<String, String> errors = new HashMap<>();
        errors.put(fieldName, errorMessage);
        this.validationErrors = Collections.unmodifiableMap(errors);
    }

    /**
     * 构造一个带有实体名称和多个验证错误的数据验证异常
     *
     * @param entityName 实体名称
     * @param validationErrors 验证错误映射
     */
    public DataValidationException(String entityName, Map<String, String> validationErrors) {
        super(String.format("%s验证失败: %d个错误", entityName, validationErrors.size()));
        this.entityName = entityName;
        this.validationErrors = Collections.unmodifiableMap(new HashMap<>(validationErrors));
    }

    /**
     * 获取实体名称
     *
     * @return 实体名称
     */
    public String getEntityName() {
        return entityName;
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
        sb.append(entityName).append("验证错误:\n");

        validationErrors.forEach((field, error) ->
                sb.append(" - ").append(field).append(": ").append(error).append("\n")
        );

        return sb.toString();
    }
}