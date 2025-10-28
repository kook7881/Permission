package com.zoyo.auth.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 失败
     */
    ERROR(500, "操作失败"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(1001, "用户名已存在"),
    
    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS(1002, "邮箱已存在"),
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1003, "用户不存在"),
    
    /**
     * 密码错误
     */
    PASSWORD_ERROR(1004, "密码错误"),
    
    /**
     * 账号已被禁用
     */
    ACCOUNT_DISABLED(1005, "账号已被禁用"),
    
    /**
     * 账号已被锁定
     */
    ACCOUNT_LOCKED(1006, "账号已被锁定"),
    
    /**
     * 账号已过期
     */
    ACCOUNT_EXPIRED(1007, "账号已过期"),
    
    /**
     * 密码已过期
     */
    CREDENTIALS_EXPIRED(1008, "密码已过期"),
    
    /**
     * Token无效
     */
    TOKEN_INVALID(2001, "Token无效"),
    
    /**
     * Token已过期
     */
    TOKEN_EXPIRED(2002, "Token已过期");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 消息
     */
    private final String message;
}

