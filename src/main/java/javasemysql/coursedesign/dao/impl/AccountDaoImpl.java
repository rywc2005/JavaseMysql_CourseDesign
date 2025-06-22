package javasemysql.coursedesign.dao.impl;

import javasemysql.coursedesign.dao.AccountDao;
import javasemysql.coursedesign.dto.AccountQueryParam;
import javasemysql.coursedesign.model.Account;
import javasemysql.coursedesign.utils.LogUtils;

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
 * 账户数据访问实现类
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class AccountDaoImpl implements AccountDao {

    private static final Logger logger = Logger.getLogger(AccountDaoImpl.class.getName());

    @Override
    public Account findById(Connection conn, int id) throws SQLException {
        Account account = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM account WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                account = mapResultSetToAccount(rs);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return account;
    }

    @Override
    public List<Account> findByUserId(Connection conn, int userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM account WHERE user_id = ? ORDER BY id ASC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                accounts.add(account);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return accounts;
    }

    @Override
    public List<Account> findByParam(Connection conn, AccountQueryParam param) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM account WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(param.getUserId());

            // 添加名称过滤条件
            if (param.getName() != null && !param.getName().isEmpty()) {
                sql.append(" AND name LIKE ?");
                params.add("%" + param.getName() + "%");
            }

            // 添加类型过滤条件
            if (param.getType() != null && !param.getType().isEmpty()) {
                sql.append(" AND type = ?");
                params.add(param.getType());
            }

            // 添加排序
            sql.append(" ORDER BY id ASC");

            pstmt = conn.prepareStatement(sql.toString());

            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                accounts.add(account);
            }
        } finally {
            closeResources(null, pstmt, rs);
        }

        return accounts;
    }

    @Override
    public boolean insert(Connection conn, Account account) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "INSERT INTO account (user_id, name, type, balance, description) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, account.getUserId());
            pstmt.setString(2, account.getName());
            pstmt.setString(3, account.getType());
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getDescription());

            int rows = pstmt.executeUpdate();

            // 获取生成的主键
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getInt(1));
                }
                return true;
            }

            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    @Override
    public boolean update(Connection conn, Account account) throws SQLException {
        PreparedStatement pstmt = null;

        try {
            String sql = "UPDATE account SET name = ?, type = ?, balance = ?, description = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, account.getName());
            pstmt.setString(2, account.getType());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setString(4, account.getDescription());
            pstmt.setInt(5, account.getId());

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
            // 删除关联的收入记录
            String deleteIncomesSql = "DELETE FROM income WHERE account_id = ?";
            pstmt = conn.prepareStatement(deleteIncomesSql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            closeResources(null, pstmt, null);

            // 删除关联的支出记录
            String deleteExpensesSql = "DELETE FROM expense WHERE account_id = ?";
            pstmt = conn.prepareStatement(deleteExpensesSql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            closeResources(null, pstmt, null);

            // 删除关联的账单记录
            String deleteBillsSql = "DELETE FROM bill WHERE account_id = ?";
            pstmt = conn.prepareStatement(deleteBillsSql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            closeResources(null, pstmt, null);

            // 删除账户
            String deleteAccountSql = "DELETE FROM account WHERE id = ?";
            pstmt = conn.prepareStatement(deleteAccountSql);
            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    /**
     * 将ResultSet映射到Account对象
     *
     * @param rs ResultSet
     * @return Account对象
     * @throws SQLException 如果数据库操作失败
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setUserId(rs.getInt("user_id"));
        account.setName(rs.getString("name"));
        account.setType(rs.getString("type"));
        account.setBalance(rs.getDouble("balance"));
        account.setDescription(rs.getString("description"));
        return account;
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