package javasemysql.coursedesign.dao;

import javasemysql.coursedesign.dto.UserQueryParam;
import javasemysql.coursedesign.model.User;
import java.util.List;

/**
 * 用户数据访问接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface UserDao {

    /**
     * 插入用户
     *
     * @param user 用户对象
     * @return 是否插入成功
     */
    boolean insert(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean update(User user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean delete(int userId);

    /**
     * 根据ID查找用户
     *
     * @param userId 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    User findById(int userId);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户对象，如果不存在则返回null
     */
    User findByEmail(String email);

    /**
     * 查找所有用户
     *
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 根据条件查询用户
     *
     * @param param 查询参数
     * @return 用户列表
     */
    List<User> findByCondition(UserQueryParam param);

    /**
     * 获取符合条件的用户总数
     *
     * @param param 查询参数
     * @return 用户总数
     */
    int countByCondition(UserQueryParam param);

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     * @return 是否更新成功
     */
    boolean updateLastLogin(int userId);
}