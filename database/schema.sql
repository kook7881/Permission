-- ============================================
-- Zoyo 认证系统 - 数据库表结构
-- ============================================
-- 数据库: zoyo_auth
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `zoyo_auth` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `zoyo_auth`;

-- ============================================
-- 说明：大部分表由JPA自动创建
-- 这里只包含需要手动创建或有特殊要求的表
-- ============================================

-- 查看所有表
SHOW TABLES;

-- 查看表数量
SELECT COUNT(*) as table_count 
FROM information_schema.tables 
WHERE table_schema = 'zoyo_auth';
