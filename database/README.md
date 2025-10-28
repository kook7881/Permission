# 数据库脚本说明

## 📁 文件说明

### 1. schema.sql - 数据库表结构
- 创建数据库 `zoyo_auth`
- 说明：大部分表由JPA自动创建，无需手动执行SQL

### 2. init-data.sql - 初始化数据
包含系统初始化所需的基础数据：
- 超级管理员账号（admin/admin123）
- 菜单权限数据
- 角色权限关联

### 3. test-data.sql - 测试数据（可选）
包含开发和测试环境的模拟数据：
- 部门数据（9个部门）
- 测试用户（8个用户，密码都是123456）
- 登录日志（用于监控大屏展示）
- **注意**：仅用于开发测试，生产环境请勿使用

## 🚀 使用方法

### 方式一：使用MySQL命令行

```bash
# 1. 创建数据库（可选，JPA会自动创建表）
mysql -uroot -p < database/schema.sql

# 2. 初始化数据
mysql -uroot -p < database/init-data.sql

# 3. 导入测试数据（可选，仅用于开发测试）
mysql -uroot -p < database/test-data.sql
```

### 方式二：使用MySQL客户端（Navicat/MySQL Workbench）

1. 打开MySQL客户端
2. 连接到数据库服务器
3. 依次执行以下文件：
   - `schema.sql` - 创建表结构
   - `init-data.sql` - 初始化数据

### 方式三：使用批处理脚本（Windows）

创建 `init-database.bat` 文件：

```batch
@echo off
chcp 65001 >nul
echo ========================================
echo 初始化数据库
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=123456

echo 1. 创建数据库表结构...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < database/schema.sql

echo.
echo 2. 初始化数据...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < database/init-data.sql

echo.
echo ========================================
echo 数据库初始化完成！
echo ========================================
echo.
pause
```

## 📋 初始化后的数据

### 超级管理员账号
- **用户名**: admin
- **密码**: admin123
- **邮箱**: admin@zoyo.com
- **手机**: 13800138000

### 菜单结构
```
├─ 监控大屏 (dashboard:view)
├─ 系统管理 (system:manage)
│  ├─ 用户管理 (system:user:view)
│  ├─ 部门管理 (system:dept:view)
│  ├─ 角色管理 (system:role:view)
│  └─ 权限管理 (system:permission:view)
└─ 个人中心 (profile:view)
```

## ⚠️ 注意事项

1. **密码安全**
   - 初始密码为 `admin123`
   - 首次登录后请立即修改密码
   - 密码已经过前端MD5+后端BCrypt双重加密

2. **数据库配置**
   - 默认数据库名：`zoyo_auth`
   - 字符集：`utf8mb4`
   - 排序规则：`utf8mb4_unicode_ci`

3. **重新初始化**
   - 如需重新初始化，请先备份现有数据
   - 取消 `init-data.sql` 中的 DELETE 语句注释
   - 重新执行初始化脚本

4. **JPA自动建表**
   - 项目使用JPA，大部分表会自动创建
   - `schema.sql` 只包含必要的表结构
   - 如需手动创建所有表，请参考实体类定义

## 🔧 故障排除

### 问题1：连接数据库失败
**解决方案**：
- 检查MySQL服务是否启动
- 确认用户名和密码是否正确
- 检查数据库连接配置

### 问题2：表已存在
**解决方案**：
- 脚本使用 `CREATE TABLE IF NOT EXISTS`，不会报错
- 如需重建表，请先删除现有表

### 问题3：数据重复
**解决方案**：
- 脚本使用 `ON DUPLICATE KEY UPDATE`，不会插入重复数据
- 如需清空数据，请执行 DELETE 语句

## 📚 相关文档

- [菜单表设计和初始化指南.md](../菜单表设计和初始化指南.md)
- [今日完成工作总结.md](../今日完成工作总结.md)
- [API文档.md](../API文档.md)
