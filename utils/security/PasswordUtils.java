package com.PFM.CD.utils.security;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

/**
 * 密码处理工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class PasswordUtils {

    private static final int BCRYPT_ROUNDS = 12;
    private static final int TOKEN_LENGTH = 32;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 私有构造函数，防止实例化
     */
    private PasswordUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 使用BCrypt算法对密码进行哈希
     *
     * @param plainPassword 明文密码
     * @return 密码哈希
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param hashedPassword 哈希后的密码
     * @return 如果密码匹配返回true，否则返回false
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @param includeLowercase 是否包含小写字母
     * @param includeUppercase 是否包含大写字母
     * @param includeDigits 是否包含数字
     * @param includeSpecial 是否包含特殊字符
     * @return 随机密码
     */
    public static String generateRandomPassword(int length, boolean includeLowercase,
                                                boolean includeUppercase, boolean includeDigits,
                                                boolean includeSpecial) {
        StringBuilder charPool = new StringBuilder();

        if (includeLowercase) {
            charPool.append("abcdefghijklmnopqrstuvwxyz");
        }

        if (includeUppercase) {
            charPool.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }

        if (includeDigits) {
            charPool.append("0123456789");
        }

        if (includeSpecial) {
            charPool.append("!@#$%^&*()_-+=<>?");
        }

        // 确保字符池不为空
        if (charPool.length() == 0) {
            throw new IllegalArgumentException("至少需要选择一种字符类型");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(charPool.length());
            password.append(charPool.charAt(randomIndex));
        }

        return password.toString();
    }

    /**
     * 生成随机令牌
     *
     * @return 随机令牌
     */
    public static String generateToken() {
        return generateToken(TOKEN_LENGTH);
    }

    /**
     * 生成指定长度的随机令牌
     *
     * @param length 令牌长度
     * @return 随机令牌
     */
    public static String generateToken(int length) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(randomIndex));
        }

        return token.toString();
    }

    /**
     * 生成随机盐值
     *
     * @param length 盐值长度
     * @return 随机盐值的字节数组
     */
    public static byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * 检查密码强度
     *
     * @param password 密码
     * @return 密码强度（0-4，数字越大强度越高）
     */
    public static int checkPasswordStrength(String password) {
        int strength = 0;

        // 密码长度大于8
        if (password.length() >= 8) {
            strength++;
        }

        // 包含小写字母
        if (password.matches(".*[a-z].*")) {
            strength++;
        }

        // 包含大写字母
        if (password.matches(".*[A-Z].*")) {
            strength++;
        }

        // 包含数字
        if (password.matches(".*\\d.*")) {
            strength++;
        }

        // 包含特殊字符
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            strength++;
        }

        return strength;
    }

    /**
     * 获取密码强度描述
     *
     * @param password 密码
     * @return 密码强度描述
     */
    public static String getPasswordStrengthDescription(String password) {
        int strength = checkPasswordStrength(password);

        switch (strength) {
            case 0:
            case 1:
                return "非常弱";
            case 2:
                return "弱";
            case 3:
                return "中等";
            case 4:
                return "强";
            case 5:
                return "非常强";
            default:
                return "未知";
        }
    }
}