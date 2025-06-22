package javasemysql.coursedesign.dao.impl;

import javasemysql.coursedesign.dao.IncomeDao;
import javasemysql.coursedesign.dto.IncomeQueryParam;
import javasemysql.coursedesign.model.Income;
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
 * 收入数据访问实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class IncomeDaoImpl implements IncomeDao {

    private static final Logger logger = Logger.getLogger(IncomeDaoImpl.class.getName());

    @Override
    public Income findById(Connection conn, int id) throws SQLException {
        Income income = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM income WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                income = mapResultSetToIncome(rs);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return income;
    }

    @Override
    public List<Income> findByUserId(Connection conn, int userId) throws SQLException {
        List<Income> incomes = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM income WHERE user_id = ? ORDER BY date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Income income = mapResultSetToIncome(rs);
                incomes.add(income);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return incomes;
    }

    @Override
    public List<Income> findByParam(Connection conn, IncomeQueryParam param) throws SQLException {
        List<Income> incomes = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM income WHERE user_id = ?");
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

            // 添加关键字搜索
            if (param.getKeyword() != null && !param.getKeyword().isEmpty()) {
                sql.append(" AND (description LIKE ? OR category LIKE ?)");
                params.add("%" + param.getKeyword() + "%");
                params.add("%" + param.getKeyword() + "%");
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
                Income income = mapResultSetToIncome(rs);
                incomes.add(income);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return incomes;
    }

    @Override
    public boolean insert(Connection conn, Income income) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO income (user_id, account_id, category, amount, date, description) VALUES ( ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, income.getUserId());
            pstmt.setInt(2, income.getAccountId());
            pstmt.setString(3, income.getCategory());
            pstmt.setDouble(4, income.getAmount());
            pstmt.setTimestamp(5, new Timestamp(income.getDate().getTime()));
            pstmt.setString(6, income.getDescription());

            int rows = pstmt.executeUpdate();

            // 获取生成的主键
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    income.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public boolean update(Connection conn, Income income) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE income SET account_id = ?,  category = ?, amount = ?, date = ?, description = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, income.getAccountId());
            pstmt.setString(2, income.getCategory());
            pstmt.setDouble(3, income.getAmount());
            pstmt.setTimestamp(4, new Timestamp(income.getDate().getTime()));
            pstmt.setString(5, income.getDescription());
            pstmt.setInt(6, income.getId());

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
            String sql = "DELETE FROM income WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public double getTotalIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalIncome = 0.0;

        try {
            String sql = "SELECT SUM(amount) FROM income WHERE user_id = ?";
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
                totalIncome = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return totalIncome;
    }

    @Override
    public double getTotalIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalIncome = 0.0;

        try {
            String sql = "SELECT SUM(amount) FROM income WHERE user_id = ? AND account_id = ?";
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
                totalIncome = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return totalIncome;
    }

    @Override
    public double getTotalIncomeByDateRangeAndCategory(Connection conn, int userId, Date startDate, Date endDate, String category) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalIncome = 0.0;

        try {
            String sql = "SELECT SUM(amount) FROM income WHERE user_id = ? AND category = ?";
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
                totalIncome = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return totalIncome;
    }

    @Override
    public double getAvgIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double avgIncome = 0.0;

        try {
            String sql = "SELECT AVG(amount) FROM income WHERE user_id = ?";
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
                avgIncome = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return avgIncome;
    }

    @Override
    public double getAvgIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double avgIncome = 0.0;

        try {
            String sql = "SELECT AVG(amount) FROM income WHERE user_id = ? AND account_id = ?";
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
                avgIncome = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return avgIncome;
    }

    @Override
    public double getMaxIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double maxIncome = 0.0;

        try {
            String sql = "SELECT MAX(amount) FROM income WHERE user_id = ?";
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
                maxIncome = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return maxIncome;
    }

    @Override
    public double getMaxIncomeByDateRange(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double maxIncome = 0.0;

        try {
            String sql = "SELECT MAX(amount) FROM income WHERE user_id = ? AND account_id = ?";
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
                maxIncome = rs.getDouble(1);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return maxIncome;
    }

    @Override
    public Map<String, Double> getIncomeByCategory(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Double> categoryIncomes = new HashMap<>();

        try {
            String sql = "SELECT category, SUM(amount) FROM income WHERE user_id = ?";
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
                categoryIncomes.put(category, amount);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return categoryIncomes;
    }

    @Override
    public Map<String, Double> getIncomeByCategory(Connection conn, int userId, Date startDate, Date endDate, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Double> categoryIncomes = new HashMap<>();

        try {
            String sql = "SELECT category, SUM(amount) FROM income WHERE user_id = ? AND account_id = ?";
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
                categoryIncomes.put(category, amount);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return categoryIncomes;
    }

    @Override
    public List<Object[]> getIncomeByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Object[]> timeIncomes = new ArrayList<>();

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

            String sql = "SELECT " + dateFormat + " AS time_period, SUM(amount) FROM income WHERE user_id = ?";
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
                timeIncomes.add(row);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return timeIncomes;
    }

    @Override
    public List<Object[]> getIncomeByTime(Connection conn, int userId, Date startDate, Date endDate, String groupBy, int accountId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Object[]> timeIncomes = new ArrayList<>();

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

            String sql = "SELECT " + dateFormat + " AS time_period, SUM(amount) FROM income WHERE user_id = ? AND account_id = ?";
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
                timeIncomes.add(row);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return timeIncomes;
    }

    /**
     * 将ResultSet映射到Income对象
     *
     * @param rs ResultSet
     * @return Income对象
     * @throws SQLException 如果数据库操作失败
     */
    private Income mapResultSetToIncome(ResultSet rs) throws SQLException {
        Income income = new Income();
        income.setId(rs.getInt("id"));
        income.setUserId(rs.getInt("user_id"));
        income.setAccountId(rs.getInt("account_id"));
        income.setCategory(rs.getString("category"));
        income.setAmount(rs.getDouble("amount"));
        income.setDescription(rs.getString("description"));
        return income;
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