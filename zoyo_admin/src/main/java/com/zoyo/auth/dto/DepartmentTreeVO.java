package com.zoyo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 部门树形结构视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentTreeVO {
    
    /**
     * 部门ID
     */
    private Long id;
    
    /**
     * 部门名称
     */
    private String name;
    
    /**
     * 父部门ID
     */
    private Long parentId;
    
    /**
     * 部门编码
     */
    private String code;
    
    /**
     * 显示顺序
     */
    private Integer sort;
    
    /**
     * 负责人ID
     */
    private Long leaderId;
    
    /**
     * 负责人姓名
     */
    private String leaderName;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 部门状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 子部门列表
     */
    private List<DepartmentTreeVO> children;
    
    /**
     * 备注
     */
    private String remark;
}
