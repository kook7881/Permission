# Zoyo 认证系统

基于前后端分离架构的企业级认证系统。

## 📁 项目结构

```
zoyo/
├── zoyo_admin/              # 后端项目（Spring Boot 3）
│   ├── src/main/java/       # Java 源代码（23 个文件）
│   ├── src/main/resources/  # 配置文件
│   ├── pom.xml              # Maven 配置
│   └── README.md            # 后端文档
│
├── zoyo_front/              # 前端项目（Vue 3）
│   ├── src/                 # 前端源代码
│   │   ├── api/            # API 接口
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # 状态管理
│   │   └── types/          # 类型定义
│   ├── package.json         # npm 配置
│   └── README.md            # 前端文档
│
├── README.md                # 本文档
├── 启动说明.md              # 快速启动指南
└── 项目总览.md              # 项目完整概览
```

## 🚀 快速开始

### 方式一：查看启动说明（推荐）

阅读 **[启动说明.md](启动说明.md)** 获取详细的 5 步启动指南。

### 方式二：快速启动

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE zoyo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 启动后端（新终端）
cd D:\code\zoyo\zoyo_admin
mvn spring-boot:run

# 3. 启动前端（新终端）
cd D:\code\zoyo\zoyo_front
npm install  # 首次运行
npm run dev
```

访问：**http://localhost:3000**

## 📚 文档导航

### 快速入门
- 📖 [启动说明.md](启动说明.md) - 5 步快速启动
- 📊 [项目总览.md](项目总览.md) - 项目完整概览
- 🔒 [安全机制说明.md](安全机制说明.md) - 密码加密机制

### 开发文档
- 📖 [API文档.md](API文档.md) - 完整的 API 接口文档
- 📝 [开发规范.md](开发规范.md) - 代码规范和最佳实践
- 🚀 [部署指南.md](部署指南.md) - 生产环境部署指南

### 子项目文档
- 📖 [后端文档](zoyo_admin/README.md) - Spring Boot 后端
- 📖 [前端文档](zoyo_front/README.md) - Vue 3 前端

## 🎯 技术栈

### 后端（zoyo_admin）

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| Spring Data JPA | 3.x | ORM 框架 |
| MySQL | 8.0+ | 数据库 |
| JWT | 0.12.3 | Token 认证 |
| Maven | 3.6+ | 构建工具 |

### 前端（zoyo_front）

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4 | 前端框架 |
| TypeScript | 5.3 | 编程语言 |
| Vite | 5.0 | 构建工具 |
| Element Plus | 2.5 | UI 组件库 |
| Pinia | 2.1 | 状态管理 |
| Vue Router | 4.2 | 路由管理 |
| Axios | 1.6 | HTTP 客户端 |

## ✨ 功能特性

### 用户认证
- ✅ 用户注册（带参数校验）
- ✅ 用户登录（支持用户名/邮箱登录）
- ✅ JWT Token 认证
- ✅ 自动登录（Token 持久化）
- ✅ 退出登录

### 用户管理
- ✅ 查看个人信息
- ✅ 编辑个人信息
- ✅ 修改密码
- ✅ 账号状态管理

### 安全特性
- ✅ BCrypt 密码加密
- ✅ JWT Token 认证
- ✅ Spring Security 集成
- ✅ 路由守卫
- ✅ 跨域支持（CORS）
- ✅ 统一异常处理

## 🌐 服务地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost:3000 | Vue 3 用户界面 |
| 后端 API | http://localhost:8080/api | Spring Boot 接口 |
| 健康检查 | http://localhost:8080/api/auth/health | 后端健康检查 |

## 📝 API 接口

### 认证接口（/api/auth）
- `POST /auth/register` - 用户注册
- `POST /auth/login` - 用户登录
- `POST /auth/logout` - 用户登出
- `GET /auth/health` - 健康检查

### 用户接口（/api/user，需要 Token）
- `GET /user/current` - 获取当前用户信息
- `GET /user/{id}` - 根据 ID 获取用户信息
- `PUT /user/update` - 更新用户信息
- `POST /user/password` - 修改密码
- `DELETE /user/{id}` - 删除用户

## 🏗️ 架构说明

### 后端架构（三层架构）

```
Controller 层（表现层）
    ↓
Service 层（业务层 - 接口与实现分离）
    ↓
Repository 层（持久层）
    ↓
MySQL 数据库
```

**特点**：
- 严格的三层架构
- 接口与实现分离
- 面向接口编程
- 遵循 SOLID 原则

### 前端架构

```
Views（页面组件）
    ↓
API（接口调用）
    ↓
Request（HTTP 封装）
    ↓
Backend API
```

**特点**：
- 组件化开发
- 状态管理（Pinia）
- 路由守卫
- HTTP 拦截器

## 🔧 开发环境要求

### 后端
- JDK 21
- Maven 3.6+
- MySQL 8.0+
- IntelliJ IDEA（推荐）

### 前端
- Node.js 18+
- npm 9+
- VS Code（推荐）

## 🚀 部署说明

### 开发环境
```bash
# 后端
cd zoyo_admin
mvn spring-boot:run

# 前端
cd zoyo_front
npm run dev
```

### 生产环境
```bash
# 后端打包
cd zoyo_admin
mvn clean package
java -jar target/auth-system-1.0.0.jar

# 前端构建
cd zoyo_front
npm run build
# 部署 dist 目录到 Nginx
```

## 🆘 常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 检查 `zoyo_admin/src/main/resources/application.yml` 中的配置

### 2. 跨域问题
- 开发环境：前端使用 Vite 代理（已配置）
- 生产环境：后端已配置 CORS（CorsConfig.java）

### 3. Token 失效
- Token 默认有效期 24 小时
- 失效后自动跳转登录页

### 4. 端口被占用
- 后端：修改 `application.yml` 中的 `server.port`
- 前端：修改 `vite.config.ts` 中的 `server.port`

## 📊 项目统计

| 项目 | 文件数 | 代码行数 | 状态 |
|------|--------|----------|------|
| 后端 | 23 个 Java | ~2700 行 | ✅ |
| 前端 | 12 个 Vue/TS | ~1500 行 | ✅ |
| 文档 | 10+ 个 MD | ~40000 字 | ✅ |

## 🎯 项目亮点

1. **前后端完全分离** - 独立开发、独立部署
2. **现代化技术栈** - Spring Boot 3 + Vue 3
3. **企业级架构** - 三层架构 + 组件化
4. **完善的文档** - 从入门到精通
5. **开箱即用** - 配置完善、功能完整
6. **安全可靠** - 多层安全防护

## 🔄 后续扩展建议

### 功能扩展
- [ ] 角色权限管理（RBAC）
- [ ] 用户头像上传
- [ ] 邮箱验证
- [ ] 第三方登录
- [ ] 操作日志

### 技术优化
- [ ] Redis 缓存
- [ ] API 文档（Swagger）
- [ ] 单元测试
- [ ] Docker 容器化
- [ ] CI/CD 流水线

## 📄 许可证

MIT License

---

**项目完成时间**: 2025-10-27  
**最后更新时间**: 2025-10-27  
**项目状态**: ✅ 完成，可正常使用

**开始使用**: 查看 [启动说明.md](启动说明.md) 快速启动项目！
