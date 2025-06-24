package com.PFM.CD.service.interfaces;

import com.PFM.CD.entity.User;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.exception.AuthenticationException;

/**
 * 用户服务接口，提供用户相关的业务逻辑
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码（明文）
     * @param email 邮箱
     * @return 注册成功的用户
     * @throws ServiceException 如果注册过程中发生错误
     */
    User register(String username, String password, String email) throws ServiceException;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录成功的用户
     * @throws AuthenticationException 如果认证失败
     * @throws ServiceException 如果登录过程中发生其他错误
     */
    User login(String username, String password) throws AuthenticationException, ServiceException;

    /**
     * 修改用户密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文）
     * @return 是否修改成功
     * @throws AuthenticationException 如果旧密码不正确
     * @throws ServiceException 如果修改过程中发生其他错误
     */
    boolean changePassword(int userId, String oldPassword, String newPassword)
            throws AuthenticationException, ServiceException;

    /**
     * 更新用户信息
     *
     * @param user 需要更新的用户信息
     * @return 更新后的用户
     * @throws ServiceException 如果更新过程中发生错误
     */
    User updateUserInfo(User user) throws ServiceException;

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     * @throws ServiceException 如果获取过程中发生错误
     */
    User getUserInfo(int userId) throws ServiceException;

    /**
     * 通过用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     * @throws ServiceException 如果获取过程中发生错误
     */
    User getUserByUsername(String username) throws ServiceException;

    /**
     * 通过邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     * @throws ServiceException 如果获取过程中发生错误
     */
    User getUserByEmail(String email) throws ServiceException;

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 如果可用返回true，否则返回false
     * @throws ServiceException 如果检查过程中发生错误
     */
    boolean isUsernameAvailable(String username) throws ServiceException;

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 如果可用返回true，否则返回false
     * @throws ServiceException 如果检查过程中发生错误
     */
    boolean isEmailAvailable(String email) throws ServiceException;
}