package com.PFM.CD.service.exception;

/**
 * 导出异常，当导出报表或数据时发生错误抛出
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ExportException extends ServiceException {

    private static final long serialVersionUID = 1L;

    private final String exportFormat;
    private final String targetResource;

    /**
     * 构造一个带有错误消息的导出异常
     *
     * @param message 错误消息
     */
    public ExportException(String message) {
        super(message);
        this.exportFormat = null;
        this.targetResource = null;
    }

    /**
     * 构造一个带有错误消息、导出格式和目标资源的导出异常
     *
     * @param message 错误消息
     * @param exportFormat 导出格式
     * @param targetResource 目标资源
     */
    public ExportException(String message, String exportFormat, String targetResource) {
        super(message);
        this.exportFormat = exportFormat;
        this.targetResource = targetResource;
    }

    /**
     * 构造一个带有错误消息、导出格式、目标资源和原因的导出异常
     *
     * @param message 错误消息
     * @param exportFormat 导出格式
     * @param targetResource 目标资源
     * @param cause 原始异常
     */
    public ExportException(String message, String exportFormat, String targetResource, Throwable cause) {
        super(message, cause);
        this.exportFormat = exportFormat;
        this.targetResource = targetResource;
    }

    /**
     * 获取导出格式
     *
     * @return 导出格式
     */
    public String getExportFormat() {
        return exportFormat;
    }

    /**
     * 获取目标资源
     *
     * @return 目标资源
     */
    public String getTargetResource() {
        return targetResource;
    }

    /**
     * 创建一个PDF导出异常
     *
     * @param reportId 报表ID
     * @param cause 原始异常
     * @return 导出异常实例
     */
    public static ExportException pdfExportFailed(int reportId, Throwable cause) {
        return new ExportException("导出报表为PDF失败", "PDF", "报表ID=" + reportId, cause);
    }

    /**
     * 创建一个Excel导出异常
     *
     * @param reportId 报表ID
     * @param cause 原始异常
     * @return 导出异常实例
     */
    public static ExportException excelExportFailed(int reportId, Throwable cause) {
        return new ExportException("导出报表为Excel失败", "Excel", "报表ID=" + reportId, cause);
    }

    /**
     * 创建一个数据导出异常
     *
     * @param dataType 数据类型
     * @param format 导出格式
     * @param cause 原始异常
     * @return 导出异常实例
     */
    public static ExportException dataExportFailed(String dataType, String format, Throwable cause) {
        return new ExportException("导出" + dataType + "为" + format + "失败", format, dataType, cause);
    }

    /**
     * 创建一个文件写入异常
     *
     * @param filePath 文件路径
     * @param cause 原始异常
     * @return 导出异常实例
     */
    public static ExportException fileWriteFailed(String filePath, Throwable cause) {
        return new ExportException("写入文件失败: " + filePath, "文件", filePath, cause);
    }
}