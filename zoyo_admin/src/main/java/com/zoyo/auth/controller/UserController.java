package com.zoyo.auth.controller;

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
        log.info("获取当前用户信息");
        UserDTO userDTO = userService.getCurrentUser();
        return Result.success(userDTO);
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        log.info("获取用户信息: {}", id);
        UserDTO userDTO = userService.getUserById(id);
        return Result.success(userDTO);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        log.info("更新用户信息");
        UserDTO updated = userService.updateUser(userDTO);
        return Result.success("更新成功", updated);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        log.info("修改密码");
        userService.updatePassword(request);
        return Result.success("密码修改成功", null);
    }
    
    /**
     * 删除用户（需要管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("删除用户: {}", id);
        userService.deleteUser(id);
        return Result.success("删除成功", null);
    }
}
