package com.PFM.CD.dao;

import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.BasicRowProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CustomRowProcessor extends BasicRowProcessor {
    private static final BeanProcessor beanProcessor;

    static {
        // 数据库列名到Java属性名的映射
        Map<String, String> columnToPropertyOverrides = new HashMap<>();
        columnToPropertyOverrides.put("user_id", "userId");
        columnToPropertyOverrides.put("account_id", "accountId");
        columnToPropertyOverrides.put("account_name", "accountName");
        columnToPropertyOverrides.put("category_id", "categoryId");
        columnToPropertyOverrides.put("category_name", "categoryName");
        columnToPropertyOverrides.put("category_type", "categoryType");
        columnToPropertyOverrides.put("transaction_id", "transactionId");
        columnToPropertyOverrides.put("source_account_id", "sourceAccountId");
        columnToPropertyOverrides.put("destination_account_id", "destinationAccountId");
        columnToPropertyOverrides.put("transaction_type", "transactionType");
        columnToPropertyOverrides.put("transaction_date", "transactionDate");
        columnToPropertyOverrides.put("budget_id", "budgetId");
        columnToPropertyOverrides.put("period_type", "periodType");
        columnToPropertyOverrides.put("start_date", "startDate");
        columnToPropertyOverrides.put("end_date", "endDate");
        columnToPropertyOverrides.put("total_amount", "totalAmount");
        columnToPropertyOverrides.put("budget_category_id", "budgetCategoryId");
        columnToPropertyOverrides.put("allocated_amount", "allocatedAmount");
        columnToPropertyOverrides.put("spent_amount", "spentAmount");
        columnToPropertyOverrides.put("report_id", "reportId");
        columnToPropertyOverrides.put("report_type", "reportType");
        columnToPropertyOverrides.put("generated_date", "generatedDate");
        columnToPropertyOverrides.put("password_hash", "passwordHash");
        columnToPropertyOverrides.put("created_at", "createdAt");
        columnToPropertyOverrides.put("source_account_name", "sourceAccountName");
        columnToPropertyOverrides.put("destination_account_name", "destinationAccountName");

        beanProcessor = new BeanProcessor(columnToPropertyOverrides) {
            @Override
            protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
                if (propType == LocalDate.class && rs.getObject(index) != null) {
                    return rs.getDate(index).toLocalDate();
                }
                return super.processColumn(rs, index, propType);
            }
        };
    }

    public CustomRowProcessor() {
        super(beanProcessor);
    }
}