package javasemysql.coursedesign.model;

/**
 * 账户实体类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class Account {

    private int id;
    private int userId;
    private String name;
    private String type;
    private double balance;
    private String description;

    /**
     * 默认构造函数
     */
    public Account() {
    }

    /**
     * 构造函数
     *
     * @param id 账户ID
     * @param userId 用户ID
     * @param name 账户名称
     * @param type 账户类型
     * @param balance 账户余额
     * @param description 账户描述
     */
    public Account(int id, int userId, String name, String type, double balance, String description) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.description = description;
    }

    /**
     * 获取账户ID
     *
     * @return 账户ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置账户ID
     *
     * @param id 账户ID
     */
    public void setId(int id) {
        this.id = id;
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

    /**
     * 获取账户余额
     *
     * @return 账户余额
     */
    public double getBalance() {
        return balance;
    }

    /**
     * 设置账户余额
     *
     * @param balance 账户余额
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * 获取账户描述
     *
     * @return 账户描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置账户描述
     *
     * @param description 账户描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", balance=" + balance +
                ", description='" + description + '\'' +
                '}';
    }
}