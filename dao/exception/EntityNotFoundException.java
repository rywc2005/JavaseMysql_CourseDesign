package com.PFM.CD.dao.exception;

/**
 * 实体不存在异常，当查询的实体不存在时抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class EntityNotFoundException extends DaoException {

    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final Object entityId;

    /**
     * 构造一个带有实体名称和ID的实体不存在异常
     *
     * @param entityName 实体名称
     * @param entityId 实体ID
     */
    public EntityNotFoundException(String entityName, Object entityId) {
        super(String.format("找不到%s: ID=%s", entityName, entityId));
        this.entityName = entityName;
        this.entityId = entityId;
    }

    /**
     * 构造一个带有自定义错误消息的实体不存在异常
     *
     * @param message 错误消息
     * @param entityName 实体名称
     * @param entityId 实体ID
     */
    public EntityNotFoundException(String message, String entityName, Object entityId) {
        super(message);
        this.entityName = entityName;
        this.entityId = entityId;
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
     * 获取实体ID
     *
     * @return 实体ID
     */
    public Object getEntityId() {
        return entityId;
    }
}