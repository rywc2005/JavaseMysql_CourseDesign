package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.interfaces.AccountDao;
import com.PFM.CD.entity.Account;
import com.PFM.CD.entity.enums.AccountStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户数据访问实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class AccountDaoImpl extends BaseDaoImpl<Account, Integer> implements AccountDao {

    @Override
    public boolean save(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, account_name, balance, status) VALUES (?, ?, ?, ?)";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, account.getUserId());
                ps.setString(2, account.getAccountName());
                ps.setBigDecimal(3, account.getBalance());
                ps.setString(4, account.getStatus().toString());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            account.setAccountId(rs.getInt(1));
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public Account findById(Integer accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean update(Account account) throws SQLException {
        String sql = "UPDATE accounts SET account_name = ?, balance = ?, status = ? WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getAccountName());
            ps.setBigDecimal(2, account.getBalance());
            ps.setString(3, account.getStatus().toString());
            ps.setInt(4, account.getAccountId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Account> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        }

        return accounts;
    }

    @Override
    public List<Account> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        }

        return accounts;
    }

    @Override
    public List<Account> findByUserIdAndStatus(int userId, AccountStatus status) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ? AND status = ?";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, status.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }
        }

        return accounts;
    }

    @Override
    public Account findByUserIdAndName(int userId, String accountName) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ? AND account_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, accountName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAccount(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean updateBalance(int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int accountId, AccountStatus newStatus) throws SQLException {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.toString());
            ps.setInt(2, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public BigDecimal calculateTotalBalance(int userId) throws SQLException {
        String sql = "SELECT SUM(balance) FROM accounts WHERE user_id = ? AND status = 'ACTIVE'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal(1);
                    return result != null ? result : BigDecimal.ZERO;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    @Override
    public boolean isAccountNameExists(int userId, String accountName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE user_id = ? AND account_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, accountName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    /**
     * 将ResultSet映射为Account对象
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setAccountName(rs.getString("account_name"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(AccountStatus.valueOf(rs.getString("status")));
        return account;
    }
}