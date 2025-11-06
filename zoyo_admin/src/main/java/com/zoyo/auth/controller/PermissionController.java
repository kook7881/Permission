package com.zoyo.auth.controller;

import com.zoyo.auth.annotation.Log;
import com.zoyo.auth.common.OperationType;
import com.zoyo.auth.common.Result;
import com.zoyo.auth.dto.PermissionDTO;
import com.zoyo.auth.entity.Permission;
import com.zoyo.auth.service.IPermissionService;
import com.zoyo.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {
    
    private final IPermissionService permissionService;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取权限树（用于角色分配权限，只需要角色查询权限即可）
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAnyAuthority('system:permission:query', 'system:role:query')")
    public Result<List<PermissionDTO>> getPermissionTree() {
        List<PermissionDTO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }
    
    /**
     * 获取权限列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<Permission>> getPermissionList() {
        List<Permission> permissions = permissionService.getPermissionList();
        return Result.success(permissions);
    }
    
    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        return Result.success(permission);
    }
    
    /**
     * 创建权限
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('system:permission:create')")
    @Log(module = "权限管理", businessType = OperationType.INSERT, description = "创建权限")
    public Result<Permission> createPermission(@RequestBody PermissionDTO permissionDTO) {
        Permission permission = permissionService.createPermission(permissionDTO);
        return Result.success(permission);
    }
    
    /**
     * 更新权限
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('system:permission:update')")
    @Log(module = "权限管理", businessType = OperationType.UPDATE, description = "更新权限信息")
    public Result<Permission> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {
        Permission permission = permissionService.updatePermission(id, permissionDTO);
        return Result.success(permission);
    }
    
    /**
     * 删除权限
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    @Log(module = "权限管理", businessType = OperationType.DELETE, description = "删除权限")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }
    
    /**
     * 获取当前用户权限列表
     */
    @GetMapping("/user-permissions")
    public Result<List<String>> getUserPermissions(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<String> permissions = permissionService.getUserPermissionCodes(userId);
        return Result.success(permissions);
    }
    
    /**
     * 获取当前用户菜单树
     */
    @GetMapping("/user-menus")
    public Result<List<PermissionDTO>> getUserMenus(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<PermissionDTO> menus = permissionService.getUserMenuTree(userId);
        return Result.success(menus);
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未找到有效的Token");
    }
}
