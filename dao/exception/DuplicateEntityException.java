package com.PFM.CD.dao.exception;

/**
 * 实体重复异常，当尝试创建已存在的实体时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class DuplicateEntityException extends DaoException {

    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * 构造一个带有实体名称、字段名称和字段值的实体重复异常
     *
     * @param entityName 实体名称
     * @param fieldName 冲突字段名称
     * @param fieldValue 冲突字段值
     */
    public DuplicateEntityException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s已存在: %s=%s", entityName, fieldName, fieldValue));
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * 构造一个带有自定义错误消息的实体重复异常
     *
     * @param message 错误消息
     * @param entityName 实体名称
     * @param fieldName 冲突字段名称
     * @param fieldValue 冲突字段值
     */
    public DuplicateEntityException(String message, String entityName, String fieldName, Object fieldValue) {
        super(message);
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
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
}