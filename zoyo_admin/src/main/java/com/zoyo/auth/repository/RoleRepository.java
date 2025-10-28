package com.zoyo.auth.repository;

import com.zoyo.auth.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问层
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色编码查询角色
     */
    Optional<Role> findByRoleCode(String roleCode);
    
    /**
     * 检查角色编码是否存在
     */
    boolean existsByRoleCode(String roleCode);
    
    /**
     * 检查角色编码是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleCode = :roleCode AND r.id != :id")
    boolean existsByRoleCodeAndIdNot(@Param("roleCode") String roleCode, @Param("id") Long id);
    
    /**
     * 根据角色名称查询角色
     */
    Optional<Role> findByRoleName(String roleName);
    
    /**
     * 根据状态查询角色列表
     */
    List<Role> findByStatus(Integer status);
    
    /**
     * 查询所有角色（按排序字段排序）
     */
    List<Role> findAllByOrderByRoleSortAsc();
    
    /**
     * 根据角色名称模糊查询（分页）
     */
    Page<Role> findByRoleNameContaining(String roleName, Pageable pageable);
    
    /**
     * 根据角色名称和状态查询（分页）
     */
    Page<Role> findByRoleNameContainingAndStatus(String roleName, Integer status, Pageable pageable);
    
    /**
     * 根据状态查询（分页）
     */
    Page<Role> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 查询非系统内置角色
     */
    List<Role> findByIsSystemFalse();
    
    /**
     * 根据用户ID查询角色列表
     */
    @Query("SELECT r FROM Role r JOIN UserRole ur ON r.id = ur.roleId WHERE ur.userId = :userId")
    List<Role> findByUserId(@Param("userId") Long userId);
    
    /**
     * 统计角色数量
     */
    @Query("SELECT COUNT(r) FROM Role r WHERE r.status = 1")
    Long countActiveRoles();
}
