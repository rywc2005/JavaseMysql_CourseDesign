package javasemysql.coursedesign.dao.impl;

import javasemysql.coursedesign.dao.BackupDao;
import javasemysql.coursedesign.dto.BackupQueryParam;
import javasemysql.coursedesign.model.Backup;
import javasemysql.coursedesign.utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 备份数据访问实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BackupDaoImpl implements BackupDao {

    private static final Logger logger = Logger.getLogger(BackupDaoImpl.class.getName());

    @Override
    public Backup findById(Connection conn, int id) throws SQLException {
        Backup backup = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM backup WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                backup = mapResultSetToBackup(rs);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return backup;
    }

    @Override
    public List<Backup> findByUserId(Connection conn, int userId) throws SQLException {
        List<Backup> backups = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM backup WHERE user_id = ? ORDER BY created_at DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Backup backup = mapResultSetToBackup(rs);
                backups.add(backup);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return backups;
    }

    @Override
    public List<Backup> findByParam(Connection conn, BackupQueryParam param) throws SQLException {
        List<Backup> backups = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM backup WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(param.getUserId());

            // 添加描述过滤条件
            if (param.getDescription() != null && !param.getDescription().isEmpty()) {
                sql.append(" AND description LIKE ?");
                params.add("%" + param.getDescription() + "%");
            }

            // 添加日期范围过滤条件
            if (param.getStartDate() != null) {
                sql.append(" AND created_at >= ?");
                params.add(new Timestamp(param.getStartDate().getTime()));
            }

            if (param.getEndDate() != null) {
                sql.append(" AND created_at <= ?");
                params.add(new Timestamp(param.getEndDate().getTime()));
            }

            // 添加排序
            sql.append(" ORDER BY created_at DESC");

            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Backup backup = mapResultSetToBackup(rs);
                backups.add(backup);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return backups;
    }

    @Override
    public boolean insert(Connection conn, Backup backup) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO backup (user_id, file_path, created_at) VALUES ( ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, backup.getUserId());
            pstmt.setString(2, backup.getFilePath());
            pstmt.setTimestamp(3, new Timestamp(backup.getCreatedAt().getTime()));

            int rows = pstmt.executeUpdate();

            // 获取生成的主键
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    backup.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public boolean update(Connection conn, Backup backup) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE backup SET file_path = ?, created_at = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, backup.getFilePath());
            pstmt.setTimestamp(2, new Timestamp(backup.getCreatedAt().getTime()));
            pstmt.setInt(3, backup.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public boolean delete(Connection conn, int id) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "DELETE FROM backup WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    /**
     * 将ResultSet映射到Backup对象
     *
     * @param rs ResultSet
     * @return Backup对象
     * @throws SQLException 如果数据库操作失败
     */
    private Backup mapResultSetToBackup(ResultSet rs) throws SQLException {
        Backup backup = new Backup();
        backup.setId(rs.getInt("id"));
        backup.setUserId(rs.getInt("user_id"));
        backup.setFilePath(rs.getString("file_path"));
        backup.setCreatedAt(rs.getTimestamp("created_at"));
        return backup;
    }

    /**
     * 关闭数据库资源
     *
     * @param conn 数据库连接
     * @param stmt PreparedStatement
     * @param rs ResultSet
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            LogUtils.error("关闭数据库资源失败", e);
        }
    }
}