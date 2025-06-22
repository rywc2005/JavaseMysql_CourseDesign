package javasemysql.coursedesign.service;

import javasemysql.coursedesign.dto.UserQueryParam;
import javasemysql.coursedesign.model.User;
import java.util.List;

public interface UserService {

    // 用户登录
    User login(String username, String password);

    // 用户注册
    boolean register(User user);

    // 更新用户信息
    boolean updateUser(User user);

    // 修改密码
    boolean changePassword(int userId, String oldPassword, String newPassword);

    // 获取用户信息
    User getUserById(int userId);

    // 获取所有用户
    List<User> getAllUsers();

    // 按条件查询用户
    List<User> queryUsers(UserQueryParam param);

    // 获取用户总数
    int countUsers(UserQueryParam param);

    // 删除用户
    boolean deleteUser(int userId);

    // 重置密码
    boolean resetPassword(String email);

    boolean isUsernameExists(String username);

    boolean isEmailExists(String email);

    boolean verifyPassword(int id, String oldPassword);

    boolean updatePassword(int id, String newPassword);
}