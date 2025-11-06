package com.zoyo.auth.annotation;

import com.zoyo.auth.common.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    
    /**
     * 模块名称
     */
    String module() default "";
    
    /**
     * 业务类型
     */
    OperationType businessType() default OperationType.OTHER;
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否保存请求参数
     */
    boolean isSaveRequestData() default true;
    
    /**
     * 是否保存响应结果
     */
    boolean isSaveResponseData() default true;
}
