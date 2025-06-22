package javasemysql.coursedesign.model;

    import java.sql.Timestamp;

    public class Account {
        private int id;
        private int userId;
        private String name;
        private double balance;
        private Timestamp createdAt;

        // 构造方法
        public Account() {}

        public Account(int id, int userId, String name, double balance, Timestamp createdAt) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.balance = balance;
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", name='" + name + '\'' +
                    ", balance=" + balance +
                    ", createdAt=" + createdAt +
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public Timestamp getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
        }
    }