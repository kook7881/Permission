package com.zoyo.auth.repository;

import com.zoyo.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查询用户
     * @param username 用户名
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 更新用户最后登录信息
     * @param userId 用户ID
     * @param loginTime 登录时间
     * @param loginIp 登录IP
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp WHERE u.id = :userId")
    @org.springframework.transaction.annotation.Transactional
    void updateLastLoginInfo(@Param("userId") Long userId, 
                            @Param("loginTime") LocalDateTime loginTime, 
                            @Param("loginIp") String loginIp);
    
    /**
     * 更新用户最后退出时间
     * @param userId 用户ID
     * @param logoutTime 退出时间
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogoutTime = :logoutTime WHERE u.id = :userId")
    @org.springframework.transaction.annotation.Transactional
    void updateLastLogoutTime(@Param("userId") Long userId, 
                             @Param("logoutTime") LocalDateTime logoutTime);
    
    /**
     * 统计指定时间之后登录的用户数
     */
    Long countByLastLoginTimeAfter(LocalDateTime time);
    
    /**
     * 统计指定时间之后注册的用户数
     */
    Long countByCreateTimeAfter(LocalDateTime time);
    
    /**
     * 统计指定时间段内登录的用户数
     */
    Long countByLastLoginTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定时间段内注册的用户数
     */
    Long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据ID查询未删除的用户
     */
    Optional<User> findByIdAndDeletedFalse(Long id);
    
    /**
     * 根据用户名查询未删除的用户
     */
    Optional<User> findByUsernameAndDeletedFalse(String username);
    
    /**
     * 根据邮箱查询未删除的用户
     */
    Optional<User> findByEmailAndDeletedFalse(String email);
    
    /**
     * 根据手机号查询未删除的用户
     */
    Optional<User> findByPhoneAndDeletedFalse(String phone);
    
    /**
     * 检查用户名是否存在（排除指定ID）
     */
    boolean existsByUsernameAndDeletedFalseAndIdNot(String username, Long id);
    
    /**
     * 检查邮箱是否存在（排除指定ID）
     */
    boolean existsByEmailAndDeletedFalseAndIdNot(String email, Long id);
    
    /**
     * 检查手机号是否存在（排除指定ID）
     */
    boolean existsByPhoneAndDeletedFalseAndIdNot(String phone, Long id);
    
    /**
     * 检查用户名是否存在（未删除）
     */
    boolean existsByUsernameAndDeletedFalse(String username);
    
    /**
     * 检查邮箱是否存在（未删除）
     */
    boolean existsByEmailAndDeletedFalse(String email);
    
    /**
     * 检查手机号是否存在（未删除）
     */
    boolean existsByPhoneAndDeletedFalse(String phone);
    
    /**
     * 根据部门ID查询用户列表
     */
    List<User> findByDeptIdAndDeletedFalse(Long deptId);
    
    /**
     * 统计部门下的用户数量
     */
    long countByDeptIdAndDeletedFalse(Long deptId);
    
    /**
     * 查询所有未删除的用户
     */
    List<User> findByDeletedFalse();
    
    /**
     * 查询最近登录的用户（前10条）
     */
    List<User> findTop10ByLastLoginTimeIsNotNullOrderByLastLoginTimeDesc();
}

