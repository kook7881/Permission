package com.zoyo.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * IP工具类
 */
@Slf4j
public class IpUtil {
    
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IP = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final int IP_MAX_LENGTH = 15;
    
    /**
     * 获取客户端真实IP地址
     * 
     * @param request HttpServletRequest
     * @return IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        
        String ip = null;
        
        // 1. 尝试从X-Forwarded-For获取(经过代理)
        ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // X-Forwarded-For可能包含多个IP,取第一个
            int index = ip.indexOf(',');
            if (index != -1) {
                ip = ip.substring(0, index);
            }
            return ip.trim();
        }
        
        // 2. 尝试从X-Real-IP获取(Nginx代理)
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }
        
        // 3. 尝试从Proxy-Client-IP获取(Apache代理)
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }
        
        // 4. 尝试从WL-Proxy-Client-IP获取(WebLogic代理)
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }
        
        // 5. 尝试从HTTP_CLIENT_IP获取
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }
        
        // 6. 尝试从HTTP_X_FORWARDED_FOR获取
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return ip.trim();
        }
        
        // 7. 最后从RemoteAddr获取
        ip = request.getRemoteAddr();
        
        // 处理本地回环地址
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IP;
        }
        
        return ip != null ? ip.trim() : UNKNOWN;
    }
    
    /**
     * 验证IP是否有效
     * 
     * @param ip IP地址
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && 
               !ip.isEmpty() && 
               !UNKNOWN.equalsIgnoreCase(ip);
    }
    
    /**
     * 根据IP地址获取地理位置
     * 注意: 这是一个简化实现,实际项目中应该集成第三方IP地址库或API
     * 
     * @param ip IP地址
     * @return 地理位置
     */
    public static String getLocationByIp(String ip) {
        if (ip == null || ip.isEmpty() || UNKNOWN.equals(ip)) {
            return "未知";
        }
        
        // 本地IP
        if (isLocalIp(ip)) {
            return "内网IP";
        }
        
        // TODO: 实际项目中应该集成IP地址库
        // 可选方案:
        // 1. 离线IP库: ip2region (推荐,速度快)
        // 2. 在线API: 高德地图API、百度地图API、淘宝IP库等
        // 3. MaxMind GeoIP2数据库
        
        try {
            // 这里返回默认值,实际应该调用IP地址解析服务
            return "未知位置";
        } catch (Exception e) {
            log.error("解析IP地址位置失败: {}", ip, e);
            return "解析失败";
        }
    }
    
    /**
     * 判断是否为本地IP
     * 
     * @param ip IP地址
     * @return 是否为本地IP
     */
    private static boolean isLocalIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        // 127.x.x.x
        if (ip.startsWith("127.")) {
            return true;
        }
        
        // 10.x.x.x
        if (ip.startsWith("10.")) {
            return true;
        }
        
        // 172.16.x.x - 172.31.x.x
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                try {
                    int second = Integer.parseInt(parts[1]);
                    if (second >= 16 && second <= 31) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
        
        // 192.168.x.x
        if (ip.startsWith("192.168.")) {
            return true;
        }
        
        // localhost IPv6
        if (LOCALHOST_IPV6.equals(ip) || "::1".equals(ip)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 判断是否为IPv4地址
     * 
     * @param ip IP地址
     * @return 是否为IPv4
     */
    public static boolean isIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
