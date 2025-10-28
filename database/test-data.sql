-- ============================================
-- Zoyo 认证系统 - 测试数据
-- ============================================
-- 注意：仅用于开发和测试环境，生产环境请勿使用
-- 所有测试用户密码：123456
-- ============================================

USE `zoyo_auth`;

-- ============================================
-- 1. 部门测试数据
-- ============================================

INSERT INTO sys_department (name, parent_id, code, sort, leader_id, phone, email, status, create_time, update_time, create_by, update_by, deleted, remark)
VALUES
-- 顶级部门
('总公司', 0, 'HQ', 0, NULL, '010-12345678', 'hq@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '总公司'),

-- 一级部门
('技术部', 1, 'TECH', 1, NULL, '010-12345679', 'tech@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '技术研发部门'),
('市场部', 1, 'MARKET', 2, NULL, '010-12345680', 'market@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '市场营销部门'),
('人力资源部', 1, 'HR', 3, NULL, '010-12345681', 'hr@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '人力资源部门'),
('财务部', 1, 'FINANCE', 4, NULL, '010-12345682', 'finance@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '财务管理部门'),

-- 二级部门（技术部下）
('前端开发组', 2, 'TECH-FE', 1, NULL, '010-12345683', 'fe@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '前端开发团队'),
('后端开发组', 2, 'TECH-BE', 2, NULL, '010-12345684', 'be@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '后端开发团队'),
('测试组', 2, 'TECH-QA', 3, NULL, '010-12345685', 'qa@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '质量保证团队'),
('运维组', 2, 'TECH-OPS', 4, NULL, '010-12345686', 'ops@zoyo.com', 1, NOW(), NOW(), 1, 1, 0, '运维团队')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ============================================
-- 2. 测试用户数据
-- ============================================

INSERT INTO sys_user (username, password, email, phone, real_name, dept_id, position, gender, status, account_non_expired, account_non_locked, credentials_non_expired, enabled, create_time, update_time, create_by, update_by, deleted, remark)
VALUES
-- 技术部用户
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'zhangsan@zoyo.com', '13800138001', '张三', 6, '前端工程师', 1, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '前端开发工程师'),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'lisi@zoyo.com', '13800138002', '李四', 7, '后端工程师', 1, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '后端开发工程师'),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'wangwu@zoyo.com', '13800138003', '王五', 8, '测试工程师', 2, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '测试工程师'),
('zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'zhaoliu@zoyo.com', '13800138004', '赵六', 9, '运维工程师', 1, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '运维工程师'),

-- 市场部用户
('sunqi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'sunqi@zoyo.com', '13800138005', '孙七', 3, '市场专员', 2, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '市场推广专员'),
('zhouba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'zhouba@zoyo.com', '13800138006', '周八', 3, '销售经理', 1, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '销售经理'),

-- 人力资源部用户
('wujiu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'wujiu@zoyo.com', '13800138007', '吴九', 4, 'HR专员', 2, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '人力资源专员'),

-- 财务部用户
('zhengshi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'zhengshi@zoyo.com', '13800138008', '郑十', 5, '会计', 2, 1, 1, 1, 1, 1, NOW(), NOW(), 1, 1, 0, '财务会计')
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- ============================================
-- 3. 创建普通用户角色
-- ============================================

INSERT INTO sys_role (role_name, role_code, role_sort, data_scope, status, is_system, create_time, update_time, remark)
VALUES ('普通用户', 'USER', 3, 3, 1, 0, NOW(), NOW(), '普通用户角色，拥有基本权限')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- ============================================
-- 4. 为测试用户分配角色
-- ============================================

-- 获取普通用户角色ID
SET @user_role_id = (SELECT id FROM sys_role WHERE role_code = 'USER' LIMIT 1);

-- 为测试用户分配普通用户角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, @user_role_id
FROM sys_user u
WHERE u.username IN ('zhangsan', 'lisi', 'wangwu', 'zhaoliu', 'sunqi', 'zhouba', 'wujiu', 'zhengshi')
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- ============================================
-- 5. 模拟历史登录数据（用于监控大屏）
-- ============================================

-- 插入登录日志
INSERT INTO sys_login_log (user_id, username, login_time, login_ip, login_location, browser, os, device_type, status, message)
SELECT 
    u.id,
    u.username,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    CONCAT('192.168.1.', FLOOR(1 + RAND() * 254)),
    CASE FLOOR(RAND() * 8)
        WHEN 0 THEN '北京'
        WHEN 1 THEN '上海'
        WHEN 2 THEN '广州'
        WHEN 3 THEN '深圳'
        WHEN 4 THEN '杭州'
        WHEN 5 THEN '南京'
        WHEN 6 THEN '成都'
        ELSE '武汉'
    END,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'Chrome'
        WHEN 1 THEN 'Firefox'
        ELSE 'Edge'
    END,
    CASE FLOOR(RAND() * 3)
        WHEN 0 THEN 'Windows'
        WHEN 1 THEN 'MacOS'
        ELSE 'Linux'
    END,
    'PC',
    1,
    '登录成功'
FROM sys_user u
WHERE u.username IN ('zhangsan', 'lisi', 'wangwu', 'zhaoliu', 'sunqi', 'zhouba', 'wujiu', 'zhengshi');

-- ============================================
-- 6. 验证测试数据
-- ============================================

SELECT '✅ 测试数据统计' AS '';

SELECT 
    '部门数量' as '统计项', 
    COUNT(*) as '数量' 
FROM sys_department 
WHERE deleted = 0
UNION ALL
SELECT 
    '测试用户数量', 
    COUNT(*) 
FROM sys_user 
WHERE username LIKE 'test%' OR username IN ('zhangsan', 'lisi', 'wangwu', 'zhaoliu', 'sunqi', 'zhouba', 'wujiu', 'zhengshi')
UNION ALL
SELECT 
    '登录日志数量', 
    COUNT(*) 
FROM sys_login_log;

SELECT '' AS '', '📋 测试用户列表' AS '';

SELECT 
    username AS '用户名',
    real_name AS '真实姓名',
    email AS '邮箱',
    '123456' AS '密码'
FROM sys_user
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'zhaoliu', 'sunqi', 'zhouba', 'wujiu', 'zhengshi');
