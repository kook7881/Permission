package com.zoyo.auth.service.impl;

import com.zoyo.auth.common.ResultCode;
import com.zoyo.auth.dto.LoginRequest;
import com.zoyo.auth.dto.LoginResponse;
import com.zoyo.auth.dto.RegisterRequest;
import com.zoyo.auth.entity.User;
import com.zoyo.auth.exception.BusinessException;
import com.zoyo.auth.repository.UserRepository;
import com.zoyo.auth.service.IAuthService;
import com.zoyo.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final com.zoyo.auth.util.PasswordUtil passwordUtil;
    private final com.zoyo.auth.repository.LoginLogRepository loginLogRepository;
    
    /**
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        // 创建用户
        // 注意：前端发送的密码已经是 MD5(password + salt) 加密后的
        // 后端再使用 BCrypt 进行二次加密存储
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRealName(request.getRealName());
        user.setStatus(1);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        
        userRepository.save(user);
        log.info("用户注册成功: {}", request.getUsername());
    }
    
    /**
     * 用户登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // 获取用户信息
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmail(username))
                    .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
            
            // 检查账号状态
            if (!user.getEnabled()) {
                throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
            }
            if (!user.getAccountNonLocked()) {
                throw new BusinessException(ResultCode.ACCOUNT_LOCKED);
            }
            
            // 生成JWT Token（包含userId）
            java.util.Map<String, Object> claims = new java.util.HashMap<>();
            claims.put("userId", user.getId());
            String token = jwtUtil.generateToken(user.getUsername(), claims);
            
            // 更新最后登录信息
            String clientIp = getClientIp(httpRequest);
            userRepository.updateLastLoginInfo(user.getId(), LocalDateTime.now(), clientIp);
            
            // 记录登录日志
            saveLoginLog(user.getId(), user.getUsername(), clientIp, httpRequest, true, "登录成功");
            
            // 构建响应
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .realName(user.getRealName())
                    .status(user.getStatus())
                    .build();
            
            log.info("用户登录成功: {}", request.getUsername());
            
            return LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userInfo(userInfo)
                    .build();
            
        } catch (org.springframework.security.authentication.DisabledException e) {
            log.error("账户已被禁用: {}", request.getUsername());
            
            // 记录登录失败日志
            String clientIp = getClientIp(httpRequest);
            saveLoginLog(null, request.getUsername(), clientIp, httpRequest, false, "账户已被禁用");
            
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
            
        } catch (org.springframework.security.authentication.LockedException e) {
            log.error("账户已被锁定: {}", request.getUsername());
            
            // 记录登录失败日志
            String clientIp = getClientIp(httpRequest);
            saveLoginLog(null, request.getUsername(), clientIp, httpRequest, false, "账户已被锁定");
            
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED);
            
        } catch (org.springframework.security.authentication.AccountExpiredException e) {
            log.error("账户已过期: {}", request.getUsername());
            
            // 记录登录失败日志
            String clientIp = getClientIp(httpRequest);
            saveLoginLog(null, request.getUsername(), clientIp, httpRequest, false, "账户已过期");
            
            throw new BusinessException(ResultCode.ACCOUNT_EXPIRED);
            
        } catch (org.springframework.security.authentication.CredentialsExpiredException e) {
            log.error("密码已过期: {}", request.getUsername());
            
            // 记录登录失败日志
            String clientIp = getClientIp(httpRequest);
            saveLoginLog(null, request.getUsername(), clientIp, httpRequest, false, "密码已过期");
            
            throw new BusinessException(ResultCode.CREDENTIALS_EXPIRED);
            
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.error("用户不存在: {}", request.getUsername());
            
            // 记录登录失败日志
            String clientIp = getClientIp(httpRequest);
            saveLoginLog(null, request.getUsername(), clientIp, httpRequest, false, "用户不存在");
            
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
            
        } catch (AuthenticationException e) {
            log.error("用户登录失败: {}", e.getMessage());
            
            // 记录登录失败日志
            String clientIp = getClientIp(httpRequest);
            saveLoginLog(null, request.getUsername(), clientIp, httpRequest, false, "用户名或密码错误");
            
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    /**
     * 保存登录日志
     */
    private void saveLoginLog(Long userId, String username, String ip, HttpServletRequest request, boolean success, String message) {
        try {
            com.zoyo.auth.entity.LoginLog loginLog = new com.zoyo.auth.entity.LoginLog();
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginIp(ip);
            loginLog.setLoginLocation(getLocationFromIp(ip));
            loginLog.setStatus(success ? 1 : 0);
            loginLog.setMessage(message);
            
            // 解析User-Agent获取浏览器和操作系统信息
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null) {
                loginLog.setBrowser(parseBrowser(userAgent));
                loginLog.setOs(parseOS(userAgent));
                loginLog.setDeviceType(parseDeviceType(userAgent));
            }
            
            loginLogRepository.save(loginLog);
        } catch (Exception e) {
            log.error("保存登录日志失败", e);
        }
    }
    
    /**
     * 从IP获取位置（简单模拟）
     */
    private String getLocationFromIp(String ip) {
        if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1")) {
            return "本地";
        }
        // 实际应该使用IP库查询
        return "未知";
    }
    
    /**
     * 解析浏览器
     */
    private String parseBrowser(String userAgent) {
        if (userAgent.contains("Edge")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Opera")) return "Opera";
        return "Unknown";
    }
    
    /**
     * 解析操作系统
     */
    private String parseOS(String userAgent) {
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "MacOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iOS") || userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        return "Unknown";
    }
    
    /**
     * 解析设备类型
     */
    private String parseDeviceType(String userAgent) {
        if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) {
            return "Mobile";
        }
        if (userAgent.contains("Tablet") || userAgent.contains("iPad")) {
            return "Tablet";
        }
        return "PC";
    }
    
    /**
     * 用户退出登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(HttpServletRequest httpRequest) {
        try {
            // 从请求头获取Token
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                
                // 从Token中获取用户ID
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    // 更新最后退出时间
                    userRepository.updateLastLogoutTime(userId, LocalDateTime.now());
                    
                    log.info("用户退出登录成功: userId={}", userId);
                }
            }
        } catch (Exception e) {
            log.error("处理退出登录失败", e);
            // 退出登录失败不抛出异常，避免影响用户体验
        }
    }
}
