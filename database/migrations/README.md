# 数据库迁移脚本

## 📋 说明

此目录包含数据库结构变更的迁移脚本。每个迁移脚本都有编号和描述性名称。

## 📁 迁移脚本列表

### 001_fix_login_log_user_id.sql
- **日期**：2025-10-28
- **目的**：修复登录日志表的user_id字段约束
- **问题**：登录失败时报错 `Column 'user_id' cannot be null`
- **修改**：将`sys_login_log.user_id`字段改为允许NULL
- **原因**：登录失败时（用户不存在或密码错误）无法获取user_id

## 🚀 使用方法

### 方法1：使用批处理脚本（推荐）
```bash
cd database/migrations
run-migration.bat
```

### 方法2：手动执行
```bash
mysql -uroot -p < database/migrations/001_fix_login_log_user_id.sql
```

### 方法3：在MySQL客户端中执行
```sql
source database/migrations/001_fix_login_log_user_id.sql;
```

## ⚠️ 注意事项

1. **执行顺序**：按照编号顺序执行迁移脚本
2. **备份数据**：执行迁移前建议备份数据库
3. **测试环境**：先在测试环境验证，再在生产环境执行
4. **记录执行**：记录每次迁移的执行时间和结果

## 📝 迁移记录

| 编号 | 脚本名称 | 执行日期 | 执行人 | 状态 |
|------|---------|---------|--------|------|
| 001 | fix_login_log_user_id | 待执行 | - | ⏳ |

## 🔍 验证迁移

执行迁移后，可以通过以下SQL验证：

```sql
-- 查看表结构
DESCRIBE sys_login_log;

-- 验证user_id字段是否允许NULL
SHOW COLUMNS FROM sys_login_log WHERE Field = 'user_id';
```

## 📚 相关文档

- [../CHANGELOG.md](../CHANGELOG.md) - 数据库变更日志
- [../README.md](../README.md) - 数据库使用说明
