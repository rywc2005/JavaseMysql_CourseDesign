package com.PFM.CD.dao;

import com.PFM.CD.entity.User;

import java.sql.SQLException;
import java.util.List;

/**
 * 用户数据访问对象接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface UserDao {

    /**
     * 创建新用户
     *
     * @param user 要创建的用户对象
     * @return 创建后的用户对象（包含自动生成的ID）
     * @throws SQLException 如果数据库操作失败
     */
    User createUser(User user) throws SQLException;

    /**
     * 根据ID查找用户
     *
     * @param userId 用户ID
     * @return 找到的用户对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    User findById(int userId) throws SQLException;

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 找到的用户对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    User findByUsername(String username) throws SQLException;

    /**
     * 根据邮箱查找用户
     *
     * @param email 电子邮箱
     * @return 找到的用户对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    User findByEmail(String email) throws SQLException;

    /**
     * 获取所有用户
     *
     * @return 用户对象列表
     * @throws SQLException 如果数据库操作失败
     */
    List<User> findAll() throws SQLException;

    /**
     * 更新用户信息
     *
     * @param user 要更新的用户对象
     * @return 受影响的行数
     * @throws SQLException 如果数据库操作失败
     */
    int updateUser(User user) throws SQLException;

    /**
     * 删除用户
     *
     * @param userId 要删除的用户ID
     * @return 受影响的行数
     * @throws SQLException 如果数据库操作失败
     */
    int deleteUser(int userId) throws SQLException;

    /**
     * 验证用户密码
     *
     * @param username 用户名
     * @param hashedPassword 哈希密码
     * @return 验证是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean verifyPassword(String username, String hashedPassword) throws SQLException;
}