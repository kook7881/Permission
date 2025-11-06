package com.zoyo.auth.service.impl;

import com.zoyo.auth.dto.DashboardStatsDTO;
import com.zoyo.auth.repository.LoginLogRepository;
import com.zoyo.auth.repository.UserRepository;
import com.zoyo.auth.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 监控大屏服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements IDashboardService {
    
    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    
    /**
     * 获取监控大屏统计数据
     */
    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // 总用户数
        stats.setTotalUsers(userRepository.count());
        
        // 今日登录用户数（今天有登录记录的用户）
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        stats.setTodayLoginUsers(userRepository.countByLastLoginTimeAfter(todayStart));
        
        // 今日注册用户数
        stats.setTodayRegisterUsers(userRepository.countByCreateTimeAfter(todayStart));
        
        // 在线用户数（最近30分钟登录）
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        stats.setOnlineUsers(userRepository.countByLastLoginTimeAfter(thirtyMinutesAgo));
        
        // 登录趋势（最近7天）
        stats.setLoginTrend(getLoginTrend(7));
        
        // 注册趋势（最近30天）
        stats.setRegisterTrend(getRegisterTrend(30));
        
        // 24小时活跃度分布
        stats.setHourlyActivity(getHourlyActivity());
        
        // 城市分布数据（模拟数据，实际应从IP解析）
        stats.setCityDistribution(getCityDistribution());
        
        // 最近登录记录
        stats.setRecentLogins(getRecentLogins(10));
        
        return stats;
    }
    
    /**
     * 获取登录趋势
     */
    private DashboardStatsDTO.TrendData getLoginTrend(int days) {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            labels.add(date.format(formatter));
            Long count = userRepository.countByLastLoginTimeBetween(startOfDay, endOfDay);
            data.add(count);
        }
        
        return new DashboardStatsDTO.TrendData(labels, data);
    }
    
    /**
     * 获取注册趋势
     */
    private DashboardStatsDTO.TrendData getRegisterTrend(int days) {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            labels.add(date.format(formatter));
            Long count = userRepository.countByCreateTimeBetween(startOfDay, endOfDay);
            data.add(count);
        }
        
        return new DashboardStatsDTO.TrendData(labels, data);
    }
    
    /**
     * 获取24小时活跃度分布
     */
    private List<DashboardStatsDTO.HourlyData> getHourlyActivity() {
        List<DashboardStatsDTO.HourlyData> hourlyData = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        
        // 获取最近24小时的登录记录
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime hourStart = oneDayAgo.withHour(hour).withMinute(0).withSecond(0);
            LocalDateTime hourEnd = hourStart.plusHours(1);
            
            Long count = userRepository.countByLastLoginTimeBetween(hourStart, hourEnd);
            hourlyData.add(new DashboardStatsDTO.HourlyData(hour, count));
        }
        
        return hourlyData;
    }
    
    /**
     * 获取城市分布数据
     * 注意：这里使用模拟数据，实际应该从lastLoginIp解析城市信息
     */
    private List<DashboardStatsDTO.CityData> getCityDistribution() {
        // 主要城市坐标
        Map<String, double[]> cityCoordinates = new HashMap<>();
        cityCoordinates.put("北京", new double[]{116.4074, 39.9042});
        cityCoordinates.put("上海", new double[]{121.4737, 31.2304});
        cityCoordinates.put("广州", new double[]{113.2644, 23.1291});
        cityCoordinates.put("深圳", new double[]{114.0579, 22.5431});
        cityCoordinates.put("杭州", new double[]{120.1551, 30.2741});
        cityCoordinates.put("南京", new double[]{118.7969, 32.0603});
        cityCoordinates.put("成都", new double[]{104.0668, 30.5728});
        cityCoordinates.put("武汉", new double[]{114.3055, 30.5931});
        cityCoordinates.put("西安", new double[]{108.9398, 34.3416});
        cityCoordinates.put("重庆", new double[]{106.5516, 29.5630});
        cityCoordinates.put("天津", new double[]{117.2010, 39.0842});
        cityCoordinates.put("苏州", new double[]{120.5954, 31.2989});
        cityCoordinates.put("郑州", new double[]{113.6254, 34.7466});
        cityCoordinates.put("长沙", new double[]{112.9388, 28.2282});
        cityCoordinates.put("沈阳", new double[]{123.4328, 41.8045});
        cityCoordinates.put("青岛", new double[]{120.3826, 36.0671});
        cityCoordinates.put("济南", new double[]{117.1205, 36.6519});
        cityCoordinates.put("大连", new double[]{121.6147, 38.9140});
        cityCoordinates.put("厦门", new double[]{118.0894, 24.4798});
        cityCoordinates.put("福州", new double[]{119.2965, 26.0745});
        
        List<DashboardStatsDTO.CityData> cityData = new ArrayList<>();
        
        // 简单模拟：根据用户总数按比例分配到各城市
        Long totalUsers = userRepository.count();
        Random random = new Random(42); // 固定种子保证数据一致性
        
        for (Map.Entry<String, double[]> entry : cityCoordinates.entrySet()) {
            String cityName = entry.getKey();
            double[] coords = entry.getValue();
            
            // 模拟用户数（实际应该从IP解析）
            Long userCount = (long) (totalUsers * (0.02 + random.nextDouble() * 0.08));
            
            cityData.add(new DashboardStatsDTO.CityData(
                cityName,
                coords[0],
                coords[1],
                userCount
            ));
        }
        
        // 按用户数降序排序
        cityData.sort((a, b) -> Long.compare(b.getUserCount(), a.getUserCount()));
        
        return cityData;
    }
    
    /**
     * 获取最近登录记录
     */
    private List<DashboardStatsDTO.LoginRecord> getRecentLogins(int limit) {
        List<DashboardStatsDTO.LoginRecord> loginRecords = new ArrayList<>();
        
        // 从登录日志表查询最近的登录记录
        var loginLogs = loginLogRepository.findTop10ByOrderByLoginTimeDesc();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);
        
        for (var loginLog : loginLogs) {
            String loginTime = loginLog.getLoginTime() != null 
                ? loginLog.getLoginTime().format(formatter) 
                : "-";
            
            String ipAddress = loginLog.getLoginIp() != null 
                ? loginLog.getLoginIp() 
                : "-";
            
            String location = loginLog.getLoginLocation() != null && !loginLog.getLoginLocation().isEmpty()
                ? loginLog.getLoginLocation()
                : getLocationFromIp(ipAddress);
            
            String status = loginLog.getStatus() == 1 ? "成功" : "失败";
            
            // 查询用户信息（真实姓名和在线状态）
            String realName = loginLog.getUsername();
            Boolean onlineStatus = false;
            
            try {
                var userOpt = userRepository.findByUsername(loginLog.getUsername());
                if (userOpt.isPresent()) {
                    var user = userOpt.get();
                    
                    // 设置真实姓名
                    if (user.getRealName() != null) {
                        realName = user.getRealName();
                    }
                    
                    // 计算在线状态：
                    // 只有当这条登录记录是用户的最后一次登录时，才根据时间判断在线状态
                    if (loginLog.getLoginTime() != null 
                        && user.getLastLoginTime() != null 
                        && user.getStatus() == 1) {
                        
                        // 判断这条登录记录是否是最后一次登录（时间差在5秒内认为是同一次）
                        long secondsDiff = Math.abs(
                            java.time.Duration.between(loginLog.getLoginTime(), user.getLastLoginTime()).getSeconds()
                        );
                        boolean isLastLogin = secondsDiff < 5;
                        
                        // 只有最后一次登录才判断在线状态
                        if (isLastLogin) {
                            boolean loginRecent = user.getLastLoginTime().isAfter(thirtyMinutesAgo);
                            boolean hasLoggedOut = user.getLastLogoutTime() != null 
                                && user.getLastLogoutTime().isAfter(user.getLastLoginTime());
                            onlineStatus = loginRecent && !hasLoggedOut;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("查询用户信息失败: {}", loginLog.getUsername(), e);
            }
            
            loginRecords.add(new DashboardStatsDTO.LoginRecord(
                loginLog.getUsername(),
                realName,
                loginTime,
                ipAddress,
                location,
                status,
                onlineStatus
            ));
        }
        
        return loginRecords;
    }
    
    /**
     * 从IP地址获取位置信息（简单模拟）
     */
    private String getLocationFromIp(String ip) {
        if (ip == null || ip.equals("-") || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1")) {
            return "本地";
        }
        
        // 实际应该使用IP库查询，这里简单模拟
        String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "武汉"};
        int index = Math.abs(ip.hashCode()) % cities.length;
        return cities[index];
    }
}
