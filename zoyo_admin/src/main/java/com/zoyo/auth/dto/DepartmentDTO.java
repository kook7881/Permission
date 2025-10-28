package com.zoyo.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    
    /**
     * 部门ID
     */
    private Long id;
    
    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称长度不能超过100个字符")
    private String name;
    
    /**
     * 父部门ID（0表示顶级部门）
     */
    @NotNull(message = "父部门ID不能为空")
    private Long parentId;
    
    /**
     * 部门编码
     */
    @Size(max = 50, message = "部门编码长度不能超过50个字符")
    private String code;
    
    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    @Min(value = 0, message = "显示顺序不能为负数")
    private Integer sort;
    
    /**
     * 负责人ID
     */
    private Long leaderId;
    
    /**
     * 负责人姓名
     */
    private String leaderName;
    
    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String phone;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    /**
     * 部门状态：0-禁用，1-正常
     */
    @NotNull(message = "部门状态不能为空")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
