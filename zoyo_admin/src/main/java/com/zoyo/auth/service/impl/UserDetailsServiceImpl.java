package com.zoyo.auth.service.impl;

import com.zoyo.auth.entity.User;
import com.zoyo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security用户详情服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户(支持用户名或邮箱登录)
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        // 加载用户的角色和权限
        List<SimpleGrantedAuthority> authorities = loadUserAuthorities(user.getId());
        
        // 构建Spring Security的UserDetails对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(!user.getAccountNonExpired())
                .accountLocked(!user.getAccountNonLocked())
                .credentialsExpired(!user.getCredentialsNonExpired())
                .disabled(!user.getEnabled())
                .build();
    }
    
    /**
     * 加载用户的角色和权限
     */
    private List<SimpleGrantedAuthority> loadUserAuthorities(Long userId) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 查询用户的角色
        String roleSql = """
            SELECT DISTINCT r.role_code
            FROM sys_role r
            INNER JOIN sys_user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = ? AND r.status = 1
            """;
        
        List<String> roles = jdbcTemplate.queryForList(roleSql, String.class, userId);
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        
        // 查询用户的权限
        String permissionSql = """
            SELECT DISTINCT p.permission_code
            FROM sys_permission p
            INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
            INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
            WHERE ur.user_id = ? AND p.status = 1
            """;
        
        List<String> permissions = jdbcTemplate.queryForList(permissionSql, String.class, userId);
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        
        // 如果没有任何权限，至少给一个基础角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.warn("用户 {} 没有配置任何角色和权限，使用默认 ROLE_USER", userId);
        }
        
        return authorities;
    }
}
