package com.PFM.CD.dao.interfaces;

import com.PFM.CD.entity.User;

import java.sql.SQLException;

/**
 * 用户数据访问接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface UserDao extends BaseDao<User, Integer> {

    /**
     * 通过用户名查找用户
     *
     * @param username 用户名
     * @return 找到的用户，如果不存在返回null
     */
    User findByUsername(String username) throws SQLException;

    /**
     * 通过邮箱查找用户
     *
     * @param email 邮箱
     * @return 找到的用户，如果不存在返回null
     */
    User findByEmail(String email) throws SQLException;

    /**
     * 验证用户凭据
     *
     * @param username 用户名
     * @param passwordHash 密码哈希
     * @return 如果凭据有效返回对应用户，否则返回null
     */
    User authenticate(String username, String passwordHash) throws SQLException;

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param newPasswordHash 新的密码哈希
     * @return 是否成功
     */
    boolean updatePassword(int userId, String newPasswordHash) throws SQLException;

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @return 如果存在返回true，否则返回false
     */
    boolean isUsernameExists(String username) throws SQLException;

    /**
     * 检查邮箱是否已存在
     *
     * @param email 邮箱
     * @return 如果存在返回true，否则返回false
     */
    boolean isEmailExists(String email) throws SQLException;
}