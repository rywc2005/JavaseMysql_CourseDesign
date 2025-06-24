package com.PFM.CD.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CSV文件处理工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class CsvUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    /**
     * 私有构造函数，防止实例化
     */
    private CsvUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 读取CSV文件
     *
     * @param filePath 文件路径
     * @return CSV数据（行列表，每行是字段列表）
     * @throws IOException 如果读取过程中发生IO错误
     */
    public static List<List<String>> readCsv(String filePath) throws IOException {
        return readCsv(filePath, DEFAULT_SEPARATOR, DEFAULT_QUOTE, StandardCharsets.UTF_8);
    }

    /**
     * 读取CSV文件（指定分隔符和引号字符）
     *
     * @param filePath 文件路径
     * @param separator 分隔符
     * @param quote 引号字符
     * @param charset 字符集
     * @return CSV数据（行列表，每行是字段列表）
     * @throws IOException 如果读取过程中发生IO错误
     */
    public static List<List<String>> readCsv(String filePath, char separator, char quote, Charset charset)
            throws IOException {
        List<List<String>> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), charset))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> values = parseCsvLine(line, separator, quote);
                records.add(values);
            }
        }

        return records;
    }

    /**
     * 写入CSV文件
     *
     * @param filePath 文件路径
     * @param records CSV数据（行列表，每行是字段列表）
     * @throws IOException 如果写入过程中发生IO错误
     */
    public static void writeCsv(String filePath, List<List<String>> records) throws IOException {
        writeCsv(filePath, records, DEFAULT_SEPARATOR, DEFAULT_QUOTE, StandardCharsets.UTF_8);
    }

    /**
     * 写入CSV文件（指定分隔符和引号字符）
     *
     * @param filePath 文件路径
     * @param records CSV数据（行列表，每行是字段列表）
     * @param separator 分隔符
     * @param quote 引号字符
     * @param charset 字符集
     * @throws IOException 如果写入过程中发生IO错误
     */
    public static void writeCsv(String filePath, List<List<String>> records, char separator, char quote,
                                Charset charset) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), charset))) {
            for (List<String> record : records) {
                bw.write(formatCsvLine(record, separator, quote));
                bw.newLine();
            }
        }
    }

    /**
     * 解析CSV行
     *
     * @param line CSV行
     * @param separator 分隔符
     * @param quote 引号字符
     * @return 字段列表
     */
    private static List<String> parseCsvLine(String line, char separator, char quote) {
        List<String> result = new ArrayList<>();

        if (line == null || line.isEmpty()) {
            return result;
        }

        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == quote) {
                // 处理引号字符
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == quote) {
                    // 转义的引号（两个连续的引号）
                    field.append(quote);
                    i++; // 跳过下一个引号
                } else {
                    // 切换引号状态
                    inQuotes = !inQuotes;
                }
            } else if (c == separator && !inQuotes) {
                // 处理分隔符（不在引号内）
                result.add(field.toString());
                field.setLength(0); // 清空字段
            } else {
                // 普通字符
                field.append(c);
            }
        }

        // 添加最后一个字段
        result.add(field.toString());

        return result;
    }

    /**
     * 格式化CSV行
     *
     * @param record 字段列表
     * @param separator 分隔符
     * @param quote 引号字符
     * @return 格式化后的CSV行
     */
    private static String formatCsvLine(List<String> record, char separator, char quote) {
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < record.size(); i++) {
            if (i > 0) {
                line.append(separator);
            }

            String field = record.get(i);
            if (field == null) {
                field = "";
            }

            // 检查是否需要引号
            boolean needQuotes = field.contains(String.valueOf(separator)) ||
                    field.contains(String.valueOf(quote)) ||
                    field.contains("\n") ||
                    field.contains("\r");

            if (needQuotes) {
                // 添加引号并转义内部引号
                line.append(quote);
                line.append(field.replace(String.valueOf(quote), String.valueOf(quote) + quote));
                line.append(quote);
            } else {
                line.append(field);
            }
        }

        return line.toString();
    }

    /**
     * 解析CSV文件的第一行作为标题行
     *
     * @param filePath 文件路径
     * @return 标题列表
     * @throws IOException 如果读取过程中发生IO错误
     */
    public static List<String> readCsvHeader(String filePath) throws IOException {
        List<List<String>> records = readCsv(filePath);
        return records.isEmpty() ? new ArrayList<>() : records.get(0);
    }

    /**
     * 从CSV字符串中解析数据
     *
     * @param csvContent CSV内容字符串
     * @return CSV数据（行列表，每行是字段列表）
     */
    public static List<List<String>> parseCsvContent(String csvContent) {
        return parseCsvContent(csvContent, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /**
     * 从CSV字符串中解析数据（指定分隔符和引号字符）
     *
     * @param csvContent CSV内容字符串
     * @param separator 分隔符
     * @param quote 引号字符
     * @return CSV数据（行列表，每行是字段列表）
     */
    public static List<List<String>> parseCsvContent(String csvContent, char separator, char quote) {
        List<List<String>> records = new ArrayList<>();

        if (csvContent == null || csvContent.isEmpty()) {
            return records;
        }

        String[] lines = csvContent.split("\r\n|\n|\r");
        for (String line : lines) {
            List<String> values = parseCsvLine(line, separator, quote);
            records.add(values);
        }

        return records;
    }

    /**
     * 将CSV数据转换为CSV字符串
     *
     * @param records CSV数据（行列表，每行是字段列表）
     * @return CSV内容字符串
     */
    public static String toCsvContent(List<List<String>> records) {
        return toCsvContent(records, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    /**
     * 将CSV数据转换为CSV字符串（指定分隔符和引号字符）
     *
     * @param records CSV数据（行列表，每行是字段列表）
     * @param separator 分隔符
     * @param quote 引号字符
     * @return CSV内容字符串
     */
    public static String toCsvContent(List<List<String>> records, char separator, char quote) {
        StringBuilder sb = new StringBuilder();

        for (List<String> record : records) {
            if (sb.length() > 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(formatCsvLine(record, separator, quote));
        }

        return sb.toString();
    }

    /**
     * 将二维字符串数组转换为CSV数据
     *
     * @param data 二维字符串数组
     * @return CSV数据（行列表，每行是字段列表）
     */
    public static List<List<String>> arrayToCsv(String[][] data) {
        List<List<String>> records = new ArrayList<>();

        for (String[] row : data) {
            records.add(Arrays.asList(row));
        }

        return records;
    }

    /**
     * 将CSV数据转换为二维字符串数组
     *
     * @param records CSV数据（行列表，每行是字段列表）
     * @return 二维字符串数组
     */
    public static String[][] csvToArray(List<List<String>> records) {
        String[][] data = new String[records.size()][];

        for (int i = 0; i < records.size(); i++) {
            List<String> record = records.get(i);
            data[i] = record.toArray(new String[0]);
        }

        return data;
    }
}