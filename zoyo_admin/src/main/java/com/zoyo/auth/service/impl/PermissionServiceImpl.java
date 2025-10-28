package com.zoyo.auth.service.impl;

import com.zoyo.auth.dto.PermissionDTO;
import com.zoyo.auth.entity.Permission;
import com.zoyo.auth.repository.PermissionRepository;
import com.zoyo.auth.repository.RolePermissionRepository;
import com.zoyo.auth.repository.UserRepository;
import com.zoyo.auth.repository.UserRoleRepository;
import com.zoyo.auth.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    @Override
    public List<PermissionDTO> getPermissionTree() {
        log.info("获取权限树");
        List<Permission> permissions = permissionRepository.findByStatusOrderBySortOrderAsc(1);
        return buildPermissionTree(permissions, 0L);
    }

    @Override
    public List<Permission> getPermissionList() {
        log.info("获取权限列表");
        return permissionRepository.findByStatusOrderBySortOrderAsc(1);
    }

    @Override
    public Permission getPermissionById(Long id) {
        log.info("根据ID获取权限: {}", id);
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("权限不存在"));
    }

    @Override
    @Transactional
    public Permission createPermission(PermissionDTO permissionDTO) {
        log.info("创建权限: {}", permissionDTO.getPermissionName());
        
        // 检查权限标识是否已存在
        if (permissionRepository.existsByPermissionCode(permissionDTO.getPermissionCode())) {
            throw new RuntimeException("权限标识已存在");
        }

        Permission permission = new Permission();
        permission.setParentId(permissionDTO.getParentId());
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setPermissionCode(permissionDTO.getPermissionCode());
        permission.setPermissionType(permissionDTO.getPermissionType());
        permission.setRoutePath(permissionDTO.getRoutePath());
        permission.setComponentPath(permissionDTO.getComponentPath());
        permission.setIcon(permissionDTO.getIcon());
        permission.setSortOrder(permissionDTO.getSortOrder());
        permission.setVisible(permissionDTO.getVisible());
        permission.setStatus(1);
        permission.setRemark(permissionDTO.getRemark());
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());

        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public Permission updatePermission(Long id, PermissionDTO permissionDTO) {
        log.info("更新权限: {}", id);
        Permission permission = getPermissionById(id);

        // 检查权限标识是否已被其他权限使用
        if (permissionRepository.existsByPermissionCodeAndIdNot(permissionDTO.getPermissionCode(), id)) {
            throw new RuntimeException("权限标识已存在");
        }

        permission.setParentId(permissionDTO.getParentId());
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setPermissionCode(permissionDTO.getPermissionCode());
        permission.setPermissionType(permissionDTO.getPermissionType());
        permission.setRoutePath(permissionDTO.getRoutePath());
        permission.setComponentPath(permissionDTO.getComponentPath());
        permission.setIcon(permissionDTO.getIcon());
        permission.setSortOrder(permissionDTO.getSortOrder());
        permission.setVisible(permissionDTO.getVisible());
        permission.setRemark(permissionDTO.getRemark());
        permission.setUpdateTime(LocalDateTime.now());

        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        log.info("删除权限: {}", id);
        Permission permission = getPermissionById(id);

        // 检查是否有子权限
        if (permissionRepository.existsByParentId(id)) {
            throw new RuntimeException("存在子权限，无法删除");
        }

        // 检查是否有角色使用该权限
        Long roleCount = rolePermissionRepository.countByPermissionId(id);
        if (roleCount > 0) {
            throw new RuntimeException("该权限已被角色使用，无法删除");
        }

        permissionRepository.delete(permission);
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        log.info("获取用户权限: {}", userId);
        
        // 直接使用Repository的查询方法
        return permissionRepository.findByUserId(userId);
    }

    @Override
    public List<PermissionDTO> getUserMenuTree(Long userId) {
        log.info("获取用户菜单树: {}", userId);
        
        // 直接使用Repository的查询方法获取菜单权限
        List<Permission> menuPermissions = permissionRepository.findMenusByUserId(userId);
        
        return buildPermissionTree(menuPermissions, 0L);
    }

    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        log.info("获取用户权限标识: {}", userId);
        
        // 直接使用Repository的查询方法
        return permissionRepository.findPermissionCodesByUserId(userId);
    }

    /**
     * 构建权限树
     */
    private List<PermissionDTO> buildPermissionTree(List<Permission> permissions, Long parentId) {
        List<PermissionDTO> tree = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId().equals(parentId)) {
                PermissionDTO dto = convertToDTO(permission);
                dto.setChildren(buildPermissionTree(permissions, permission.getId()));
                tree.add(dto);
            }
        }
        return tree;
    }

    /**
     * 转换为DTO
     */
    private PermissionDTO convertToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(permission.getId());
        dto.setParentId(permission.getParentId());
        dto.setPermissionName(permission.getPermissionName());
        dto.setPermissionCode(permission.getPermissionCode());
        dto.setPermissionType(permission.getPermissionType());
        dto.setRoutePath(permission.getRoutePath());
        dto.setComponentPath(permission.getComponentPath());
        dto.setIcon(permission.getIcon());
        dto.setSortOrder(permission.getSortOrder());
        dto.setVisible(permission.getVisible());
        dto.setStatus(permission.getStatus());
        dto.setRemark(permission.getRemark());
        dto.setCreateTime(permission.getCreateTime());
        dto.setUpdateTime(permission.getUpdateTime());
        return dto;
    }
}
