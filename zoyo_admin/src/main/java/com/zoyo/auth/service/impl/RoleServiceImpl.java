package com.zoyo.auth.service.impl;

import com.zoyo.auth.dto.RoleDTO;
import com.zoyo.auth.entity.Role;
import com.zoyo.auth.entity.RolePermission;
import com.zoyo.auth.repository.RolePermissionRepository;
import com.zoyo.auth.repository.RoleRepository;
import com.zoyo.auth.repository.UserRoleRepository;
import com.zoyo.auth.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {
    
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    
    @Override
    public Page<Role> getRoleList(String roleName, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "roleSort"));
        
        if (roleName != null && !roleName.isEmpty() && status != null) {
            return roleRepository.findByRoleNameContainingAndStatus(roleName, status, pageable);
        } else if (roleName != null && !roleName.isEmpty()) {
            return roleRepository.findByRoleNameContaining(roleName, pageable);
        } else if (status != null) {
            return roleRepository.findByStatus(status, pageable);
        } else {
            return roleRepository.findAll(pageable);
        }
    }
    
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAllByOrderByRoleSortAsc();
    }
    
    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
    }
    
    @Override
    @Transactional
    public Role createRole(RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        if (roleRepository.existsByRoleCode(roleDTO.getRoleCode())) {
            throw new RuntimeException("角色编码已存在: " + roleDTO.getRoleCode());
        }
        
        Role role = new Role();
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleCode(roleDTO.getRoleCode());
        role.setRoleSort(roleDTO.getRoleSort() != null ? roleDTO.getRoleSort() : 0);
        role.setDataScope(roleDTO.getDataScope() != null ? roleDTO.getDataScope() : 1);
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);
        role.setIsSystem(false); // 用户创建的角色不是系统内置
        role.setRemark(roleDTO.getRemark());
        
        return roleRepository.save(role);
    }
    
    @Override
    @Transactional
    public Role updateRole(Long id, RoleDTO roleDTO) {
        Role role = getRoleById(id);
        
        // 系统内置角色不允许修改角色编码
        if (role.getIsSystem() && !role.getRoleCode().equals(roleDTO.getRoleCode())) {
            throw new RuntimeException("系统内置角色不允许修改角色编码");
        }
        
        // 检查角色编码是否已被其他角色使用
        if (!role.getRoleCode().equals(roleDTO.getRoleCode()) &&
            roleRepository.existsByRoleCodeAndIdNot(roleDTO.getRoleCode(), id)) {
            throw new RuntimeException("角色编码已存在: " + roleDTO.getRoleCode());
        }
        
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleCode(roleDTO.getRoleCode());
        role.setRoleSort(roleDTO.getRoleSort());
        role.setDataScope(roleDTO.getDataScope());
        role.setStatus(roleDTO.getStatus());
        role.setRemark(roleDTO.getRemark());
        
        return roleRepository.save(role);
    }
    
    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        
        // 系统内置角色不允许删除
        if (role.getIsSystem()) {
            throw new RuntimeException("系统内置角色不允许删除");
        }
        
        // 检查是否有用户使用该角色
        Long userCount = userRoleRepository.countByRoleId(id);
        if (userCount > 0) {
            throw new RuntimeException("该角色已分配给用户，无法删除");
        }
        
        // 删除角色权限关联
        rolePermissionRepository.deleteByRoleId(id);
        
        // 删除角色
        roleRepository.deleteById(id);
        
        log.info("删除角色成功: {}", id);
    }
    
    @Override
    @Transactional
    public void batchDeleteRoles(List<Long> ids) {
        for (Long id : ids) {
            deleteRole(id);
        }
    }
    
    @Override
    @Transactional
    public void updateRoleStatus(Long id, Integer status) {
        Role role = getRoleById(id);
        role.setStatus(status);
        roleRepository.save(role);
        
        log.info("更新角色状态成功: id={}, status={}", id, status);
    }
    
    @Override
    public List<Long> getRolePermissions(Long roleId) {
        return rolePermissionRepository.findPermissionIdsByRoleId(roleId);
    }
    
    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 验证角色是否存在
        getRoleById(roleId);
        
        // 删除原有权限
        rolePermissionRepository.deleteByRoleId(roleId);
        
        // 分配新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> new RolePermission(roleId, permissionId))
                    .collect(Collectors.toList());
            rolePermissionRepository.saveAll(rolePermissions);
        }
        
        log.info("为角色分配权限成功: roleId={}, permissionIds={}", roleId, permissionIds);
    }
    
    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleRepository.findByUserId(userId);
    }
}
