package com.PFM.CD.dao.interfaces;

import com.PFM.CD.entity.Report;
import com.PFM.CD.entity.enums.ReportType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * 报表数据访问接口
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public interface ReportDao extends BaseDao<Report, Integer> {

    /**
     * 查找用户的所有报表
     *
     * @param userId 用户ID
     * @return 报表列表
     */
    List<Report> findByUserId(int userId) throws SQLException;

    /**
     * 按类型查找用户报表
     *
     * @param userId 用户ID
     * @param reportType 报表类型
     * @return 报表列表
     */
    List<Report> findByUserIdAndType(int userId, ReportType reportType) throws SQLException;

    /**
     * 按生成日期范围查找用户报表
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 报表列表
     */
    List<Report> findByUserIdAndGeneratedDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException;

    /**
     * 按覆盖日期范围查找用户报表
     *
     * @param userId 用户ID
     * @param date 要覆盖的日期
     * @return 报表列表
     */
    List<Report> findByUserIdAndCoverageDate(int userId, LocalDate date) throws SQLException;

    /**
     * 获取用户最近生成的报表
     *
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 报表列表
     */
    List<Report> findRecentByUserId(int userId, int limit) throws SQLException;

    /**
     * 查找用户在指定日期范围内生成的特定类型报表
     *
     * @param userId 用户ID
     * @param reportType 报表类型
     * @param startGeneratedDate 开始生成日期
     * @param endGeneratedDate 结束生成日期
     * @return 报表列表
     */
    List<Report> findByUserIdTypeAndGeneratedDateRange(int userId, ReportType reportType,
                                                       LocalDate startGeneratedDate,
                                                       LocalDate endGeneratedDate) throws SQLException;

    /**
     * 删除指定日期之前的用户报表
     *
     * @param userId 用户ID
     * @param date 日期
     * @return 删除的记录数
     */
    int deleteOlderThan(int userId, LocalDate date) throws SQLException;
}