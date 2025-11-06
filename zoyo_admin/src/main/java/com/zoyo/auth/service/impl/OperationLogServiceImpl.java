package com.zoyo.auth.service.impl;

import com.zoyo.auth.entity.OperationLog;
import com.zoyo.auth.exception.BusinessException;
import com.zoyo.auth.repository.OperationLogRepository;
import com.zoyo.auth.service.IOperationLogService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements IOperationLogService {
    
    private final OperationLogRepository operationLogRepository;
    
    @Override
    @Transactional
    public void saveOperationLog(OperationLog operationLog) {
        try {
            operationLogRepository.save(operationLog);
            log.debug("保存操作日志成功: {}", operationLog.getDescription());
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
            // 不抛出异常，避免影响业务操作
        }
    }
    
    @Override
    public Page<OperationLog> queryOperationLogs(
            String username,
            String module,
            Integer businessType,
            Integer status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable) {
        
        Specification<OperationLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 用户名模糊查询
            if (username != null && !username.isEmpty()) {
                predicates.add(cb.like(root.get("username"), "%" + username + "%"));
            }
            
            // 模块精确查询
            if (module != null && !module.isEmpty()) {
                predicates.add(cb.equal(root.get("module"), module));
            }
            
            // 业务类型精确查询
            if (businessType != null) {
                predicates.add(cb.equal(root.get("businessType"), businessType));
            }
            
            // 状态精确查询
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            // 时间范围查询
            if (startTime != null && endTime != null) {
                predicates.add(cb.between(root.get("operationTime"), startTime, endTime));
            } else if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("operationTime"), startTime));
            } else if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("operationTime"), endTime));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return operationLogRepository.findAll(spec, pageable);
    }
    
    @Override
    public OperationLog getOperationLogById(Long id) {
        return operationLogRepository.findById(id)
                .orElseThrow(() -> new BusinessException("操作日志不存在"));
    }
    
    @Override
    public List<OperationLog> getOperationLogsByUserId(Long userId) {
        return operationLogRepository.findByUserIdOrderByOperationTimeDesc(userId);
    }
    
    @Override
    public List<OperationLog> getOperationLogsByUsername(String username) {
        return operationLogRepository.findByUsernameOrderByOperationTimeDesc(username);
    }
    
    @Override
    @Transactional
    public void deleteOperationLog(Long id) {
        if (!operationLogRepository.existsById(id)) {
            throw new BusinessException("操作日志不存在");
        }
        operationLogRepository.deleteById(id);
        log.info("删除操作日志成功: {}", id);
    }
    
    @Override
    @Transactional
    public void batchDeleteOperationLogs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("删除ID列表不能为空");
        }
        operationLogRepository.deleteByIdIn(ids);
        log.info("批量删除操作日志成功: {}", ids.size());
    }
    
    @Override
    @Transactional
    public void clearOperationLogs() {
        operationLogRepository.deleteAll();
        log.warn("清空所有操作日志");
    }
    
    @Override
    @Transactional
    public void cleanOperationLogs(LocalDateTime beforeTime) {
        operationLogRepository.deleteByOperationTimeBefore(beforeTime);
        log.info("清理 {} 之前的操作日志", beforeTime);
    }
    
    @Override
    public Long countUserOperations(Long userId) {
        return operationLogRepository.countByUserId(userId);
    }
    
    @Override
    public Long countModuleOperations(String module) {
        return operationLogRepository.countByModule(module);
    }
}
