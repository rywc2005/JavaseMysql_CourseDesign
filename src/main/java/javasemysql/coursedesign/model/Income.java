package javasemysql.coursedesign.model;

import java.util.Date;

/**
 * 收入实体类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class Income {

    private int id;
    private int userId;
    private int accountId;
    private String accountName;
    private String category;
    private double amount;
    private Date date;
    private String description;

    /**
     * 默认构造函数
     */
    public Income() {
    }

    /**
     * 构造函数
     *
     * @param id 收入ID
     * @param userId 用户ID
     * @param accountId 账户ID
     * @param accountName 账户名称
     * @param category 类别
     * @param amount 金额
     * @param date 日期
     * @param description 描述
     */
    public Income(int id, int userId, int accountId, String accountName, String category, double amount, Date date, String description) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.accountName = accountName;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    /**
     * 获取收入ID
     *
     * @return 收入ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置收入ID
     *
     * @param id 收入ID
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
     * 获取账户ID
     *
     * @return 账户ID
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * 设置账户ID
     *
     * @param accountId 账户ID
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    /**
     * 获取账户名称
     *
     * @return 账户名称
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * 设置账户名称
     *
     * @param accountName 账户名称
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * 获取类别
     *
     * @return 类别
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置类别
     *
     * @param category 类别
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取金额
     *
     * @return 金额
     */
    public double getAmount() {
        return amount;
    }

    /**
     * 设置金额
     *
     * @param amount 金额
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * 获取日期
     *
     * @return 日期
     */
    public Date getDate() {
        return date;
    }

    /**
     * 设置日期
     *
     * @param date 日期
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + id +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}