package com.PFM.CD.service.impl;

import com.PFM.CD.dao.interfaces.CategoryDao;
import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.service.exception.ServiceException;
import com.PFM.CD.service.interfaces.CategoryService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 分类服务实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    /**
     * 构造函数
     *
     * @param categoryDao 分类DAO接口
     */
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public Category createCategory(String categoryName, CategoryType categoryType) throws ServiceException {
        try {
            // 检查分类名称是否已存在
            if (categoryDao.isCategoryExists(categoryName, categoryType)) {
                throw new ServiceException("分类名称已存在: " + categoryName);
            }

            // 创建新分类对象
            Category category = new Category();
            category.setCategoryName(categoryName);
            category.setCategoryType(categoryType);

            // 保存分类
            boolean success = categoryDao.save(category);
            if (!success) {
                throw new ServiceException("创建分类失败");
            }

            return category;
        } catch (SQLException e) {
            throw new ServiceException("创建分类过程中发生数据库错误", e);
        }
    }

    @Override
    public Category getCategoryById(int categoryId) throws ServiceException {
        try {
            Category category = categoryDao.findById(categoryId);
            if (category == null) {
                throw new ServiceException("分类不存在: " + categoryId);
            }
            return category;
        } catch (SQLException e) {
            throw new ServiceException("获取分类信息过程中发生数据库错误", e);
        }
    }

    @Override
    public Category updateCategory(Category category) throws ServiceException {
        try {
            // 检查分类是否存在
            Category existingCategory = categoryDao.findById(category.getCategoryId());
            if (existingCategory == null) {
                throw new ServiceException("分类不存在: " + category.getCategoryId());
            }

            // 检查分类名称是否已被其他分类使用
            Category categoryWithSameName = categoryDao.findByNameAndType(
                    category.getCategoryName(), category.getCategoryType());
            if (categoryWithSameName != null &&
                    categoryWithSameName.getCategoryId() != category.getCategoryId()) {
                throw new ServiceException("分类名称已存在: " + category.getCategoryName());
            }

            // 更新分类信息
            boolean success = categoryDao.update(category);
            if (!success) {
                throw new ServiceException("更新分类信息失败");
            }

            return category;
        } catch (SQLException e) {
            throw new ServiceException("更新分类信息过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean deleteCategory(int categoryId) throws ServiceException {
        try {
            // 检查分类是否存在
            Category category = categoryDao.findById(categoryId);
            if (category == null) {
                throw new ServiceException("分类不存在: " + categoryId);
            }

            // 删除分类
            return categoryDao.delete(categoryId);
        } catch (SQLException e) {
            throw new ServiceException("删除分类过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Category> getAllCategories() throws ServiceException {
        try {
            return categoryDao.findAll();
        } catch (SQLException e) {
            throw new ServiceException("获取所有分类过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Category> getCategoriesByType(CategoryType type) throws ServiceException {
        try {
            return categoryDao.findByType(type);
        } catch (SQLException e) {
            throw new ServiceException("获取指定类型分类过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Category> getMostUsedCategories(int limit) throws ServiceException {
        try {
            return categoryDao.findMostUsedCategories(limit);
        } catch (SQLException e) {
            throw new ServiceException("获取最常用分类过程中发生数据库错误", e);
        }
    }

    @Override
    public boolean isCategoryNameAvailable(String categoryName, CategoryType categoryType) throws ServiceException {
        try {
            return !categoryDao.isCategoryExists(categoryName, categoryType);
        } catch (SQLException e) {
            throw new ServiceException("检查分类名称可用性过程中发生数据库错误", e);
        }
    }

    @Override
    public int createDefaultCategories() throws ServiceException {
        // 默认收入分类
        List<String> defaultIncomeCategories = Arrays.asList(
                "工资", "奖金", "投资收益", "利息", "租金收入", "礼金", "退款", "其他收入"
        );

        // 默认支出分类
        List<String> defaultExpenseCategories = Arrays.asList(
                "食品", "餐厅", "交通", "住房", "水电煤", "通讯", "娱乐", "购物",
                "医疗", "教育", "旅行", "保险", "贷款", "税费", "礼品", "其他支出"
        );

        List<Category> categoriesToSave = new ArrayList<>();

        // 创建默认收入分类
        for (String name : defaultIncomeCategories) {
            Category category = new Category();
            category.setCategoryName(name);
            category.setCategoryType(CategoryType.INCOME);
            categoriesToSave.add(category);
        }

        // 创建默认支出分类
        for (String name : defaultExpenseCategories) {
            Category category = new Category();
            category.setCategoryName(name);
            category.setCategoryType(CategoryType.EXPENSE);
            categoriesToSave.add(category);
        }

        try {
            Connection conn = null;
            try {
                conn = categoryDao.getConnection();
                categoryDao.beginTransaction(conn);

                int count = categoryDao.batchSave(categoriesToSave);

                categoryDao.commitTransaction(conn);
                return count;
            } catch (SQLException e) {
                if (conn != null) {
                    categoryDao.rollbackTransaction(conn);
                }
                throw e;
            } finally {
                categoryDao.closeConnection(conn);
            }
        } catch (SQLException e) {
            throw new ServiceException("创建默认分类过程中发生数据库错误", e);
        }
    }

    @Override
    public Category findByNameAndType(String categoryName, CategoryType categoryType) throws ServiceException {
        try {
            return categoryDao.findByNameAndType(categoryName, categoryType);
        } catch (SQLException e) {
            throw new ServiceException("查找分类过程中发生数据库错误", e);
        }
    }

    @Override
    public List<Category> getCategoriesByIds(List<Integer> categoryIds) throws ServiceException {
        try {
            return categoryDao.findByIds(categoryIds);
        } catch (SQLException e) {
            throw new ServiceException("获取多个分类过程中发生数据库错误", e);
        }
    }
}