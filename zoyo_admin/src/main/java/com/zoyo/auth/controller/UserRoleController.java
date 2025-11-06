package com.zoyo.auth.controller;

import com.zoyo.auth.common.Result;
import com.zoyo.auth.entity.Role;
import com.zoyo.auth.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色管理控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserRoleController {
    
    private final IUserRoleService userRoleService;
    
    /**
     * 获取用户角色列表
     */
    @GetMapping("/{id}/roles")
    public Result<List<Role>> getUserRoles(@PathVariable Long id) {
        List<Role> roles = userRoleService.getUserRoles(id);
        return Result.success(roles);
    }
    
    /**
     * 为用户分配角色
     */
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userRoleService.assignRoles(id, roleIds);
        return Result.success();
    }
    
    /**
     * 移除用户角色
     */
    @DeleteMapping("/{id}/role/{roleId}")
    public Result<Void> removeRole(@PathVariable Long id, @PathVariable Long roleId) {
        userRoleService.removeRole(id, roleId);
        return Result.success();
    }
}
