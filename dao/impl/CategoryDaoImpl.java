package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.interfaces.CategoryDao;
import com.PFM.CD.dao.interfaces.ConnectionManager;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.CategoryType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类数据访问实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class CategoryDaoImpl extends BaseDaoImpl<Category, Integer> implements CategoryDao {

    public CategoryDaoImpl(ConnectionManager connectionManager) {
        super();
    }

    public CategoryDaoImpl() {

    }

    @Override
    public boolean save(Category category) throws SQLException {
        String sql = "INSERT INTO categories (category_name, category_type) VALUES (?, ?)";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, category.getCategoryName());
                ps.setString(2, category.getCategoryType().toString());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            category.setCategoryId(rs.getInt(1));
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public Category findById(Integer categoryId) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean update(Category category) throws SQLException {
        String sql = "UPDATE categories SET category_name = ?, category_type = ? WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getCategoryType().toString());
            ps.setInt(3, category.getCategoryId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer categoryId) throws SQLException {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Category> findAll() throws SQLException {
        String sql = "SELECT * FROM categories ORDER BY category_type, category_name";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }

        return categories;
    }

    @Override
    public List<Category> findByType(CategoryType type) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_type = ? ORDER BY category_name";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }

        return categories;
    }

    @Override
    public Category findByNameAndType(String categoryName, CategoryType type) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_name = ? AND category_type = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoryName);
            ps.setString(2, type.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Category> findMostUsedCategories(int limit) throws SQLException {
        String sql = "SELECT c.*, COUNT(t.transaction_id) as usage_count " +
                "FROM categories c " +
                "JOIN transactions t ON c.category_id = t.category_id " +
                "GROUP BY c.category_id " +
                "ORDER BY usage_count DESC " +
                "LIMIT ?";

        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }

        return categories;
    }

    @Override
    public boolean isCategoryExists(String categoryName, CategoryType type) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_name = ? AND category_type = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoryName);
            ps.setString(2, type.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    @Override
    public List<Category> findByIds(List<Integer> categoryIds) throws SQLException {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < categoryIds.size(); i++) {
            placeholders.append(i > 0 ? ", ?" : "?");
        }

        String sql = "SELECT * FROM categories WHERE category_id IN (" + placeholders.toString() + ")";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < categoryIds.size(); i++) {
                ps.setInt(i + 1, categoryIds.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }

        return categories;
    }

    @Override
    public int batchSave(List<Category> categories) throws SQLException {
        String sql = "INSERT INTO categories (category_name, category_type) VALUES (?, ?)";
        int count = 0;

        try (Connection conn = getConnection()) {
            beginTransaction(conn);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Category category : categories) {
                    ps.setString(1, category.getCategoryName());
                    ps.setString(2, category.getCategoryType().toString());
                    ps.addBatch();
                }

                int[] results = ps.executeBatch();

                // Get the generated keys for all inserted records
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (rs.next() && i < categories.size()) {
                        categories.get(i).setCategoryId(rs.getInt(1));
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
     * 将ResultSet映射为Category对象
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setCategoryType(CategoryType.valueOf(rs.getString("category_type")));
        return category;
    }
}