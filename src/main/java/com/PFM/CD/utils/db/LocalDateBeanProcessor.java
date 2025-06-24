package com.PFM.CD.utils.db;

import org.apache.commons.dbutils.BeanProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义BeanProcessor，处理LocalDate类型转换
 */
public class LocalDateBeanProcessor extends BeanProcessor {

    public LocalDateBeanProcessor() {
        this(new HashMap<>());
    }

    public LocalDateBeanProcessor(Map<String, String> columnToPropertyOverrides) {
        super(columnToPropertyOverrides);
    }

    @Override
    protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
        if (propType == LocalDate.class) {
            // 如果目标类型是LocalDate，将java.sql.Date转换为LocalDate
            java.sql.Date sqlDate = rs.getDate(index);
            return (sqlDate != null) ? sqlDate.toLocalDate() : null;
        }

        // 其他类型使用默认处理
        return super.processColumn(rs, index, propType);
    }
}