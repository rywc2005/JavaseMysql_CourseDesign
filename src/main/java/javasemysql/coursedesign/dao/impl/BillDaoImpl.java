package javasemysql.coursedesign.dao.impl;

import javasemysql.coursedesign.dao.BillDao;
import javasemysql.coursedesign.dto.BillQueryParam;
import javasemysql.coursedesign.model.Bill;
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
 * 账单数据访问实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class BillDaoImpl implements BillDao {

    private static final Logger logger = Logger.getLogger(BillDaoImpl.class.getName());

    @Override
    public Bill findById(Connection conn, int id) throws SQLException {
        Bill bill = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM bill WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                bill = mapResultSetToBill(rs);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return bill;
    }

    @Override
    public List<Bill> findByUserId(Connection conn, int userId) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM bill WHERE user_id = ? ORDER BY due_date ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return bills;
    }

    @Override
    public List<Bill> findByParam(Connection conn, BillQueryParam param) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM bill WHERE user_id = ?");
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

            // 添加付款状态过滤条件
            if (param.isPaid() != null) {
                sql.append(" AND is_paid = ?");
                params.add(param.isPaid());
            }

            // 添加日期范围过滤条件
            if (param.getStartDate() != null) {
                sql.append(" AND due_date >= ?");
                params.add(new Timestamp(param.getStartDate().getTime()));
            }

            if (param.getEndDate() != null) {
                sql.append(" AND due_date <= ?");
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
            sql.append(" ORDER BY due_date ASC");

            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return bills;
    }

    @Override
    public List<Bill> findUpcomingBills(Connection conn, int userId, Date startDate, Date endDate) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM bill WHERE user_id = ? AND is_paid = false AND due_date >= ? AND due_date <= ? ORDER BY due_date ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(3, new Timestamp(endDate.getTime()));

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return bills;
    }

    @Override
    public List<Bill> findOverdueBills(Connection conn, int userId, Date currentDate) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM bill WHERE user_id = ? AND is_paid = false AND due_date < ? ORDER BY due_date ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, new Timestamp(currentDate.getTime()));

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Bill bill = mapResultSetToBill(rs);
                bills.add(bill);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return bills;
    }

    @Override
    public boolean insert(Connection conn, Bill bill) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO bill (user_id, account_id, account_name, category, amount, due_date, is_paid, payment_date, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, bill.getUserId());
            pstmt.setInt(2, bill.getAccountId());
            pstmt.setString(3, bill.getAccountName());
            pstmt.setString(4, bill.getCategory());
            pstmt.setDouble(5, bill.getAmount());
            pstmt.setTimestamp(6, new Timestamp(bill.getDueDate().getTime()));
            pstmt.setBoolean(7, bill.isPaid());

            if (bill.getPaymentDate() != null) {
                pstmt.setTimestamp(8, new Timestamp(bill.getPaymentDate().getTime()));
            } else {
                pstmt.setNull(8, java.sql.Types.TIMESTAMP);
            }

            pstmt.setString(9, bill.getDescription());

            int rows = pstmt.executeUpdate();

            // 获取生成的主键
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bill.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public boolean update(Connection conn, Bill bill) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE bill SET account_id = ?, account_name = ?, category = ?, amount = ?, due_date = ?, is_paid = ?, payment_date = ?, description = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bill.getAccountId());
            pstmt.setString(2, bill.getAccountName());
            pstmt.setString(3, bill.getCategory());
            pstmt.setDouble(4, bill.getAmount());
            pstmt.setTimestamp(5, new Timestamp(bill.getDueDate().getTime()));
            pstmt.setBoolean(6, bill.isPaid());

            if (bill.getPaymentDate() != null) {
                pstmt.setTimestamp(7, new Timestamp(bill.getPaymentDate().getTime()));
            } else {
                pstmt.setNull(7, java.sql.Types.TIMESTAMP);
            }

            pstmt.setString(8, bill.getDescription());
            pstmt.setInt(9, bill.getId());

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
            String sql = "DELETE FROM bill WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    /**
     * 将ResultSet映射到Bill对象
     *
     * @param rs ResultSet
     * @return Bill对象
     * @throws SQLException 如果数据库操作失败
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setUserId(rs.getInt("user_id"));
        bill.setAccountId(rs.getInt("account_id"));
        bill.setAccountName(rs.getString("account_name"));
        bill.setCategory(rs.getString("category"));
        bill.setAmount(rs.getDouble("amount"));
        bill.setDueDate(rs.getTimestamp("due_date"));
        bill.setPaid(rs.getBoolean("is_paid"));

        Timestamp paymentDate = rs.getTimestamp("payment_date");
        if (paymentDate != null) {
            bill.setPaymentDate(paymentDate);
        }

        bill.setDescription(rs.getString("description"));
        return bill;
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