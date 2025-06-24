package com.PFM.CD.utils.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密解密工具类
 *
 * @author rywc2005
 * @since 2025-06-24
 */
public class EncryptionUtils {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    /**
     * 私有构造函数，防止实例化
     */
    private EncryptionUtils() {
        throw new IllegalStateException("工具类不应被实例化");
    }

    /**
     * 生成AES密钥
     *
     * @return Base64编码的密钥
     * @throws NoSuchAlgorithmException 如果没有找到AES算法
     */
    public static String generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 使用AES-GCM算法加密数据
     *
     * @param plainText 明文
     * @param base64Key Base64编码的密钥
     * @return Base64编码的加密数据（包含IV和密文）
     * @throws Exception 如果加密过程中发生错误
     */
    public static String encryptAES(String plainText, String base64Key) throws Exception {
        // 解码密钥
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        Key key = new SecretKeySpec(keyBytes, "AES");

        // 初始化Cipher
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);

        // 生成随机IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // 加密
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 将IV和密文组合
        byte[] result = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, result, iv.length, encryptedBytes.length);

        // Base64编码
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 使用AES-GCM算法解密数据
     *
     * @param encryptedBase64 Base64编码的加密数据（包含IV和密文）
     * @param base64Key Base64编码的密钥
     * @return 解密后的明文
     * @throws Exception 如果解密过程中发生错误
     */
    public static String decryptAES(String encryptedBase64, String base64Key) throws Exception {
        // 解码密钥
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        Key key = new SecretKeySpec(keyBytes, "AES");

        // 解码加密数据
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        // 提取IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedBytes, 0, iv, 0, iv.length);

        // 提取密文
        byte[] cipherText = new byte[encryptedBytes.length - iv.length];
        System.arraycopy(encryptedBytes, iv.length, cipherText, 0, cipherText.length);

        // 初始化Cipher
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        // 解密
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(cipherText);

        // 转换为字符串
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 简单的Base64编码
     *
     * @param input 输入字符串
     * @return Base64编码的字符串
     */
    public static String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 简单的Base64解码
     *
     * @param base64 Base64编码的字符串
     * @return 解码后的字符串
     */
    public static String decodeBase64(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * 使用异或操作进行简单加密
     *
     * @param input 输入字符串
     * @param key 密钥
     * @return 加密后的十六进制字符串
     */
    public static String xorEncrypt(String input, String key) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[inputBytes.length];

        for (int i = 0; i < inputBytes.length; i++) {
            result[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        // 转换为十六进制字符串
        StringBuilder hexString = new StringBuilder();
        for (byte b : result) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * 使用异或操作进行简单解密
     *
     * @param hexInput 十六进制字符串
     * @param key 密钥
     * @return 解密后的字符串
     */
    public static String xorDecrypt(String hexInput, String key) {
        // 将十六进制字符串转换为字节数组
        int len = hexInput.length();
        byte[] inputBytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            inputBytes[i / 2] = (byte) ((Character.digit(hexInput.charAt(i), 16) << 4)
                    + Character.digit(hexInput.charAt(i + 1), 16));
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[inputBytes.length];

        for (int i = 0; i < inputBytes.length; i++) {
            result[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(result, StandardCharsets.UTF_8);
    }
}