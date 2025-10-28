package com.zoyo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 监控大屏统计数据DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    
    /**
     * 总用户数
     */
    private Long totalUsers;
    
    /**
     * 今日登录用户数
     */
    private Long todayLoginUsers;
    
    /**
     * 今日注册用户数
     */
    private Long todayRegisterUsers;
    
    /**
     * 在线用户数（最近30分钟登录）
     */
    private Long onlineUsers;
    
    /**
     * 用户增长趋势（按天统计）
     */
    private TrendData userTrend;
    
    /**
     * 登录趋势（最近7天）
     */
    private TrendData loginTrend;
    
    /**
     * 注册趋势（最近30天）
     */
    private TrendData registerTrend;
    
    /**
     * 24小时活跃度分布
     */
    private List<HourlyData> hourlyActivity;
    
    /**
     * 城市分布数据
     */
    private List<CityData> cityDistribution;
    
    /**
     * 最近登录记录
     */
    private List<LoginRecord> recentLogins;
    
    /**
     * 趋势数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private List<String> labels;
        private List<Long> data;
    }
    
    /**
     * 小时数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyData {
        private Integer hour;
        private Long count;
    }
    
    /**
     * 城市数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CityData {
        private String name;
        private Double longitude;
        private Double latitude;
        private Long userCount;
    }
    
    /**
     * 登录记录
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRecord {
        private String username;
        private String realName;
        private String loginTime;
        private String ipAddress;
        private String location;
        private String status;
        private Boolean onlineStatus;  // 在线状态
    }
}
