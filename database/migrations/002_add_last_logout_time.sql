-- 添加最后退出时间字段
-- 用于准确判断用户在线状态
-- 日期：2025-10-28

USE zoyo_admin;

-- 添加last_logout_time字段
ALTER TABLE sys_user 
ADD COLUMN last_logout_time DATETIME NULL COMMENT '最后退出时间' AFTER last_login_ip;

-- 验证修改
DESCRIBE sys_user;
