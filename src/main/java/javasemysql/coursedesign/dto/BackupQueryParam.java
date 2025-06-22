package javasemysql.coursedesign.dto;

import java.util.Date;

/**
 * 备份查询参数
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BackupQueryParam {

    private int userId;
    private String description;
    private Date startDate;
    private Date endDate;

    /**
     * 构造函数
     *
     * @param userId 用户ID
     */
    public BackupQueryParam(int userId) {
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
}