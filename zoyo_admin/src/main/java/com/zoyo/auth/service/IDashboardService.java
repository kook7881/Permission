package com.zoyo.auth.service;

import com.zoyo.auth.dto.DashboardStatsDTO;

/**
 * 监控大屏服务接口
 */
public interface IDashboardService {
    
    /**
     * 获取监控大屏统计数据
     * @return 监控大屏统计数据
     */
    DashboardStatsDTO getDashboardStats();
}
