package javasemysql.coursedesign.utils;

import java.util.regex.Pattern;
import java.util.Date;

/**
 * 数据验证工具类，提供各种数据验证方法
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class ValidationUtils {

    // 用户名验证：字母开头，允许5-20字节，允许字母数字下划线
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]{4,19}$");

    // 密码验证：必须包含大小写字母和数字，特殊字符可选，8-20字符
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9!@#$%^&*]{8,20}$");

    // 简单密码验证：6-20个字符，不包含空格
    private static final Pattern SIMPLE_PASSWORD_PATTERN =
            Pattern.compile("^[^\\s]{6,20}$");

    // 邮箱验证
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    // 金额验证：正数，最多两位小数
    private static final Pattern AMOUNT_PATTERN =
            Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    // 电话号码验证：11位数字，以1开头
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^1\\d{10}$");

    /**
     * 验证用户名
     *
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 验证密码（强规则）
     *
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证密码（简单规则）
     *
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidSimplePassword(String password) {
        return password != null && SIMPLE_PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 验证邮箱
     *
     * @param email 邮箱
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证金额
     *
     * @param amount 金额
     * @return 是否有效
     */
    public static boolean isValidAmount(String amount) {
        return amount != null && AMOUNT_PATTERN.matcher(amount).matches();
    }

    /**
     * 验证金额
     *
     * @param amount 金额
     * @return 是否有效
     */
    public static boolean isValidAmount(double amount) {
        return amount >= 0;
    }

    /**
     * 验证手机号码
     *
     * @param phone 手机号码
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证日期格式
     *
     * @param dateStr 日期字符串
     * @return 是否有效
     */
    public static boolean isValidDate(String dateStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证日期有效性
     *
     * @param date 日期对象
     * @return 是否有效
     */
    public static boolean isValidDate(Date date) {
        return date != null;
    }

    /**
     * 验证日期范围
     *
     * @param date 日期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 是否在范围内
     */
    public static boolean isDateInRange(Date date, Date startDate, Date endDate) {
        if (date == null) {
            return false;
        }

        boolean afterStart = true;
        boolean beforeEnd = true;

        if (startDate != null) {
            afterStart = !date.before(startDate);
        }

        if (endDate != null) {
            beforeEnd = !date.after(endDate);
        }

        return afterStart && beforeEnd;
    }

    /**
     * 验证数值范围
     *
     * @param value 数值
     * @param min 最小值
     * @param max 最大值
     * @return 是否在范围内
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * 验证字符串不为空
     *
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * 验证对象不为空
     *
     * @param obj 对象
     * @return 是否不为空
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * 验证字符串长度
     *
     * @param str 字符串
     * @param min 最小长度
     * @param max 最大长度
     * @return 是否符合长度要求
     */
    public static boolean isLengthValid(String str, int min, int max) {
        if (str == null) {
            return min == 0;
        }

        int length = str.length();
        return length >= min && length <= max;
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }
        // 检查是否以1开头
        if (phone.charAt(0) != '1') {
            return false;
        }
        // 检查是否全为数字
        for (int i = 0; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}