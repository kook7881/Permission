package com.zoyo.auth.service;

import com.zoyo.auth.dto.RoleDTO;
import com.zoyo.auth.entity.Role;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 角色服务接口
 */
public interface IRoleService {
    
    /**
     * 获取角色列表（分页）
     * @param roleName 角色名称
     * @param status 状态
     * @param page 页码
     * @param size 每页大小
     * @return 角色分页列表
     */
    Page<Role> getRoleList(String roleName, Integer status, int page, int size);
    
    /**
     * 获取所有角色（不分页）
     * @return 角色列表
     */
    List<Role> getAllRoles();
    
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色信息
     */
    Role getRoleById(Long id);
    
    /**
     * 创建角色
     * @param roleDTO 角色信息
     * @return 创建的角色
     */
    Role createRole(RoleDTO roleDTO);
    
    /**
     * 更新角色
     * @param id 角色ID
     * @param roleDTO 角色信息
     * @return 更新后的角色
     */
    Role updateRole(Long id, RoleDTO roleDTO);
    
    /**
     * 删除角色
     * @param id 角色ID
     */
    void deleteRole(Long id);
    
    /**
     * 批量删除角色
     * @param ids 角色ID列表
     */
    void batchDeleteRoles(List<Long> ids);
    
    /**
     * 更新角色状态
     * @param id 角色ID
     * @param status 状态
     */
    void updateRoleStatus(Long id, Integer status);
    
    /**
     * 获取角色的权限ID列表
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissions(Long roleId);
    
    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
    
    /**
     * 根据用户ID获取角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Long userId);
}
