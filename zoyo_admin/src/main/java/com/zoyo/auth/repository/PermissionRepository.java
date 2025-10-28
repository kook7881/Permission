package com.zoyo.auth.repository;

import com.zoyo.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限数据访问层
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 根据权限标识查询权限
     */
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    /**
     * 检查权限标识是否存在
     */
    boolean existsByPermissionCode(String permissionCode);
    
    /**
     * 检查权限标识是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p WHERE p.permissionCode = :permissionCode AND p.id != :id")
    boolean existsByPermissionCodeAndIdNot(@Param("permissionCode") String permissionCode, @Param("id") Long id);
    
    /**
     * 根据父ID查询子权限列表
     */
    List<Permission> findByParentIdOrderBySortOrderAsc(Long parentId);
    
    /**
     * 根据权限类型查询权限列表
     */
    List<Permission> findByPermissionTypeOrderBySortOrderAsc(Integer permissionType);
    
    /**
     * 根据状态查询权限列表
     */
    List<Permission> findByStatusOrderBySortOrderAsc(Integer status);
    
    /**
     * 查询所有权限（按排序字段排序）
     */
    List<Permission> findAllByOrderBySortOrderAsc();
    
    /**
     * 查询所有菜单权限（按排序字段排序）
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionType = 1 AND p.status = 1 ORDER BY p.sortOrder ASC")
    List<Permission> findAllMenus();
    
    /**
     * 根据父ID和状态查询权限列表
     */
    List<Permission> findByParentIdAndStatusOrderBySortOrderAsc(Long parentId, Integer status);
    
    /**
     * 检查是否有子权限
     */
    boolean existsByParentId(Long parentId);
    
    /**
     * 根据角色ID查询权限列表
     */
    @Query("SELECT p FROM Permission p JOIN RolePermission rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId AND p.status = 1 ORDER BY p.sortOrder ASC")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查询权限列表（通过角色关联）
     */
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN RolePermission rp ON p.id = rp.permissionId " +
           "JOIN UserRole ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND p.status = 1 " +
           "ORDER BY p.sortOrder ASC")
    List<Permission> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询菜单权限列表
     */
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN RolePermission rp ON p.id = rp.permissionId " +
           "JOIN UserRole ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND p.permissionType = 1 AND p.status = 1 AND p.visible = true " +
           "ORDER BY p.sortOrder ASC")
    List<Permission> findMenusByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查询权限标识列表
     */
    @Query("SELECT DISTINCT p.permissionCode FROM Permission p " +
           "JOIN RolePermission rp ON p.id = rp.permissionId " +
           "JOIN UserRole ur ON rp.roleId = ur.roleId " +
           "WHERE ur.userId = :userId AND p.status = 1")
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);
}
