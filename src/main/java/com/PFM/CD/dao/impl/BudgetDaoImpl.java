package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.interfaces.BudgetDao;
import com.PFM.CD.dao.interfaces.ConnectionManager;
import com.PFM.CD.entity.Budget;
import com.PFM.CD.entity.BudgetCategory;
import com.PFM.CD.entity.enums.PeriodType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 预算数据访问实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class BudgetDaoImpl extends BaseDaoImpl<Budget, Integer> implements BudgetDao {

    public BudgetDaoImpl(ConnectionManager connectionManager) {
        super();
    }

    @Override
    public boolean save(Budget budget) throws SQLException {
        String sql = "INSERT INTO budgets (user_id, name, period_type, start_date, end_date, total_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, budget.getUserId());
                ps.setString(2, budget.getName());
                ps.setString(3, budget.getPeriodType().toString());
                ps.setDate(4, Date.valueOf(budget.getStartDate()));
                ps.setDate(5, Date.valueOf(budget.getEndDate()));
                ps.setBigDecimal(6, budget.getTotalAmount());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            budget.setBudgetId(rs.getInt(1));
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public Budget findById(Integer budgetId) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE budget_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBudget(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean update(Budget budget) throws SQLException {
        String sql = "UPDATE budgets SET name = ?, period_type = ?, start_date = ?, " +
                "end_date = ?, total_amount = ? WHERE budget_id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, budget.getName());
            ps.setString(2, budget.getPeriodType().toString());
            ps.setDate(3, Date.valueOf(budget.getStartDate()));
            ps.setDate(4, Date.valueOf(budget.getEndDate()));
            ps.setBigDecimal(5, budget.getTotalAmount());
            ps.setInt(6, budget.getBudgetId());
            ps.setInt(7, budget.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer budgetId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE budget_id = ?";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, budgetId);
                return ps.executeUpdate() > 0;
            }
        });
    }

    @Override
    public List<Budget> findAll() throws SQLException {
        String sql = "SELECT * FROM budgets ORDER BY start_date DESC";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                budgets.add(mapResultSetToBudget(rs));
            }
        }

        return budgets;
    }

    @Override
    public List<Budget> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE user_id = ? ORDER BY start_date DESC";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    budgets.add(mapResultSetToBudget(rs));
                }
            }
        }

        return budgets;
    }

    @Override
    public List<Budget> findActiveByUserIdAndDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE user_id = ? AND ? BETWEEN start_date AND end_date";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    budgets.add(mapResultSetToBudget(rs));
                }
            }
        }

        return budgets;
    }

    @Override
    public List<Budget> findByUserIdAndPeriodType(int userId, PeriodType periodType) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE user_id = ? AND period_type = ? ORDER BY start_date DESC";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, periodType.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    budgets.add(mapResultSetToBudget(rs));
                }
            }
        }

        return budgets;
    }

    @Override
    public List<Budget> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE user_id = ? AND " +
                "((start_date BETWEEN ? AND ?) OR " +
                "(end_date BETWEEN ? AND ?) OR " +
                "(start_date <= ? AND end_date >= ?)) " +
                "ORDER BY start_date DESC";

        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            ps.setDate(4, Date.valueOf(startDate));
            ps.setDate(5, Date.valueOf(endDate));
            ps.setDate(6, Date.valueOf(startDate));
            ps.setDate(7, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    budgets.add(mapResultSetToBudget(rs));
                }
            }
        }

        return budgets;
    }

    @Override
    public Budget findBudgetWithCategories(int budgetId) throws SQLException {
        Budget budget = findById(budgetId);

        if (budget != null) {
            String sql = "SELECT bc.*, c.category_name " +
                    "FROM budget_categories bc " +
                    "JOIN categories c ON bc.category_id = c.category_id " +
                    "WHERE bc.budget_id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, budgetId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        BudgetCategory budgetCategory = new BudgetCategory();
                        budgetCategory.setBudgetCategoryId(rs.getInt("budget_category_id"));
                        budgetCategory.setBudgetId(rs.getInt("budget_id"));
                        budgetCategory.setCategoryId(rs.getInt("category_id"));
                        budgetCategory.setAllocatedAmount(rs.getBigDecimal("allocated_amount"));
                        budgetCategory.setSpentAmount(rs.getBigDecimal("spent_amount"));
                        budgetCategory.setCategoryName(rs.getString("category_name"));

                        budget.addBudgetCategory(budgetCategory);
                    }
                }
            }
        }

        return budget;
    }

    @Override
    public List<Budget> findRecentByUserId(int userId, int limit) throws SQLException {
        String sql = "SELECT * FROM budgets WHERE user_id = ? ORDER BY start_date DESC LIMIT ?";
        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    budgets.add(mapResultSetToBudget(rs));
                }
            }
        }

        return budgets;
    }

    @Override
    public boolean isBudgetNameExists(int userId, String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM budgets WHERE user_id = ? AND name = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    @Override
    public int copyBudget(int budgetId, String newName, LocalDate newStartDate) throws SQLException {
        Connection conn = null;
        int newBudgetId = -1;

        try {
            conn = getConnection();
            beginTransaction(conn);

            // First get the original budget
            Budget originalBudget = findById(budgetId);
            if (originalBudget == null) {
                return -1;
            }

            // Calculate new end date based on the period type
            LocalDate newEndDate = originalBudget.getPeriodType().calculateEndDate(newStartDate);

            // Create a new budget
            String insertBudgetSql = "INSERT INTO budgets (user_id, name, period_type, start_date, end_date, total_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertBudgetSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, originalBudget.getUserId());
                ps.setString(2, newName);
                ps.setString(3, originalBudget.getPeriodType().toString());
                ps.setDate(4, Date.valueOf(newStartDate));
                ps.setDate(5, Date.valueOf(newEndDate));
                ps.setBigDecimal(6, originalBudget.getTotalAmount());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            newBudgetId = rs.getInt(1);
                        } else {
                            return -1;
                        }
                    }
                } else {
                    return -1;
                }
            }

            // Copy budget categories
            String selectCategoriesSql = "SELECT * FROM budget_categories WHERE budget_id = ?";
            String insertCategorySql = "INSERT INTO budget_categories (budget_id, category_id, allocated_amount, spent_amount) " +
                    "VALUES (?, ?, ?, 0.00)";

            try (PreparedStatement selectPs = conn.prepareStatement(selectCategoriesSql)) {
                selectPs.setInt(1, budgetId);

                try (ResultSet rs = selectPs.executeQuery();
                     PreparedStatement insertPs = conn.prepareStatement(insertCategorySql)) {

                    while (rs.next()) {
                        insertPs.setInt(1, newBudgetId);
                        insertPs.setInt(2, rs.getInt("category_id"));
                        insertPs.setBigDecimal(3, rs.getBigDecimal("allocated_amount"));
                        insertPs.addBatch();
                    }

                    insertPs.executeBatch();
                }
            }

            commitTransaction(conn);
            return newBudgetId;

        } catch (SQLException e) {
            if (conn != null) {
                rollbackTransaction(conn);
            }
            throw e;
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 将ResultSet映射为Budget对象
     */
    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setBudgetId(rs.getInt("budget_id"));
        budget.setUserId(rs.getInt("user_id"));
        budget.setName(rs.getString("name"));
        budget.setPeriodType(PeriodType.valueOf(rs.getString("period_type")));
        budget.setStartDate(rs.getDate("start_date").toLocalDate());
        budget.setEndDate(rs.getDate("end_date").toLocalDate());
        budget.setTotalAmount(rs.getBigDecimal("total_amount"));
        return budget;
    }
}