package javasemysql.coursedesign.dao;

import javasemysql.coursedesign.dto.AccountQueryParam;
import javasemysql.coursedesign.model.Account;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 账户数据访问接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface AccountDao {

    /**
     * 根据ID查找账户
     *
     * @param conn 数据库连接
     * @param id 账户ID
     * @return 账户对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    Account findById(Connection conn, int id) throws SQLException;

    /**
     * 根据用户ID查找账户列表
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @return 账户列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Account> findByUserId(Connection conn, int userId) throws SQLException;

    /**
     * 根据查询参数查找账户
     *
     * @param conn 数据库连接
     * @param param 查询参数
     * @return 账户列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Account> findByParam(Connection conn, AccountQueryParam param) throws SQLException;

    /**
     * 插入账户
     *
     * @param conn 数据库连接
     * @param account 账户对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean insert(Connection conn, Account account) throws SQLException;

    /**
     * 更新账户
     *
     * @param conn 数据库连接
     * @param account 账户对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean update(Connection conn, Account account) throws SQLException;

    /**
     * 删除账户
     *
     * @param conn 数据库连接
     * @param id 账户ID
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean delete(Connection conn, int id) throws SQLException;
}