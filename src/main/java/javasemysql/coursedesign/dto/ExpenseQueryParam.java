package javasemysql.coursedesign.dto;

import java.util.Date;

/**
 * 支出查询参数
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class ExpenseQueryParam {

    private int userId;
    private String category;
    private int accountId;
    private Date startDate;
    private Date endDate;
    private double minAmount;
    private double maxAmount;
    private String keyword;

    /**
     * 构造函数
     *
     * @param userId 用户ID
     */
    public ExpenseQueryParam(int userId) {
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
     * 获取最小金额
     *
     * @return 最小金额
     */
    public double getMinAmount() {
        return minAmount;
    }

    /**
     * 设置最小金额
     *
     * @param minAmount 最小金额
     */
    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * 获取最大金额
     *
     * @return 最大金额
     */
    public double getMaxAmount() {
        return maxAmount;
    }

    /**
     * 设置最大金额
     *
     * @param maxAmount 最大金额
     */
    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    /**
     * 获取关键字
     *
     * @return 关键字
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * 设置关键字
     *
     * @param keyword 关键字
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setDescription(String keyword) {
        this.keyword = keyword;
    }
}