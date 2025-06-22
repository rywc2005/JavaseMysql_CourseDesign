package javasemysql.coursedesign.model;

    import java.sql.Timestamp;

    public class User {
        private int id;
        private String name;
        private String password;
        private String email;
        private String role;
        private Timestamp createdAt;

        // 构造方法
        public User() {}

        public User(int id, String name, String password, String email, String role, Timestamp createdAt) {
            this.id = id;
            this.name = name;
            this.password = password;
            this.email = email;
            this.role = role;
            this.createdAt = createdAt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Timestamp getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    ", email='" + email + '\'' +
                    ", role='" + role + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }