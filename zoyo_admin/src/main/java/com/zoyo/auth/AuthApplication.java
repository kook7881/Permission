package com.zoyo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证系统主启动类
 * 
 * @author zoyo
 * @since 1.0.0
 */
@SpringBootApplication
public class AuthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("""
            
            ========================================
            认证系统启动成功！
            访问地址: http://localhost:8080/api
            ========================================
            """);
    }
}
