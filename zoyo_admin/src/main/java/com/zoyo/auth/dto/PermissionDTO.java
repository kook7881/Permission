package com.zoyo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    
    private Long id;
    private Long parentId;
    private String permissionName;
    private String permissionCode;
    private Integer permissionType;
    private String routePath;
    private String componentPath;
    private String icon;
    private Integer sortOrder;
    private Boolean visible;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    /**
     * 子权限列表
     */
    private List<PermissionDTO> children;
}
