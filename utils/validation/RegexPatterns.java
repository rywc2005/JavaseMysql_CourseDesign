package com.PFM.CD.utils.validation;

/**
 * 正则表达式模式常量类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class RegexPatterns {

    /**
     * 私有构造函数，防止实例化
     */
    private RegexPatterns() {
        throw new IllegalStateException("常量类不应被实例化");
    }

    /**
     * 邮箱格式正则表达式
     */
    public static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    /**
     * 用户名格式正则表达式（字母、数字、下划线和连字符）
     */
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,50}$";

    /**
     * 密码格式正则表达式（至少包含一个大写字母、一个小写字母、一个数字和一个特殊字符）
     */
    public static final String STRONG_PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,}$";

    /**
     * 手机号格式正则表达式（中国大陆手机号）
     */
    public static final String CHINA_MOBILE_PATTERN = "^1[3-9]\\d{9}$";

    /**
     * 身份证号格式正则表达式（中国大陆居民身份证号18位）
     */
    public static final String CHINA_ID_CARD_PATTERN =
            "^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";

    /**
     * 金额格式正则表达式（非负数，最多两位小数）
     */
    public static final String AMOUNT_PATTERN = "^\\d+(\\.\\d{1,2})?$";

    /**
     * 日期格式正则表达式（YYYY-MM-DD）
     */
    public static final String DATE_PATTERN = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";

    /**
     * 时间格式正则表达式（HH:MM:SS）
     */
    public static final String TIME_PATTERN = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";

    /**
     * 日期时间格式正则表达式（YYYY-MM-DD HH:MM:SS）
     */
    public static final String DATETIME_PATTERN =
            "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]) ([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";

    /**
     * 银行卡号格式正则表达式（13-19位数字）
     */
    public static final String BANK_CARD_PATTERN = "^\\d{13,19}$";

    /**
     * URL格式正则表达式
     */
    public static final String URL_PATTERN =
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";

    /**
     * IPv4地址格式正则表达式
     */
    public static final String IPV4_PATTERN =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    /**
     * 中文字符格式正则表达式
     */
    public static final String CHINESE_PATTERN = "^[\\u4e00-\\u9fa5]+$";

    /**
     * 邮政编码格式正则表达式（中国大陆邮政编码）
     */
    public static final String POSTAL_CODE_PATTERN = "^[1-9]\\d{5}$";
}