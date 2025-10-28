package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 */
@Entity
@Table(name = "sys_user_role", 
    uniqueConstraints = @UniqueConstraint(name = "uk_user_role", columnNames = {"userId", "roleId"}),
    indexes = {
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_role_id", columnList = "roleId")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;
    
    /**
     * 角色ID
     */
    @Column(nullable = false)
    private Long roleId;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 构造函数
     */
    public UserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
