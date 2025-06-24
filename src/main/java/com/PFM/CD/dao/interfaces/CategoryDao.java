package com.PFM.CD.dao.interfaces;

import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.CategoryType;

import java.sql.SQLException;
import java.util.List;

/**
 * 分类数据访问接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface CategoryDao extends BaseDao<Category, Integer> {

    /**
     * 按类型查找分类
     *
     * @param type 分类类型
     * @return 分类列表
     */
    List<Category> findByType(CategoryType type) throws SQLException;

    /**
     * 通过名称和类型查找分类
     *
     * @param categoryName 分类名称
     * @param type 分类类型
     * @return 找到的分类，如果不存在返回null
     */
    Category findByNameAndType(String categoryName, CategoryType type) throws SQLException;

    /**
     * 查找最常用的分类
     *
     * @param limit 返回数量限制
     * @return 分类列表
     */
    List<Category> findMostUsedCategories(int limit) throws SQLException;

    /**
     * 检查分类名称和类型组合是否已存在
     *
     * @param categoryName 分类名称
     * @param type 分类类型
     * @return 如果存在返回true，否则返回false
     */
    boolean isCategoryExists(String categoryName, CategoryType type) throws SQLException;

    /**
     * 通过ID列表查找分类
     *
     * @param categoryIds ID列表
     * @return 分类列表
     */
    List<Category> findByIds(List<Integer> categoryIds) throws SQLException;

    /**
     * 批量保存分类
     *
     * @param categories 分类列表
     * @return 成功保存的数量
     */
    int batchSave(List<Category> categories) throws SQLException;
}