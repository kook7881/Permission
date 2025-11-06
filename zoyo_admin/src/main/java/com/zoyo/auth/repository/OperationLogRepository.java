package com.zoyo.auth.repository;

import com.zoyo.auth.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Repository
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long>, 
                                               JpaSpecificationExecutor<OperationLog> {
    
    /**
     * 根据用户ID查询操作日志
     */
    List<OperationLog> findByUserIdOrderByOperationTimeDesc(Long userId);
    
    /**
     * 根据用户名查询操作日志
     */
    List<OperationLog> findByUsernameOrderByOperationTimeDesc(String username);
    
    /**
     * 根据业务类型查询操作日志
     */
    List<OperationLog> findByBusinessTypeOrderByOperationTimeDesc(Integer businessType);
    
    /**
     * 根据状态查询操作日志
     */
    List<OperationLog> findByStatusOrderByOperationTimeDesc(Integer status);
    
    /**
     * 根据时间范围查询操作日志
     */
    List<OperationLog> findByOperationTimeBetweenOrderByOperationTimeDesc(
            LocalDateTime startTime, 
            LocalDateTime endTime
    );
    
    /**
     * 统计用户操作次数
     */
    @Query("SELECT COUNT(ol) FROM OperationLog ol WHERE ol.userId = :userId")
    Long countByUserId(Long userId);
    
    /**
     * 统计模块操作次数
     */
    @Query("SELECT COUNT(ol) FROM OperationLog ol WHERE ol.module = :module")
    Long countByModule(String module);
    
    /**
     * 删除指定时间之前的日志
     */
    @Modifying
    @Query("DELETE FROM OperationLog ol WHERE ol.operationTime < :beforeTime")
    void deleteByOperationTimeBefore(LocalDateTime beforeTime);
    
    /**
     * 批量删除
     */
    void deleteByIdIn(List<Long> ids);
}
