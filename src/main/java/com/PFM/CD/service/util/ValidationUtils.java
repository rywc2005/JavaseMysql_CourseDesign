package com.PFM.CD.service.util;

import com.PFM.CD.service.ServiceConstants;
import com.PFM.CD.service.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_-]+$");

    /**
     * 私有构造函数，防止实例化
     */
    private ValidationUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 验证用户名
     *
     * @param username 用户名
     * @throws ValidationException 如果验证失败
     */
    public static void validateUsername(String username) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (username == null || username.isEmpty()) {
            errors.put("username", "用户名不能为空");
        } else if (username.length() < ServiceConstants.MIN_USERNAME_LENGTH ||
                username.length() > ServiceConstants.MAX_USERNAME_LENGTH) {
            errors.put("username", "用户名长度必须在" + ServiceConstants.MIN_USERNAME_LENGTH +
                    "到" + ServiceConstants.MAX_USERNAME_LENGTH + "之间");
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            errors.put("username", "用户名只能包含字母、数字、下划线和连字符");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    /**
     * 验证密码
     *
     * @param password 密码
     * @throws ValidationException 如果验证失败
     */
    public static void validatePassword(String password) throws ValidationException {
        Map<String, String> errors = new HashMap<>();

        if (password == null || password.isEmpty()) {
            errors.put("password", "密码不能为空");
        } else if (password.length() < ServiceConstants.MIN_PASSWORD_LENGTH ||
                password.length() > ServiceConstants.MAX_PASSWORD_LENGTH) {
            errors.put("password", "密码长度必须在" + ServiceConstants.MIN_PASSWORD_LENGTH +
                    "到" + ServiceConstants.MAX_PASSWORD_LENGTH + "之间");
        } else if (!password.matches(".*[A-Z].*")) {
            errors.put("password", "密码必须包含至少一个大写字母");
        } else if (!password.matches(".*[a-z].*")) {
            errors.put("password", "密码必须包含至少一个小写字母");
        } else if (!password.matches(".*\\d.*")) {
            errors.put("password", "密码必须包含至少一个数字");
        } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.put("password", "密码必须包含至少一个特殊字符");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    /**
     * 验证邮箱
     *
     * @param email 邮箱
     * @throws ValidationException 如果验证失败
     */
    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.isEmpty()) {
            throw new ValidationException("email", "邮箱不能为空");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("email", "邮箱格式不正确");
        }
    }

    /**
     * 验证金额
     *
     * @param amount 金额
     * @param fieldName 字段名称
     * @throws ValidationException 如果验证失败
     */
    public static void validateAmount(BigDecimal amount, String fieldName) throws ValidationException {
        if (amount == null) {
            throw new ValidationException(fieldName, "金额不能为空");
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName, "金额必须为正数");
        } else if (amount.compareTo(ServiceConstants.MAX_TRANSACTION_AMOUNT) > 0) {
            throw new ValidationException(fieldName, "金额不能超过" +
                    ServiceConstants.MAX_TRANSACTION_AMOUNT);
        }
    }

    /**
     * 验证日期范围
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @throws ValidationException 如果验证失败
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate) throws ValidationException {
        if (startDate == null) {
            throw new ValidationException("startDate", "开始日期不能为空");
        } else if (endDate == null) {
            throw new ValidationException("endDate", "结束日期不能为空");
        } else if (endDate.isBefore(startDate)) {
            throw new ValidationException("dateRange", "结束日期不能早于开始日期");
        }
    }

    /**
     * 验证账户名称
     *
     * @param accountName 账户名称
     * @throws ValidationException 如果验证失败
     */
    public static void validateAccountName(String accountName) throws ValidationException {
        if (accountName == null || accountName.isEmpty()) {
            throw new ValidationException("accountName", "账户名称不能为空");
        } else if (accountName.length() > 100) {
            throw new ValidationException("accountName", "账户名称长度不能超过100");
        }
    }

    /**
     * 验证分类名称
     *
     * @param categoryName 分类名称
     * @throws ValidationException 如果验证失败
     */
    public static void validateCategoryName(String categoryName) throws ValidationException {
        if (categoryName == null || categoryName.isEmpty()) {
            throw new ValidationException("categoryName", "分类名称不能为空");
        } else if (categoryName.length() > ServiceConstants.MAX_CATEGORY_NAME_LENGTH) {
            throw new ValidationException("categoryName", "分类名称长度不能超过" +
                    ServiceConstants.MAX_CATEGORY_NAME_LENGTH);
        }
    }

    /**
     * 验证交易描述
     *
     * @param description 交易描述
     * @throws ValidationException 如果验证失败
     */
    public static void validateTransactionDescription(String description) throws ValidationException {
        if (description != null && description.length() > ServiceConstants.MAX_TRANSACTION_DESCRIPTION_LENGTH) {
            throw new ValidationException("description", "交易描述长度不能超过" +
                    ServiceConstants.MAX_TRANSACTION_DESCRIPTION_LENGTH);
        }
    }
}