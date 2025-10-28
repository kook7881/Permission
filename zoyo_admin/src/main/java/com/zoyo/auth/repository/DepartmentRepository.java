package com.zoyo.auth.repository;

import com.zoyo.auth.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门数据访问接口
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    /**
     * 查询所有未删除的部门
     */
    List<Department> findByDeletedFalseOrderBySortAsc();
    
    /**
     * 根据父部门ID查询子部门
     */
    List<Department> findByParentIdAndDeletedFalseOrderBySortAsc(Long parentId);
    
    /**
     * 根据ID查询未删除的部门
     */
    Optional<Department> findByIdAndDeletedFalse(Long id);
    
    /**
     * 根据部门名称查询（排除指定ID）
     */
    Optional<Department> findByNameAndDeletedFalseAndIdNot(String name, Long id);
    
    /**
     * 根据部门名称查询
     */
    Optional<Department> findByNameAndDeletedFalse(String name);
    
    /**
     * 检查部门下是否有子部门
     */
    boolean existsByParentIdAndDeletedFalse(Long parentId);
    
    /**
     * 根据状态查询部门
     */
    List<Department> findByStatusAndDeletedFalseOrderBySortAsc(Integer status);
    
    /**
     * 查询部门树（递归查询所有层级）
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = false ORDER BY d.sort ASC")
    List<Department> findAllTree();
}
