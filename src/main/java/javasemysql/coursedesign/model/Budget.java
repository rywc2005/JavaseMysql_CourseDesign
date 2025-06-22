package javasemysql.coursedesign.model;

    import java.sql.Date;
    import java.sql.Timestamp;

public class Budget {
        private int id;
        private int userId;
        private String category;
        private double amount;
        private Timestamp startDate;
        private Timestamp endDate;

        // 构造方法
        public Budget() {}

        @Override
        public String toString() {
            return "Budget{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", category='" + category + '\'' +
                    ", amount=" + amount +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    '}';
        }

        public Budget(int id, int userId, String category,
                      double amount, Timestamp startDate, Timestamp endDate) {
            this.id = id;
            this.userId = userId;
            this.category = category;
            this.amount = amount;
            this.startDate = startDate;
            this.endDate = endDate;
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public Timestamp getStartDate() {
            return startDate;
        }

        public void setStartDate(Timestamp startDate) {
            this.startDate = startDate;
        }

        public Timestamp getEndDate() {
            return endDate;
        }

        public void setEndDate(Timestamp endDate) {
            this.endDate = endDate;
        }

        public double getUsedAmount() {
            // 假设有一个方法可以计算已使用的预算金额
            // 这里返回0.0作为示例
            return 0.0;
        }

        public void setUsedAmount(double usedAmount) {
            // 假设有一个方法可以设置已使用的预算金额
            // 这里不做实际操作，作为示例
        }

    public double getUsagePercentage() {
        double usedAmount = getUsedAmount();
        if (amount == 0) {
            return 0; // 避免除以零
        }
        return (usedAmount / amount) * 100;

    }
}