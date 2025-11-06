package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Entity
@Table(name = "sys_operation_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_operation_time", columnList = "operation_time"),
    @Index(name = "idx_business_type", columnList = "business_type"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 操作模块
     */
    @Column(length = 50)
    private String module;
    
    /**
     * 业务类型：0-其它 1-新增 2-修改 3-删除 4-查询 5-导出 6-导入 7-授权
     */
    @Column(nullable = false)
    private Integer businessType;
    
    /**
     * 业务类型数组（用于批量操作）
     */
    @Column(length = 200)
    private String businessTypes;
    
    /**
     * 操作描述
     */
    @Column(length = 200)
    private String description;
    
    /**
     * 请求方法
     */
    @Column(length = 200)
    private String method;
    
    /**
     * 请求方式：GET/POST/PUT/DELETE等
     */
    @Column(length = 10)
    private String requestMethod;
    
    /**
     * 操作人员ID（未登录时为null）
     */
    @Column(nullable = true)
    private Long userId;
    
    /**
     * 操作人员用户名（未登录时为null）
     */
    @Column(nullable = true, length = 50)
    private String username;
    
    /**
     * 操作人员姓名
     */
    @Column(length = 50)
    private String realName;
    
    /**
     * 请求URL
     */
    @Column(length = 500)
    private String requestUrl;
    
    /**
     * 请求IP
     */
    @Column(length = 50)
    private String requestIp;
    
    /**
     * 请求地点
     */
    @Column(length = 100)
    private String requestLocation;
    
    /**
     * 请求参数
     */
    @Column(columnDefinition = "TEXT")
    private String requestParam;
    
    /**
     * 返回结果
     */
    @Column(columnDefinition = "TEXT")
    private String responseResult;
    
    /**
     * 操作状态：0-失败 1-成功
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer status = 1;
    
    /**
     * 错误消息
     */
    @Column(columnDefinition = "TEXT")
    private String errorMsg;
    
    /**
     * 操作时间
     */
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime operationTime;
    
    /**
     * 执行时长（毫秒）
     */
    @Column
    private Long executionTime;
    
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
}
