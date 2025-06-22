package javasemysql.coursedesign.dao.impl;

import javasemysql.coursedesign.dao.UserDao;
import javasemysql.coursedesign.dto.UserQueryParam;
import javasemysql.coursedesign.model.User;
import javasemysql.coursedesign.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 用户数据访问实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class UserDaoImpl implements UserDao {

    private static final Logger logger = Logger.getLogger(UserDaoImpl.class.getName());

    @Override
    public boolean insert(User user) {
        String sql = "INSERT INTO user (name, password, email, role, created_at) VALUES (?, ?, ?, ?, NOW())";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());

            int result = ps.executeUpdate();

            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error inserting user", e);
            return false;
        } finally {
            DBUtils.closeResources(conn, ps, null);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE user SET name = ?, password = ?, email = ?, role = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());
            ps.setInt(5, user.getId());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating user", e);
            return false;
        } finally {
            DBUtils.closeResources(conn, ps, null);
        }
    }

    @Override
    public boolean delete(int userId) {
        String sql = "DELETE FROM user WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting user", e);
            return false;
        } finally {
            DBUtils.closeResources(conn, ps, null);
        }
    }

    @Override
    public User findById(int userId) {
        String sql = "SELECT * FROM user WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by ID", e);
        } finally {
            DBUtils.closeResources(conn, ps, rs);
        }

        return null;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE name = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by username", e);
        } finally {
            DBUtils.closeResources(conn, ps, rs);
        }

        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by email", e);
        } finally {
            DBUtils.closeResources(conn, ps, rs);
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM user";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all users", e);
        } finally {
            DBUtils.closeResources(conn, ps, rs);
        }

        return users;
    }

    @Override
    public List<User> findByCondition(UserQueryParam param) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user WHERE 1=1");
        List<Object> paramList = new ArrayList<>();

        // 拼接查询条件
        if (param.getName() != null && !param.getName().isEmpty()) {
            sql.append(" AND name LIKE ?");
            paramList.add("%" + param.getName() + "%");
        }

        if (param.getEmail() != null && !param.getEmail().isEmpty()) {
            sql.append(" AND email LIKE ?");
            paramList.add("%" + param.getEmail() + "%");
        }

        if (param.getRole() != null && !param.getRole().isEmpty()) {
            sql.append(" AND role = ?");
            paramList.add(param.getRole());
        }

        // 添加排序
        sql.append(" ORDER BY id DESC");

        // 添加分页
        if (param.getPageParam() != null) {
            sql.append(" LIMIT ?, ?");
            paramList.add(param.getPageParam().getOffset());
            paramList.add(param.getPageParam().getLimit());
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding users by condition", e);
        } finally {
            DBUtils.closeResources(conn, ps, rs);
        }

        return users;
    }

    @Override
    public int countByCondition(UserQueryParam param) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM user WHERE 1=1");
        List<Object> paramList = new ArrayList<>();

        // 拼接查询条件
        if (param.getName() != null && !param.getName().isEmpty()) {
            sql.append(" AND name LIKE ?");
            paramList.add("%" + param.getName() + "%");
        }

        if (param.getEmail() != null && !param.getEmail().isEmpty()) {
            sql.append(" AND email LIKE ?");
            paramList.add("%" + param.getEmail() + "%");
        }

        if (param.getRole() != null && !param.getRole().isEmpty()) {
            sql.append(" AND role = ?");
            paramList.add(param.getRole());
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            ps = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting users by condition", e);
        } finally {
            DBUtils.closeResources(conn, ps, rs);
        }

        return 0;
    }

    @Override
    public boolean updateLastLogin(int userId) {
        return false;
    }

    /**
     * 将ResultSet映射为User对象
     *
     * @param rs 结果集
     * @return User对象
     * @throws SQLException 如果映射失败
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}