package com.zoyo.auth.controller;

import com.zoyo.auth.annotation.Log;
import com.zoyo.auth.common.OperationType;
import com.zoyo.auth.common.Result;
import com.zoyo.auth.entity.OperationLog;
import com.zoyo.auth.service.IOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志控制器
 */
@Slf4j
@RestController
@RequestMapping("/operation-log")
@RequiredArgsConstructor
public class OperationLogController {
    
    private final IOperationLogService operationLogService;
    
    /**
     * 分页查询操作日志
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:operlog:query')")
    public Result<Page<OperationLog>> queryOperationLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer businessType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "operationTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        log.info("分页查询操作日志: username={}, module={}, businessType={}, status={}, page={}, size={}", 
                username, module, businessType, status, page, size);
        
        Sort sort = sortOrder.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OperationLog> result = operationLogService.queryOperationLogs(
                username, module, businessType, status, startTime, endTime, pageable);
        return Result.success(result);
    }
    
    /**
     * 获取操作日志详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:operlog:query')")
    public Result<OperationLog> getOperationLogById(@PathVariable Long id) {
        log.info("获取操作日志详情: {}", id);
        OperationLog operationLog = operationLogService.getOperationLogById(id);
        return Result.success(operationLog);
    }
    
    /**
     * 根据用户ID查询操作日志
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('system:operlog:query')")
    public Result<List<OperationLog>> getOperationLogsByUserId(@PathVariable Long userId) {
        log.info("根据用户ID查询操作日志: {}", userId);
        List<OperationLog> logs = operationLogService.getOperationLogsByUserId(userId);
        return Result.success(logs);
    }
    
    /**
     * 删除操作日志
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @Log(module = "操作日志", businessType = OperationType.DELETE, description = "删除操作日志")
    public Result<Void> deleteOperationLog(@PathVariable Long id) {
        log.info("删除操作日志: {}", id);
        operationLogService.deleteOperationLog(id);
        return Result.success();
    }
    
    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @Log(module = "操作日志", businessType = OperationType.DELETE, description = "批量删除操作日志")
    public Result<Void> batchDeleteOperationLogs(@RequestBody List<Long> ids) {
        log.info("批量删除操作日志: {}", ids);
        operationLogService.batchDeleteOperationLogs(ids);
        return Result.success();
    }
    
    /**
     * 清空操作日志
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @Log(module = "操作日志", businessType = OperationType.DELETE, description = "清空所有操作日志")
    public Result<Void> clearOperationLogs() {
        log.warn("清空所有操作日志");
        operationLogService.clearOperationLogs();
        return Result.success();
    }
    
    /**
     * 清理指定时间之前的日志
     */
    @DeleteMapping("/clean")
    @PreAuthorize("hasAuthority('system:operlog:delete')")
    @Log(module = "操作日志", businessType = OperationType.DELETE, description = "清理历史操作日志")
    public Result<Void> cleanOperationLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime) {
        log.info("清理 {} 之前的操作日志", beforeTime);
        operationLogService.cleanOperationLogs(beforeTime);
        return Result.success();
    }
    
    /**
     * 统计用户操作次数
     */
    @GetMapping("/count/user/{userId}")
    @PreAuthorize("hasAuthority('system:operlog:query')")
    public Result<Long> countUserOperations(@PathVariable Long userId) {
        log.info("统计用户操作次数: {}", userId);
        Long count = operationLogService.countUserOperations(userId);
        return Result.success(count);
    }
}
