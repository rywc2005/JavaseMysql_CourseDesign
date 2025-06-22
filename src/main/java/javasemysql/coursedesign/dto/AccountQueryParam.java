package javasemysql.coursedesign.dto;

/**
 * 账户查询参数
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class AccountQueryParam {

    private int userId;
    private String name;
    private String type;

    /**
     * 构造函数
     *
     * @param userId 用户ID
     */
    public AccountQueryParam(int userId) {
        this.userId = userId;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 获取账户名称
     *
     * @return 账户名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置账户名称
     *
     * @param name 账户名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取账户类型
     *
     * @return 账户类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置账户类型
     *
     * @param type 账户类型
     */
    public void setType(String type) {
        this.type = type;
    }
}