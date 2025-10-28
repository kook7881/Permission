package com.zoyo.auth.repository;

import com.zoyo.auth.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 登录日志数据访问层
 */
@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    
    /**
     * 查询最近的登录记录（前10条）
     */
    List<LoginLog> findTop10ByOrderByLoginTimeDesc();
}
