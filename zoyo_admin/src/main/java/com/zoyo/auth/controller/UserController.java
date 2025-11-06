package com.zoyo.auth.controller;

import com.zoyo.auth.annotation.Log;
import com.zoyo.auth.common.OperationType;
import com.zoyo.auth.common.Result;
import com.zoyo.auth.dto.UpdatePasswordRequest;
import com.zoyo.auth.dto.UserDTO;
import com.zoyo.auth.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户信息管理相关请求
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final IUserService userService;
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<UserDTO> getCurrentUser() {
        UserDTO userDTO = userService.getCurrentUser();
        return Result.success(userDTO);
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return Result.success(userDTO);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    @Log(module = "个人中心", businessType = OperationType.UPDATE, description = "更新个人信息")
    public Result<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        UserDTO updated = userService.updateUser(userDTO);
        return Result.success("更新成功", updated);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/password")
    @Log(module = "个人中心", businessType = OperationType.UPDATE, description = "修改密码")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return Result.success("密码修改成功", null);
    }
    
    /**
     * 删除用户（需要管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "用户管理", businessType = OperationType.DELETE, description = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }
}
