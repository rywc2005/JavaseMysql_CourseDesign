package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.interfaces.BudgetCategoryDao;
import com.PFM.CD.dao.interfaces.ConnectionManager;
import com.PFM.CD.entity.BudgetCategory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 预算分类数据访问实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class BudgetCategoryDaoImpl extends BaseDaoImpl<BudgetCategory, Integer> implements BudgetCategoryDao {

    public BudgetCategoryDaoImpl(ConnectionManager connectionManager) {
        super();
    }

    @Override
    public boolean save(BudgetCategory budgetCategory) throws SQLException {
        String sql = "INSERT INTO budget_categories (budget_id, category_id, allocated_amount, spent_amount) " +
                "VALUES (?, ?, ?, ?)";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, budgetCategory.getBudgetId());
                ps.setInt(2, budgetCategory.getCategoryId());
                ps.setBigDecimal(3, budgetCategory.getAllocatedAmount());
                ps.setBigDecimal(4, budgetCategory.getSpentAmount() != null ?
                        budgetCategory.getSpentAmount() : BigDecimal.ZERO);

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            budgetCategory.setBudgetCategoryId(rs.getInt(1));
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public BudgetCategory findById(Integer budgetCategoryId) throws SQLException {
        String sql = "SELECT bc.*, c.category_name " +
                "FROM budget_categories bc " +
                "JOIN categories c ON bc.category_id = c.category_id " +
                "WHERE bc.budget_category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetCategoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBudgetCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean update(BudgetCategory budgetCategory) throws SQLException {
        String sql = "UPDATE budget_categories SET budget_id = ?, category_id = ?, " +
                "allocated_amount = ?, spent_amount = ? WHERE budget_category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetCategory.getBudgetId());
            ps.setInt(2, budgetCategory.getCategoryId());
            ps.setBigDecimal(3, budgetCategory.getAllocatedAmount());
            ps.setBigDecimal(4, budgetCategory.getSpentAmount());
            ps.setInt(5, budgetCategory.getBudgetCategoryId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer budgetCategoryId) throws SQLException {
        String sql = "DELETE FROM budget_categories WHERE budget_category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetCategoryId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<BudgetCategory> findAll() throws SQLException {
        String sql = "SELECT bc.*, c.category_name " +
                "FROM budget_categories bc " +
                "JOIN categories c ON bc.category_id = c.category_id";

        List<BudgetCategory> budgetCategories = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                budgetCategories.add(mapResultSetToBudgetCategory(rs));
            }
        }

        return budgetCategories;
    }

    @Override
    public List<BudgetCategory> findByBudgetId(int budgetId) throws SQLException {
        String sql = "SELECT bc.*, c.category_name " +
                "FROM budget_categories bc " +
                "JOIN categories c ON bc.category_id = c.category_id " +
                "WHERE bc.budget_id = ?";

        List<BudgetCategory> budgetCategories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    budgetCategories.add(mapResultSetToBudgetCategory(rs));
                }
            }
        }

        return budgetCategories;
    }

    @Override
    public List<BudgetCategory> findByCategoryId(int categoryId) throws SQLException {
        String sql = "SELECT bc.*, c.category_name " +
                "FROM budget_categories bc " +
                "JOIN categories c ON bc.category_id = c.category_id " +
                "WHERE bc.category_id = ?";

        List<BudgetCategory> budgetCategories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    budgetCategories.add(mapResultSetToBudgetCategory(rs));
                }
            }
        }

        return budgetCategories;
    }

    @Override
    public BudgetCategory findByBudgetIdAndCategoryId(int budgetId, int categoryId) throws SQLException {
        String sql = "SELECT bc.*, c.category_name " +
                "FROM budget_categories bc " +
                "JOIN categories c ON bc.category_id = c.category_id " +
                "WHERE bc.budget_id = ? AND bc.category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);
            ps.setInt(2, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBudgetCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean updateSpentAmount(int budgetCategoryId, BigDecimal spentAmount) throws SQLException {
        String sql = "UPDATE budget_categories SET spent_amount = ? WHERE budget_category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, spentAmount);
            ps.setInt(2, budgetCategoryId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean increaseSpentAmount(int budgetCategoryId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE budget_categories SET spent_amount = spent_amount + ? WHERE budget_category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setInt(2, budgetCategoryId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean decreaseSpentAmount(int budgetCategoryId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE budget_categories SET spent_amount = GREATEST(0, spent_amount - ?) WHERE budget_category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setInt(2, budgetCategoryId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int batchSave(List<BudgetCategory> budgetCategories) throws SQLException {
        String sql = "INSERT INTO budget_categories (budget_id, category_id, allocated_amount, spent_amount) " +
                "VALUES (?, ?, ?, ?)";

        int count = 0;

        try (Connection conn = getConnection()) {
            beginTransaction(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (BudgetCategory budgetCategory : budgetCategories) {
                    ps.setInt(1, budgetCategory.getBudgetId());
                    ps.setInt(2, budgetCategory.getCategoryId());
                    ps.setBigDecimal(3, budgetCategory.getAllocatedAmount());
                    ps.setBigDecimal(4, budgetCategory.getSpentAmount() != null ?
                            budgetCategory.getSpentAmount() : BigDecimal.ZERO);
                    ps.addBatch();
                }

                int[] results = ps.executeBatch();

                // Get the generated keys for all inserted records
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (rs.next() && i < budgetCategories.size()) {
                        budgetCategories.get(i).setBudgetCategoryId(rs.getInt(1));
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

    @Override
    public boolean deleteByBudgetId(int budgetId) throws SQLException {
        String sql = "DELETE FROM budget_categories WHERE budget_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, budgetId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateBudgetCategorySpentAmountByTransaction(int userId, int categoryId,
                                                                BigDecimal amount,
                                                                LocalDate transactionDate) throws SQLException {
        String sql = "UPDATE budget_categories bc " +
                "JOIN budgets b ON bc.budget_id = b.budget_id " +
                "SET bc.spent_amount = bc.spent_amount + ? " +
                "WHERE b.user_id = ? AND bc.category_id = ? " +
                "AND ? BETWEEN b.start_date AND b.end_date";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, amount);
            ps.setInt(2, userId);
            ps.setInt(3, categoryId);
            ps.setDate(4, Date.valueOf(transactionDate));

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * 将ResultSet映射为BudgetCategory对象
     */
    private BudgetCategory mapResultSetToBudgetCategory(ResultSet rs) throws SQLException {
        BudgetCategory budgetCategory = new BudgetCategory();
        budgetCategory.setBudgetCategoryId(rs.getInt("budget_category_id"));
        budgetCategory.setBudgetId(rs.getInt("budget_id"));
        budgetCategory.setCategoryId(rs.getInt("category_id"));
        budgetCategory.setAllocatedAmount(rs.getBigDecimal("allocated_amount"));
        budgetCategory.setSpentAmount(rs.getBigDecimal("spent_amount"));

        // 如果结果集中包含category_name列
        try {
            budgetCategory.setCategoryName(rs.getString("category_name"));
        } catch (SQLException e) {
            // 该列可能不在结果集中
        }

        return budgetCategory;
    }
}