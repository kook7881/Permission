package com.zoyo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDTO {
    
    /**
     * 用户名（模糊查询）
     */
    private String username;
    
    /**
     * 邮箱（模糊查询）
     */
    private String email;
    
    /**
     * 手机号（模糊查询）
     */
    private String phone;
    
    /**
     * 真实姓名（模糊查询）
     */
    private String realName;
    
    /**
     * 用户状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 部门ID
     */
    private Long deptId;
    
    /**
     * 页码（从0开始）
     */
    @Builder.Default
    private Integer page = 0;
    
    /**
     * 每页大小
     */
    @Builder.Default
    private Integer size = 10;
    
    /**
     * 排序字段
     */
    @Builder.Default
    private String sortBy = "createTime";
    
    /**
     * 排序方向：asc-升序，desc-降序
     */
    @Builder.Default
    private String sortDirection = "desc";
}
