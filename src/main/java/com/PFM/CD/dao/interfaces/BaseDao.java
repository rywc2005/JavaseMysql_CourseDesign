package com.PFM.CD.dao.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO基础接口，定义通用数据访问操作
 *
 * @param <T> 实体类型
 * @param <K> 主键类型
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface BaseDao<T, K> {

    /**
     * 保存实体
     *
     * @param entity 要保存的实体
     * @return 是否成功
     */
    boolean save(T entity) throws SQLException;

    /**
     * 通过ID查找实体
     *
     * @param id 实体ID
     * @return 找到的实体，如果不存在返回null
     */
    T findById(K id) throws SQLException;

    /**
     * 更新实体
     *
     * @param entity 要更新的实体
     * @return 是否成功
     */
    boolean update(T entity) throws SQLException;

    /**
     * 删除实体
     *
     * @param id 要删除的实体ID
     * @return 是否成功
     */
    boolean delete(K id) throws SQLException;

    /**
     * 查找所有实体
     *
     * @return 实体列表
     */
    List<T> findAll() throws SQLException;

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     */
    Connection getConnection() throws SQLException;

    /**
     * 开始事务
     *
     * @param conn 数据库连接
     */
    void beginTransaction(Connection conn) throws SQLException;

    /**
     * 提交事务
     *
     * @param conn 数据库连接
     */
    void commitTransaction(Connection conn) throws SQLException;

    /**
     * 回滚事务
     *
     * @param conn 数据库连接
     */
    void rollbackTransaction(Connection conn) throws SQLException;

    /**
     * 关闭数据库连接
     *
     * @param conn 数据库连接
     */
    void closeConnection(Connection conn);
}