package com.PFM.CD.dao.util;

import java.util.List;
import java.util.StringJoiner;

/**
 * SQL工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class SqlUtils {

    /**
     * 私有构造函数，防止实例化
     */
    private SqlUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 生成IN子句的占位符
     *
     * @param size 列表大小
     * @return IN子句的占位符
     */
    public static String generateInClausePlaceholders(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("列表大小必须大于0");
        }

        StringJoiner joiner = new StringJoiner(",", "(", ")");
        for (int i = 0; i < size; i++) {
            joiner.add("?");
        }

        return joiner.toString();
    }

    /**
     * 生成插入语句的值部分
     *
     * @param columnCount 列数
     * @param rowCount 行数
     * @return 插入语句的值部分
     */
    public static String generateInsertValuesClauses(int columnCount, int rowCount) {
        if (columnCount <= 0 || rowCount <= 0) {
            throw new IllegalArgumentException("列数和行数必须大于0");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rowCount; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append("(");
            for (int j = 0; j < columnCount; j++) {
                if (j > 0) {
                    sb.append(", ");
                }
                sb.append("?");
            }
            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * 生成分页查询的LIMIT子句
     *
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return LIMIT子句
     */
    public static String generateLimitClause(int page, int pageSize) {
        if (page <= 0) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("每页大小必须大于0");
        }

        int offset = (page - 1) * pageSize;
        return String.format("LIMIT %d, %d", offset, pageSize);
    }

    /**
     * 生成ORDER BY子句
     *
     * @param columns 排序列
     * @param ascending 是否升序
     * @return ORDER BY子句
     */
    public static String generateOrderByClause(List<String> columns, boolean ascending) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("排序列不能为空");
        }

        StringJoiner joiner = new StringJoiner(", ", "ORDER BY ", "");
        for (String column : columns) {
            joiner.add(column + (ascending ? " ASC" : " DESC"));
        }

        return joiner.toString();
    }

    /**
     * 生成查询条件子句
     *
     * @param condition 条件
     * @param value 值
     * @return 查询条件子句
     */
    public static String generateWhereClause(String condition, Object value) {
        if (condition == null || condition.isEmpty()) {
            throw new IllegalArgumentException("条件不能为空");
        }

        return value != null ? condition : "";
    }

    /**
     * 生成LIKE模式
     *
     * @param keyword 关键词
     * @param position 位置（前匹配、后匹配、包含）
     * @return LIKE模式
     */
    public static String generateLikePattern(String keyword, LikePatternPosition position) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("关键词不能为空");
        }

        // 转义特殊字符
        String escaped = keyword.replace("%", "\\%").replace("_", "\\_");

        switch (position) {
            case START:
                return escaped + "%";
            case END:
                return "%" + escaped;
            case ANYWHERE:
                return "%" + escaped + "%";
            default:
                return escaped;
        }
    }

    /**
     * LIKE模式位置枚举
     */
    public enum LikePatternPosition {
        START,    // 前匹配
        END,      // 后匹配
        ANYWHERE, // 包含
        EXACT     // 精确匹配
    }
}