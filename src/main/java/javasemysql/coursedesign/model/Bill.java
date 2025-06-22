package javasemysql.coursedesign.model;

import java.sql.Date;

public class Bill {
    private int id;
    private int userId;
    private int accountId;
    private double amount;
    private Date dueDate;
    private String status;

    // 构造方法
    public Bill() {}

    public Bill(int id, int userId, int accountId,
               double amount, Date dueDate, String status) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", userId=" + userId +
                ", accountId=" + accountId +
                ", amount=" + amount +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                '}';
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

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public boolean isPaid() {
        if (this.status.equals("已支付")){
            return true;
        } else {
            return false;
        }
    }
    public boolean setPaid(boolean isPaid) {
        if (this.status.equals("未支付")){
            this.status = "已支付";
            return true;
        } else {
            return false;
        }
    }
}