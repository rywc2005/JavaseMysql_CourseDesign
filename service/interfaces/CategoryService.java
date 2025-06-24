package com.PFM.CD.service.interfaces;

import com.PFM.CD.entity.Category;
import com.PFM.CD.entity.enums.CategoryType;
import com.PFM.CD.service.exception.ServiceException;

import java.util.List;

/**
 * 分类服务接口，提供分类相关的业务逻辑
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface CategoryService {

    /**
     * 创建新分类
     *
     * @param categoryName 分类名称
     * @param categoryType 分类类型
     * @return 创建的分类
     * @throws ServiceException 如果创建过程中发生错误
     */
    Category createCategory(String categoryName, CategoryType categoryType) throws ServiceException;

    /**
     * 获取分类信息
     *
     * @param categoryId 分类ID
     * @return 分类信息
     * @throws ServiceException 如果获取过程中发生错误
     */
    Category getCategoryById(int categoryId) throws ServiceException;

    /**
     * 更新分类信息
     *
     * @param category 需要更新的分类信息
     * @return 更新后的分类
     * @throws ServiceException 如果更新过程中发生错误
     */
    Category updateCategory(Category category) throws ServiceException;

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @return 是否删除成功
     * @throws ServiceException 如果删除过程中发生错误
     */
    boolean deleteCategory(int categoryId) throws ServiceException;

    /**
     * 获取所有分类
     *
     * @return 分类列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Category> getAllCategories() throws ServiceException;

    /**
     * 获取指定类型的分类
     *
     * @param type 分类类型
     * @return 分类列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Category> getCategoriesByType(CategoryType type) throws ServiceException;

    /**
     * 获取最常用的分类
     *
     * @param limit 数量限制
     * @return 分类列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Category> getMostUsedCategories(int limit) throws ServiceException;

    /**
     * 检查分类名称是否可用
     *
     * @param categoryName 分类名称
     * @param categoryType 分类类型
     * @return 如果可用返回true，否则返回false
     * @throws ServiceException 如果检查过程中发生错误
     */
    boolean isCategoryNameAvailable(String categoryName, CategoryType categoryType) throws ServiceException;

    /**
     * 批量创建默认分类
     *
     * @return 创建的分类数量
     * @throws ServiceException 如果创建过程中发生错误
     */
    int createDefaultCategories() throws ServiceException;

    /**
     * 通过名称和类型查找分类
     *
     * @param categoryName 分类名称
     * @param categoryType 分类类型
     * @return 分类信息，如果不存在返回null
     * @throws ServiceException 如果查找过程中发生错误
     */
    Category findByNameAndType(String categoryName, CategoryType categoryType) throws ServiceException;

    /**
     * 通过ID列表获取分类
     *
     * @param categoryIds ID列表
     * @return 分类列表
     * @throws ServiceException 如果获取过程中发生错误
     */
    List<Category> getCategoriesByIds(List<Integer> categoryIds) throws ServiceException;
}