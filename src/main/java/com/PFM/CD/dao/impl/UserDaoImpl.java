package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.UserDao;
import com.PFM.CD.entity.User;
import com.PFM.CD.utils.db.DatabaseUtil;

import com.PFM.CD.utils.db.LocalDateBeanProcessor;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * 用户数据访问对象实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class UserDaoImpl implements UserDao {
    private final QueryRunner queryRunner;
    private final BasicRowProcessor rowProcessor;

    public UserDaoImpl() {
        this.queryRunner = DatabaseUtil.createQueryRunner();
        this.rowProcessor = new BasicRowProcessor(new LocalDateBeanProcessor());
    }

    @Override
    public User createUser(User user) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDate.now());
            }

            String sql = "INSERT INTO users (username, password_hash, email, created_at) VALUES (?, ?, ?, ?)";

            long userId = queryRunner.insert(conn, sql, new ScalarHandler<Long>(),
                    user.getUsername(),
                    user.getPasswordHash(),
                    user.getEmail(),
                    java.sql.Date.valueOf(user.getCreatedAt()));

            user.setUserId((int) userId);
            return user;
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    @Override
    public User findById(int userId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            String sql = "SELECT user_id AS userId, username, password_hash AS passwordHash, " +
                    "email, created_at AS createdAt FROM users WHERE user_id = ?";
            return queryRunner.query(conn, sql, new BeanHandler<>(User.class, rowProcessor), userId);
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    @Override
    public User findByUsername(String username) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            String sql = "SELECT user_id AS userId, username, password_hash AS passwordHash, " +
                    "email, created_at AS createdAt FROM users WHERE username = ?";
            return queryRunner.query(conn, sql, new BeanHandler<>(User.class, rowProcessor), username);
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            String sql = "SELECT user_id AS userId, username, password_hash AS passwordHash, " +
                    "email, created_at AS createdAt FROM users WHERE email = ?";
            return queryRunner.query(conn, sql, new BeanHandler<>(User.class, rowProcessor), email);
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            String sql = "SELECT user_id AS userId, username, password_hash AS passwordHash, " +
                    "email, created_at AS createdAt FROM users";
            return queryRunner.query(conn, sql, new BeanListHandler<>(User.class, rowProcessor));
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }
    @Override
    public int updateUser(User user) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            String sql = "UPDATE users SET username = ?, password_hash = ?, email = ? WHERE user_id = ?";
            return queryRunner.update(conn, sql,
                    user.getUsername(),
                    user.getPasswordHash(),
                    user.getEmail(),
                    user.getUserId());
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    @Override
    public int deleteUser(int userId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            String sql = "DELETE FROM users WHERE user_id = ?";
            return queryRunner.update(conn, sql, userId);
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }

    @Override
    public boolean verifyPassword(String username, String hashedPassword) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();

            String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password_hash = ?";
            Long count = queryRunner.query(conn, sql, new ScalarHandler<>(), username, hashedPassword);
            return count != null && count > 0;
        } finally {
            DatabaseUtil.closeConnection(conn);
        }
    }
}