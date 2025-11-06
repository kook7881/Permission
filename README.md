# Zoyo 认证系统

> 基于前后端分离架构的企业级认证与权限管理系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-brightgreen.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📖 项目简介

Zoyo 是一个功能完善的企业级认证与权限管理系统，采用现代化技术栈构建，提供完整的用户管理、角色管理、权限管理、部门管理等功能，并配备实时监控大屏。

### 🎯 核心特性

- 🔐 **完整的RBAC权限体系** - 用户、角色、权限三级管理
- 📊 **实时监控大屏** - 数据可视化、用户活跃度分析
- 🏢 **部门组织管理** - 树形结构、层级管理
- 📝 **登录日志追踪** - 详细记录登录信息、IP定位
- 🎨 **现代化UI设计** - 响应式布局、暗色主题
- 🔒 **多层安全防护** - JWT认证、密码加密、权限校验

## 📁 项目结构

```
zoyo/
├── zoyo_admin/              # 后端项目（Spring Boot 3）
│   ├── src/main/java/       # Java 源代码
│   │   └── com/zoyo/auth/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层（接口）
│   │       │   └── impl/    # 服务实现
│   │       ├── repository/  # 数据访问层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       ├── config/      # 配置类
│   │       ├── security/    # 安全配置
│   │       ├── exception/   # 异常处理
│   │       └── util/        # 工具类
│   ├── src/main/resources/  # 配置文件
│   └── pom.xml              # Maven 配置
│
├── zoyo_front/              # 前端项目（Vue 3 + TypeScript）
│   ├── src/
│   │   ├── api/            # API 接口定义
│   │   ├── views/          # 页面组件
│   │   │   ├── Login.vue   # 登录页
│   │   │   ├── Dashboard.vue  # 监控大屏
│   │   │   ├── system/     # 系统管理
│   │   │   └── ...
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理（Pinia）
│   │   ├── types/          # TypeScript 类型
│   │   └── utils/          # 工具函数
│   └── package.json         # npm 配置
│
├── database/                # 数据库脚本
│   ├── schema.sql          # 数据库创建
│   ├── init-data.sql       # 初始化数据
│   ├── test-data.sql       # 测试数据
│   ├── migrations/         # 数据库迁移
│   └── README.md           # 数据库文档
│
└── README.md                # 本文档
```

## 🚀 快速开始

### 环境要求

**后端**
- JDK 21+
- Maven 3.6+
- MySQL 8.0+

**前端**
- Node.js 18+
- npm 9+

### 1. 数据库初始化

```bash
# 进入数据库脚本目录
cd database

# 方式1：使用批处理脚本（Windows）
init-database.bat

# 方式2：手动执行
mysql -uroot -p < schema.sql
mysql -uroot -p < init-data.sql
mysql -uroot -p < test-data.sql  # 可选：测试数据
```

### 2. 启动后端

```bash
cd zoyo_admin

# 修改配置（如需要）
# 编辑 src/main/resources/application.yml
# 修改数据库连接信息

# 启动应用
mvn spring-boot:run

# 或者使用 IDE 运行 AuthSystemApplication.java
```

后端服务：http://localhost:8080/api

### 3. 启动前端

```bash
cd zoyo_front

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

前端应用：http://localhost:3000

### 4. 登录系统

**超级管理员账号**
- 用户名：`admin`
- 密码：`admin123`

**测试账号**
- 用户名：`zoyo`
- 密码：`123456`

## 🎯 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| Spring Data JPA | 3.x | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| JWT | 0.12.3 | Token 认证 |
| Lombok | - | 代码简化 |
| Maven | 3.6+ | 项目构建 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4 | 渐进式框架 |
| TypeScript | 5.3 | 类型安全 |
| Vite | 5.0 | 构建工具 |
| Element Plus | 2.5 | UI 组件库 |
| ECharts | 5.x | 数据可视化 |
| Pinia | 2.1 | 状态管理 |
| Vue Router | 4.2 | 路由管理 |
| Axios | 1.6 | HTTP 客户端 |
| Crypto-JS | 4.2 | 加密库 |

## ✨ 功能特性

### 🔐 认证与授权

- ✅ 用户注册（邮箱验证、参数校验）
- ✅ 用户登录（用户名/邮箱登录）
- ✅ JWT Token 认证
- ✅ 自动登录（Token 持久化）
- ✅ 退出登录
- ✅ 密码加密（BCrypt + MD5 双重加密）
- ✅ 权限校验（方法级、路由级）

### 👥 用户管理

- ✅ 用户列表（分页、搜索、筛选）
- ✅ 用户创建/编辑/删除
- ✅ 用户状态管理（启用/禁用）
- ✅ 批量操作
- ✅ 密码重置
- ✅ 角色分配
- ✅ 部门分配

### 🎭 角色管理

- ✅ 角色列表（分页、搜索）
- ✅ 角色创建/编辑/删除
- ✅ 权限分配
- ✅ 数据权限范围
- ✅ 系统内置角色保护

### � 权限管理

- ✅ 权限树形展示
- ✅ 权限创建/编辑/删除
- ✅ 菜单权限
- ✅ 按钮权限
- ✅ 权限类型（目录/菜单/按钮）
- ✅ 权限排序

### 🏢 部门管理

- ✅ 部门树形结构
- ✅ 部门创建/编辑/删除
- ✅ 层级管理
- ✅ 部门负责人
- ✅ 部门用户统计

### 📊 监控大屏

- ✅ 实时数据统计
- ✅ 用户增长趋势
- ✅ 登录趋势分析
- ✅ 24小时活跃度
- ✅ 地域分布（中国地图）
- ✅ 设备分布
- ✅ 最近登录记录
- ✅ 在线状态显示

### 📝 日志管理

- ✅ 登录日志记录
- ✅ IP 地址记录
- ✅ 登录地点
- ✅ 设备信息
- ✅ 浏览器信息
- ✅ 登录状态（成功/失败）

## 🏗️ 系统架构

### 后端架构

```
┌─────────────────────────────────────────┐
│           Controller 层                  │
│        (表现层 - RESTful API)            │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           Service 层                     │
│    (业务层 - 接口与实现分离)             │
│    - IUserService / UserServiceImpl     │
│    - IRoleService / RoleServiceImpl     │
│    - IPermissionService / ...           │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Repository 层                    │
│      (持久层 - Spring Data JPA)         │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           MySQL 数据库                   │
└─────────────────────────────────────────┘
```

**设计原则**
- 严格的三层架构
- 接口与实现分离
- 面向接口编程
- 遵循 SOLID 原则
- 统一异常处理
- 统一响应格式

### 前端架构

```
┌─────────────────────────────────────────┐
│            Views 层                      │
│         (页面组件)                       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│            API 层                        │
│        (接口调用封装)                    │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Request 层                      │
│      (HTTP 封装 + 拦截器)               │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Backend API                     │
└─────────────────────────────────────────┘
```

**设计特点**
- 组件化开发
- 状态管理（Pinia）
- 路由守卫
- HTTP 拦截器
- 响应式设计
- TypeScript 类型安全

## 🔒 安全机制

### 密码安全
- **前端加密**：MD5(password + salt)
- **后端加密**：BCrypt(前端加密后的密码)
- **双重加密**：确保密码安全

### Token 认证
- **JWT Token**：无状态认证
- **有效期**：24小时
- **自动刷新**：前端自动处理
- **安全存储**：LocalStorage

### 权限控制
- **方法级权限**：@PreAuthorize 注解
- **路由级权限**：前端路由守卫
- **按钮级权限**：v-if 指令控制
- **数据权限**：基于部门的数据隔离

### 其他安全
- **CORS 配置**：跨域请求控制
- **SQL 注入防护**：JPA 参数化查询
- **XSS 防护**：前端输入过滤
- **CSRF 防护**：Token 验证

## 🌐 API 接口

### 认证接口（/api/auth）
```
POST   /auth/register      # 用户注册
POST   /auth/login         # 用户登录
POST   /auth/logout        # 用户登出
GET    /auth/health        # 健康检查
```

### 用户接口（/api/user）
```
GET    /user/current       # 获取当前用户
GET    /user/{id}          # 获取用户详情
PUT    /user/update        # 更新用户信息
POST   /user/password      # 修改密码
```

### 用户管理接口（/api/user-management）
```
GET    /user-management/users          # 用户列表
POST   /user-management/users          # 创建用户
PUT    /user-management/users/{id}     # 更新用户
DELETE /user-management/users/{id}     # 删除用户
PUT    /user-management/users/{id}/status  # 更新状态
```

### 角色接口（/api/role）
```
GET    /role/list          # 角色列表
GET    /role/all           # 所有角色
POST   /role/create        # 创建角色
PUT    /role/update/{id}   # 更新角色
DELETE /role/delete/{id}   # 删除角色
POST   /role/{id}/permissions  # 分配权限
```

### 权限接口（/api/permission）
```
GET    /permission/tree    # 权限树
GET    /permission/list    # 权限列表
POST   /permission/create  # 创建权限
PUT    /permission/update/{id}  # 更新权限
DELETE /permission/delete/{id}  # 删除权限
GET    /permission/user-menus   # 用户菜单
```

### 部门接口（/api/department）
```
GET    /department/tree    # 部门树
POST   /department/create  # 创建部门
PUT    /department/update/{id}  # 更新部门
DELETE /department/delete/{id}  # 删除部门
```

### 监控接口（/api/dashboard）
```
GET    /dashboard/stats    # 监控统计数据
```

## 📊 数据库设计

### 核心表结构

```sql
sys_user              # 用户表
sys_role              # 角色表
sys_permission        # 权限表
sys_department        # 部门表
sys_user_role         # 用户角色关联表
sys_role_permission   # 角色权限关联表
sys_login_log         # 登录日志表
```

### ER 关系
- 用户 ←→ 角色（多对多）
- 角色 ←→ 权限（多对多）
- 用户 → 部门（多对一）
- 部门 → 部门（树形结构）

## 🎨 界面预览

### 登录页面
- 现代化设计
- 响应式布局
- 表单验证

### 监控大屏
- 实时数据展示
- 数据可视化（ECharts）
- 中国地图分布
- 趋势分析图表

### 系统管理
- 用户管理
- 角色管理
- 权限管理
- 部门管理

## 🔧 配置说明

### 后端配置（application.yml）

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zoyo_admin
    username: root
    password: your_password
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时
```

### 前端配置（vite.config.ts）

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 📝 开发指南

### 后端开发

1. **创建实体类**（Entity）
2. **创建Repository接口**
3. **创建Service接口**
4. **实现Service**（ServiceImpl）
5. **创建Controller**
6. **添加权限注解**

### 前端开发

1. **定义API接口**（api/）
2. **创建页面组件**（views/）
3. **配置路由**（router/）
4. **添加权限控制**
5. **状态管理**（stores/）

## 🚀 部署指南

### 后端部署

```bash
# 打包
mvn clean package

# 运行
java -jar target/auth-system-1.0.0.jar

# 或使用 Docker
docker build -t zoyo-admin .
docker run -p 8080:8080 zoyo-admin
```

### 前端部署

```bash
# 构建
npm run build

# 部署 dist 目录到 Nginx
# 配置 Nginx 反向代理
```

## 🆘 常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务状态
- 验证数据库配置信息
- 确认数据库已创建

### 2. 前端跨域问题
- 开发环境：Vite 代理已配置
- 生产环境：后端 CORS 已配置

### 3. Token 失效
- Token 有效期 24 小时
- 失效后自动跳转登录页
- 可在配置中调整有效期

### 4. 权限问题
- 检查用户角色分配
- 检查角色权限配置
- 查看后端日志

## 📈 项目统计

| 项目 | 文件数 | 代码行数 | 状态 |
|------|--------|----------|------|
| 后端 | 50+ Java | ~5000 行 | ✅ 完成 |
| 前端 | 30+ Vue/TS | ~3000 行 | ✅ 完成 |
| 数据库 | 7 张表 | - | ✅ 完成 |
| 文档 | 15+ MD | ~50000 字 | ✅ 完成 |

## 🎯 项目亮点

1. **完整的RBAC权限体系** - 用户、角色、权限三级管理
2. **实时监控大屏** - 数据可视化、实时统计
3. **接口与实现分离** - 面向接口编程
4. **双重密码加密** - 前后端双重加密
5. **完善的日志系统** - 登录日志、操作日志
6. **现代化技术栈** - Spring Boot 3 + Vue 3
7. **企业级架构** - 三层架构 + 组件化
8. **完整的文档** - 从入门到部署
9. **开箱即用** - 配置完善、功能完整
10. **安全可靠** - 多层安全防护

## 🔄 后续规划

### 功能扩展
- [ ] 操作日志记录
- [ ] 数据字典管理
- [ ] 系统配置管理
- [ ] 消息通知系统
- [ ] 文件上传管理
- [ ] 定时任务管理
- [ ] 第三方登录（OAuth2）
- [ ] 多租户支持

### 技术优化
- [ ] Redis 缓存
- [ ] Swagger API 文档
- [ ] 单元测试覆盖
- [ ] Docker 容器化
- [ ] CI/CD 流水线
- [ ] 性能监控
- [ ] 日志收集（ELK）
- [ ] 服务监控（Prometheus）

## 📚 文档导航

- **[项目结构说明](./PROJECT_STRUCTURE.md)** - 完整的目录结构和文件说明
- **[API文档](./API文档.md)** - 完整的后端API接口文档
- **[数据库文档](./database/README.md)** - 数据库初始化和迁移说明
- **[前端文档](./zoyo_front/README.md)** - 前端项目说明
- **[历史文档](./docs/README.md)** - 开发过程中的修复说明和总结归档

## 📄 许可证

MIT License

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系方式

如有问题或建议，请提交 Issue。

---

**项目开始时间**: 2025-10-22  
**最后更新时间**: 2025-10-28  
**项目状态**: ✅ 完成，持续优化中

**开始使用**: 查看上方快速开始章节，5分钟启动项目！
