package com.zoyo.auth.controller;

import com.zoyo.auth.common.Result;
import com.zoyo.auth.dto.DashboardStatsDTO;
import com.zoyo.auth.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 监控大屏控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    
    private final IDashboardService dashboardService;
    
    /**
     * 获取监控大屏统计数据
     */
    @GetMapping("/stats")
    public Result<DashboardStatsDTO> getDashboardStats() {
        log.info("获取监控大屏统计数据");
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return Result.success(stats);
    }
}
