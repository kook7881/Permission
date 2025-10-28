package com.zoyo.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 部门实体类
 */
@Entity
@Table(name = "sys_department", indexes = {
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 部门名称
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * 父部门ID（0表示顶级部门）
     */
    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L;
    
    /**
     * 部门编码
     */
    @Column(length = 50)
    private String code;
    
    /**
     * 显示顺序
     */
    @Column(nullable = false)
    private Integer sort = 0;
    
    /**
     * 负责人ID
     */
    @Column(name = "leader_id")
    private Long leaderId;
    
    /**
     * 联系电话
     */
    @Column(length = 20)
    private String phone;
    
    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;
    
    /**
     * 部门状态：0-禁用，1-正常
     */
    @Column(nullable = false)
    private Integer status = 1;
    
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
     * 备注
     */
    @Column(length = 500)
    private String remark;
}
