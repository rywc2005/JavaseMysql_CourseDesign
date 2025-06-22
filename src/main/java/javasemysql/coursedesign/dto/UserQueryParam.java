package javasemysql.coursedesign.dto;

/**
 * 用户查询参数数据传输对象
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class UserQueryParam {
    private String name;            // 用户名
    private String email;           // 邮箱
    private String role;            // 角色
    private PageParam pageParam;    // 分页参数

    /**
     * 默认构造函数
     */
    public UserQueryParam() {
        this.pageParam = new PageParam();
    }

    /**
     * 带分页参数的构造函数
     *
     * @param pageParam 分页参数
     */
    public UserQueryParam(PageParam pageParam) {
        this.pageParam = pageParam != null ? pageParam : new PageParam();
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
     * 获取分页参数
     *
     * @return 分页参数
     */
    public PageParam getPageParam() {
        return pageParam;
    }

    /**
     * 设置分页参数
     *
     * @param pageParam 分页参数
     */
    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam != null ? pageParam : new PageParam();
    }
}