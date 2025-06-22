package javasemysql.coursedesign.dto;

import java.util.Date;

/**
 * 预算查询参数
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BudgetQueryParam {

    private int userId;
    private String category;
    private Date startDate;
    private Date endDate;
    private double minAmount;
    private double maxAmount;

    /**
     * 构造函数
     *
     * @param userId 用户ID
     */
    public BudgetQueryParam(int userId) {
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

    public void setIsExceeded(boolean b) {
        // This method is not used in this DTO, but can be implemented if needed
        // It could be used to set a flag indicating if the budget exceeds a certain limit
        // For now, it does nothing

    }
}