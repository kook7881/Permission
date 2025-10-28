package com.zoyo.auth.service;

import com.zoyo.auth.dto.LoginRequest;
import com.zoyo.auth.dto.LoginResponse;
import com.zoyo.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 */
public interface IAuthService {
    
    /**
     * 用户注册
     * @param request 注册请求
     */
    void register(RegisterRequest request);
    
    /**
     * 用户登录
     * @param request 登录请求
     * @param httpRequest HTTP请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);
    
    /**
     * 用户退出登录
     * @param httpRequest HTTP请求
     */
    void logout(HttpServletRequest httpRequest);
}
