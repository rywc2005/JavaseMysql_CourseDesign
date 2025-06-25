package com.PFM.CD.utils.db;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoredProcedureExecutor {
    private static final QueryRunner queryRunner = new QueryRunner();

    /**
     * 调用存储过程（无返回结果）
     * @param procName 存储过程名称
     * @param params 输入参数
     */
    public static void execute(String procName, Object... params) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 将Object[]转换为String[]（处理非CharSequence类型参数）
            String[] paramStrings = Stream.of(params)
                    .map(String::valueOf)
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            String sql = "{call " + procName + "(" + String.join(",", paramStrings) + ")}";
            queryRunner.update(conn, sql, params);
        }
    }

    /**
     * 调用存储过程（返回结果集）
     * @param procName 存储过程名称
     * @param params 输入参数
     * @return 结果集（Map列表）
     */
    public static List<Map<String, Object>> executeForResult(String procName, Object... params) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 将Object[]转换为String[]（处理非CharSequence类型参数）
            String[] paramStrings = Stream.of(params)
                    .map(String::valueOf)
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            String sql = "{call " + procName + "(" + String.join(",", paramStrings) + ")}";
            return queryRunner.query(conn, sql, new MapListHandler(), params);
        }
    }
}
