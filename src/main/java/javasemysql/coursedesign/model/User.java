package javasemysql.coursedesign.model;

import java.util.Date;

public class User {
    private int id;
    private String name;
    private String password;
    private String email;
    private String role;
    private Date createdAt;
    private Date lastLogin;

    // 构造方法
    public User() {}

    public User(int id, String name, String password, String email, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.createdAt = new Date();
    }

    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public void setUsername(String username) {
        // 这里可以添加逻辑来设置用户名
        this.name = username;
    }

    public void setPhone(String phone) {
        // 这里可以添加逻辑来设置电话号码
        // 假设我们没有电话号码字段，这里只是示例
        System.out.println("Setting phone number is not implemented.");
    }

    public void setCreateTime(Date date) {
        // 这里可以添加逻辑来设置创建时间
        if (date != null) {
            this.createdAt = date;
        } else {
            this.createdAt = new Date(); // 如果传入null，则使用当前时间
        }
    }

    public String getUsername() {
        // 这里可以添加逻辑来获取用户名
        return this.name;
    }
}