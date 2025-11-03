package com.zoyo.auth.service.impl;

import com.zoyo.auth.entity.LoginLog;
import com.zoyo.auth.repository.LoginLogRepository;
import com.zoyo.auth.service.ILoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements ILoginLogService {
    
    private final LoginLogRepository loginLogRepository;
    
    /**
     * 分页查询登录日志
     */
    @Override
    public Page<LoginLog> queryLoginLogs(String username, Integer status, Pageable pageable) {
        log.debug("分页查询登录日志: username={}, status={}", username, status);
        
        Specification<LoginLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 用户名模糊查询
            if (username != null && !username.trim().isEmpty()) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            
            // 状态查询
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return loginLogRepository.findAll(spec, pageable);
    }
    
    /**
     * 根据ID获取登录日志
     */
    @Override
    public LoginLog getLoginLogById(Long id) {
        log.debug("获取登录日志: {}", id);
        return loginLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("登录日志不存在"));
    }
    
    /**
     * 删除登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLoginLog(Long id) {
        log.info("删除登录日志: {}", id);
        loginLogRepository.deleteById(id);
    }
    
    /**
     * 批量删除登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLoginLogs(List<Long> ids) {
        log.info("批量删除登录日志: {}", ids);
        loginLogRepository.deleteAllById(ids);
    }
    
    /**
     * 清空登录日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearLoginLogs() {
        log.warn("清空所有登录日志");
        loginLogRepository.deleteAll();
    }
}
