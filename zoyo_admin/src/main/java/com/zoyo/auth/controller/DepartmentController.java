package com.zoyo.auth.controller;

import com.zoyo.auth.common.Result;
import com.zoyo.auth.dto.DepartmentDTO;
import com.zoyo.auth.dto.DepartmentTreeVO;
import com.zoyo.auth.service.IDepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final IDepartmentService departmentService;
    
    /**
     * 获取部门树
     * 注意：用户管理、角色管理等页面需要查询部门列表，所以这里允许多个权限
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAnyAuthority('system:dept:query', 'system:dept', 'system:user', 'system:user:view', 'system:role', 'system:role:view')")
    public Result<List<DepartmentTreeVO>> getDepartmentTree() {
        log.info("获取部门树");
        List<DepartmentTreeVO> tree = departmentService.getDepartmentTree();
        return Result.success(tree);
    }
    
    /**
     * 获取所有部门列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<List<DepartmentDTO>> getAllDepartments() {
        log.info("获取所有部门列表");
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }
    
    /**
     * 根据ID获取部门
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        log.info("获取部门: {}", id);
        DepartmentDTO department = departmentService.getDepartmentById(id);
        return Result.success(department);
    }
    
    /**
     * 创建部门
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:create')")
    public Result<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("创建部门: {}", departmentDTO.getName());
        DepartmentDTO department = departmentService.createDepartment(departmentDTO);
        return Result.success(department);
    }
    
    /**
     * 更新部门
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:update')")
    public Result<DepartmentDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("更新部门: {}", id);
        departmentDTO.setId(id);
        DepartmentDTO department = departmentService.updateDepartment(departmentDTO);
        return Result.success(department);
    }
    
    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        log.info("删除部门: {}", id);
        departmentService.deleteDepartment(id);
        return Result.success();
    }
    
    /**
     * 获取子部门列表
     */
    @GetMapping("/{parentId}/children")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<List<DepartmentDTO>> getChildDepartments(@PathVariable Long parentId) {
        log.info("获取子部门列表: {}", parentId);
        List<DepartmentDTO> departments = departmentService.getChildDepartments(parentId);
        return Result.success(departments);
    }
    
    /**
     * 检查部门下是否有用户
     */
    @GetMapping("/{id}/has-users")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<Boolean> hasUsers(@PathVariable Long id) {
        boolean hasUsers = departmentService.hasUsers(id);
        return Result.success(hasUsers);
    }
    
    /**
     * 检查部门下是否有子部门
     */
    @GetMapping("/{id}/has-children")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Result<Boolean> hasChildren(@PathVariable Long id) {
        boolean hasChildren = departmentService.hasChildren(id);
        return Result.success(hasChildren);
    }
}
