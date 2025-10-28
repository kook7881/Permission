package com.zoyo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改密码请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {
    
    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    
    /**
     * 新密码（前端已加密，32位MD5哈希值）
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 64, message = "新密码长度必须在6-64个字符之间")
    private String newPassword;
}
