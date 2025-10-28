package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 角色权限关联实体类
 */
@Entity
@Table(name = "sys_role_permission",
    uniqueConstraints = @UniqueConstraint(name = "uk_role_permission", columnNames = {"roleId", "permissionId"}),
    indexes = {
        @Index(name = "idx_role_id", columnList = "roleId"),
        @Index(name = "idx_permission_id", columnList = "permissionId")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 角色ID
     */
    @Column(nullable = false)
    private Long roleId;
    
    /**
     * 权限ID
     */
    @Column(nullable = false)
    private Long permissionId;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 构造函数
     */
    public RolePermission(Long roleId, Long permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}
