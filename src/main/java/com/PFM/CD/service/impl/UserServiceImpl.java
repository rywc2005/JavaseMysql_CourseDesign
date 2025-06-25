package com.PFM.CD.service.impl;

import com.PFM.CD.dao.interfaces.UserDao;
import com.PFM.CD.entity.User;
import com.PFM.CD.service.exception.AuthenticationException;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.UserService;

import java.sql.SQLException;
import java.time.LocalDate;
import com.PFM.CD.utils.security.PasswordHasher;
/**
 * 用户服务实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    /**
     * 构造函数
     *
     * @param userDao 用户DAO接口
     */
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User register(String username, String password, String email) throws ServiceException {
        try {
            // 检查用户名和邮箱是否已存在
            if (userDao.isUsernameExists(username)) {
                throw new ServiceException("用户名已存在: " + username);
            }

            if (userDao.isEmailExists(email)) {
                throw new ServiceException("邮箱已存在: " + email);
            }

            // 创建新用户对象
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordHasher.hashPassword(password));
            user.setEmail(email);
            user.setCreatedAt(LocalDate.now());

            // 保存用户
            boolean success = userDao.save(user);
            if (!success) {
                throw new ServiceException("创建用户失败");
            }

            return user;
        } catch (SQLException e) {
            throw new ServiceException("注册过程中发生数据库错误", e);
        }
    }

    @Override
    public User login(String username, String password) throws AuthenticationException, ServiceException {
        try {
            // 获取用户信息
            User user = userDao.findByUsername(username);
            if (user == null) {
                throw new AuthenticationException("用户名或密码不正确");
            }

            // 验证密码
            if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                throw new AuthenticationException("用户名或密码不正确");
            }

            return user;
        } catch (SQLException e) {
            throw new ServiceException("登录过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword)
            throws AuthenticationException, ServiceException {
        try {
            // 获取用户信息
            User user = userDao.findById(userId);
            if (user == null) {
                throw new ServiceException("用户不存在: " + userId);
            }

            // 验证旧密码
            if (!PasswordHasher.verifyPassword(oldPassword, user.getPasswordHash())) {
                throw new AuthenticationException("旧密码不正确");
            }

            // 更新密码
            String newPasswordHash = PasswordHasher.hashPassword(newPassword);
            return userDao.updatePassword(userId, newPasswordHash);
        } catch (SQLException e) {
            throw new ServiceException("修改密码过程中发生数据库错误", e);
        }
    }

    @Override
    public User updateUserInfo(User user) throws ServiceException {
        try {
            // 检查用户是否存在
            User existingUser = userDao.findById(user.getUserId());
            if (existingUser == null) {
                throw new ServiceException("用户不存在: " + user.getUserId());
            }

            // 检查用户名是否已被其他用户使用
            User userWithSameUsername = userDao.findByUsername(user.getUsername());
            if (userWithSameUsername != null && userWithSameUsername.getUserId() != user.getUserId()) {
                throw new ServiceException("用户名已存在: " + user.getUsername());
            }

            // 检查邮箱是否已被其他用户使用
            User userWithSameEmail = userDao.findByEmail(user.getEmail());
            if (userWithSameEmail != null && userWithSameEmail.getUserId() != user.getUserId()) {
                throw new ServiceException("邮箱已存在: " + user.getEmail());
            }

            // 保留原始密码和创建日期
            user.setPasswordHash(existingUser.getPasswordHash());
            user.setCreatedAt(existingUser.getCreatedAt());

            // 更新用户信息
            boolean success = userDao.update(user);
            if (!success) {
                throw new ServiceException("更新用户信息失败");
            }

            return user;
        } catch (SQLException e) {
            throw new ServiceException("更新用户信息过程中发生数据库错误", e);
        }
    }

    @Override
    public User getUserInfo(int userId) throws ServiceException {
        try {
            User user = userDao.findById(userId);
            if (user == null) {
                throw new ServiceException("用户不存在: " + userId);
            }
            return user;
        } catch (SQLException e) {
            throw new ServiceException("获取用户信息过程中发生数据库错误", e);
        }
    }

    @Override
    public User getUserByUsername(String username) throws ServiceException {
        try {
            return userDao.findByUsername(username);
        } catch (SQLException e) {
            throw new ServiceException("获取用户信息过程中发生数据库错误", e);
        }
    }

    @Override
    public User getUserByEmail(String email) throws ServiceException {
        try {
            return userDao.findByEmail(email);
        } catch (SQLException e) {
            throw new ServiceException("获取用户信息过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean isUsernameAvailable(String username) throws ServiceException {
        try {
            return !userDao.isUsernameExists(username);
        } catch (SQLException e) {
            throw new ServiceException("检查用户名可用性过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean isEmailAvailable(String email) throws ServiceException {
        try {
            return !userDao.isEmailExists(email);
        } catch (SQLException e) {
            throw new ServiceException("检查邮箱可用性过程中发生数据库错误", e);
        }
    }

    @Override
    public void resetPassword(String username, String email, String newPassword) {
        try {
            // 检查用户名和邮箱是否匹配
            User user = userDao.findByUsername(username);
            if (user == null || !user.getEmail().equals(email)) {
                throw new ServiceException("用户名或邮箱不匹配");
            }

            // 更新密码
            String newPasswordHash = PasswordHasher.hashPassword(newPassword);
            boolean success = userDao.updatePassword(user.getUserId(), newPasswordHash);
            if (!success) {
                throw new ServiceException("重置密码失败");
            }
        } catch (SQLException | ServiceException e) {
            try {
                throw new ServiceException("重置密码过程中发生数据库错误", e);
            } catch (ServiceException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}