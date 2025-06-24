package com.PFM.CD.utils.format;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 数字格式化工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class NumberFormatter {

    /**
     * 私有构造函数，防止实例化
     */
    private NumberFormatter() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 格式化数字（保留两位小数）
     *
     * @param number 数字
     * @return 格式化后的字符串
     */
    public static String format(double number) {
        return format(number, 2);
    }

    /**
     * 格式化数字（保留指定位数小数）
     *
     * @param number 数字
     * @param decimalPlaces 小数位数
     * @return 格式化后的字符串
     */
    public static String format(double number, int decimalPlaces) {
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(decimalPlaces);
        df.setGroupingUsed(true);

        return df.format(number);
    }

    /**
     * 格式化BigDecimal（保留两位小数）
     *
     * @param number BigDecimal数字
     * @return 格式化后的字符串
     */
    public static String format(BigDecimal number) {
        return format(number, 2);
    }

    /**
     * 格式化BigDecimal（保留指定位数小数）
     *
     * @param number BigDecimal数字
     * @param decimalPlaces 小数位数
     * @return 格式化后的字符串
     */
    public static String format(BigDecimal number, int decimalPlaces) {
        if (number == null) {
            return "";
        }

        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(decimalPlaces);
        df.setGroupingUsed(true);

        return df.format(number);
    }

    /**
     * 格式化为百分比（保留两位小数）
     *
     * @param number 数字
     * @return 格式化后的百分比字符串
     */
    public static String formatPercent(double number) {
        return formatPercent(number, 2);
    }

    /**
     * 格式化为百分比（保留指定位数小数）
     *
     * @param number 数字
     * @param decimalPlaces 小数位数
     * @return 格式化后的百分比字符串
     */
    public static String formatPercent(double number, int decimalPlaces) {
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(0);
        percentFormat.setMaximumFractionDigits(decimalPlaces);

        return percentFormat.format(number);
    }

    /**
     * 格式化BigDecimal为百分比（保留两位小数）
     *
     * @param number BigDecimal数字
     * @return 格式化后的百分比字符串
     */
    public static String formatPercent(BigDecimal number) {
        return formatPercent(number, 2);
    }

    /**
     * 格式化BigDecimal为百分比（保留指定位数小数）
     *
     * @param number BigDecimal数字
     * @param decimalPlaces 小数位数
     * @return 格式化后的百分比字符串
     */
    public static String formatPercent(BigDecimal number, int decimalPlaces) {
        if (number == null) {
            return "";
        }

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(0);
        percentFormat.setMaximumFractionDigits(decimalPlaces);

        return percentFormat.format(number.doubleValue());
    }

    /**
     * 格式化为带千位分隔符的数字
     *
     * @param number 数字
     * @return 格式化后的带千位分隔符的字符串
     */
    public static String formatWithGrouping(long number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(number);
    }

    /**
     * 格式化为带千位分隔符的数字
     *
     * @param number 数字
     * @return 格式化后的带千位分隔符的字符串
     */
    public static String formatWithGrouping(double number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);

        return numberFormat.format(number);
    }

    /**
     * 格式化为科学计数法
     *
     * @param number 数字
     * @return 格式化后的科学计数法字符串
     */
    public static String formatScientific(double number) {
        DecimalFormat scientificFormat = new DecimalFormat("0.###E0");
        return scientificFormat.format(number);
    }

    /**
     * 将数字四舍五入到指定小数位数
     *
     * @param number 数字
     * @param decimalPlaces 小数位数
     * @return 四舍五入后的数字
     */
    public static double round(double number, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * 将BigDecimal四舍五入到指定小数位数
     *
     * @param number BigDecimal数字
     * @param decimalPlaces 小数位数
     * @return 四舍五入后的BigDecimal
     */
    public static BigDecimal round(BigDecimal number, int decimalPlaces) {
        if (number == null) {
            return null;
        }

        return number.setScale(decimalPlaces, RoundingMode.HALF_UP);
    }

    /**
     * 解析数字字符串为double
     *
     * @param numberString 数字字符串
     * @return 解析后的double值，如果解析失败返回0
     */
    public static double parseDouble(String numberString) {
        if (numberString == null || numberString.isEmpty()) {
            return 0;
        }

        try {
            // 移除非数字字符（保留小数点和负号）
            String cleanedString = numberString.replaceAll("[^\\d.-]", "");
            return Double.parseDouble(cleanedString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 解析数字字符串为BigDecimal
     *
     * @param numberString 数字字符串
     * @return 解析后的BigDecimal值，如果解析失败返回BigDecimal.ZERO
     */
    public static BigDecimal parseBigDecimal(String numberString) {
        if (numberString == null || numberString.isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            // 移除非数字字符（保留小数点和负号）
            String cleanedString = numberString.replaceAll("[^\\d.-]", "");
            return new BigDecimal(cleanedString);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}