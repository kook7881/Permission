-- ============================================
-- Zoyo 认证系统 - 初始化数据
-- ============================================
-- 数据库: zoyo_auth
-- 说明: 包含超级管理员账号和菜单权限数据
-- ============================================

USE `zoyo_auth`;

-- ============================================
-- 1. 创建超级管理员账号
-- ============================================
-- 用户名: admin
-- 密码: admin123
-- 注意: 密码已经过前端MD5+后端BCrypt双重加密
-- ============================================

INSERT INTO sys_user (
    username,
    password,
    email,
    phone,
    real_name,
    dept_id,
    position,
    avatar,
    gender,
    status,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    enabled,
    last_login_time,
    last_login_ip,
    create_time,
    update_time,
    create_by,
    update_by,
    deleted,
    delete_time,
    remark
)
VALUES (
    'admin',                                                                    -- 用户名
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',          -- 密码: admin123
    'admin@zoyo.com',                                                           -- 邮箱
    '13800138000',                                                              -- 手机号
    '超级管理员',                                                                -- 真实姓名
    NULL,                                                                       -- 部门ID
    '系统管理员',                                                                -- 职位
    NULL,                                                                       -- 头像
    0,                                                                          -- 性别: 0-未知
    1,                                                                          -- 状态: 1-正常
    TRUE,                                                                       -- 账号未过期
    TRUE,                                                                       -- 账号未锁定
    TRUE,                                                                       -- 密码未过期
    TRUE,                                                                       -- 账号已启用
    NULL,                                                                       -- 最后登录时间
    NULL,                                                                       -- 最后登录IP
    NOW(),                                                                      -- 创建时间
    NOW(),                                                                      -- 更新时间
    NULL,                                                                       -- 创建人ID
    NULL,                                                                       -- 更新人ID
    FALSE,                                                                      -- 未删除
    NULL,                                                                       -- 删除时间
    '系统初始化创建的超级管理员账号'                                              -- 备注
)
ON DUPLICATE KEY UPDATE 
    password = '$2a$10$1j9UGhWZOrD/fKtuS7J/FeVyCuW28ino0rK6OCj2bYPz.5E8tmSVC',
    email = 'admin@zoyo.com',
    phone = '13800138000',
    real_name = '超级管理员',
    position = '系统管理员',
    status = 1,
    account_non_expired = TRUE,
    account_non_locked = TRUE,
    credentials_non_expired = TRUE,
    enabled = TRUE,
    deleted = FALSE,
    update_time = NOW(),
    remark = '系统初始化创建的超级管理员账号';

-- ============================================
-- 2. 初始化菜单数据
-- ============================================
-- 清空现有菜单数据（可选，如果需要重新初始化请取消注释）
-- DELETE FROM sys_permission WHERE permission_type = 1;
-- DELETE FROM sys_role_permission WHERE permission_id IN (SELECT id FROM sys_permission WHERE permission_type = 1);

-- 插入一级菜单
INSERT INTO sys_permission (parent_id, permission_name, permission_code, permission_type, route_path, component_path, icon, sort_order, visible, status, remark, create_time, update_time) VALUES
(0, '监控大屏', 'dashboard:view', 1, '/dashboard', 'Dashboard', 'HomeFilled', 1, 1, 1, '监控大屏菜单', NOW(), NOW()),
(0, '系统管理', 'system:manage', 1, '/system', NULL, 'Setting', 2, 1, 1, '系统管理菜单', NOW(), NOW()),
(0, '个人中心', 'profile:view', 1, '/profile', 'Profile', 'User', 3, 1, 1, '个人中心菜单', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    permission_name = VALUES(permission_name),
    update_time = NOW();

-- 插入系统管理的子菜单
SET @system_menu_id = (SELECT id FROM sys_permission WHERE permission_code = 'system:manage' LIMIT 1);

INSERT INTO sys_permission (parent_id, permission_name, permission_code, permission_type, route_path, component_path, icon, sort_order, visible, status, remark, create_time, update_time) VALUES
(@system_menu_id, '用户管理', 'system:user:view', 1, '/system/user', 'system/UserManagement', 'User', 1, 1, 1, '用户管理菜单', NOW(), NOW()),
(@system_menu_id, '部门管理', 'system:dept:view', 1, '/system/department', 'system/DepartmentManagement', 'OfficeBuilding', 2, 1, 1, '部门管理菜单', NOW(), NOW()),
(@system_menu_id, '角色管理', 'system:role:view', 1, '/system/role', 'system/RoleManagement', 'UserFilled', 3, 1, 1, '角色管理菜单', NOW(), NOW()),
(@system_menu_id, '权限管理', 'system:permission:view', 1, '/system/permission', 'system/PermissionManagement', 'Lock', 4, 1, 1, '权限管理菜单', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    permission_name = VALUES(permission_name),
    parent_id = VALUES(parent_id),
    update_time = NOW();

-- ============================================
-- 3. 为admin角色分配所有菜单权限
-- ============================================
-- 假设admin的角色ID为1，如果不是请修改
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE permission_type = 1 AND status = 1
ON DUPLICATE KEY UPDATE role_id = role_id;

-- ============================================
-- 4. 验证初始化结果
-- ============================================

-- 验证超级管理员账号
SELECT 
    '✅ 超级管理员账号' AS '初始化项目',
    COUNT(*) AS '数量'
FROM sys_user
WHERE username = 'admin';

SELECT 
    id AS '用户ID',
    username AS '用户名',
    email AS '邮箱',
    real_name AS '真实姓名',
    CASE status WHEN 1 THEN '正常' ELSE '禁用' END AS '状态'
FROM sys_user
WHERE username = 'admin';

-- 验证菜单数据
SELECT 
    '✅ 菜单权限' AS '初始化项目',
    COUNT(*) AS '数量'
FROM sys_permission 
WHERE permission_type = 1;

-- 查看菜单树结构
SELECT 
    CASE 
        WHEN p1.parent_id = 0 THEN CONCAT('├─ ', p1.permission_name)
        ELSE CONCAT('│  └─ ', p1.permission_name)
    END AS '菜单树',
    p1.permission_code AS '权限代码',
    p1.route_path AS '路由路径'
FROM sys_permission p1
WHERE p1.permission_type = 1
ORDER BY 
    CASE WHEN p1.parent_id = 0 THEN p1.id ELSE p1.parent_id END,
    p1.parent_id,
    p1.sort_order;

-- 显示登录信息
SELECT 
    '' AS '',
    '📋 登录信息' AS '';

SELECT 
    'admin' AS '用户名',
    'admin123' AS '密码',
    'http://localhost:3000' AS '登录地址';

SELECT 
    '' AS '',
    '⚠️  重要提示：首次登录后请立即修改密码！' AS '';
