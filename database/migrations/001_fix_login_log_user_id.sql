-- 修复登录日志表的user_id字段，允许为NULL
-- 原因：登录失败时（用户不存在或密码错误），无法获取user_id
-- 日期：2025-10-28

USE zoyo_admin;

-- 修改user_id字段，允许为NULL
ALTER TABLE sys_login_log 
MODIFY COLUMN user_id BIGINT NULL COMMENT '用户ID（登录失败时可能为null）';

-- 验证修改
DESCRIBE sys_login_log;
