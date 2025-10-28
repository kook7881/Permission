package com.zoyo.auth.controller;

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
        log.info("获取权限树");
        List<PermissionDTO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }
    
    /**
     * 获取权限列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<Permission>> getPermissionList() {
        log.info("获取权限列表");
        List<Permission> permissions = permissionService.getPermissionList();
        return Result.success(permissions);
    }
    
    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        log.info("获取权限详情: id={}", id);
        Permission permission = permissionService.getPermissionById(id);
        return Result.success(permission);
    }
    
    /**
     * 创建权限
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('system:permission:create')")
    public Result<Permission> createPermission(@RequestBody PermissionDTO permissionDTO) {
        log.info("创建权限: {}", permissionDTO);
        Permission permission = permissionService.createPermission(permissionDTO);
        return Result.success(permission);
    }
    
    /**
     * 更新权限
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('system:permission:update')")
    public Result<Permission> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {
        log.info("更新权限: id={}, permissionDTO={}", id, permissionDTO);
        Permission permission = permissionService.updatePermission(id, permissionDTO);
        return Result.success(permission);
    }
    
    /**
     * 删除权限
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public Result<Void> deletePermission(@PathVariable Long id) {
        log.info("删除权限: id={}", id);
        permissionService.deletePermission(id);
        return Result.success();
    }
    
    /**
     * 获取当前用户权限列表
     */
    @GetMapping("/user-permissions")
    public Result<List<String>> getUserPermissions(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("获取用户权限: userId={}", userId);
        List<String> permissions = permissionService.getUserPermissionCodes(userId);
        return Result.success(permissions);
    }
    
    /**
     * 获取当前用户菜单树
     */
    @GetMapping("/user-menus")
    public Result<List<PermissionDTO>> getUserMenus(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        log.info("获取用户菜单: userId={}", userId);
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
