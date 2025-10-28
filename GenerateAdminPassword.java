import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.security.MessageDigest;

/**
 * 生成管理员密码
 * 
 * 密码加密流程：
 * 1. 前端: MD5(password + SECRET_KEY)
 * 2. 后端: BCrypt(前端加密后的密码)
 */
public class GenerateAdminPassword {
    
    private static final String SECRET_KEY = "zoyo-auth-system-2024";
    
    public static void main(String[] args) {
        String rawPassword = "admin123";
        
        // 步骤1: 模拟前端 MD5 加密
        String frontendEncrypted = md5(rawPassword + SECRET_KEY);
        System.out.println("步骤1 - 前端MD5加密:");
        System.out.println("  明文密码: " + rawPassword);
        System.out.println("  加密密钥: " + SECRET_KEY);
        System.out.println("  MD5结果: " + frontendEncrypted);
        System.out.println();
        
        // 步骤2: 后端 BCrypt 加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String bcryptEncrypted = encoder.encode(frontendEncrypted);
        System.out.println("步骤2 - 后端BCrypt加密:");
        System.out.println("  输入: " + frontendEncrypted);
        System.out.println("  BCrypt结果: " + bcryptEncrypted);
        System.out.println();
        
        // 验证
        boolean matches = encoder.matches(frontendEncrypted, bcryptEncrypted);
        System.out.println("验证结果: " + (matches ? "✓ 密码匹配" : "✗ 密码不匹配"));
        System.out.println();
        
        // SQL 语句
        System.out.println("=".repeat(80));
        System.out.println("用于 SQL 的密码值:");
        System.out.println("=".repeat(80));
        System.out.println(bcryptEncrypted);
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("完整 SQL 语句:");
        System.out.println("UPDATE sys_user SET password = '" + bcryptEncrypted + "' WHERE username = 'admin';");
    }
    
    /**
     * MD5 加密
     */
    private static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
