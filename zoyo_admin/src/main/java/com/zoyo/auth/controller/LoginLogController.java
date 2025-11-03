package com.zoyo.auth.controller;

import com.zoyo.auth.common.Result;
import com.zoyo.auth.entity.LoginLog;
import com.zoyo.auth.service.ILoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志控制器
 */
@Slf4j
@RestController
@RequestMapping("/login-log")
@RequiredArgsConstructor
public class LoginLogController {
    
    private final ILoginLogService loginLogService;
    
    /**
     * 分页查询登录日志
     */
    @GetMapping("/list")
    // @PreAuthorize("hasAuthority('system:loginlog:query')") // 暂时移除权限检查
    public Result<Page<LoginLog>> queryLoginLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "loginTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        log.info("分页查询登录日志: username={}, status={}, page={}, size={}", username, status, page, size);
        
        Sort sort = sortOrder.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LoginLog> result = loginLogService.queryLoginLogs(username, status, pageable);
        return Result.success(result);
    }
    
    /**
     * 获取登录日志详情
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('system:loginlog:query')") // 暂时移除权限检查
    public Result<LoginLog> getLoginLogById(@PathVariable Long id) {
        log.info("获取登录日志详情: {}", id);
        LoginLog loginLog = loginLogService.getLoginLogById(id);
        return Result.success(loginLog);
    }
    
    /**
     * 删除登录日志
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('system:loginlog:delete')") // 暂时移除权限检查
    public Result<Void> deleteLoginLog(@PathVariable Long id) {
        log.info("删除登录日志: {}", id);
        loginLogService.deleteLoginLog(id);
        return Result.success();
    }
    
    /**
     * 批量删除登录日志
     */
    @DeleteMapping("/batch")
    // @PreAuthorize("hasAuthority('system:loginlog:delete')") // 暂时移除权限检查
    public Result<Void> batchDeleteLoginLogs(@RequestBody List<Long> ids) {
        log.info("批量删除登录日志: {}", ids);
        loginLogService.batchDeleteLoginLogs(ids);
        return Result.success();
    }
    
    /**
     * 清空登录日志
     */
    @DeleteMapping("/clear")
    // @PreAuthorize("hasAuthority('system:loginlog:delete')") // 暂时移除权限检查
    public Result<Void> clearLoginLogs() {
        log.warn("清空所有登录日志");
        loginLogService.clearLoginLogs();
        return Result.success();
    }
}
