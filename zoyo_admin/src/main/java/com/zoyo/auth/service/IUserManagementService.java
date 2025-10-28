package com.zoyo.auth.service;

import com.zoyo.auth.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 用户管理服务接口
 */
public interface IUserManagementService {
    
    /**
     * 创建用户
     * @param createDTO 用户创建DTO
     * @return 用户详细信息
     */
    UserDetailVO createUser(UserCreateDTO createDTO);
    
    /**
     * 更新用户
     * @param updateDTO 用户更新DTO
     * @return 用户详细信息
     */
    UserDetailVO updateUser(UserUpdateDTO updateDTO);
    
    /**
     * 删除用户（软删除）
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 批量删除用户
     * @param ids 用户ID列表
     */
    void batchDeleteUsers(List<Long> ids);
    
    /**
     * 修改用户状态
     * @param id 用户ID
     * @param status 状态：0-禁用，1-正常
     */
    void updateUserStatus(Long id, Integer status);
    
    /**
     * 批量修改用户状态
     * @param ids 用户ID列表
     * @param status 状态：0-禁用，1-正常
     */
    void batchUpdateUserStatus(List<Long> ids, Integer status);
    
    /**
     * 分页查询用户列表
     * @param queryDTO 查询条件
     * @return 用户分页数据
     */
    Page<UserDetailVO> queryUsers(UserQueryDTO queryDTO);
    
    /**
     * 获取用户详细信息
     * @param id 用户ID
     * @return 用户详细信息
     */
    UserDetailVO getUserDetail(Long id);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return true-存在，false-不存在
     */
    boolean checkUsernameExists(String username, Long excludeId);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return true-存在，false-不存在
     */
    boolean checkEmailExists(String email, Long excludeId);
    
    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return true-存在，false-不存在
     */
    boolean checkPhoneExists(String phone, Long excludeId);
    
    /**
     * 重置用户密码（管理员操作）
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);
    
    /**
     * 修改密码（用户自己操作）
     * @param changeDTO 密码修改DTO
     */
    void changePassword(PasswordChangeDTO changeDTO);
    
    /**
     * 分配用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);
    
    /**
     * 获取用户角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleDTO> getUserRoles(Long userId);
}
