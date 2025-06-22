package javasemysql.coursedesign.model;

import java.util.Date;

/**
 * 预算实体类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class Budget {

    private int id;
    private int userId;
    private String category;
    private double amount;
    private Date startDate;
    private Date endDate;
    private String description;
    private double usedAmount;  // 非持久化字段，运行时计算

    /**
     * 默认构造函数
     */
    public Budget() {
    }

    /**
     * 构造函数
     *
     * @param id 预算ID
     * @param userId 用户ID
     * @param category 类别
     * @param amount 金额
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param description 描述
     */
    public Budget(int id, int userId, String category, double amount, Date startDate, Date endDate, String description) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    /**
     * 获取预算ID
     *
     * @return 预算ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置预算ID
     *
     * @param id 预算ID
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
     * 获取开始日期
     *
     * @return 开始日期
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置开始日期
     *
     * @param startDate 开始日期
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取结束日期
     *
     * @return 结束日期
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 设置结束日期
     *
     * @param endDate 结束日期
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    /**
     * 获取已使用金额
     *
     * @return 已使用金额
     */
    public double getUsedAmount() {
        return usedAmount;
    }

    /**
     * 设置已使用金额
     *
     * @param usedAmount 已使用金额
     */
    public void setUsedAmount(double usedAmount) {
        this.usedAmount = usedAmount;
    }

    /**
     * 获取剩余金额
     *
     * @return 剩余金额
     */
    public double getRemainingAmount() {
        return amount - usedAmount;
    }

    /**
     * 获取使用百分比
     *
     * @return 使用百分比
     */
    public double getUsagePercentage() {
        if (amount <= 0) {
            return 0;
        }
        return (usedAmount / amount) * 100;
    }

    /**
     * 检查是否超出预算
     *
     * @return 是否超出预算
     */
    public boolean isOverBudget() {
        return usedAmount > amount;
    }

    /**
     * 检查预算是否活跃（当前日期在预算时间范围内）
     *
     * @return 是否活跃
     */
    public boolean isActive() {
        Date now = new Date();
        return !startDate.after(now) && !endDate.before(now);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", userId=" + userId +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                ", usedAmount=" + usedAmount +
                '}';
    }
}