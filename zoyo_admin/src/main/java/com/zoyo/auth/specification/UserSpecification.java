package com.zoyo.auth.specification;

import com.zoyo.auth.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户动态查询规范
 */
public class UserSpecification {
    
    /**
     * 构建用户查询条件
     * 
     * @param username 用户名（模糊查询）
     * @param email 邮箱（模糊查询）
     * @param phone 手机号（模糊查询）
     * @param status 状态
     * @param deptId 部门ID
     * @param realName 真实姓名（模糊查询）
     * @return Specification
     */
    public static Specification<User> buildSpecification(
            String username,
            String email,
            String phone,
            Integer status,
            Long deptId,
            String realName) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 默认只查询未删除的用户
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            
            // 用户名模糊查询
            if (username != null && !username.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("username"), 
                    "%" + username.trim() + "%"
                ));
            }
            
            // 邮箱模糊查询
            if (email != null && !email.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("email"), 
                    "%" + email.trim() + "%"
                ));
            }
            
            // 手机号模糊查询
            if (phone != null && !phone.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("phone"), 
                    "%" + phone.trim() + "%"
                ));
            }
            
            // 真实姓名模糊查询
            if (realName != null && !realName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("realName"), 
                    "%" + realName.trim() + "%"
                ));
            }
            
            // 状态精确查询
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // 部门ID精确查询
            if (deptId != null) {
                predicates.add(criteriaBuilder.equal(root.get("deptId"), deptId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 查询所有未删除的用户
     */
    public static Specification<User> notDeleted() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("deleted"), false);
    }
    
    /**
     * 根据部门ID查询用户
     */
    public static Specification<User> byDeptId(Long deptId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("deptId"), deptId));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 根据状态查询用户
     */
    public static Specification<User> byStatus(Integer status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
