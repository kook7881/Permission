package com.zoyo.auth.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoyo.auth.annotation.Log;
import com.zoyo.auth.entity.OperationLog;
import com.zoyo.auth.entity.User;
import com.zoyo.auth.service.IOperationLogService;
import com.zoyo.auth.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    
    private final IOperationLogService operationLogService;
    private final ObjectMapper objectMapper;
    
    /**
     * 环绕通知，记录操作日志
     */
    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();
        
        // 构建操作日志对象
        OperationLog operationLog = OperationLog.builder()
                .module(logAnnotation.module())
                .businessType(logAnnotation.businessType().getCode())
                .description(logAnnotation.description())
                .operationTime(LocalDateTime.now())
                .build();
        
        // 设置请求信息
        if (request != null) {
            operationLog.setRequestUrl(request.getRequestURI());
            operationLog.setRequestMethod(request.getMethod());
            operationLog.setRequestIp(IpUtil.getIpAddress(request));
            operationLog.setRequestLocation(IpUtil.getLocationByIp(operationLog.getRequestIp()));
            
            // 获取浏览器和操作系统信息
            String userAgent = request.getHeader("User-Agent");
            operationLog.setBrowser(parseBrowser(userAgent));
            operationLog.setOs(parseOS(userAgent));
            operationLog.setDeviceType(parseDeviceType(userAgent));
        }
        
        // 设置方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        operationLog.setMethod(className + "." + methodName);
        
        // 设置操作人信息
        setUserInfo(operationLog);
        
        // 保存请求参数
        if (logAnnotation.isSaveRequestData()) {
            String params = getRequestParams(joinPoint);
            operationLog.setRequestParam(params);
        }
        
        // 执行目标方法
        Object result = null;
        try {
            result = joinPoint.proceed();
            operationLog.setStatus(1); // 成功
            
            // 保存响应结果
            if (logAnnotation.isSaveResponseData() && result != null) {
                String response = objectMapper.writeValueAsString(result);
                // 限制响应结果长度
                if (response.length() > 2000) {
                    response = response.substring(0, 2000) + "...";
                }
                operationLog.setResponseResult(response);
            }
        } catch (Exception e) {
            operationLog.setStatus(0); // 失败
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算执行时长
            long executionTime = System.currentTimeMillis() - startTime;
            operationLog.setExecutionTime(executionTime);
            
            // 异步保存操作日志
            saveLogAsync(operationLog);
        }
        
        return result;
    }
    
    /**
     * 获取HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    /**
     * 设置用户信息
     */
    private void setUserInfo(OperationLog operationLog) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !(authentication.getPrincipal() instanceof String)) {
                
                if (authentication.getPrincipal() instanceof User) {
                    User user = (User) authentication.getPrincipal();
                    operationLog.setUserId(user.getId());
                    operationLog.setUsername(user.getUsername());
                    operationLog.setRealName(user.getRealName());
                } else {
                    // 其他类型的 Principal
                    operationLog.setUsername(authentication.getName());
                }
            } else {
                // 未登录或匿名用户
                operationLog.setUsername("匿名用户");
            }
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            // 设置默认值，避免插入失败
            operationLog.setUsername("系统");
        }
    }
    
    /**
     * 获取请求参数
     */
    private String getRequestParams(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return null;
            }
            
            // 过滤掉不需要记录的参数类型
            Map<String, Object> params = new HashMap<>();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            
            for (int i = 0; i < args.length && i < paramNames.length; i++) {
                Object arg = args[i];
                // 过滤掉HttpServletRequest等对象
                if (arg != null && 
                    !arg.getClass().getName().startsWith("org.springframework") &&
                    !arg.getClass().getName().startsWith("jakarta.servlet")) {
                    params.put(paramNames[i], arg);
                }
            }
            
            String paramsJson = objectMapper.writeValueAsString(params);
            // 限制参数长度，避免过长
            if (paramsJson.length() > 2000) {
                paramsJson = paramsJson.substring(0, 2000) + "...";
            }
            return paramsJson;
        } catch (Exception e) {
            log.error("获取请求参数失败", e);
            return null;
        }
    }
    
    /**
     * 异步保存日志
     */
    private void saveLogAsync(OperationLog operationLog) {
        try {
            operationLogService.saveOperationLog(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
    
    /**
     * 解析浏览器
     */
    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Edge")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Opera")) return "Opera";
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) return "IE";
        
        return "Other";
    }
    
    /**
     * 解析操作系统
     */
    private String parseOS(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Windows NT 10.0")) return "Windows 10";
        if (userAgent.contains("Windows NT 6.3")) return "Windows 8.1";
        if (userAgent.contains("Windows NT 6.2")) return "Windows 8";
        if (userAgent.contains("Windows NT 6.1")) return "Windows 7";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac OS X")) return "Mac OS X";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        
        return "Other";
    }
    
    /**
     * 解析设备类型
     */
    private String parseDeviceType(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Mobile") || userAgent.contains("Android") || 
            userAgent.contains("iPhone")) {
            return "Mobile";
        }
        if (userAgent.contains("Tablet") || userAgent.contains("iPad")) {
            return "Tablet";
        }
        
        return "PC";
    }
}
