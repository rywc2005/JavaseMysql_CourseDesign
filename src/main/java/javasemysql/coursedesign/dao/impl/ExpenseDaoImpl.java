package javasemysql.coursedesign.dao.impl;

import javasemysql.coursedesign.dao.ExpenseDao;
import javasemysql.coursedesign.dto.ExpenseQueryParam;
import javasemysql.coursedesign.model.Expense;
import javasemysql.coursedesign.utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 支出数据访问实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class ExpenseDaoImpl implements ExpenseDao {

    private static final Logger logger = Logger.getLogger(ExpenseDaoImpl.class.getName());

    @Override
    public Expense findById(Connection conn, int id) throws SQLException {
        Expense expense = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM expense WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                expense = mapResultSetToExpense(rs);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return expense;
    }

    @Override
    public List<Expense> findByUserId(Connection conn, int userId) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM expense WHERE user_id = ? ORDER BY date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Expense expense = mapResultSetToExpense(rs);
                expenses.add(expense);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return expenses;
    }

    @Override
    public List<Expense> findByParam(Connection conn, ExpenseQueryParam param) throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM expense WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(param.getUserId());

            // 添加类别过滤条件
            if (param.getCategory() != null && !param.getCategory().isEmpty()) {
                sql.append(" AND category LIKE ?");
                params.add("%" + param.getCategory() + "%");
            }

            // 添加账户ID过滤条件
            if (param.getAccountId() > 0) {
                sql.append(" AND account_id = ?");
                params.add(param.getAccountId());
            }

            // 添加日期范围过滤条件
            if (param.getStartDate() != null) {
                sql.append(" AND date >= ?");
                params.add(new Timestamp(param.getStartDate().getTime()));
            }

            if (param.getEndDate() != null) {
                sql.append(" AND date <= ?");
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
            sql.append(" ORDER BY date DESC");

            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Expense expense = mapResultSetToExpense(rs);
                expenses.add(expense);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return expenses;
    }

    @Override
    public boolean insert(Connection conn, Expense expense) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO expense (user_id, account_id, account_name, category, amount, date, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, expense.getUserId());
            pstmt.setInt(2, expense.getAccountId());
            pstmt.setString(3, expense.getAccountName());
            pstmt.setString(4, expense.getCategory());
            pstmt.setDouble(5, expense.getAmount());
            pstmt.setTimestamp(6, new Timestamp(expense.getDate().getTime()));
            pstmt.setString(7, expense.getDescription());

            int rows = pstmt.executeUpdate();

            // 获取生成的主键
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    expense.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public boolean update(Connection conn, Expense expense) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE expense SET account_id = ?, account_name = ?, category = ?, amount = ?, date = ?, description = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, expense.getAccountId());
            pstmt.setString(2, expense.getAccountName());
            pstmt.setString(3, expense.getCategory());
            pstmt.setDouble(4, expense.getAmount());
            pstmt.setTimestamp(5, new Timestamp(expense.getDate().getTime()));
            pstmt.setString(6, expense.getDescription());
            pstmt.setInt(7, expense.getId());

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
            String sql = "DELETE FROM expense WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public double getTotalExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalExpense = 0.0;

        try {
            String sql = "SELECT SUM(amount) FROM expense WHERE user_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalExpense = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return totalExpense;
    }

    @Override
    public double getTotalExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalExpense = 0.0;

        try {
            String sql = "SELECT SUM(amount) FROM expense WHERE user_id = ? AND account_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);
            params.add(accountId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalExpense = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return totalExpense;
    }

    @Override
    public double getTotalExpenseByDateRangeAndCategory(Connection conn, int userId, Date startDate, Date endDate, String category) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalExpense = 0.0;

        try {
            String sql = "SELECT SUM(amount) FROM expense WHERE user_id = ? AND category = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);
            params.add(category);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalExpense = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return totalExpense;
    }

    @Override
    public double getAvgExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double avgExpense = 0.0;

        try {
            String sql = "SELECT AVG(amount) FROM expense WHERE user_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                avgExpense = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return avgExpense;
    }

    @Override
    public double getAvgExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double avgExpense = 0.0;

        try {
            String sql = "SELECT AVG(amount) FROM expense WHERE user_id = ? AND account_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);
            params.add(accountId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                avgExpense = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return avgExpense;
    }

    @Override
    public double getMaxExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double maxExpense = 0.0;

        try {
            String sql = "SELECT MAX(amount) FROM expense WHERE user_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                maxExpense = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return maxExpense;
    }

    @Override
    public double getMaxExpenseByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double maxExpense = 0.0;

        try {
            String sql = "SELECT MAX(amount) FROM expense WHERE user_id = ? AND account_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);
            params.add(accountId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                maxExpense = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return maxExpense;
    }

    @Override
    public Map<String, Double> getExpenseByCategory(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Double> categoryExpenses = new HashMap<>();

        try {
            String sql = "SELECT category, SUM(amount) FROM expense WHERE user_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            sql += " GROUP BY category ORDER BY SUM(amount) DESC";

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String category = rs.getString(1);
                double amount = rs.getDouble(2);
                categoryExpenses.put(category, amount);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return categoryExpenses;
    }

    @Override
    public Map<String, Double> getExpenseByCategory(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Double> categoryExpenses = new HashMap<>();

        try {
            String sql = "SELECT category, SUM(amount) FROM expense WHERE user_id = ? AND account_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);
            params.add(accountId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            sql += " GROUP BY category ORDER BY SUM(amount) DESC";

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String category = rs.getString(1);
                double amount = rs.getDouble(2);
                categoryExpenses.put(category, amount);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return categoryExpenses;
    }

    @Override
    public List<Object[]> getExpenseByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Object[]> timeExpenses = new ArrayList<>();

        try {
            String dateFormat;

            // 根据分组类型设置日期格式
            switch (groupBy.toLowerCase()) {
                case "day":
                    dateFormat = "DATE(date)";
                    break;
                case "week":
                    dateFormat = "CONCAT(YEAR(date), '-', WEEK(date))";
                    break;
                case "month":
                    dateFormat = "DATE_FORMAT(date, '%Y-%m')";
                    break;
                case "quarter":
                    dateFormat = "CONCAT(YEAR(date), '-Q', QUARTER(date))";
                    break;
                case "year":
                    dateFormat = "YEAR(date)";
                    break;
                default:
                    dateFormat = "DATE(date)";
            }

            String sql = "SELECT " + dateFormat + " AS time_period, SUM(amount) FROM expense WHERE user_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            sql += " GROUP BY time_period ORDER BY ";

            // 按时间排序
            switch (groupBy.toLowerCase()) {
                case "day":
                    sql += "DATE(date)";
                    break;
                case "week":
                    sql += "YEAR(date), WEEK(date)";
                    break;
                case "month":
                    sql += "YEAR(date), MONTH(date)";
                    break;
                case "quarter":
                    sql += "YEAR(date), QUARTER(date)";
                    break;
                case "year":
                    sql += "YEAR(date)";
                    break;
                default:
                    sql += "DATE(date)";
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getObject(1);
                row[1] = rs.getDouble(2);
                timeExpenses.add(row);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return timeExpenses;
    }

    @Override
    public List<Object[]> getExpenseByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Object[]> timeExpenses = new ArrayList<>();

        try {
            String dateFormat;

            // 根据分组类型设置日期格式
            switch (groupBy.toLowerCase()) {
                case "day":
                    dateFormat = "DATE(date)";
                    break;
                case "week":
                    dateFormat = "CONCAT(YEAR(date), '-', WEEK(date))";
                    break;
                case "month":
                    dateFormat = "DATE_FORMAT(date, '%Y-%m')";
                    break;
                case "quarter":
                    dateFormat = "CONCAT(YEAR(date), '-Q', QUARTER(date))";
                    break;
                case "year":
                    dateFormat = "YEAR(date)";
                    break;
                default:
                    dateFormat = "DATE(date)";
            }

            String sql = "SELECT " + dateFormat + " AS time_period, SUM(amount) FROM expense WHERE user_id = ? AND account_id = ?";
            List<Object> params = new ArrayList<>();
            params.add(userId);
            params.add(accountId);

            if (startDate != null) {
                sql += " AND date >= ?";
                params.add(new Timestamp(startDate.getTime()));
            }

            if (endDate != null) {
                sql += " AND date <= ?";
                params.add(new Timestamp(endDate.getTime()));
            }

            sql += " GROUP BY time_period ORDER BY ";

            // 按时间排序
            switch (groupBy.toLowerCase()) {
                case "day":
                    sql += "DATE(date)";
                    break;
                case "week":
                    sql += "YEAR(date), WEEK(date)";
                    break;
                case "month":
                    sql += "YEAR(date), MONTH(date)";
                    break;
                case "quarter":
                    sql += "YEAR(date), QUARTER(date)";
                    break;
                case "year":
                    sql += "YEAR(date)";
                    break;
                default:
                    sql += "DATE(date)";
            }

            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getObject(1);
                row[1] = rs.getDouble(2);
                timeExpenses.add(row);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return timeExpenses;
    }

    /**
     * 将ResultSet映射到Expense对象
     *
     * @param rs ResultSet
     * @return Expense对象
     * @throws SQLException 如果数据库操作失败
     */
    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setUserId(rs.getInt("user_id"));
        expense.setAccountId(rs.getInt("account_id"));
        expense.setAccountName(rs.getString("account_name"));
        expense.setCategory(rs.getString("category"));
        expense.setAmount(rs.getDouble("amount"));
        expense.setDate(rs.getTimestamp("date"));
        expense.setDescription(rs.getString("description"));
        return expense;
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