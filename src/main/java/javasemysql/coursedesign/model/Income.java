package javasemysql.coursedesign.model;

    import java.sql.Date;

    public class Income {
        private int id;
        private int userId;
        private int accountId;
        private String category;
        private double amount;
        private String description;
        private Date date;

        // 构造方法
        public Income() {}

        public Income(int id, int userId, int accountId, String category,
                     double amount, String description, Date date) {
            this.id = id;
            this.userId = userId;
            this.accountId = accountId;
            this.category = category;
            this.amount = amount;
            this.description = description;
            this.date = date;
        }

        @Override
        public String toString() {
            return "Income{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", accountId=" + accountId +
                    ", category='" + category + '\'' +
                    ", amount=" + amount +
                    ", description='" + description + '\'' +
                    ", date=" + date +
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }