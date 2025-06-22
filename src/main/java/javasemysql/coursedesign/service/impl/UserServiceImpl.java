package javasemysql.coursedesign.service.impl;

import javasemysql.coursedesign.dao.UserDao;
import javasemysql.coursedesign.dao.impl.UserDaoImpl;
import javasemysql.coursedesign.dto.UserQueryParam;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.service.UserService;
import javasemysql.coursedesign.utils.EncryptUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private final UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    @Override
    public User login(String username, String password) {
        try {
            User user = userDao.findByUsername(username);
            if (user != null && EncryptUtils.verify(password, user.getPassword())) {
                userDao.updateLastLogin(user.getId());
                return user;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during login", e);
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        try {
            // 检查用户名是否已存在
            if (userDao.findByUsername(user.getName()) != null) {
                return false;
            }

            // 检查邮箱是否已存在
            if (userDao.findByEmail(user.getEmail()) != null) {
                return false;
            }

            // 加密密码
            user.setPassword(EncryptUtils.encrypt(user.getPassword()));

            // 设置默认角色
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("user");
            }

            return userDao.insert(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during registration", e);
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        try {
            User existingUser = userDao.findById(user.getId());
            if (existingUser == null) {
                return false;
            }

            // 如果密码被修改，需要加密
            if (!user.getPassword().equals(existingUser.getPassword())) {
                user.setPassword(EncryptUtils.encrypt(user.getPassword()));
            }

            return userDao.update(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating user", e);
            return false;
        }
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        try {
            User user = userDao.findById(userId);
            if (user == null) {
                return false;
            }

            // 验证旧密码
            if (!EncryptUtils.verify(oldPassword, user.getPassword())) {
                return false;
            }

            // 更新密码
            user.setPassword(EncryptUtils.encrypt(newPassword));
            return userDao.update(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing password", e);
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        return userDao.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public List<User> queryUsers(UserQueryParam param) {
        return userDao.findByCondition(param);
    }

    @Override
    public int countUsers(UserQueryParam param) {
        return userDao.countByCondition(param);
    }

    @Override
    public boolean deleteUser(int userId) {
        return userDao.delete(userId);
    }

    @Override
    public boolean resetPassword(String email) {
        try {
            User user = userDao.findByEmail(email);
            if (user == null) {
                return false;
            }

            // 生成随机密码
            String newPassword = EncryptUtils.generateRandomPassword(10);

            // 更新密码
            user.setPassword(EncryptUtils.encrypt(newPassword));
            boolean result = userDao.update(user);

            if (result) {
                // 在实际应用中，这里应该发送邮件通知用户新密码
                logger.log(Level.INFO, "Password reset for user: " + user.getName() + ", new password: " + newPassword);
            }

            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error resetting password", e);
            return false;
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        try {
            return userDao.findByUsername(username) != null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking username existence", e);
            return false;
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        try {
            return userDao.findByEmail(email) != null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking email existence", e);
            return false;
        }
    }

    @Override
    public boolean verifyPassword(int id, String oldPassword) {
        try {
            User user = userDao.findById(id);
            if (user == null) {
                return false;
            }

            // 验证旧密码
            return EncryptUtils.verify(oldPassword, user.getPassword());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error verifying password", e);
            return false;
        }
    }

    @Override
    public boolean updatePassword(int id, String newPassword) {
        try {
            User user = userDao.findById(id);
            if (user == null) {
                return false;
            }

            // 加密新密码
            user.setPassword(EncryptUtils.encrypt(newPassword));
            return userDao.update(user);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating password", e);
            return false;
        }
    }
}