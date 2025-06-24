package com.PFM.CD.utils.format;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 货币格式化工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class CurrencyFormatter {

    private static final Map<String, Locale> CURRENCY_LOCALES = new HashMap<>();

    static {
        CURRENCY_LOCALES.put("CNY", Locale.CHINA);
        CURRENCY_LOCALES.put("USD", Locale.US);
        CURRENCY_LOCALES.put("EUR", Locale.GERMANY);
        CURRENCY_LOCALES.put("JPY", Locale.JAPAN);
        CURRENCY_LOCALES.put("GBP", Locale.UK);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private CurrencyFormatter() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 格式化货币金额（默认使用中国区域设置）
     *
     * @param amount 金额
     * @return 格式化后的货币字符串
     */
    public static String format(BigDecimal amount) {
        return format(amount, "CNY");
    }

    /**
     * 格式化货币金额
     *
     * @param amount 金额
     * @param currencyCode 货币代码（ISO 4217）
     * @return 格式化后的货币字符串
     */
    public static String format(BigDecimal amount, String currencyCode) {
        Locale locale = CURRENCY_LOCALES.getOrDefault(currencyCode, Locale.getDefault());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(Currency.getInstance(currencyCode));

        return currencyFormatter.format(amount);
    }

    /**
     * 格式化货币金额（自定义格式）
     *
     * @param amount 金额
     * @param symbol 货币符号
     * @param decimalPlaces 小数位数
     * @param groupingUsed 是否使用千位分隔符
     * @return 格式化后的货币字符串
     */
    public static String format(BigDecimal amount, String symbol, int decimalPlaces, boolean groupingUsed) {
        // 设置小数位数和舍入模式
        amount = amount.setScale(decimalPlaces, RoundingMode.HALF_UP);

        // 创建格式化器
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(decimalPlaces);
        decimalFormat.setMaximumFractionDigits(decimalPlaces);
        decimalFormat.setGroupingUsed(groupingUsed);

        // 格式化金额
        String formattedAmount = decimalFormat.format(amount);

        // 添加货币符号
        return symbol + formattedAmount;
    }

    /**
     * 解析货币字符串为BigDecimal
     *
     * @param currencyString 货币字符串
     * @return 解析后的金额
     * @throws Exception 如果解析失败
     */
    public static BigDecimal parse(String currencyString) throws Exception {
        // 移除货币符号和千位分隔符
        String cleanedString = currencyString.replaceAll("[^\\d.,]", "")
                .replace(",", ".");

        // 如果有多个小数点，只保留最后一个
        int lastDotIndex = cleanedString.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            int firstDotIndex = cleanedString.indexOf(".");
            if (firstDotIndex != lastDotIndex) {
                cleanedString = cleanedString.substring(0, firstDotIndex) +
                        cleanedString.substring(firstDotIndex + 1);
            }
        }

        return new BigDecimal(cleanedString);
    }

    /**
     * 将金额转换为中文大写
     *
     * @param amount 金额
     * @return 中文大写金额
     */
    public static String toChinese(BigDecimal amount) {
        // 四舍五入到分
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        // 分解成整数部分和小数部分
        String[] parts = amount.toString().split("\\.");
        String integerPart = parts[0];
        String decimalPart = parts.length > 1 ? parts[1] : "00";

        // 如果小数部分长度不足2位，补0
        if (decimalPart.length() == 1) {
            decimalPart += "0";
        }

        // 中文数字
        String[] chineseNumbers = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

        // 整数部分的位级单位
        String[] integerUnits = {"", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆"};

        // 处理整数部分
        StringBuilder result = new StringBuilder();

        // 处理负数
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            result.append("负");
            integerPart = integerPart.substring(1);
        }

        // 处理整数部分为0的情况
        if (integerPart.equals("0")) {
            result.append("零元");
        } else {
            for (int i = 0; i < integerPart.length(); i++) {
                int digit = Character.getNumericValue(integerPart.charAt(i));
                int position = integerPart.length() - i - 1;

                if (digit != 0) {
                    result.append(chineseNumbers[digit]).append(integerUnits[position]);
                } else {
                    // 处理连续的零
                    if (i < integerPart.length() - 1 &&
                            Character.getNumericValue(integerPart.charAt(i + 1)) != 0) {
                        result.append(chineseNumbers[digit]);
                    }
                }
            }
            result.append("元");
        }

        // 处理小数部分
        int jiao = Character.getNumericValue(decimalPart.charAt(0));
        int fen = Character.getNumericValue(decimalPart.charAt(1));

        if (jiao == 0 && fen == 0) {
            result.append("整");
        } else {
            if (jiao != 0) {
                result.append(chineseNumbers[jiao]).append("角");
            }
            if (fen != 0) {
                result.append(chineseNumbers[fen]).append("分");
            }
        }

        return result.toString();
    }

    /**
     * 获取简单的货币符号
     *
     * @param currencyCode 货币代码（ISO 4217）
     * @return 货币符号
     */
    public static String getCurrencySymbol(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        Locale locale = CURRENCY_LOCALES.getOrDefault(currencyCode, Locale.getDefault());
        return currency.getSymbol(locale);
    }
}