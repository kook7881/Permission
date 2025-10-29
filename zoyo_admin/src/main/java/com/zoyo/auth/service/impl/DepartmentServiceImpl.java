package com.zoyo.auth.service.impl;

import com.zoyo.auth.dto.DepartmentDTO;
import com.zoyo.auth.dto.DepartmentTreeVO;
import com.zoyo.auth.entity.Department;
import com.zoyo.auth.entity.User;
import com.zoyo.auth.repository.DepartmentRepository;
import com.zoyo.auth.repository.UserRepository;
import com.zoyo.auth.service.IDepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements IDepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    
    /**
     * 获取部门树
     */
    @Override
    public List<DepartmentTreeVO> getDepartmentTree() {
        log.debug("获取部门树");
        
        List<Department> allDepartments = departmentRepository.findByDeletedFalseOrderBySortAsc();
        return buildDepartmentTree(allDepartments, 0L);
    }
    
    /**
     * 获取所有部门列表
     */
    @Override
    public List<DepartmentDTO> getAllDepartments() {
        log.debug("获取所有部门列表");
        
        List<Department> departments = departmentRepository.findByDeletedFalseOrderBySortAsc();
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取部门
     */
    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        log.debug("获取部门: {}", id);
        
        Department department = departmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        
        return convertToDTO(department);
    }
    
    /**
     * 创建部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        log.info("创建部门: {}", departmentDTO.getName());
        
        // 检查部门名称是否存在
        if (departmentRepository.findByNameAndDeletedFalse(departmentDTO.getName()).isPresent()) {
            throw new RuntimeException("部门名称已存在");
        }
        
        // 创建部门实体
        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setParentId(departmentDTO.getParentId() != null ? departmentDTO.getParentId() : 0L);
        department.setCode(departmentDTO.getCode());
        department.setSort(departmentDTO.getSort() != null ? departmentDTO.getSort() : 0);
        department.setLeaderId(departmentDTO.getLeaderId());
        department.setPhone(departmentDTO.getPhone());
        department.setEmail(departmentDTO.getEmail());
        department.setStatus(departmentDTO.getStatus());
        department.setRemark(departmentDTO.getRemark());
        department.setDeleted(false);
        
        // 设置创建人
        Long currentUserId = getCurrentUserId();
        department.setCreateBy(currentUserId);
        department.setUpdateBy(currentUserId);
        
        // 保存部门
        department = departmentRepository.save(department);
        
        log.info("部门创建成功: {}", department.getName());
        return convertToDTO(department);
    }
    
    /**
     * 更新部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO) {
        log.info("更新部门: {}", departmentDTO.getId());
        
        // 查询部门
        Department department = departmentRepository.findByIdAndDeletedFalse(departmentDTO.getId())
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        
        // 检查部门名称是否存在（排除自己）
        if (departmentDTO.getName() != null && !departmentDTO.getName().equals(department.getName())) {
            if (departmentRepository.findByNameAndDeletedFalseAndIdNot(departmentDTO.getName(), department.getId()).isPresent()) {
                throw new RuntimeException("部门名称已存在");
            }
            department.setName(departmentDTO.getName());
        }
        
        // 检查父部门是否是自己或自己的子部门
        if (departmentDTO.getParentId() != null && !departmentDTO.getParentId().equals(department.getParentId())) {
            if (departmentDTO.getParentId().equals(department.getId())) {
                throw new RuntimeException("父部门不能是自己");
            }
            // 检查是否是子部门（防止循环引用）
            if (isDescendant(department.getId(), departmentDTO.getParentId())) {
                throw new RuntimeException("父部门不能是自己的子部门");
            }
            department.setParentId(departmentDTO.getParentId());
        }
        
        // 更新其他字段
        if (departmentDTO.getCode() != null) {
            department.setCode(departmentDTO.getCode());
        }
        if (departmentDTO.getSort() != null) {
            department.setSort(departmentDTO.getSort());
        }
        if (departmentDTO.getLeaderId() != null) {
            department.setLeaderId(departmentDTO.getLeaderId());
        }
        if (departmentDTO.getPhone() != null) {
            department.setPhone(departmentDTO.getPhone());
        }
        if (departmentDTO.getEmail() != null) {
            department.setEmail(departmentDTO.getEmail());
        }
        if (departmentDTO.getStatus() != null) {
            department.setStatus(departmentDTO.getStatus());
        }
        if (departmentDTO.getRemark() != null) {
            department.setRemark(departmentDTO.getRemark());
        }
        
        // 设置更新人
        department.setUpdateBy(getCurrentUserId());
        
        // 保存部门
        department = departmentRepository.save(department);
        
        log.info("部门更新成功: {}", department.getName());
        return convertToDTO(department);
    }
    
    /**
     * 删除部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long id) {
        log.info("删除部门: {}", id);
        
        Department department = departmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        
        // 检查是否有子部门
        if (departmentRepository.existsByParentIdAndDeletedFalse(id)) {
            throw new RuntimeException("该部门下存在子部门，无法删除");
        }
        
        // 检查是否有用户
        if (userRepository.countByDeptIdAndDeletedFalse(id) > 0) {
            throw new RuntimeException("该部门下存在用户，无法删除");
        }
        
        // 软删除
        department.setDeleted(true);
        department.setUpdateBy(getCurrentUserId());
        departmentRepository.save(department);
        
        log.info("部门删除成功: {}", id);
    }
    
    /**
     * 获取子部门列表
     */
    @Override
    public List<DepartmentDTO> getChildDepartments(Long parentId) {
        log.debug("获取子部门列表: {}", parentId);
        
        List<Department> departments = departmentRepository.findByParentIdAndDeletedFalseOrderBySortAsc(parentId);
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 检查部门下是否有用户
     */
    @Override
    public boolean hasUsers(Long deptId) {
        return userRepository.countByDeptIdAndDeletedFalse(deptId) > 0;
    }
    
    /**
     * 检查部门下是否有子部门
     */
    @Override
    public boolean hasChildren(Long deptId) {
        return departmentRepository.existsByParentIdAndDeletedFalse(deptId);
    }
    
    /**
     * 构建部门树
     */
    private List<DepartmentTreeVO> buildDepartmentTree(List<Department> allDepartments, Long parentId) {
        List<DepartmentTreeVO> tree = new ArrayList<>();
        
        for (Department dept : allDepartments) {
            if (dept.getParentId().equals(parentId)) {
                DepartmentTreeVO node = convertToTreeVO(dept);
                node.setChildren(buildDepartmentTree(allDepartments, dept.getId()));
                tree.add(node);
            }
        }
        
        return tree;
    }
    
    /**
     * 检查目标部门是否是当前部门的子孙部门
     * @param currentDeptId 当前部门ID
     * @param targetParentId 目标父部门ID
     * @return true-是子孙部门，false-不是
     */
    private boolean isDescendant(Long currentDeptId, Long targetParentId) {
        // 如果目标父部门是0（根部门），则不存在循环
        if (targetParentId == 0) {
            return false;
        }
        
        // 获取所有子部门
        List<Department> children = departmentRepository.findByParentIdAndDeletedFalseOrderBySortAsc(currentDeptId);
        
        // 遍历所有子部门
        for (Department child : children) {
            // 如果子部门ID等于目标父部门ID，说明目标是当前部门的子部门
            if (child.getId().equals(targetParentId)) {
                return true;
            }
            // 递归检查子部门的子部门
            if (isDescendant(child.getId(), targetParentId)) {
                return true;
            }
        }
        
        return false;
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
     * 将Department实体转换为DepartmentDTO
     */
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .parentId(department.getParentId())
                .code(department.getCode())
                .sort(department.getSort())
                .leaderId(department.getLeaderId())
                .phone(department.getPhone())
                .email(department.getEmail())
                .status(department.getStatus())
                .createTime(department.getCreateTime())
                .updateTime(department.getUpdateTime())
                .remark(department.getRemark())
                .build();
        
        // 获取负责人姓名
        if (department.getLeaderId() != null) {
            userRepository.findByIdAndDeletedFalse(department.getLeaderId())
                    .ifPresent(user -> dto.setLeaderName(user.getRealName()));
        }
        
        return dto;
    }
    
    /**
     * 将Department实体转换为DepartmentTreeVO
     */
    private DepartmentTreeVO convertToTreeVO(Department department) {
        DepartmentTreeVO vo = DepartmentTreeVO.builder()
                .id(department.getId())
                .name(department.getName())
                .parentId(department.getParentId())
                .code(department.getCode())
                .sort(department.getSort())
                .leaderId(department.getLeaderId())
                .phone(department.getPhone())
                .email(department.getEmail())
                .status(department.getStatus())
                .remark(department.getRemark())
                .build();
        
        // 获取负责人姓名
        if (department.getLeaderId() != null) {
            userRepository.findByIdAndDeletedFalse(department.getLeaderId())
                    .ifPresent(user -> vo.setLeaderName(user.getRealName()));
        }
        
        return vo;
    }
}
