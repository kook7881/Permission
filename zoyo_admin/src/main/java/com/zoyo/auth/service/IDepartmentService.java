package com.zoyo.auth.service;

import com.zoyo.auth.dto.DepartmentDTO;
import com.zoyo.auth.dto.DepartmentTreeVO;

import java.util.List;

/**
 * 部门管理服务接口
 */
public interface IDepartmentService {
    
    /**
     * 获取部门树
     * @return 部门树列表
     */
    List<DepartmentTreeVO> getDepartmentTree();
    
    /**
     * 获取所有部门列表
     * @return 部门列表
     */
    List<DepartmentDTO> getAllDepartments();
    
    /**
     * 根据ID获取部门
     * @param id 部门ID
     * @return 部门信息
     */
    DepartmentDTO getDepartmentById(Long id);
    
    /**
     * 创建部门
     * @param departmentDTO 部门信息
     * @return 创建的部门
     */
    DepartmentDTO createDepartment(DepartmentDTO departmentDTO);
    
    /**
     * 更新部门
     * @param departmentDTO 部门信息
     * @return 更新后的部门
     */
    DepartmentDTO updateDepartment(DepartmentDTO departmentDTO);
    
    /**
     * 删除部门
     * @param id 部门ID
     */
    void deleteDepartment(Long id);
    
    /**
     * 获取子部门列表
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<DepartmentDTO> getChildDepartments(Long parentId);
    
    /**
     * 检查部门下是否有用户
     * @param deptId 部门ID
     * @return true-有用户，false-无用户
     */
    boolean hasUsers(Long deptId);
    
    /**
     * 检查部门下是否有子部门
     * @param deptId 部门ID
     * @return true-有子部门，false-无子部门
     */
    boolean hasChildren(Long deptId);
}
