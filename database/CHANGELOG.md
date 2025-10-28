# 数据库脚本整理记录

## 📅 整理时间
2025-10-28

## 📋 整理内容

### 合并的文件

#### 从项目根目录
- ✅ `create-admin.sql` → 合并到 `init-data.sql`
- ✅ `create-menu-table.sql` → 合并到 `schema.sql`
- ✅ `init-menu-data.sql` → 合并到 `init-data.sql`
- ✅ `create-menu-table.bat` → 删除
- ✅ `init-menu-data.bat` → 删除

#### 从 zoyo_admin/src/main/resources/db/
- ✅ `rbac-schema.sql` → 内容已由JPA自动创建，删除
- ✅ `schema.sql` → 简化为 `database/schema.sql`
- ✅ `test-data.sql` → 整理为 `database/test-data.sql`
- ✅ `user-management-init.sql` → 合并到 `database/test-data.sql`

### 新的文件结构

```
database/
├── README.md              # 使用说明文档
├── CHANGELOG.md           # 整理记录（本文件）
├── schema.sql             # 数据库创建脚本
├── init-data.sql          # 初始化数据（必需）
├── test-data.sql          # 测试数据（可选）
├── init-database.bat      # 一键初始化脚本
└── migrations/            # 数据库迁移脚本目录
    ├── README.md
    ├── 001_fix_login_log_user_id.sql
    └── run-migration.bat
```

## 📝 文件说明

### schema.sql
- **用途**：创建数据库
- **内容**：只创建数据库，表结构由JPA自动生成
- **执行时机**：首次部署时

### init-data.sql
- **用途**：初始化必需的基础数据
- **内容**：
  - 超级管理员账号（admin/admin123）
  - 系统菜单权限
  - 角色权限关联
- **执行时机**：首次部署时（必需）

### test-data.sql
- **用途**：导入测试数据
- **内容**：
  - 部门数据（9个部门）
  - 测试用户（8个用户）
  - 模拟登录日志
- **执行时机**：开发测试环境（可选）
- **注意**：生产环境请勿使用

## 🎯 设计原则

1. **简化结构**：将分散的SQL文件整理到统一目录
2. **职责分离**：
   - schema.sql - 数据库创建
   - init-data.sql - 必需数据
   - test-data.sql - 测试数据
3. **JPA优先**：充分利用JPA自动建表功能，减少手动SQL
4. **易于维护**：清晰的文件命名和完善的文档

## ✨ 改进点

1. **集中管理**：所有数据库脚本集中在 `database/` 目录
2. **文档完善**：添加详细的README和使用说明
3. **自动化**：提供批处理脚本一键初始化
4. **环境区分**：明确区分必需数据和测试数据
5. **删除冗余**：删除重复和过时的SQL文件

## 🚀 使用方法

### 快速开始
```bash
cd database
init-database.bat
```

### 手动执行
```bash
# 1. 创建数据库
mysql -uroot -p < database/schema.sql

# 2. 初始化数据
mysql -uroot -p < database/init-data.sql

# 3. 导入测试数据（可选）
mysql -uroot -p < database/test-data.sql
```

## 📌 注意事项

1. **JPA自动建表**：项目使用JPA，大部分表会自动创建
2. **密码加密**：所有密码都经过前端MD5+后端BCrypt双重加密
3. **测试数据**：test-data.sql仅用于开发测试，生产环境请勿使用
4. **备份数据**：重新初始化前请备份现有数据

## 🔗 相关文档

- [database/README.md](./README.md) - 详细使用说明
- [../今日完成工作总结.md](../今日完成工作总结.md) - 项目工作总结

---

## 📅 2025-10-28 - 修复登录日志问题

### 问题描述
当用户状态为离线或登录失败时，系统报错：`Column 'user_id' cannot be null`

### 原因分析
- `sys_login_log`表的`user_id`字段设置了`NOT NULL`约束
- 登录失败时（用户不存在或密码错误），无法获取到`user_id`
- 导致插入登录日志时违反数据库约束

### 解决方案
1. **修改实体类**：`LoginLog.java`的`userId`字段改为`nullable = true`
2. **数据库迁移**：执行`migrations/001_fix_login_log_user_id.sql`修改表结构
3. **自动更新**：由于配置了`ddl-auto: update`，重启应用即可自动更新表结构

### 迁移脚本
```bash
# 方法1：重启应用（推荐，JPA自动更新）
# 停止应用 -> 重新启动

# 方法2：手动执行迁移脚本
mysql -uroot -p < database/migrations/001_fix_login_log_user_id.sql
```

### 影响范围
- ✅ 登录失败时可以正常记录日志
- ✅ 用户不存在时可以记录登录尝试
- ✅ 不影响现有的登录成功日志

---

## 📅 2025-10-28 - 优化登录错误提示

### 问题描述
禁用账户后登录时，错误提示显示"密码错误"，而不是"账户已被禁用"

### 原因分析
- `AuthServiceImpl`中所有`AuthenticationException`都被统一处理
- 没有区分不同类型的认证异常（禁用、锁定、过期等）
- 导致用户无法获得准确的错误提示

### 解决方案

#### 1. 细化异常处理
在`AuthServiceImpl.login()`方法中，区分处理不同类型的认证异常：

- `DisabledException` → 账户已被禁用
- `LockedException` → 账户已被锁定
- `AccountExpiredException` → 账户已过期
- `CredentialsExpiredException` → 密码已过期
- `UsernameNotFoundException` → 用户不存在
- 其他`AuthenticationException` → 密码错误

#### 2. 添加错误码
在`ResultCode`枚举中添加新的错误码：

- `ACCOUNT_EXPIRED(1007, "账号已过期")`
- `CREDENTIALS_EXPIRED(1008, "密码已过期")`

### 改进效果

**修改前：**
- 禁用账户登录 → "密码错误" ❌
- 锁定账户登录 → "密码错误" ❌
- 账户过期登录 → "密码错误" ❌

**修改后：**
- 禁用账户登录 → "账户已被禁用" ✅
- 锁定账户登录 → "账户已被锁定" ✅
- 账户过期登录 → "账户已过期" ✅
- 密码过期登录 → "密码已过期" ✅
- 用户不存在 → "用户不存在" ✅
- 密码错误 → "密码错误" ✅

### 登录日志记录

每种失败情况都会记录到`sys_login_log`表中，包含准确的失败原因：

| 场景 | 日志消息 |
|------|---------|
| 账户禁用 | "账户已被禁用" |
| 账户锁定 | "账户已被锁定" |
| 账户过期 | "账户已过期" |
| 密码过期 | "密码已过期" |
| 用户不存在 | "用户不存在" |
| 密码错误 | "用户名或密码错误" |

### 安全考虑

- 对于"用户不存在"的情况，可以考虑返回通用的"用户名或密码错误"以防止用户名枚举攻击
- 当前实现返回具体错误，便于用户理解问题，可根据安全需求调整

### 影响范围
- ✅ 提升用户体验，提供准确的错误提示
- ✅ 便于管理员排查登录问题
- ✅ 登录日志更加详细和准确
- ✅ 不影响现有功能


---

## 📅 2025-10-28 - 修复前端显示"服务器错误"问题

### 问题描述
禁用账户登录时，前端显示"服务器错误"而不是"账户已被禁用"

### 原因分析
- `AuthServiceImpl`中抛出的是`RuntimeException`
- 全局异常处理器对`RuntimeException`返回HTTP 500状态码
- 前端收到500状态码后显示"服务器错误"

### 解决方案

#### 1. 创建业务异常类
创建`BusinessException`类，用于业务逻辑中的可预期异常：

```java
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String message;
    
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}
```

#### 2. 添加异常处理
在`GlobalExceptionHandler`中添加对`BusinessException`的处理：

```java
@ExceptionHandler(BusinessException.class)
@ResponseStatus(HttpStatus.OK)  // 返回200状态码
public Result<Void> handleBusinessException(BusinessException ex) {
    log.warn("业务异常: {}", ex.getMessage());
    return Result.error(ex.getCode(), ex.getMessage());
}
```

#### 3. 替换异常类型
将`AuthServiceImpl`中所有的`RuntimeException`替换为`BusinessException`

### 改进效果

**修改前：**
- 禁用账户登录 → HTTP 500 → 前端显示"服务器错误" ❌
- 密码错误 → HTTP 500 → 前端显示"服务器错误" ❌

**修改后：**
- 禁用账户登录 → HTTP 200 + 错误码1005 → 前端显示"账户已被禁用" ✅
- 密码错误 → HTTP 200 + 错误码1004 → 前端显示"密码错误" ✅
- 账户锁定 → HTTP 200 + 错误码1006 → 前端显示"账户已被锁定" ✅
- 账户过期 → HTTP 200 + 错误码1007 → 前端显示"账户已过期" ✅
- 密码过期 → HTTP 200 + 错误码1008 → 前端显示"密码已过期" ✅
- 用户不存在 → HTTP 200 + 错误码1003 → 前端显示"用户不存在" ✅

### 设计说明

**为什么返回HTTP 200？**

业务异常（如用户不存在、密码错误）是可预期的正常业务流程，不是系统错误：
- HTTP 200：请求成功处理，但业务逻辑判断失败
- HTTP 500：服务器内部错误，如空指针、数据库连接失败等

这样设计的好处：
1. 前端可以正确解析错误信息并显示给用户
2. 区分业务异常和系统异常
3. 符合RESTful API设计规范

### 影响范围
- ✅ 所有登录相关的错误提示都能正确显示
- ✅ 前端不再显示"服务器错误"
- ✅ 用户体验大幅提升
- ✅ 便于前端统一处理业务异常


---

## 📅 2025-10-28 - 添加用户在线状态判断

### 问题描述
用户已登录，但在用户列表中显示为"离线"状态

### 原因分析
- `UserDetailVO`中没有在线状态字段
- 后端没有计算和返回在线状态
- 前端无法正确判断用户是否在线

### 解决方案

#### 1. 添加在线状态字段
在`UserDetailVO`中添加`onlineStatus`字段：

```java
/**
 * 在线状态：true-在线，false-离线
 * 判断逻辑：最后登录时间在30分钟内视为在线
 */
private Boolean onlineStatus;
```

#### 2. 实现在线状态计算逻辑
在`UserManagementServiceImpl.convertToDetailVO()`方法中添加在线状态计算：

```java
// 计算在线状态
// 判断逻辑：最后登录时间在30分钟内且账户状态为正常，视为在线
boolean isOnline = false;
if (user.getLastLoginTime() != null && user.getStatus() == 1) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);
    isOnline = user.getLastLoginTime().isAfter(thirtyMinutesAgo);
}
vo.setOnlineStatus(isOnline);
```

### 在线状态判断规则

用户被判定为"在线"需要同时满足以下条件：
1. ✅ 最后登录时间不为空
2. ✅ 账户状态为正常（status = 1）
3. ✅ 最后登录时间在30分钟内

任何一个条件不满足，都会被判定为"离线"。

### 改进效果

**修改前：**
- 用户刚登录 → 显示"离线" ❌
- 无法区分在线和离线用户 ❌

**修改后：**
- 用户刚登录 → 显示"在线" ✅
- 30分钟内活跃 → 显示"在线" ✅
- 超过30分钟未活动 → 显示"离线" ✅
- 账户被禁用 → 显示"离线" ✅

### 配置说明

在线状态的超时时间目前硬编码为30分钟。如果需要调整，可以：

1. **修改超时时间**：在代码中修改`minusMinutes(30)`的参数
2. **配置化**：将超时时间提取到配置文件中

建议的超时时间：
- **开发环境**：30分钟（当前值）
- **生产环境**：15-30分钟
- **高安全环境**：5-10分钟

### 注意事项

1. **心跳机制**：如果需要更准确的在线状态，建议实现心跳机制
2. **性能考虑**：当前实现在每次查询用户列表时计算，性能开销很小
3. **实时性**：在线状态不是实时的，有最多30分钟的延迟
4. **Token过期**：JWT Token的过期时间应该与在线状态超时时间保持一致或更长

### 未来优化方向

如果需要更精确的在线状态管理，可以考虑：

1. **Redis缓存**：使用Redis存储用户在线状态，设置过期时间
2. **WebSocket心跳**：前端定期发送心跳包更新在线状态
3. **Session管理**：使用Spring Session管理用户会话
4. **在线用户列表**：维护一个实时的在线用户列表

### 影响范围
- ✅ 用户列表正确显示在线状态
- ✅ 管理员可以准确了解用户活跃情况
- ✅ 不影响现有功能
- ✅ 性能影响可忽略不计


---

## 📅 2025-10-28 - 修复监控大屏在线状态显示

### 问题描述
在监控大屏的"最近登录"列表中，用户显示为"离线"状态，即使刚刚登录

### 原因分析
- `DashboardStatsDTO.LoginRecord`中没有`onlineStatus`字段
- `DashboardServiceImpl.getRecentLogins()`方法没有计算在线状态
- 前端无法显示正确的在线状态

### 解决方案

#### 1. 添加在线状态字段
在`DashboardStatsDTO.LoginRecord`中添加`onlineStatus`字段：

```java
public static class LoginRecord {
    private String username;
    private String realName;
    private String loginTime;
    private String ipAddress;
    private String location;
    private String status;
    private Boolean onlineStatus;  // 新增：在线状态
}
```

#### 2. 实现在线状态计算
在`DashboardServiceImpl.getRecentLogins()`方法中添加在线状态计算逻辑：

```java
// 计算在线状态：最后登录时间在30分钟内且账户状态正常
if (user.getLastLoginTime() != null && user.getStatus() == 1) {
    onlineStatus = user.getLastLoginTime().isAfter(thirtyMinutesAgo);
}
```

### 改进效果

**修改前：**
- 监控大屏最近登录列表中，所有用户都显示"离线" ❌
- 无法区分哪些用户当前在线 ❌

**修改后：**
- 30分钟内登录的用户显示"在线" ✅
- 超过30分钟的用户显示"离线" ✅
- 账户被禁用的用户显示"离线" ✅
- 实时反映用户在线状态 ✅

### API响应变化

**修改前：**
```json
{
  "recentLogins": [
    {
      "username": "zoyo",
      "realName": "陈平安",
      "loginTime": "2025-10-28 15:43:30",
      "ipAddress": "0:0:0:0:0:0:0:1",
      "location": "本地",
      "status": "成功"
      // 没有onlineStatus字段
    }
  ]
}
```

**修改后：**
```json
{
  "recentLogins": [
    {
      "username": "zoyo",
      "realName": "陈平安",
      "loginTime": "2025-10-28 15:43:30",
      "ipAddress": "0:0:0:0:0:0:0:1",
      "location": "本地",
      "status": "成功",
      "onlineStatus": true  // 新增字段
    }
  ]
}
```

### 使用方法

1. **重启应用**
2. **刷新监控大屏页面**
3. **查看"最近登录"列表**

现在会正确显示每个用户的在线状态。

### 注意事项

1. **在线状态判断规则**：
   - 最后登录时间在30分钟内
   - 账户状态为正常（status = 1）
   - 两个条件同时满足才显示"在线"

2. **与用户管理页面一致**：
   - 监控大屏和用户管理页面使用相同的在线状态判断逻辑
   - 确保全系统在线状态显示一致

3. **性能考虑**：
   - 每次查询最近登录记录时都会计算在线状态
   - 由于只查询10条记录，性能影响可忽略

### 影响范围
- ✅ 监控大屏正确显示用户在线状态
- ✅ 管理员可以实时了解用户活跃情况
- ✅ 不影响其他功能
- ✅ API响应增加了onlineStatus字段（向后兼容）


---

## 📅 2025-10-28 - 添加退出登录时间记录

### 问题描述
用户退出登录后，仍然显示为"在线"状态，因为系统只记录了登录时间，没有记录退出时间

### 原因分析
- 系统只在登录时更新`lastLoginTime`
- 退出登录时没有更新任何字段
- 在线状态判断只基于`lastLoginTime`，无法区分用户是否已退出

### 解决方案

#### 1. 添加lastLogoutTime字段
在`sys_user`表中添加`last_logout_time`字段：

```sql
ALTER TABLE sys_user 
ADD COLUMN last_logout_time DATETIME NULL COMMENT '最后退出时间';
```

#### 2. 实现退出登录逻辑
- 在`IAuthService`接口中添加`logout()`方法
- 在`AuthServiceImpl`中实现退出逻辑，更新`lastLogoutTime`
- 在`AuthController`中调用退出方法

#### 3. 优化在线状态判断
修改在线状态判断逻辑，同时考虑登录时间和退出时间：

```java
// 判断条件：
// 1. 账户状态正常（status = 1）
// 2. 最后登录时间不为空
// 3. 最后登录时间在30分钟内
// 4. 未退出（lastLogoutTime为空 或 lastLogoutTime早于lastLoginTime）

boolean loginRecent = user.getLastLoginTime().isAfter(thirtyMinutesAgo);
boolean hasLoggedOut = user.getLastLogoutTime() != null 
    && user.getLastLogoutTime().isAfter(user.getLastLoginTime());
isOnline = loginRecent && !hasLoggedOut;
```

### 改进效果

**修改前：**
- 用户登录 → 显示"在线" ✅
- 用户退出 → 仍显示"在线" ❌（30分钟内）
- 30分钟后 → 显示"离线" ✅

**修改后：**
- 用户登录 → 显示"在线" ✅
- 用户退出 → 立即显示"离线" ✅
- 30分钟后 → 显示"离线" ✅

### 数据库迁移

**方法1：JPA自动更新（推荐）**
由于配置了`ddl-auto: update`，重启应用即可自动添加字段。

**方法2：手动执行迁移脚本**
```bash
mysql -uroot -p < database/migrations/002_add_last_logout_time.sql
```

### API变化

#### 新增退出登录接口
```
POST /api/auth/logout
Headers: Authorization: Bearer {token}
```

**响应：**
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

### 前端调用示例

```javascript
// 退出登录
const logout = async () => {
  try {
    await axios.post('/api/auth/logout', {}, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    // 清除本地Token
    localStorage.removeItem('token')
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败', error)
  }
}
```

### 在线状态判断逻辑

| 场景 | lastLoginTime | lastLogoutTime | 在线状态 |
|------|--------------|----------------|---------|
| 刚登录 | 2分钟前 | null | 在线 ✅ |
| 刚登录 | 2分钟前 | 5分钟前 | 在线 ✅ |
| 已退出 | 2分钟前 | 1分钟前 | 离线 ✅ |
| 超时 | 35分钟前 | null | 离线 ✅ |
| 超时且已退出 | 35分钟前 | 34分钟前 | 离线 ✅ |

### 注意事项

1. **向后兼容**：
   - 旧数据的`lastLogoutTime`为NULL
   - NULL被视为"未退出"，不影响在线状态判断

2. **Token有效期**：
   - JWT Token的过期时间应该与在线超时时间一致或更长
   - 当前Token有效期：24小时
   - 在线超时时间：30分钟

3. **前端处理**：
   - 前端需要在退出时调用`/api/auth/logout`接口
   - 不能只清除本地Token，必须调用后端接口

4. **性能考虑**：
   - 退出登录只更新一个字段，性能影响可忽略
   - 在线状态判断增加了一个字段比较，性能影响可忽略

### 影响范围
- ✅ 用户退出后立即显示为离线
- ✅ 在线状态判断更加准确
- ✅ 不影响现有登录功能
- ✅ 向后兼容旧数据
- ✅ 需要前端配合调用退出接口


---

## 📅 2025-10-28 - 修复历史登录记录显示在线的问题

### 问题描述
监控大屏的"最近登录记录"中，同一个用户的所有历史登录记录都显示为"在线"

### 问题示例
```
zoyo  15:46:05  在线  ✓
zoyo  15:43:30  在线  ✗ (应该是离线)
zoyo  15:36:47  在线  ✗ (应该是离线)
zoyo  15:33:34  在线  ✗ (应该是离线)
```

### 原因分析
之前的逻辑是：**根据用户的当前状态判断所有登录记录的在线状态**

```java
// 错误的逻辑
if (user.getLastLoginTime().isAfter(thirtyMinutesAgo)) {
    onlineStatus = true;  // 所有该用户的登录记录都显示在线
}
```

这导致：
- 如果用户当前在线，他的所有历史登录记录都显示"在线"
- 无法区分哪条是最新的登录

### 解决方案

修改逻辑为：**只有最后一次登录记录才判断在线状态**

```java
// 正确的逻辑
// 1. 判断这条登录记录是否是最后一次登录
long secondsDiff = Math.abs(
    Duration.between(loginLog.getLoginTime(), user.getLastLoginTime()).getSeconds()
);
boolean isLastLogin = secondsDiff < 1;

// 2. 只有最后一次登录才判断在线状态
if (isLastLogin) {
    boolean loginRecent = user.getLastLoginTime().isAfter(thirtyMinutesAgo);
    boolean hasLoggedOut = user.getLastLogoutTime() != null 
        && user.getLastLogoutTime().isAfter(user.getLastLoginTime());
    onlineStatus = loginRecent && !hasLoggedOut;
}
```

### 判断逻辑

**在线状态需要同时满足：**
1. ✅ 这条登录记录是用户的最后一次登录
2. ✅ 最后登录时间在30分钟内
3. ✅ 账户状态正常
4. ✅ 未退出登录（lastLogoutTime < lastLoginTime）

**历史登录记录：**
- ❌ 不是最后一次登录 → 显示"离线"

### 改进效果

**修改前：**
```
zoyo  15:46:05  在线  ✓
zoyo  15:43:30  在线  ✗
zoyo  15:36:47  在线  ✗
zoyo  15:33:34  在线  ✗
lisan 15:28:57  在线  ✓
```

**修改后：**
```
zoyo  15:46:05  在线  ✓ (最后一次登录)
zoyo  15:43:30  离线  ✓ (历史记录)
zoyo  15:36:47  离线  ✓ (历史记录)
zoyo  15:33:34  离线  ✓ (历史记录)
lisan 15:28:57  在线  ✓ (最后一次登录)
```

### 技术细节

**时间比较容差：**
- 使用1秒的容差判断是否是同一次登录
- 避免因为毫秒级差异导致判断错误

**为什么不直接比较loginLog.id？**
- LoginLog表的ID是自增的，但不保证与时间完全对应
- 使用时间比较更可靠

### 使用方法

1. **重启应用**
2. **刷新监控大屏页面**
3. **查看"最近登录记录"**

现在只有每个用户的最后一次登录会显示"在线"，历史记录都显示"离线"。

### 影响范围
- ✅ 正确区分最新登录和历史登录
- ✅ 避免误导（所有记录都显示在线）
- ✅ 提升数据准确性
- ✅ 不影响其他功能
