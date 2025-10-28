# 在线状态显示问题排查指南

## 问题描述
用户已登录，但在用户列表中显示为"离线"状态

## 排查步骤

### 1. 确认应用已重启
修改代码后必须重启Spring Boot应用才能生效。

```bash
# 停止应用
# 重新启动应用
```

### 2. 重新登录
修改代码后，需要重新登录才能更新`lastLoginTime`字段。

**重要**：不要使用之前的Token，必须重新登录！

### 3. 检查数据库中的lastLoginTime
在MySQL中执行以下SQL查看你的用户数据：

```sql
USE zoyo_admin;

-- 查看你的用户信息
SELECT 
    id,
    username,
    status,
    last_login_time,
    last_login_ip,
    NOW() as now_time,
    TIMESTAMPDIFF(MINUTE, last_login_time, NOW()) as minutes_since_login
FROM sys_user
WHERE username = 'your_username';  -- 替换为你的用户名
```

**预期结果**：
- `last_login_time` 应该是最近的时间（刚登录的时间）
- `minutes_since_login` 应该小于30

### 4. 检查应用日志
启动应用后，查看日志中是否有在线状态计算的调试信息：

```
用户 xxx 在线状态计算: lastLoginTime=..., now=..., thirtyMinutesAgo=..., isOnline=...
```

或者：

```
用户 xxx 在线状态为false: lastLoginTime=..., status=...
```

### 5. 检查API响应
使用浏览器开发者工具或Postman查看用户列表API的响应：

```
GET /api/user-management/users?page=1&size=10
```

检查响应中的`onlineStatus`字段：

```json
{
  "code": 200,
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "lastLoginTime": "2025-10-28T15:30:00",
        "onlineStatus": true,  // 应该是true
        ...
      }
    ]
  }
}
```

## 常见问题

### Q1: 我重启了应用，但还是显示离线
**A**: 你需要重新登录！旧的Token不会更新`lastLoginTime`。

### Q2: 数据库中lastLoginTime是NULL
**A**: 说明登录时没有更新这个字段。检查：
1. `AuthServiceImpl.login()`方法是否调用了`updateLastLoginInfo`
2. `UserRepository.updateLastLoginInfo()`方法是否正确执行
3. 数据库事务是否提交成功

### Q3: lastLoginTime有值，但超过30分钟了
**A**: 这是正常的。在线状态的判断规则是：最后登录时间在30分钟内。如果超过30分钟，就会显示为离线。

解决方法：
- 重新登录
- 或者修改超时时间（在`UserManagementServiceImpl.java`中修改`minusMinutes(30)`）

### Q4: status字段不是1
**A**: 在线状态需要同时满足：
1. `lastLoginTime`不为空
2. `status = 1`（账户状态正常）
3. 最后登录时间在30分钟内

如果账户被禁用（status = 0），即使刚登录也会显示为离线。

## 调试技巧

### 临时延长在线超时时间
如果想测试，可以临时延长超时时间：

```java
// 在 UserManagementServiceImpl.java 中
LocalDateTime thirtyMinutesAgo = now.minusMinutes(300);  // 改为300分钟（5小时）
```

### 启用DEBUG日志
在`application.yml`中添加：

```yaml
logging:
  level:
    com.zoyo.auth.service.impl.UserManagementServiceImpl: DEBUG
```

重启应用后，会看到详细的在线状态计算日志。

## 验证步骤

1. ✅ 重启应用
2. ✅ 清除浏览器缓存和Cookie
3. ✅ 重新登录（不要使用旧Token）
4. ✅ 刷新用户列表页面
5. ✅ 检查你的用户是否显示为"在线"

## 如果还是不行

请提供以下信息：

1. 数据库查询结果（上面的SQL）
2. 应用日志（包含在线状态计算的日志）
3. API响应（用户列表接口的完整响应）
4. 你的操作步骤

这样我可以帮你进一步排查问题。
