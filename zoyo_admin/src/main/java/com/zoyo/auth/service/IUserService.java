package com.zoyo.auth.service;

import com.zoyo.auth.dto.UpdatePasswordRequest;
import com.zoyo.auth.dto.UserDTO;

/**
 * 用户服务接口
 */
public interface IUserService {
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    UserDTO getCurrentUser();
    
    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    UserDTO getUserById(Long id);
    
    /**
     * 修改密码
     * @param request 修改密码请求
     */
    void updatePassword(UpdatePasswordRequest request);
    
    /**
     * 更新用户信息
     * @param userDTO 用户信息
     * @return 更新后的用户信息
     */
    UserDTO updateUser(UserDTO userDTO);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
}
