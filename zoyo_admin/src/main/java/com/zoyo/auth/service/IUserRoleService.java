package com.zoyo.auth.service;

import com.zoyo.auth.entity.Role;

import java.util.List;

/**
 * 用户角色服务接口
 */
public interface IUserRoleService {
    
    /**
     * 获取用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(Long userId);
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 移除用户的角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void removeRole(Long userId, Long roleId);
    
    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return true-拥有，false-不拥有
     */
    boolean hasRole(Long userId, String roleCode);
    
    /**
     * 检查用户是否拥有任意一个角色
     * @param userId 用户ID
     * @param roleCodes 角色编码列表
     * @return true-拥有任意一个，false-都不拥有
     */
    boolean hasAnyRole(Long userId, List<String> roleCodes);
    
    /**
     * 检查用户是否拥有所有角色
     * @param userId 用户ID
     * @param roleCodes 角色编码列表
     * @return true-拥有所有，false-不拥有所有
     */
    boolean hasAllRoles(Long userId, List<String> roleCodes);
}
