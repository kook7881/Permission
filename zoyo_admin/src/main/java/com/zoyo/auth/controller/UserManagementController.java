package com.zoyo.auth.controller;

import com.zoyo.auth.annotation.Log;
import com.zoyo.auth.common.OperationType;
import com.zoyo.auth.common.Result;
import com.zoyo.auth.dto.*;
import com.zoyo.auth.service.IUserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {
    
    private final IUserManagementService userManagementService;
    
    /**
     * 分页查询用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<Page<UserDetailVO>> queryUsers(UserQueryDTO queryDTO) {
        log.info("分页查询用户列表: {}", queryDTO);
        Page<UserDetailVO> page = userManagementService.queryUsers(queryDTO);
        return Result.success(page);
    }
    
    /**
     * 获取用户详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<UserDetailVO> getUserDetail(@PathVariable Long id) {
        log.info("获取用户详细信息: {}", id);
        UserDetailVO user = userManagementService.getUserDetail(id);
        return Result.success(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:create')")
    @Log(module = "用户管理", businessType = OperationType.INSERT, description = "创建用户")
    public Result<UserDetailVO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        log.info("创建用户: {}", createDTO.getUsername());
        UserDetailVO user = userManagementService.createUser(createDTO);
        return Result.success(user);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:update')")
    @Log(module = "用户管理", businessType = OperationType.UPDATE, description = "更新用户信息")
    public Result<UserDetailVO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("更新用户: {}", id);
        updateDTO.setId(id);
        UserDetailVO user = userManagementService.updateUser(updateDTO);
        return Result.success(user);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @Log(module = "用户管理", businessType = OperationType.DELETE, description = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: {}", id);
        userManagementService.deleteUser(id);
        return Result.success();
    }
    
    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:user:delete')")
    @Log(module = "用户管理", businessType = OperationType.DELETE, description = "批量删除用户")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> ids) {
        log.info("批量删除用户: {}", ids);
        userManagementService.batchDeleteUsers(ids);
        return Result.success();
    }
    
    /**
     * 修改用户状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:update')")
    @Log(module = "用户管理", businessType = OperationType.UPDATE, description = "修改用户状态")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        log.info("修改用户状态: id={}, status={}", id, status);
        userManagementService.updateUserStatus(id, status);
        return Result.success();
    }
    
    /**
     * 批量修改用户状态
     */
    @PutMapping("/batch/status")
    @PreAuthorize("hasAuthority('system:user:update')")
    public Result<Void> batchUpdateUserStatus(
            @RequestBody List<Long> ids,
            @RequestParam Integer status) {
        log.info("批量修改用户状态: ids={}, status={}", ids, status);
        userManagementService.batchUpdateUserStatus(ids, status);
        return Result.success();
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/username")
    public Result<Boolean> checkUsernameExists(
            @RequestParam String username,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = userManagementService.checkUsernameExists(username, excludeId);
        return Result.success(exists);
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check/email")
    public Result<Boolean> checkEmailExists(
            @RequestParam String email,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = userManagementService.checkEmailExists(email, excludeId);
        return Result.success(exists);
    }
    
    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check/phone")
    public Result<Boolean> checkPhoneExists(
            @RequestParam String phone,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = userManagementService.checkPhoneExists(phone, excludeId);
        return Result.success(exists);
    }
    
    /**
     * 重置用户密码（管理员操作）
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:reset-password')")
    @Log(module = "用户管理", businessType = OperationType.UPDATE, description = "重置用户密码")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        log.info("重置用户密码: {}", id);
        userManagementService.resetPassword(id, newPassword);
        return Result.success();
    }
    
    /**
     * 修改密码（用户自己操作）
     */
    @PutMapping("/change-password")
    @Log(module = "个人中心", businessType = OperationType.UPDATE, description = "修改个人密码")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeDTO changeDTO) {
        log.info("用户修改密码");
        userManagementService.changePassword(changeDTO);
        return Result.success();
    }
    
    /**
     * 分配用户角色
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:role')")
    @Log(module = "用户管理", businessType = OperationType.GRANT, description = "分配用户角色")
    public Result<Void> assignRoles(
            @PathVariable Long id,
            @RequestBody List<Long> roleIds) {
        log.info("分配用户角色: userId={}, roleIds={}", id, roleIds);
        userManagementService.assignRoles(id, roleIds);
        return Result.success();
    }
    
    /**
     * 获取用户角色列表
     */
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<List<RoleDTO>> getUserRoles(@PathVariable Long id) {
        log.info("获取用户角色列表: {}", id);
        List<RoleDTO> roles = userManagementService.getUserRoles(id);
        return Result.success(roles);
    }
}
