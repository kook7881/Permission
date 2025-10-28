package com.zoyo.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类
 * 处理前端加密密码的验证
 */
@Component
public class PasswordUtil {
    
    private static final String SECRET_KEY = "zoyo-auth-system-2024";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * MD5 加密
     * @param text 明文
     * @return MD5 哈希值
     */
    public String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(text.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 加密失败", e);
        }
    }
    
    /**
     * 验证前端加密的密码
     * 前端发送的是 MD5(password + salt)
     * 后端需要将明文密码也进行相同的加密后再比对
     * 
     * @param rawPassword 明文密码
     * @param frontendEncryptedPassword 前端加密后的密码
     * @return 是否匹配
     */
    public boolean matchesFrontendPassword(String rawPassword, String frontendEncryptedPassword) {
        String expectedEncrypted = md5(rawPassword + SECRET_KEY);
        return expectedEncrypted.equals(frontendEncryptedPassword);
    }
    
    /**
     * BCrypt 加密（用于数据库存储）
     * @param password 密码（可以是明文或前端加密后的）
     * @return BCrypt 哈希值
     */
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
    
    /**
     * 验证 BCrypt 密码
     * @param rawPassword 原始密码
     * @param encodedPassword BCrypt 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
