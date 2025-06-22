package javasemysql.coursedesign.utils;

/**
 * 字符串工具类，提供字符串处理的辅助方法
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class StringUtils {

    /**
     * 检查字符串是否为空或null
     *
     * @param str 待检查的字符串
     * @return 如果为空或null返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 检查字符串是否不为空且不为null
     *
     * @param str 待检查的字符串
     * @return 如果不为空且不为null返回true，否则返回false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 截断字符串到指定长度，如果超过则添加省略号
     *
     * @param str 原字符串
     * @param maxLength 最大长度
     * @return 截断后的字符串
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }

        if (str.length() <= maxLength) {
            return str;
        }

        return str.substring(0, maxLength) + "...";
    }

    /**
     * 将字符串首字母大写
     *
     * @param str 原字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 将字符串首字母小写
     *
     * @param str 原字符串
     * @return 首字母小写的字符串
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }

        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 从左侧填充字符串到指定长度
     *
     * @param str 原字符串
     * @param size 目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串
     */
    public static String padLeft(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= size) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size - str.length(); i++) {
            sb.append(padChar);
        }
        sb.append(str);

        return sb.toString();
    }

    /**
     * 从右侧填充字符串到指定长度
     *
     * @param str 原字符串
     * @param size 目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串
     */
    public static String padRight(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= size) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < size - str.length(); i++) {
            sb.append(padChar);
        }

        return sb.toString();
    }

    /**
     * 格式化金额为两位小数的字符串
     *
     * @param amount 金额
     * @return 格式化后的金额字符串
     */
    public static String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }

    /**
     * 格式化金额为货币格式
     *
     * @param amount 金额
     * @return 格式化后的货币字符串（如：¥100.00）
     */
    public static String formatCurrency(double amount) {
        return "¥" + formatAmount(amount);
    }

    /**
     * 替换字符串中的特殊字符
     *
     * @param str 原字符串
     * @return 替换后的字符串
     */
    public static String escapeSpecialChars(String str) {
        if (isEmpty(str)) {
            return str;
        }

        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        // 简单的邮箱格式验证
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}