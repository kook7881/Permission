package com.zoyo.auth.service;

import com.zoyo.auth.entity.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 登录日志服务接口
 */
public interface ILoginLogService {
    
    /**
     * 分页查询登录日志
     */
    Page<LoginLog> queryLoginLogs(String username, Integer status, Pageable pageable);
    
    /**
     * 根据ID获取登录日志
     */
    LoginLog getLoginLogById(Long id);
    
    /**
     * 删除登录日志
     */
    void deleteLoginLog(Long id);
    
    /**
     * 批量删除登录日志
     */
    void batchDeleteLoginLogs(java.util.List<Long> ids);
    
    /**
     * 清空登录日志
     */
    void clearLoginLogs();
}
