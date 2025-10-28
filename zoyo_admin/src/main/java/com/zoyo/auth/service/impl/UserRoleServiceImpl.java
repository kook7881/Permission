package com.zoyo.auth.service.impl;

import com.zoyo.auth.entity.Role;
import com.zoyo.auth.entity.UserRole;
import com.zoyo.auth.repository.RoleRepository;
import com.zoyo.auth.repository.UserRepository;
import com.zoyo.auth.repository.UserRoleRepository;
import com.zoyo.auth.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements IUserRoleService {
    
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Override
    public List<Role> getUserRoles(Long userId) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        return roleRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        // 验证所有角色是否存在
        for (Long roleId : roleIds) {
            roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        }
        
        // 删除用户原有角色
        userRoleRepository.deleteByUserId(userId);
        
        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> new UserRole(userId, roleId))
                    .collect(Collectors.toList());
            userRoleRepository.saveAll(userRoles);
        }
        
        log.info("为用户分配角色成功: userId={}, roleIds={}", userId, roleIds);
    }
    
    @Override
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));
        
        // 验证角色是否存在
        roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        
        // 删除用户角色关联
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
        
        log.info("移除用户角色成功: userId={}, roleId={}", userId, roleId);
    }
    
    @Override
    public boolean hasRole(Long userId, String roleCode) {
        List<Role> roles = getUserRoles(userId);
        return roles.stream().anyMatch(role -> role.getRoleCode().equals(roleCode));
    }
    
    @Override
    public boolean hasAnyRole(Long userId, List<String> roleCodes) {
        List<Role> roles = getUserRoles(userId);
        return roles.stream().anyMatch(role -> roleCodes.contains(role.getRoleCode()));
    }
    
    @Override
    public boolean hasAllRoles(Long userId, List<String> roleCodes) {
        List<Role> roles = getUserRoles(userId);
        List<String> userRoleCodes = roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
        return userRoleCodes.containsAll(roleCodes);
    }
}
