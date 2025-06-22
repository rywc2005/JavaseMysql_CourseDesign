package javasemysql.coursedesign.dao;

import javasemysql.coursedesign.dto.BillQueryParam;
import javasemysql.coursedesign.model.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 账单数据访问接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface BillDao {

    /**
     * 根据ID查找账单
     *
     * @param conn 数据库连接
     * @param id 账单ID
     * @return 账单对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    Bill findById(Connection conn, int id) throws SQLException;

    /**
     * 根据用户ID查找账单列表
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @return 账单列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Bill> findByUserId(Connection conn, int userId) throws SQLException;

    /**
     * 根据查询参数查找账单
     *
     * @param conn 数据库连接
     * @param param 查询参数
     * @return 账单列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Bill> findByParam(Connection conn, BillQueryParam param) throws SQLException;

    /**
     * 查找即将到期的账单
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 账单列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Bill> findUpcomingBills(Connection conn, int userId, Date startDate, Date endDate) throws SQLException;

    /**
     * 查找逾期账单
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @param currentDate 当前日期
     * @return 账单列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Bill> findOverdueBills(Connection conn, int userId, Date currentDate) throws SQLException;

    /**
     * 插入账单
     *
     * @param conn 数据库连接
     * @param bill 账单对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean insert(Connection conn, Bill bill) throws SQLException;

    /**
     * 更新账单
     *
     * @param conn 数据库连接
     * @param bill 账单对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean update(Connection conn, Bill bill) throws SQLException;

    /**
     * 删除账单
     *
     * @param conn 数据库连接
     * @param id 账单ID
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean delete(Connection conn, int id) throws SQLException;
}