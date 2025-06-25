package com.PFM.CD.utils.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import com.PFM.CD.utils.format.DateFormatter;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoredProcedureExecutor {
    private static final QueryRunner queryRunner = new QueryRunner();

    /**
     * 调用存储过程（无返回结果）
     * @param procName 存储过程名称
     * @param params 输入参数（支持LocalDate、数字等类型）
     */
    public static void execute(String procName, Object... params) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String[] paramStrings = Stream.of(params)
                    .map(StoredProcedureExecutor::convertParamToString)  // 自定义参数转换逻辑
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            String sql = "{call " + procName + "(" + String.join(",", paramStrings) + ")}";
            queryRunner.update(conn, sql, params);  // 保持原始参数类型传递给数据库
        }
    }

    /**
     * 调用存储过程（返回结果集）
     * @param procName 存储过程名称
     * @param params 输入参数（支持LocalDate、数字等类型）
     * @return 结果集（Map列表）
     */
    public static List<Map<String, Object>> executeForResult(String procName, Object... params) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            String[] paramStrings = Stream.of(params)
                    .map(StoredProcedureExecutor::convertParamToString)  // 自定义参数转换逻辑
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            String sql = "{call " + procName + "(" + String.join(",", paramStrings) + ")}";
            return queryRunner.query(conn, sql, new MapListHandler(), params);  // 保持原始参数类型
        }
    }

    /**
     * 自定义参数转换逻辑（处理日期、数字等类型）
     */
    private static String convertParamToString(Object param) {
        if (param instanceof LocalDate) {
            // 使用系统统一的日期格式（如"yyyy-MM-dd"）
            return "'" + DateFormatter.format((LocalDate) param) + "'";
        } else if (param instanceof Number) {
            // 数字类型直接转换为字符串
            return param.toString();
        } else {
            // 其他类型使用String.valueOf（防止null）
            return "'" + String.valueOf(param) + "'";
        }
    }
}
