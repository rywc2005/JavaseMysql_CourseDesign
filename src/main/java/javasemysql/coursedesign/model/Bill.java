package javasemysql.coursedesign.model;

import java.util.Calendar;
import java.util.Date;

/**
 * 账单实体类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class Bill {

    private int id;
    private int userId;
    private int accountId;
    private String accountName;
    private String category;
    private double amount;
    private Date dueDate;
    private boolean isPaid;
    private Date paymentDate;
    private String description;

    /**
     * 默认构造函数
     */
    public Bill() {
    }

    /**
     * 构造函数
     *
     * @param id 账单ID
     * @param userId 用户ID
     * @param accountId 账户ID
     * @param accountName 账户名称
     * @param category 类别
     * @param amount 金额
     * @param dueDate 到期日期
     * @param isPaid 是否已支付
     * @param paymentDate 支付日期
     * @param description 描述
     */
    public Bill(int id, int userId, int accountId, String accountName, String category, double amount, Date dueDate, boolean isPaid, Date paymentDate, String description) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.accountName = accountName;
        this.category = category;
        this.amount = amount;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
        this.paymentDate = paymentDate;
        this.description = description;
    }

    /**
     * 获取账单ID
     *
     * @return 账单ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置账单ID
     *
     * @param id 账单ID
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
     * 获取到期日期
     *
     * @return 到期日期
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * 设置到期日期
     *
     * @param dueDate 到期日期
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * 是否已支付
     *
     * @return 是否已支付
     */
    public boolean isPaid() {
        return isPaid;
    }

    /**
     * 设置是否已支付
     *
     * @param paid 是否已支付
     */
    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    /**
     * 获取支付日期
     *
     * @return 支付日期
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * 设置支付日期
     *
     * @param paymentDate 支付日期
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
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
     * 检查账单是否已逾期
     *
     * @return 是否已逾期
     */
    public boolean isOverdue() {
        if (isPaid) {
            return false;
        }

        // 未付款且已过期
        return dueDate.before(new Date());
    }

    /**
     * 获取距离到期日的天数
     *
     * @return 距离到期日的天数，如果已过期则返回负数
     */
    public int getDaysUntilDue() {
        Date today = new Date();

        if (dueDate.before(today)) {
            // 已过期
            long diff = today.getTime() - dueDate.getTime();
            return -((int) (diff / (1000 * 60 * 60 * 24)) + 1);
        } else {
            // 未过期
            long diff = dueDate.getTime() - today.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
        }
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", dueDate=" + dueDate +
                ", isPaid=" + isPaid +
                ", paymentDate=" + paymentDate +
                ", description='" + description + '\'' +
                '}';
    }

    public String getStatus() {
        if (isPaid) {
            return "已支付";
        } else if (isOverdue()) {
            return "逾期未支付";
        } else {
            return "未支付";
        }
    }

    public void setStatus(String s) {
        // 此方法用于设置状态，但在此类中不需要实现具体逻辑
        // 状态是通过 isPaid 和 isOverdue 方法动态计算的
        // 如果需要，可以在这里添加逻辑来处理特定的状态字符串
        // 例如，如果传入 "已支付"，可以设置 isPaid = true;
        // 但目前不需要此功能，因此留空
    }
}