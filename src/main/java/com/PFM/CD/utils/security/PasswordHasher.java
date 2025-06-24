package com.PFM.CD.utils.security;

import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 密码哈希处理工具类，提供多种密码哈希算法和验证方法
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class PasswordHasher {

    // 哈希算法类型枚举
    public enum HashAlgorithm {
        BCRYPT, PBKDF2, ARGON2ID
    }

    private static final int BCRYPT_ROUNDS = 12;
    private static final int PBKDF2_ITERATIONS = 310000;
    private static final int PBKDF2_HASH_SIZE = 32;
    private static final int SALT_SIZE = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static HashAlgorithm defaultAlgorithm = HashAlgorithm.BCRYPT;

    /**
     * 构造函数
     */
    public PasswordHasher() {
    }

    /**
     * 带算法的构造函数
     *
     * @param algorithm 默认哈希算法
     */
    public PasswordHasher(HashAlgorithm algorithm) {
        this.defaultAlgorithm = algorithm;
    }

    /**
     * 使用默认算法对密码进行哈希
     *
     * @param plainPassword 明文密码
     * @return 哈希后的密码
     */
    public static String hashPassword(String plainPassword) {
        return hashPassword(plainPassword, defaultAlgorithm);
    }

    /**
     * 使用指定算法对密码进行哈希
     *
     * @param plainPassword 明文密码
     * @param algorithm 哈希算法
     * @return 哈希后的密码
     */
    public static String hashPassword(String plainPassword, HashAlgorithm algorithm) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        switch (algorithm) {
            case BCRYPT:
                return hashWithBCrypt(plainPassword);
            case PBKDF2:
                return hashWithPBKDF2(plainPassword);
            case ARGON2ID:
                return hashWithArgon2id(plainPassword);
            default:
                return hashWithBCrypt(plainPassword);
        }
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param hashedPassword 哈希后的密码
     * @return 如果密码匹配返回true，否则返回false
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        // 通过哈希值前缀识别算法
        if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
            return verifyBCrypt(plainPassword, hashedPassword);
        } else if (hashedPassword.startsWith("pbkdf2:")) {
            return verifyPBKDF2(plainPassword, hashedPassword);
        } else if (hashedPassword.startsWith("argon2id:")) {
            return verifyArgon2id(plainPassword, hashedPassword);
        }

        // 默认回退到BCrypt（处理可能没有前缀的旧格式）
        return verifyBCrypt(plainPassword, hashedPassword);
    }

    /**
     * 检查密码哈希是否需要升级
     *
     * @param hashedPassword 哈希后的密码
     * @return 如果需要升级返回true，否则返回false
     */
    public boolean needsUpgrade(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }

        // 检查BCrypt工作因子
        if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
            // 提取工作因子
            try {
                int rounds = Integer.parseInt(hashedPassword.substring(4, 6));
                return rounds < BCRYPT_ROUNDS;
            } catch (NumberFormatException e) {
                return true;
            }
        }

        // 检查PBKDF2迭代次数
        if (hashedPassword.startsWith("pbkdf2:")) {
            String[] parts = hashedPassword.split(":");
            if (parts.length >= 3) {
                try {
                    int iterations = Integer.parseInt(parts[1]);
                    return iterations < PBKDF2_ITERATIONS;
                } catch (NumberFormatException e) {
                    return true;
                }
            }
        }

        // 如果不是当前默认算法或格式无法识别，建议升级
        return !hashedPassword.startsWith(getAlgorithmPrefix(defaultAlgorithm));
    }

    // ===== 私有实现方法 =====

    /**
     * 使用BCrypt哈希密码
     */
    private static String hashWithBCrypt(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * 使用PBKDF2哈希密码
     */
    private static String hashWithPBKDF2(String plainPassword) {
        try {
            byte[] salt = new byte[SALT_SIZE];
            RANDOM.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(
                    plainPassword.toCharArray(),
                    salt,
                    PBKDF2_ITERATIONS,
                    PBKDF2_HASH_SIZE * 8
            );

            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return String.format("pbkdf2:%d:%s:%s", PBKDF2_ITERATIONS, saltBase64, hashBase64);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("密码哈希失败", e);
        }
    }

    /**
     * 使用Argon2id哈希密码（示例实现，实际需要使用Argon2库）
     */
    private static String hashWithArgon2id(String plainPassword) {
        // 实际项目中应使用如Bouncy Castle或libsodium-jni等库
        // 以下为模拟实现
        byte[] salt = new byte[SALT_SIZE];
        RANDOM.nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        // 模拟Argon2参数：内存=65536KB, 迭代=3, 并行度=4
        return String.format("argon2id:65536:3:4:%s:hash-placeholder", saltBase64);
    }

    /**
     * 验证BCrypt密码
     */
    private static boolean verifyBCrypt(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * 验证PBKDF2密码
     */
    private static boolean verifyPBKDF2(String plainPassword, String hashedPassword) {
        try {
            String[] parts = hashedPassword.split(":");
            if (parts.length != 4) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] hash = Base64.getDecoder().decode(parts[3]);

            PBEKeySpec spec = new PBEKeySpec(
                    plainPassword.toCharArray(),
                    salt,
                    iterations,
                    hash.length * 8
            );

            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            // 比较哈希值
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }

            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证Argon2id密码（示例实现，实际需要使用Argon2库）
     */
    private static boolean verifyArgon2id(String plainPassword, String hashedPassword) {
        // 实际项目中应使用如Bouncy Castle或libsodium-jni等库进行验证
        // 此处为占位实现
        return false;
    }

    /**
     * 获取算法前缀
     */
    private String getAlgorithmPrefix(HashAlgorithm algorithm) {
        switch (algorithm) {
            case BCRYPT:
                return "$2a$";
            case PBKDF2:
                return "pbkdf2:";
            case ARGON2ID:
                return "argon2id:";
            default:
                return "$2a$";
        }
    }
}