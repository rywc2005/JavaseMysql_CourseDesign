package javasemysql.coursedesign.dto;

/**
 * 用户更新参数数据传输对象
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class UserUpdateParam {
    private Integer id;          // 用户ID
    private String name;         // 用户名
    private String email;        // 邮箱
    private String oldPassword;  // 旧密码
    private String newPassword;  // 新密码
    private String role;         // 角色

    /**
     * 默认构造函数
     */
    public UserUpdateParam() {
    }

    /**
     * 带用户ID的构造函数
     *
     * @param id 用户ID
     */
    public UserUpdateParam(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户名
     *
     * @param name 用户名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取旧密码
     *
     * @return 旧密码
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * 设置旧密码
     *
     * @param oldPassword 旧密码
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * 获取新密码
     *
     * @return 新密码
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * 设置新密码
     *
     * @param newPassword 新密码
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * 获取角色
     *
     * @return 角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置角色
     *
     * @param role 角色
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 检查是否要更新密码
     *
     * @return 是否要更新密码
     */
    public boolean isPasswordChangeRequired() {
        return oldPassword != null && !oldPassword.isEmpty() &&
                newPassword != null && !newPassword.isEmpty();
    }
}