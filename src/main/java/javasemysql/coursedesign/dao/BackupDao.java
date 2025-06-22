package javasemysql.coursedesign.dao;

import javasemysql.coursedesign.dto.BackupQueryParam;
import javasemysql.coursedesign.model.Backup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 备份数据访问接口
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public interface BackupDao {

    /**
     * 根据ID查找备份
     *
     * @param conn 数据库连接
     * @param id 备份ID
     * @return 备份对象，如果不存在则返回null
     * @throws SQLException 如果数据库操作失败
     */
    Backup findById(Connection conn, int id) throws SQLException;

    /**
     * 根据用户ID查找备份列表
     *
     * @param conn 数据库连接
     * @param userId 用户ID
     * @return 备份列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Backup> findByUserId(Connection conn, int userId) throws SQLException;

    /**
     * 根据查询参数查找备份
     *
     * @param conn 数据库连接
     * @param param 查询参数
     * @return 备份列表
     * @throws SQLException 如果数据库操作失败
     */
    List<Backup> findByParam(Connection conn, BackupQueryParam param) throws SQLException;

    /**
     * 插入备份
     *
     * @param conn 数据库连接
     * @param backup 备份对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean insert(Connection conn, Backup backup) throws SQLException;

    /**
     * 更新备份
     *
     * @param conn 数据库连接
     * @param backup 备份对象
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean update(Connection conn, Backup backup) throws SQLException;

    /**
     * 删除备份
     *
     * @param conn 数据库连接
     * @param id 备份ID
     * @return 是否成功
     * @throws SQLException 如果数据库操作失败
     */
    boolean delete(Connection conn, int id) throws SQLException;
}