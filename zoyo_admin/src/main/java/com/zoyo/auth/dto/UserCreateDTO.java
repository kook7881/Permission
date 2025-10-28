package com.zoyo.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户创建DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 真实姓名
     */
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;
    
    /**
     * 部门ID
     */
    private Long deptId;
    
    /**
     * 职位
     */
    @Size(max = 50, message = "职位长度不能超过50个字符")
    private String position;
    
    /**
     * 头像URL
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    @Min(value = 0, message = "性别值必须在0-2之间")
    @Max(value = 2, message = "性别值必须在0-2之间")
    private Integer gender;
    
    /**
     * 用户状态：0-禁用，1-正常
     */
    @NotNull(message = "用户状态不能为空")
    @Min(value = 0, message = "状态值必须为0或1")
    @Max(value = 1, message = "状态值必须为0或1")
    private Integer status;
    
    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
    
    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
