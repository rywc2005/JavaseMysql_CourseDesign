package javasemysql.coursedesign.dao;

import javasemysql.coursedesign.dto.BudgetQueryParam;
import javasemysql.coursedesign.model.Budget;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 预算数据访问接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface BudgetDao {

    /**
     * 根据ID查找预算
     *
     * @param conn 数据库连接
     * @param id 预算ID
     * @return 预算对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    Budget findById(Connection conn, int id) throws SQLException;

    /**
     * 根据用户ID查找预算列表
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @return 预算列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Budget> findByUserId(Connection conn, int userId) throws SQLException;

    /**
     * 根据查询参数查找预算
     *
     * @param conn 数据库连接
     * @param param 查询参数
     * @return 预算列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Budget> findByParam(Connection conn, BudgetQueryParam param) throws SQLException;

    /**
     * 查找活跃预算
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param currentDate 当前日期
     * @return 预算列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Budget> findActiveBudgets(Connection conn, int userId, Date currentDate) throws SQLException;

    /**
     * 根据类别查找活跃预算
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param category 类别
     * @param currentDate 当前日期
     * @return 预算列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Budget> findActiveBudgetsByCategory(Connection conn, int userId, String category, Date currentDate) throws SQLException;

    /**
     * 查找重叠的预算
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param category 类别
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预算列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Budget> findOverlappingBudgets(Connection conn, int userId, String category, Date startDate, Date endDate) throws SQLException;

    /**
     * 插入预算
     *
     * @param conn 数据库连接
     * @param budget 预算对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean insert(Connection conn, Budget budget) throws SQLException;

    /**
     * 更新预算
     *
     * @param conn 数据库连接
     * @param budget 预算对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean update(Connection conn, Budget budget) throws SQLException;

    /**
     * 删除预算
     *
     * @param conn 数据库连接
     * @param id 预算ID
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean delete(Connection conn, int id) throws SQLException;
}