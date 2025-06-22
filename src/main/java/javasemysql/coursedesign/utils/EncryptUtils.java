package javasemysql.coursedesign.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 加密工具类，提供密码加密和验证的方法
 *
 * @author rywc2005
 * @version 1.0
 * @date 2025-06-21
 */
public class EncryptUtils {

    private static final Logger logger = Logger.getLogger(EncryptUtils.class.getName());
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * SHA-256加密
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Error encrypting password", e);
            return null;
        }
    }

    /**
     * 带盐值的SHA-256加密
     *
     * @param password 明文密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public static String encryptWithSalt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Error encrypting password with salt", e);
            return null;
        }
    }

    /**
     * 生成盐值
     *
     * @param length 盐值长度
     * @return 生成的盐值
     */
    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 验证密码
     *
     * @param password 明文密码
     * @param hashedPassword 加密后的密码
     * @return 验证是否通过
     */
    public static boolean verify(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        String encryptedPassword = encrypt(password);
        return encryptedPassword != null && encryptedPassword.equals(hashedPassword);
    }

    /**
     * 带盐值验证密码
     *
     * @param password 明文密码
     * @param hashedPassword 加密后的密码
     * @param salt 盐值
     * @return 验证是否通过
     */
    public static boolean verifyWithSalt(String password, String hashedPassword, String salt) {
        if (password == null || hashedPassword == null || salt == null) {
            return false;
        }

        String encryptedPassword = encryptWithSalt(password, salt);
        return encryptedPassword != null && encryptedPassword.equals(hashedPassword);
    }

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @return 生成的随机密码
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8; // 最小密码长度
        }

        // 密码字符集，包含大小写字母、数字和特殊字符
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();

        // 确保至少包含一个大写字母、一个小写字母、一个数字和一个特殊字符
        sb.append(chars.charAt(RANDOM.nextInt(26))); // 大写字母
        sb.append(chars.charAt(26 + RANDOM.nextInt(26))); // 小写字母
        sb.append(chars.charAt(52 + RANDOM.nextInt(10))); // 数字
        sb.append(chars.charAt(62 + RANDOM.nextInt(10))); // 特殊字符

        // 生成剩余的密码字符
        for (int i = 4; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        // 打乱顺序
        char[] password = sb.toString().toCharArray();
        for (int i = 0; i < password.length; i++) {
            int j = RANDOM.nextInt(password.length);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }

        return new String(password);
    }
}