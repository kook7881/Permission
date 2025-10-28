package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Entity
@Table(name = "sys_role", indexes = {
    @Index(name = "idx_role_code", columnList = "roleCode"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_role_sort", columnList = "roleSort")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    /**
     * 角色ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 角色名称
     */
    @Column(nullable = false, length = 50)
    private String roleName;
    
    /**
     * 角色编码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String roleCode;
    
    /**
     * 显示顺序
     */
    @Column(nullable = false)
    private Integer roleSort = 0;
    
    /**
     * 数据范围：1-全部 2-本部门 3-本人
     */
    @Column(nullable = false)
    private Integer dataScope = 1;
    
    /**
     * 状态：0-禁用 1-启用
     */
    @Column(nullable = false)
    private Integer status = 1;
    
    /**
     * 是否系统内置
     */
    @Column(nullable = false)
    private Boolean isSystem = false;
    
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
     * 创建人
     */
    @Column(length = 50)
    private String createBy;
    
    /**
     * 更新人
     */
    @Column(length = 50)
    private String updateBy;
}
