package com.zoyo.auth.service;

import com.zoyo.auth.dto.PermissionDTO;
import com.zoyo.auth.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 */
public interface IPermissionService {
    
    /**
     * 获取权限树
     * @return 权限树列表
     */
    List<PermissionDTO> getPermissionTree();
    
    /**
     * 获取权限列表
     * @return 权限列表
     */
    List<Permission> getPermissionList();
    
    /**
     * 根据ID获取权限
     * @param id 权限ID
     * @return 权限信息
     */
    Permission getPermissionById(Long id);
    
    /**
     * 创建权限
     * @param permissionDTO 权限信息
     * @return 创建的权限
     */
    Permission createPermission(PermissionDTO permissionDTO);
    
    /**
     * 更新权限
     * @param id 权限ID
     * @param permissionDTO 权限信息
     * @return 更新后的权限
     */
    Permission updatePermission(Long id, PermissionDTO permissionDTO);
    
    /**
     * 删除权限
     * @param id 权限ID
     */
    void deletePermission(Long id);
    
    /**
     * 获取用户权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(Long userId);
    
    /**
     * 获取用户菜单树
     * @param userId 用户ID
     * @return 菜单树
     */
    List<PermissionDTO> getUserMenuTree(Long userId);
    
    /**
     * 获取用户权限标识列表
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> getUserPermissionCodes(Long userId);
}
