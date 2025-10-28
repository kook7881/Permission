package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "sys_user", indexes = {
    @Index(name = "idx_username", columnList = "username", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_phone", columnList = "phone", unique = true),
    @Index(name = "idx_dept_id", columnList = "dept_id"),
    @Index(name = "idx_deleted", columnList = "deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 密码（加密后）
     */
    @Column(nullable = false, length = 100)
    private String password;
    
    /**
     * 邮箱
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    /**
     * 手机号
     */
    @Column(length = 20)
    private String phone;
    
    /**
     * 真实姓名
     */
    @Column(length = 50)
    private String realName;
    
    /**
     * 部门ID
     */
    @Column(name = "dept_id")
    private Long deptId;
    
    /**
     * 职位/岗位
     */
    @Column(length = 50)
    private String position;
    
    /**
     * 头像URL
     */
    @Column(length = 255)
    private String avatar;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    @Column
    private Integer gender = 0;
    
    /**
     * 用户状态：0-禁用，1-正常
     */
    @Column(nullable = false)
    private Integer status = 1;
    
    /**
     * 账号是否过期：0-过期，1-未过期
     */
    @Column(nullable = false)
    private Boolean accountNonExpired = true;
    
    /**
     * 账号是否锁定：0-锁定，1-未锁定
     */
    @Column(nullable = false)
    private Boolean accountNonLocked = true;
    
    /**
     * 密码是否过期：0-过期，1-未过期
     */
    @Column(nullable = false)
    private Boolean credentialsNonExpired = true;
    
    /**
     * 账号是否启用：0-禁用，1-启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;
    
    /**
     * 最后登录时间
     */
    @Column
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录IP
     */
    @Column(length = 50)
    private String lastLoginIp;
    
    /**
     * 最后退出时间
     */
    @Column
    private LocalDateTime lastLogoutTime;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    private Long createBy;
    
    /**
     * 更新人ID
     */
    @Column(name = "update_by")
    private Long updateBy;
    
    /**
     * 是否删除：0-未删除，1-已删除
     */
    @Column(nullable = false)
    private Boolean deleted = false;
    
    /**
     * 删除时间
     */
    @Column(name = "delete_time")
    private LocalDateTime deleteTime;
    
    /**
     * 备注
     */
    @Column(length = 500)
    private String remark;
}

