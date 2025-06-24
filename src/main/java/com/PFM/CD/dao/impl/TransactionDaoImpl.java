package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.interfaces.TransactionDao;
import com.PFM.CD.entity.Transaction;
import com.PFM.CD.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易数据访问实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class TransactionDaoImpl extends BaseDaoImpl<Transaction, Integer> implements TransactionDao {

    @Override
    public boolean save(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, source_account_id, destination_account_id, " +
                "category_id, amount, transaction_type, transaction_date, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, transaction.getUserId());
                setNullableInteger(ps, 2, transaction.getSourceAccountId());
                setNullableInteger(ps, 3, transaction.getDestinationAccountId());
                ps.setInt(4, transaction.getCategoryId());
                ps.setBigDecimal(5, transaction.getAmount());
                ps.setString(6, transaction.getTransactionType().toString());
                ps.setDate(7, Date.valueOf(transaction.getTransactionDate()));
                ps.setString(8, transaction.getDescription());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            transaction.setTransactionId(rs.getInt(1));
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public Transaction findById(Integer transactionId) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.transaction_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transactionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean update(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET source_account_id = ?, destination_account_id = ?, " +
                "category_id = ?, amount = ?, transaction_type = ?, transaction_date = ?, " +
                "description = ? WHERE transaction_id = ? AND user_id = ?";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setNullableInteger(ps, 1, transaction.getSourceAccountId());
                setNullableInteger(ps, 2, transaction.getDestinationAccountId());
                ps.setInt(3, transaction.getCategoryId());
                ps.setBigDecimal(4, transaction.getAmount());
                ps.setString(5, transaction.getTransactionType().toString());
                ps.setDate(6, Date.valueOf(transaction.getTransactionDate()));
                ps.setString(7, transaction.getDescription());
                ps.setInt(8, transaction.getTransactionId());
                ps.setInt(9, transaction.getUserId());

                return ps.executeUpdate() > 0;
            }
        });
    }

    @Override
    public boolean delete(Integer transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, transactionId);
                return ps.executeUpdate() > 0;
            }
        });
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserId(int userId) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.user_id = ? " +
                "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.user_id = ? AND t.transaction_date BETWEEN ? AND ? " +
                "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserIdAndType(int userId, TransactionType type) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.user_id = ? AND t.transaction_type = ? " +
                "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserIdAndCategory(int userId, int categoryId) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.user_id = ? AND t.category_id = ? " +
                "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByUserIdAndAccount(int userId, int accountId) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.user_id = ? AND (t.source_account_id = ? OR t.destination_account_id = ?) " +
                "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, accountId);
            ps.setInt(3, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    @Override
    public List<Transaction> findRecentByUserId(int userId, int limit) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.user_id = ? " +
                "ORDER BY t.transaction_date DESC " +
                "LIMIT ?";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    @Override
    public BigDecimal calculateTotalIncome(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT SUM(amount) FROM transactions " +
                "WHERE user_id = ? AND transaction_type = 'INCOME' " +
                "AND transaction_date BETWEEN ? AND ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

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
    public BigDecimal calculateTotalExpense(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT SUM(amount) FROM transactions " +
                "WHERE user_id = ? AND transaction_type = 'EXPENSE' " +
                "AND transaction_date BETWEEN ? AND ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

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
    public Map<Integer, BigDecimal> calculateExpenseByCategory(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT category_id, SUM(amount) as total " +
                "FROM transactions " +
                "WHERE user_id = ? AND transaction_type = 'EXPENSE' " +
                "AND transaction_date BETWEEN ? AND ? " +
                "GROUP BY category_id";

        Map<Integer, BigDecimal> result = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getInt("category_id"), rs.getBigDecimal("total"));
                }
            }
        }

        return result;
    }

    @Override
    public Map<Integer, BigDecimal> calculateIncomeByCategory(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT category_id, SUM(amount) as total " +
                "FROM transactions " +
                "WHERE user_id = ? AND transaction_type = 'INCOME' " +
                "AND transaction_date BETWEEN ? AND ? " +
                "GROUP BY category_id";

        Map<Integer, BigDecimal> result = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getInt("category_id"), rs.getBigDecimal("total"));
                }
            }
        }

        return result;
    }

    @Override
    public List<Transaction> searchTransactions(int userId, String keyword) throws SQLException {
        String sql = "SELECT t.*, c.category_name, " +
                "sa.account_name as source_account_name, " +
                "da.account_name as destination_account_name " +
                "FROM transactions t " +
                "LEFT JOIN categories c ON t.category_id = c.category_id " +
                "LEFT JOIN accounts sa ON t.source_account_id = sa.account_id " +
                "LEFT JOIN accounts da ON t.destination_account_id = da.account_id " +
                "WHERE t.user_id = ? AND (" +
                "c.category_name LIKE ? OR " +
                "sa.account_name LIKE ? OR " +
                "da.account_name LIKE ? OR " +
                "t.description LIKE ?) " +
                "ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        }

        return transactions;
    }

    @Override
    public int batchSave(List<Transaction> transactions) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, source_account_id, destination_account_id, " +
                "category_id, amount, transaction_type, transaction_date, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        int count = 0;

        try (Connection conn = getConnection()) {
            beginTransaction(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Transaction transaction : transactions) {
                    ps.setInt(1, transaction.getUserId());
                    setNullableInteger(ps, 2, transaction.getSourceAccountId());
                    setNullableInteger(ps, 3, transaction.getDestinationAccountId());
                    ps.setInt(4, transaction.getCategoryId());
                    ps.setBigDecimal(5, transaction.getAmount());
                    ps.setString(6, transaction.getTransactionType().toString());
                    ps.setDate(7, Date.valueOf(transaction.getTransactionDate()));
                    ps.setString(8, transaction.getDescription());
                    ps.addBatch();
                }

                int[] results = ps.executeBatch();

                // Get the generated keys for all inserted records
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (rs.next() && i < transactions.size()) {
                        transactions.get(i).setTransactionId(rs.getInt(1));
                        i++;
                    }
                }

                for (int result : results) {
                    if (result > 0) {
                        count++;
                    }
                }

                commitTransaction(conn);
            } catch (SQLException e) {
                rollbackTransaction(conn);
                throw e;
            }
        }

        return count;
    }

    /**
     * 设置可为null的整数参数
     */
    private void setNullableInteger(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(parameterIndex, Types.INTEGER);
        } else {
            ps.setInt(parameterIndex, value);
        }
    }

    /**
     * 将ResultSet映射为Transaction对象
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setUserId(rs.getInt("user_id"));

        // Handle nullable integers
        int sourceAccountId = rs.getInt("source_account_id");
        if (!rs.wasNull()) {
            transaction.setSourceAccountId(sourceAccountId);
        }

        int destinationAccountId = rs.getInt("destination_account_id");
        if (!rs.wasNull()) {
            transaction.setDestinationAccountId(destinationAccountId);
        }

        transaction.setCategoryId(rs.getInt("category_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setTransactionType(TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
        transaction.setDescription(rs.getString("description"));

        // Set additional fields if they exist
        try {
            transaction.setCategoryName(rs.getString("category_name"));
            transaction.setSourceAccountName(rs.getString("source_account_name"));
            transaction.setDestinationAccountName(rs.getString("destination_account_name"));
        } catch (SQLException e) {
            // These fields might not be in the result set
        }

        return transaction;
    }
}