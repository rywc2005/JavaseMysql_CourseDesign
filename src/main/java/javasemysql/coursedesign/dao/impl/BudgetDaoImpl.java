package javasemysql.coursedesign.dao.impl;

import javasemysql.coursedesign.dao.BudgetDao;
import javasemysql.coursedesign.dto.BudgetQueryParam;
import javasemysql.coursedesign.model.Budget;
import javasemysql.coursedesign.utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 预算数据访问实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BudgetDaoImpl implements BudgetDao {

    private static final Logger logger = Logger.getLogger(BudgetDaoImpl.class.getName());

    @Override
    public Budget findById(Connection conn, int id) throws SQLException {
        Budget budget = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM budget WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                budget = mapResultSetToBudget(rs);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return budget;
    }

    @Override
    public List<Budget> findByUserId(Connection conn, int userId) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM budget WHERE user_id = ? ORDER BY start_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                budgets.add(budget);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return budgets;
    }

    @Override
    public List<Budget> findByParam(Connection conn, BudgetQueryParam param) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM budget WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(param.getUserId());

            // 添加类别过滤条件
            if (param.getCategory() != null && !param.getCategory().isEmpty()) {
                sql.append(" AND category LIKE ?");
                params.add("%" + param.getCategory() + "%");
            }

            // 添加日期范围过滤条件 - 查找与指定日期范围有重叠的预算
            if (param.getStartDate() != null && param.getEndDate() != null) {
                // 结束日期大于等于开始参数，且开始日期小于等于结束参数
                sql.append(" AND (end_date >= ? AND start_date <= ?)");
                params.add(new Timestamp(param.getStartDate().getTime()));
                params.add(new Timestamp(param.getEndDate().getTime()));
            } else if (param.getStartDate() != null) {
                // 结束日期大于等于开始参数
                sql.append(" AND end_date >= ?");
                params.add(new Timestamp(param.getStartDate().getTime()));
            } else if (param.getEndDate() != null) {
                // 开始日期小于等于结束参数
                sql.append(" AND start_date <= ?");
                params.add(new Timestamp(param.getEndDate().getTime()));
            }

            // 添加金额范围过滤条件
            if (param.getMinAmount() > 0) {
                sql.append(" AND amount >= ?");
                params.add(param.getMinAmount());
            }

            if (param.getMaxAmount() > 0) {
                sql.append(" AND amount <= ?");
                params.add(param.getMaxAmount());
            }

            // 添加排序
            sql.append(" ORDER BY start_date DESC");

            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                budgets.add(budget);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return budgets;
    }

    @Override
    public List<Budget> findActiveBudgets(Connection conn, int userId, Date currentDate) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM budget WHERE user_id = ? AND start_date <= ? AND end_date >= ? ORDER BY category ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, new Timestamp(currentDate.getTime()));
            pstmt.setTimestamp(3, new Timestamp(currentDate.getTime()));

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                budgets.add(budget);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return budgets;
    }

    @Override
    public List<Budget> findActiveBudgetsByCategory(Connection conn, int userId, String category, Date currentDate) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM budget WHERE user_id = ? AND category = ? AND start_date <= ? AND end_date >= ? ORDER BY start_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            pstmt.setTimestamp(3, new Timestamp(currentDate.getTime()));
            pstmt.setTimestamp(4, new Timestamp(currentDate.getTime()));

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                budgets.add(budget);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return budgets;
    }

    @Override
    public List<Budget> findOverlappingBudgets(Connection conn, int userId, String category, Date startDate, Date endDate) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 查找时间范围有重叠的预算：
            // (startA <= endB) AND (endA >= startB)
            String sql = "SELECT * FROM budget WHERE user_id = ? AND category = ? AND start_date <= ? AND end_date >= ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, category);
            pstmt.setTimestamp(3, new Timestamp(endDate.getTime()));
            pstmt.setTimestamp(4, new Timestamp(startDate.getTime()));

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                budgets.add(budget);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return budgets;
    }

    @Override
    public boolean insert(Connection conn, Budget budget) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO budget (user_id, category, amount, start_date, end_date, description) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, budget.getUserId());
            pstmt.setString(2, budget.getCategory());
            pstmt.setDouble(3, budget.getAmount());
            pstmt.setTimestamp(4, new Timestamp(budget.getStartDate().getTime()));
            pstmt.setTimestamp(5, new Timestamp(budget.getEndDate().getTime()));
            pstmt.setString(6, budget.getDescription());

            int rows = pstmt.executeUpdate();

            // 获取生成的主键
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    budget.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public boolean update(Connection conn, Budget budget) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE budget SET category = ?, amount = ?, start_date = ?, end_date = ?, description = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, budget.getCategory());
            pstmt.setDouble(2, budget.getAmount());
            pstmt.setTimestamp(3, new Timestamp(budget.getStartDate().getTime()));
            pstmt.setTimestamp(4, new Timestamp(budget.getEndDate().getTime()));
            pstmt.setString(5, budget.getDescription());
            pstmt.setInt(6, budget.getId());

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
            String sql = "DELETE FROM budget WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    /**
     * 将ResultSet映射到Budget对象
     *
     * @param rs ResultSet
     * @return Budget对象
     * @throws SQLException 如果数据库操作失败
     */
    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getInt("id"));
        budget.setUserId(rs.getInt("user_id"));
        budget.setCategory(rs.getString("category"));
        budget.setAmount(rs.getDouble("amount"));
        budget.setStartDate(rs.getTimestamp("start_date"));
        budget.setEndDate(rs.getTimestamp("end_date"));
        budget.setDescription(rs.getString("description"));
        return budget;
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