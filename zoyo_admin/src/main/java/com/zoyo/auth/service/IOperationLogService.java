package com.zoyo.auth.service;

import com.zoyo.auth.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务接口
 */
public interface IOperationLogService {
    
    /**
     * 保存操作日志
     */
    void saveOperationLog(OperationLog operationLog);
    
    /**
     * 分页查询操作日志
     */
    Page<OperationLog> queryOperationLogs(
            String username,
            String module,
            Integer businessType,
            Integer status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );
    
    /**
     * 根据ID获取操作日志
     */
    OperationLog getOperationLogById(Long id);
    
    /**
     * 根据用户ID查询操作日志
     */
    List<OperationLog> getOperationLogsByUserId(Long userId);
    
    /**
     * 根据用户名查询操作日志
     */
    List<OperationLog> getOperationLogsByUsername(String username);
    
    /**
     * 删除操作日志
     */
    void deleteOperationLog(Long id);
    
    /**
     * 批量删除操作日志
     */
    void batchDeleteOperationLogs(List<Long> ids);
    
    /**
     * 清空操作日志
     */
    void clearOperationLogs();
    
    /**
     * 清理指定时间之前的日志
     */
    void cleanOperationLogs(LocalDateTime beforeTime);
    
    /**
     * 统计用户操作次数
     */
    Long countUserOperations(Long userId);
    
    /**
     * 统计模块操作次数
     */
    Long countModuleOperations(String module);
}
