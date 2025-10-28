package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 登录日志实体类
 */
@Entity
@Table(name = "sys_login_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_login_time", columnList = "login_time"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID（登录失败时可能为null）
     */
    @Column(nullable = true)
    private Long userId;
    
    /**
     * 用户名
     */
    @Column(nullable = false, length = 50)
    private String username;
    
    /**
     * 登录时间
     */
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime loginTime;
    
    /**
     * 登录IP
     */
    @Column(length = 50)
    private String loginIp;
    
    /**
     * 登录地点
     */
    @Column(length = 100)
    private String loginLocation;
    
    /**
     * 浏览器
     */
    @Column(length = 50)
    private String browser;
    
    /**
     * 操作系统
     */
    @Column(length = 50)
    private String os;
    
    /**
     * 设备类型：PC/Mobile/Tablet
     */
    @Column(length = 20)
    private String deviceType;
    
    /**
     * 登录状态：0-失败，1-成功
     */
    @Column(nullable = false)
    private Integer status = 1;
    
    /**
     * 提示消息
     */
    @Column(length = 255)
    private String message;
}
