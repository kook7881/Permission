package com.zoyo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    
    private Long id;
    private String roleName;
    private String roleCode;
    private Integer roleSort;
    private Integer dataScope;
    private Integer status;
    private Boolean isSystem;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    
    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;
    
    // 为了兼容前端，添加别名 getter
    public String getName() {
        return roleName;
    }
    
    public String getCode() {
        return roleCode;
    }
    
    public String getDescription() {
        return remark;
    }
}
