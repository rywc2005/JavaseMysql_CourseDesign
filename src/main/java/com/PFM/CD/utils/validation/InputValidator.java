package com.PFM.CD.utils.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 输入验证工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class InputValidator {

    /**
     * 私有构造函数，防止实例化
     */
    private InputValidator() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 如果格式正确返回true，否则返回false
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        return Pattern.matches(RegexPatterns.EMAIL_PATTERN, email);
    }

    /**
     * 验证用户名格式
     *
     * @param username 用户名
     * @return 如果格式正确返回true，否则返回false
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }

        return Pattern.matches(RegexPatterns.USERNAME_PATTERN, username) &&
                username.length() >= 3 && username.length() <= 50;
    }

    /**
     * 验证密码强度
     *
     * @param password 密码
     * @return 如果密码强度符合要求返回true，否则返回false
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // 密码长度至少8位
        if (password.length() < 8) {
            return false;
        }

        // 包含至少一个小写字母
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return false;
        }

        // 包含至少一个大写字母
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return false;
        }

        // 包含至少一个数字
        if (!Pattern.compile("\\d").matcher(password).find()) {
            return false;
        }

        // 包含至少一个特殊字符
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) {
            return false;
        }

        return true;
    }

    /**
     * 验证金额格式
     *
     * @param amount 金额字符串
     * @return 如果格式正确返回true，否则返回false
     */
    public static boolean isValidAmount(String amount) {
        if (amount == null || amount.isEmpty()) {
            return false;
        }

        try {
            BigDecimal value = new BigDecimal(amount);
            return value.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证日期格式
     *
     * @param dateString 日期字符串
     * @param pattern 日期格式模式
     * @return 如果格式正确返回true，否则返回false
     */
    public static boolean isValidDate(String dateString, String pattern) {
        if (dateString == null || dateString.isEmpty()) {
            return false;
        }

        try {
            LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 验证日期范围
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 如果范围有效（开始日期不晚于结束日期）返回true，否则返回false
     */
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        return !startDate.isAfter(endDate);
    }

    /**
     * 验证整数范围
     *
     * @param value 整数值
     * @param min 最小值
     * @param max 最大值
     * @return 如果在范围内返回true，否则返回false
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * 验证表单字段
     *
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @param required 是否必填
     * @param pattern 验证模式（可选）
     * @param minLength 最小长度（可选，为null时不检查）
     * @param maxLength 最大长度（可选，为null时不检查）
     * @return 错误信息，如果验证通过返回null
     */
    public static String validateField(String fieldName, String fieldValue,
                                       boolean required, String pattern,
                                       Integer minLength, Integer maxLength) {
        // 检查必填字段
        if (required && (fieldValue == null || fieldValue.trim().isEmpty())) {
            return fieldName + "不能为空";
        }

        // 如果字段为空且非必填，则跳过后续验证
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            return null;
        }

        // 检查长度
        if (minLength != null && fieldValue.length() < minLength) {
            return fieldName + "长度不能小于" + minLength;
        }

        if (maxLength != null && fieldValue.length() > maxLength) {
            return fieldName + "长度不能大于" + maxLength;
        }

        // 检查模式
        if (pattern != null && !Pattern.matches(pattern, fieldValue)) {
            return fieldName + "格式不正确";
        }

        return null;
    }

    /**
     * 验证多个表单字段
     *
     * @param fields 字段映射（字段名到字段值）
     * @param requiredFields 必填字段列表
     * @return 错误信息映射（字段名到错误信息），如果全部验证通过返回空映射
     */
    public static Map<String, String> validateForm(Map<String, String> fields, String[] requiredFields) {
        Map<String, String> errors = new HashMap<>();

        // 验证必填字段
        for (String field : requiredFields) {
            String value = fields.get(field);
            if (value == null || value.trim().isEmpty()) {
                errors.put(field, field + "不能为空");
            }
        }

        return errors;
    }
}