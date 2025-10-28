package com.zoyo.auth.service.impl;

import com.zoyo.auth.dto.*;
import com.zoyo.auth.entity.Department;
import com.zoyo.auth.entity.Role;
import com.zoyo.auth.entity.User;
import com.zoyo.auth.entity.UserRole;
import com.zoyo.auth.repository.DepartmentRepository;
import com.zoyo.auth.repository.RoleRepository;
import com.zoyo.auth.repository.UserRepository;
import com.zoyo.auth.repository.UserRoleRepository;
import com.zoyo.auth.service.IUserManagementService;
import com.zoyo.auth.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements IUserManagementService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 超级管理员用户名（不可修改、删除）
     */
    private static final String SUPER_ADMIN_USERNAME = "admin";
    
    /**
     * 创建用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVO createUser(UserCreateDTO createDTO) {
        log.info("创建用户: {}", createDTO.getUsername());
        
        // 检查用户名是否存在
        if (userRepository.existsByUsernameAndDeletedFalse(createDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (userRepository.existsByEmailAndDeletedFalse(createDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 检查手机号是否存在
        if (createDTO.getPhone() != null && !createDTO.getPhone().isEmpty()) {
            if (userRepository.existsByPhoneAndDeletedFalse(createDTO.getPhone())) {
                throw new RuntimeException("手机号已存在");
            }
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(createDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setEmail(createDTO.getEmail());
        user.setPhone(createDTO.getPhone());
        user.setRealName(createDTO.getRealName());
        user.setDeptId(createDTO.getDeptId());
        user.setPosition(createDTO.getPosition());
        user.setAvatar(createDTO.getAvatar());
        user.setGender(createDTO.getGender());
        user.setStatus(createDTO.getStatus());
        user.setRemark(createDTO.getRemark());
        user.setDeleted(false);
        
        // 设置创建人
        Long currentUserId = getCurrentUserId();
        user.setCreateBy(currentUserId);
        user.setUpdateBy(currentUserId);
        
        // 保存用户
        user = userRepository.save(user);
        
        // 分配角色
        if (createDTO.getRoleIds() != null && !createDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), createDTO.getRoleIds());
        }
        
        log.info("用户创建成功: {}", user.getUsername());
        return getUserDetail(user.getId());
    }

    /**
     * 更新用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDetailVO updateUser(UserUpdateDTO updateDTO) {
        log.info("更新用户: {}", updateDTO.getId());
        
        // 查询用户
        User user = userRepository.findByIdAndDeletedFalse(updateDTO.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否为超级管理员
        if (isSuperAdmin(user)) {
            throw new RuntimeException("不能修改超级管理员信息");
        }
        
        // 检查邮箱是否存在
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmailAndDeletedFalseAndIdNot(updateDTO.getEmail(), user.getId())) {
                throw new RuntimeException("邮箱已存在");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        // 检查手机号是否存在
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhoneAndDeletedFalseAndIdNot(updateDTO.getPhone(), user.getId())) {
                throw new RuntimeException("手机号已存在");
            }
            user.setPhone(updateDTO.getPhone());
        }
        
        // 更新其他字段
        if (updateDTO.getRealName() != null) {
            user.setRealName(updateDTO.getRealName());
        }
        if (updateDTO.getDeptId() != null) {
            user.setDeptId(updateDTO.getDeptId());
        }
        if (updateDTO.getPosition() != null) {
            user.setPosition(updateDTO.getPosition());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getGender() != null) {
            user.setGender(updateDTO.getGender());
        }
        if (updateDTO.getStatus() != null) {
            user.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getRemark() != null) {
            user.setRemark(updateDTO.getRemark());
        }
        
        // 更新密码（如果提供）
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        // 设置更新人
        user.setUpdateBy(getCurrentUserId());
        
        // 保存用户
        user = userRepository.save(user);
        
        // 更新角色
        if (updateDTO.getRoleIds() != null) {
            assignRoles(user.getId(), updateDTO.getRoleIds());
        }
        
        log.info("用户更新成功: {}", user.getUsername());
        return getUserDetail(user.getId());
    }
    
    /**
     * 删除用户（软删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);
        
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否为超级管理员
        if (isSuperAdmin(user)) {
            throw new RuntimeException("不能删除超级管理员");
        }
        
        // 检查是否为当前登录用户
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("不能删除当前登录用户");
        }
        
        // 软删除
        user.setDeleted(true);
        user.setDeleteTime(LocalDateTime.now());
        user.setUpdateBy(getCurrentUserId());
        userRepository.save(user);
        
        log.info("用户删除成功: {}", id);
    }
    
    /**
     * 批量删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(List<Long> ids) {
        log.info("批量删除用户: {}", ids);
        
        for (Long id : ids) {
            deleteUser(id);
        }
        
        log.info("批量删除用户成功，共删除{}个用户", ids.size());
    }
    
    /**
     * 修改用户状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, Integer status) {
        log.info("修改用户状态: id={}, status={}", id, status);
        
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否为超级管理员
        if (isSuperAdmin(user)) {
            throw new RuntimeException("不能修改超级管理员状态");
        }
        
        user.setStatus(status);
        user.setEnabled(status == 1);
        user.setUpdateBy(getCurrentUserId());
        userRepository.save(user);
        
        log.info("用户状态修改成功: {}", id);
    }
    
    /**
     * 批量修改用户状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateUserStatus(List<Long> ids, Integer status) {
        log.info("批量修改用户状态: ids={}, status={}", ids, status);
        
        for (Long id : ids) {
            updateUserStatus(id, status);
        }
        
        log.info("批量修改用户状态成功，共修改{}个用户", ids.size());
    }

    /**
     * 分页查询用户列表
     */
    @Override
    public Page<UserDetailVO> queryUsers(UserQueryDTO queryDTO) {
        log.debug("分页查询用户列表: {}", queryDTO);
        
        // 构建排序
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(queryDTO.getSortDirection()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            queryDTO.getSortBy()
        );
        
        // 构建分页（前端页码从1开始，JPA从0开始，需要减1）
        int pageNumber = queryDTO.getPage() > 0 ? queryDTO.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(pageNumber, queryDTO.getSize(), sort);
        
        // 构建查询条件
        Page<User> userPage = userRepository.findAll(
            UserSpecification.buildSpecification(
                queryDTO.getUsername(),
                queryDTO.getEmail(),
                queryDTO.getPhone(),
                queryDTO.getStatus(),
                queryDTO.getDeptId(),
                queryDTO.getRealName()
            ),
            pageable
        );
        
        // 转换为VO
        return userPage.map(this::convertToDetailVO);
    }
    
    /**
     * 获取用户详细信息
     */
    @Override
    public UserDetailVO getUserDetail(Long id) {
        log.debug("获取用户详细信息: {}", id);
        
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return convertToDetailVO(user);
    }
    
    /**
     * 检查用户名是否存在
     */
    @Override
    public boolean checkUsernameExists(String username, Long excludeId) {
        if (excludeId == null) {
            return userRepository.existsByUsernameAndDeletedFalse(username);
        }
        return userRepository.existsByUsernameAndDeletedFalseAndIdNot(username, excludeId);
    }
    
    /**
     * 检查邮箱是否存在
     */
    @Override
    public boolean checkEmailExists(String email, Long excludeId) {
        if (excludeId == null) {
            return userRepository.existsByEmailAndDeletedFalse(email);
        }
        return userRepository.existsByEmailAndDeletedFalseAndIdNot(email, excludeId);
    }
    
    /**
     * 检查手机号是否存在
     */
    @Override
    public boolean checkPhoneExists(String phone, Long excludeId) {
        if (excludeId == null) {
            return userRepository.existsByPhoneAndDeletedFalse(phone);
        }
        return userRepository.existsByPhoneAndDeletedFalseAndIdNot(phone, excludeId);
    }
    
    /**
     * 重置用户密码（管理员操作）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, String newPassword) {
        log.info("重置用户密码: {}", id);
        
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否为超级管理员
        if (isSuperAdmin(user)) {
            throw new RuntimeException("不能重置超级管理员密码");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateBy(getCurrentUserId());
        userRepository.save(user);
        
        log.info("用户密码重置成功: {}", id);
    }
    
    /**
     * 修改密码（用户自己操作）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(PasswordChangeDTO changeDTO) {
        log.info("用户修改密码");
        
        // 验证新密码和确认密码是否一致
        if (!changeDTO.getNewPassword().equals(changeDTO.getConfirmPassword())) {
            throw new RuntimeException("新密码和确认密码不一致");
        }
        
        // 获取当前用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证旧密码
        if (!passwordEncoder.matches(changeDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(changeDTO.getNewPassword()));
        user.setUpdateBy(user.getId());
        userRepository.save(user);
        
        log.info("用户密码修改成功: {}", username);
    }
    
    /**
     * 分配用户角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        log.info("分配用户角色: userId={}, roleIds={}", userId, roleIds);
        
        // 检查用户是否存在
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否为超级管理员
        if (isSuperAdmin(user)) {
            throw new RuntimeException("不能修改超级管理员的角色");
        }
        
        // 删除用户现有角色
        userRoleRepository.deleteByUserId(userId);
        
        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleRepository.save(userRole);
            }
        }
        
        log.info("用户角色分配成功: {}", userId);
    }
    
    /**
     * 获取用户角色列表
     */
    @Override
    public List<RoleDTO> getUserRoles(Long userId) {
        log.debug("获取用户角色列表: {}", userId);
        
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        List<Role> roles = roleRepository.findAllById(roleIds);
        return roles.stream()
                .map(this::convertToRoleDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByUsernameAndDeletedFalse(username)
                    .map(User::getId)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 将User实体转换为UserDetailVO
     */
    private UserDetailVO convertToDetailVO(User user) {
        UserDetailVO vo = UserDetailVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .realName(user.getRealName())
                .deptId(user.getDeptId())
                .position(user.getPosition())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .status(user.getStatus())
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .createBy(user.getCreateBy())
                .updateBy(user.getUpdateBy())
                .remark(user.getRemark())
                .build();
        
        // 计算在线状态
        // 判断逻辑：
        // 1. 账户状态必须为正常（status = 1）
        // 2. 最后登录时间不为空
        // 3. 最后登录时间在30分钟内
        // 4. 如果有退出时间，退出时间必须早于登录时间（即登录后没有退出）
        boolean isOnline = false;
        if (user.getLastLoginTime() != null && user.getStatus() == 1) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);
            
            // 检查登录时间是否在30分钟内
            boolean loginRecent = user.getLastLoginTime().isAfter(thirtyMinutesAgo);
            
            // 检查是否已退出（如果有退出时间且退出时间晚于登录时间，说明已退出）
            boolean hasLoggedOut = user.getLastLogoutTime() != null 
                && user.getLastLogoutTime().isAfter(user.getLastLoginTime());
            
            // 只有登录时间在30分钟内且未退出，才算在线
            isOnline = loginRecent && !hasLoggedOut;
            
            log.debug("用户 {} 在线状态计算: lastLoginTime={}, lastLogoutTime={}, loginRecent={}, hasLoggedOut={}, isOnline={}", 
                user.getUsername(), user.getLastLoginTime(), user.getLastLogoutTime(), loginRecent, hasLoggedOut, isOnline);
        } else {
            log.debug("用户 {} 在线状态为false: lastLoginTime={}, status={}", 
                user.getUsername(), user.getLastLoginTime(), user.getStatus());
        }
        vo.setOnlineStatus(isOnline);
        
        // 获取部门名称
        if (user.getDeptId() != null) {
            departmentRepository.findByIdAndDeletedFalse(user.getDeptId())
                    .ifPresent(dept -> vo.setDeptName(dept.getName()));
        }
        
        // 获取角色列表
        vo.setRoles(getUserRoles(user.getId()));
        
        return vo;
    }
    
    /**
     * 将Role实体转换为RoleDTO
     */
    private RoleDTO convertToRoleDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .roleCode(role.getRoleCode())
                .roleSort(role.getRoleSort())
                .dataScope(role.getDataScope())
                .status(role.getStatus())
                .isSystem(role.getIsSystem())
                .remark(role.getRemark())
                .createTime(role.getCreateTime())
                .updateTime(role.getUpdateTime())
                .createBy(role.getCreateBy())
                .updateBy(role.getUpdateBy())
                .build();
    }
    
    /**
     * 检查是否为超级管理员
     * @param user 用户对象
     * @return true-是超级管理员，false-不是
     */
    private boolean isSuperAdmin(User user) {
        return SUPER_ADMIN_USERNAME.equals(user.getUsername());
    }
}
