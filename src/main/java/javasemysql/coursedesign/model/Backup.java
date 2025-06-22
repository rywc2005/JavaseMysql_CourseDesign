package javasemysql.coursedesign.model;

import java.util.Date;

/**
 * 备份实体类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class Backup {

    private int id;
    private int userId;
    private String filePath;
    private String description;
    private Date createdAt;

    /**
     * 默认构造函数
     */
    public Backup() {
    }

    /**
     * 构造函数
     *
     * @param id 备份ID
     * @param userId 用户ID
     * @param filePath 文件路径
     * @param description 描述
     * @param createdAt 创建时间
     */
    public Backup(int id, int userId, String filePath, String description, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.filePath = filePath;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * 获取备份ID
     *
     * @return 备份ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置备份ID
     *
     * @param id 备份ID
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
     * 获取文件路径
     *
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 设置文件路径
     *
     * @param filePath 文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Backup{" +
                "id=" + id +
                ", userId=" + userId +
                ", filePath='" + filePath + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}