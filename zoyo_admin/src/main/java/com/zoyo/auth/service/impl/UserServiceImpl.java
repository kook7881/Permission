package com.zoyo.auth.service.impl;

import com.zoyo.auth.common.ResultCode;
import com.zoyo.auth.dto.UpdatePasswordRequest;
import com.zoyo.auth.dto.UserDTO;
import com.zoyo.auth.entity.User;
import com.zoyo.auth.repository.UserRepository;
import com.zoyo.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserDTO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage()));
        
        return convertToDTO(user);
    }
    
    /**
     * 根据ID获取用户信息
     */
    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage()));
        
        return convertToDTO(user);
    }
    
    /**
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("用户修改密码: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage()));
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(UserDTO userDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("更新用户信息: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage()));
        
        // 更新允许修改的字段
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getRealName() != null) {
            user.setRealName(userDTO.getRealName());
        }
        if (userDTO.getRemark() != null) {
            user.setRemark(userDTO.getRemark());
        }
        
        user = userRepository.save(user);
        return convertToDTO(user);
    }
    
    /**
     * 删除用户（软删除，禁用账号）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage()));
        
        user.setEnabled(false);
        user.setStatus(0);
        userRepository.save(user);
    }
    
    /**
     * 将User实体转换为UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .realName(user.getRealName())
                .status(user.getStatus())
                .accountNonExpired(user.getAccountNonExpired())
                .accountNonLocked(user.getAccountNonLocked())
                .credentialsNonExpired(user.getCredentialsNonExpired())
                .enabled(user.getEnabled())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .remark(user.getRemark())
                .build();
    }
}
