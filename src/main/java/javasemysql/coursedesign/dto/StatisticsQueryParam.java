package javasemysql.coursedesign.dto;

import java.util.Date;

/**
 * 统计查询参数数据传输对象
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class StatisticsQueryParam {
    private Integer userId;         // 用户ID
    private Integer accountId;      // 账户ID
    private String category;        // 分类
    private Date startDate;         // 开始日期
    private Date endDate;           // 结束日期
    private String groupBy;         // 分组方式（day, week, month, year）
    private String chartType;       // 图表类型（bar, line, pie）

    /**
     * 默认构造函数
     */
    public StatisticsQueryParam() {
    }

    /**
     * 带用户ID的构造函数
     *
     * @param userId 用户ID
     */
    public StatisticsQueryParam(Integer userId) {
        this.userId = userId;
    }

    /**
     * 带用户ID和日期范围的构造函数
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    public StatisticsQueryParam(Integer userId, Date startDate, Date endDate) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取账户ID
     *
     * @return 账户ID
     */
    public Integer getAccountId() {
        return accountId;
    }

    /**
     * 设置账户ID
     *
     * @param accountId 账户ID
     */
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    /**
     * 获取分类
     *
     * @return 分类
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置分类
     *
     * @param category 分类
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
     * 获取分组方式
     *
     * @return 分组方式
     */
    public String getGroupBy() {
        return groupBy;
    }

    /**
     * 设置分组方式
     *
     * @param groupBy 分组方式
     */
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * 获取图表类型
     *
     * @return 图表类型
     */
    public String getChartType() {
        return chartType;
    }

    /**
     * 设置图表类型
     *
     * @param chartType 图表类型
     */
    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
}