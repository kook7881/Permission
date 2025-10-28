package com.zoyo.auth.repository;

import com.zoyo.auth.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联数据访问层
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    /**
     * 根据角色ID查询角色权限关联列表
     */
    List<RolePermission> findByRoleId(Long roleId);
    
    /**
     * 根据权限ID查询角色权限关联列表
     */
    List<RolePermission> findByPermissionId(Long permissionId);
    
    /**
     * 根据角色ID和权限ID查询
     */
    RolePermission findByRoleIdAndPermissionId(Long roleId, Long permissionId);
    
    /**
     * 检查角色是否拥有指定权限
     */
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
    
    /**
     * 根据角色ID删除所有权限关联
     */
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据权限ID删除所有角色关联
     */
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.permissionId = :permissionId")
    void deleteByPermissionId(@Param("permissionId") Long permissionId);
    
    /**
     * 根据角色ID和权限ID删除
     */
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId")
    void deleteByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    /**
     * 批量插入角色权限关联
     */
    @Modifying
    @Query(value = "INSERT INTO sys_role_permission (role_id, permission_id) VALUES (:roleId, :permissionId)", nativeQuery = true)
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    /**
     * 统计权限的角色数量
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.permissionId = :permissionId")
    Long countByPermissionId(@Param("permissionId") Long permissionId);
    
    /**
     * 统计角色的权限数量
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.roleId = :roleId")
    Long countByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据角色ID查询权限ID列表
     */
    @Query("SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId = :roleId")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
