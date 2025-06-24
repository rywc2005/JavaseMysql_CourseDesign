package com.PFM.CD.dao.impl;

import com.PFM.CD.dao.interfaces.ConnectionManager;
import com.PFM.CD.dao.interfaces.ReportDao;
import com.PFM.CD.entity.Report;
import com.PFM.CD.entity.enums.ReportType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表数据访问实现类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ReportDaoImpl extends BaseDaoImpl<Report, Integer> implements ReportDao {

    public ReportDaoImpl(ConnectionManager connectionManager) {
        super();
    }

    @Override
    public boolean save(Report report) throws SQLException {
        String sql = "INSERT INTO reports (user_id, report_type, start_date, end_date, generated_date, parameters) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        return executeWithTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, report.getUserId());
                ps.setString(2, report.getReportType().toString());
                ps.setDate(3, Date.valueOf(report.getStartDate()));
                ps.setDate(4, Date.valueOf(report.getEndDate()));
                ps.setDate(5, Date.valueOf(report.getGeneratedDate() != null ?
                        report.getGeneratedDate() : LocalDate.now()));
                ps.setString(6, report.getParameters());

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            report.setReportId(rs.getInt(1));
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public Report findById(Integer reportId) throws SQLException {
        String sql = "SELECT * FROM reports WHERE report_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReport(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean update(Report report) throws SQLException {
        String sql = "UPDATE reports SET report_type = ?, start_date = ?, end_date = ?, " +
                "parameters = ? WHERE report_id = ? AND user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, report.getReportType().toString());
            ps.setDate(2, Date.valueOf(report.getStartDate()));
            ps.setDate(3, Date.valueOf(report.getEndDate()));
            ps.setString(4, report.getParameters());
            ps.setInt(5, report.getReportId());
            ps.setInt(6, report.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer reportId) throws SQLException {
        String sql = "DELETE FROM reports WHERE report_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reportId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Report> findAll() throws SQLException {
        String sql = "SELECT * FROM reports ORDER BY generated_date DESC";
        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        }

        return reports;
    }

    @Override
    public List<Report> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM reports WHERE user_id = ? ORDER BY generated_date DESC";
        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    @Override
    public List<Report> findByUserIdAndType(int userId, ReportType reportType) throws SQLException {
        String sql = "SELECT * FROM reports WHERE user_id = ? AND report_type = ? ORDER BY generated_date DESC";
        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, reportType.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    @Override
    public List<Report> findByUserIdAndGeneratedDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM reports WHERE user_id = ? AND generated_date BETWEEN ? AND ? " +
                "ORDER BY generated_date DESC";

        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    @Override
    public List<Report> findByUserIdAndCoverageDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM reports WHERE user_id = ? AND ? BETWEEN start_date AND end_date " +
                "ORDER BY generated_date DESC";

        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    @Override
    public List<Report> findRecentByUserId(int userId, int limit) throws SQLException {
        String sql = "SELECT * FROM reports WHERE user_id = ? ORDER BY generated_date DESC LIMIT ?";
        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    @Override
    public List<Report> findByUserIdTypeAndGeneratedDateRange(int userId, ReportType reportType,
                                                              LocalDate startGeneratedDate,
                                                              LocalDate endGeneratedDate) throws SQLException {
        String sql = "SELECT * FROM reports WHERE user_id = ? AND report_type = ? " +
                "AND generated_date BETWEEN ? AND ? ORDER BY generated_date DESC";

        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, reportType.toString());
            ps.setDate(3, Date.valueOf(startGeneratedDate));
            ps.setDate(4, Date.valueOf(endGeneratedDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }

        return reports;
    }

    @Override
    public int deleteOlderThan(int userId, LocalDate date) throws SQLException {
        String sql = "DELETE FROM reports WHERE user_id = ? AND generated_date < ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));

            return ps.executeUpdate();
        }
    }

    /**
     * 将ResultSet映射为Report对象
     */
    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setReportId(rs.getInt("report_id"));
        report.setUserId(rs.getInt("user_id"));
        report.setReportType(ReportType.valueOf(rs.getString("report_type")));
        report.setStartDate(rs.getDate("start_date").toLocalDate());
        report.setEndDate(rs.getDate("end_date").toLocalDate());
        report.setGeneratedDate(rs.getDate("generated_date").toLocalDate());
        report.setParameters(rs.getString("parameters"));
        return report;
    }
}