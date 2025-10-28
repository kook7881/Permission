package com.zoyo.auth.exception;

import com.zoyo.auth.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑中的可预期异常，如用户不存在、密码错误等
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 错误消息
     */
    private final String message;
    
    /**
     * 构造函数
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    /**
     * 构造函数（使用ResultCode）
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
    
    /**
     * 构造函数（自定义消息）
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR.getCode();
        this.message = message;
    }
}
