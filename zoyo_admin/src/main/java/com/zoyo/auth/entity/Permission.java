package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 */
@Entity
@Table(name = "sys_permission", indexes = {
    @Index(name = "idx_parent_id", columnList = "parentId"),
    @Index(name = "idx_permission_code", columnList = "permissionCode"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_sort_order", columnList = "sortOrder")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    /**
     * 权限ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 父权限ID
     */
    @Column(nullable = false)
    private Long parentId = 0L;
    
    /**
     * 权限名称
     */
    @Column(nullable = false, length = 50)
    private String permissionName;
    
    /**
     * 权限标识
     */
    @Column(nullable = false, unique = true, length = 100)
    private String permissionCode;
    
    /**
     * 权限类型：1-菜单 2-按钮
     */
    @Column(nullable = false)
    private Integer permissionType;
    
    /**
     * 路由路径
     */
    @Column(length = 200)
    private String routePath;
    
    /**
     * 组件路径
     */
    @Column(length = 200)
    private String componentPath;
    
    /**
     * 图标
     */
    @Column(length = 50)
    private String icon;
    
    /**
     * 排序
     */
    @Column(nullable = false)
    private Integer sortOrder = 0;
    
    /**
     * 是否可见
     */
    @Column(nullable = false)
    private Boolean visible = true;
    
    /**
     * 状态：0-禁用 1-启用
     */
    @Column(nullable = false)
    private Integer status = 1;
    
    /**
     * 备注
     */
    @Column(length = 500)
    private String remark;
    
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
     * 子权限列表（不持久化到数据库）
     */
    @Transient
    private List<Permission> children;
}
