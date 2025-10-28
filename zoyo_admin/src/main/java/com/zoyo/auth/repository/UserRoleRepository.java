package com.zoyo.auth.repository;

import com.zoyo.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联数据访问层
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    /**
     * 根据用户ID查询用户角色关联列表
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * 根据角色ID查询用户角色关联列表
     */
    List<UserRole> findByRoleId(Long roleId);
    
    /**
     * 根据用户ID和角色ID查询
     */
    UserRole findByUserIdAndRoleId(Long userId, Long roleId);
    
    /**
     * 检查用户是否拥有指定角色
     */
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
    
    /**
     * 根据用户ID删除所有角色关联
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色ID删除所有用户关联
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID和角色ID删除
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 批量插入用户角色关联
     */
    @Modifying
    @Query(value = "INSERT INTO sys_user_role (user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 统计角色的用户数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.roleId = :roleId")
    Long countByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 统计用户的角色数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
