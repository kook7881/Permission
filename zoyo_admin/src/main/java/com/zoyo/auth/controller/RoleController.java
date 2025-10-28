package com.zoyo.auth.controller;

import com.zoyo.auth.common.Result;
import com.zoyo.auth.dto.RoleDTO;
import com.zoyo.auth.entity.Role;
import com.zoyo.auth.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    
    private final IRoleService roleService;
    
    /**
     * 获取角色列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Page<Role>> getRoleList(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("获取角色列表: roleName={}, status={}, page={}, size={}", roleName, status, page, size);
        Page<Role> rolePage = roleService.getRoleList(roleName, status, page, size);
        return Result.success(rolePage);
    }
    
    /**
     * 获取所有角色（不分页）
     * 注意：用户管理页面需要查询角色列表，所以这里也允许 system:user 权限
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('system:role:query', 'system:user', 'system:user:view')")
    public Result<List<Role>> getAllRoles() {
        log.info("获取所有角色");
        List<Role> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
    
    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Role> getRoleById(@PathVariable Long id) {
        log.info("获取角色详情: id={}", id);
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }
    
    /**
     * 创建角色
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('system:role:create')")
    public Result<Role> createRole(@RequestBody RoleDTO roleDTO) {
        log.info("创建角色: {}", roleDTO);
        Role role = roleService.createRole(roleDTO);
        return Result.success(role);
    }
    
    /**
     * 更新角色
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('system:role:update')")
    public Result<Role> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        log.info("更新角色: id={}, roleDTO={}", id, roleDTO);
        Role role = roleService.updateRole(id, roleDTO);
        return Result.success(role);
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        log.info("删除角色: id={}", id);
        roleService.deleteRole(id);
        return Result.success();
    }
    
    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch-delete")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> batchDeleteRoles(@RequestBody List<Long> ids) {
        log.info("批量删除角色: ids={}", ids);
        roleService.batchDeleteRoles(ids);
        return Result.success();
    }
    
    /**
     * 更新角色状态
     */
    @PutMapping("/status/{id}")
    @PreAuthorize("hasAuthority('system:role:update')")
    public Result<Void> updateRoleStatus(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        log.info("更新角色状态: id={}, status={}", id, roleDTO.getStatus());
        roleService.updateRoleStatus(id, roleDTO.getStatus());
        return Result.success();
    }
    
    /**
     * 获取角色权限
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<List<Long>> getRolePermissions(@PathVariable Long id) {
        log.info("获取角色权限: id={}", id);
        List<Long> permissionIds = roleService.getRolePermissions(id);
        return Result.success(permissionIds);
    }
    
    /**
     * 分配角色权限
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:permission')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        log.info("分配角色权限: id={}, permissionIds={}", id, permissionIds);
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }
}
