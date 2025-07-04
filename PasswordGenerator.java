import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encodedPassword = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("加密后密码: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(password, encodedPassword);
        System.out.println("密码验证结果: " + matches);
        
        // 验证我们使用的固定密码
        String fixedPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        boolean fixedMatches = encoder.matches(password, fixedPassword);
        System.out.println("固定密码验证结果: " + fixedMatches);
    }
} 