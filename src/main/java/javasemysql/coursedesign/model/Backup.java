package javasemysql.coursedesign.model;

import java.sql.Timestamp;

public class Backup {
    private int id;
    private int userId;
    private String filePath;
    private Timestamp createdAt;

    @Override
    public String toString() {
        return "Backup{" +
                "id=" + id +
                ", userId=" + userId +
                ", filePath='" + filePath + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // 构造方法
    public Backup() {}

    public Backup(int id, int userId, String filePath, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.filePath = filePath;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


}