package com.zoyo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * Token类型
     */
    @Builder.Default
    private String tokenType = "Bearer";
    
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    
    /**
     * 用户信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String realName;
        private Integer status;
    }
}
