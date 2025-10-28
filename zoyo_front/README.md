# Zoyo Front - 前端项目

基于 Vue 3 + TypeScript + Vite + Element Plus 的现代化前端项目。

## 技术栈

- **框架**: Vue 3.4
- **构建工具**: Vite 5.0
- **语言**: TypeScript 5.3
- **UI 组件库**: Element Plus 2.5
- **状态管理**: Pinia 2.1
- **路由**: Vue Router 4.2
- **HTTP 客户端**: Axios 1.6

## 项目结构

```
zoyo_front/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   │   ├── request.ts     # Axios 封装
│   │   ├── auth.ts        # 认证接口
│   │   └── user.ts        # 用户接口
│   ├── assets/            # 资源文件
│   ├── router/            # 路由配置
│   │   └── index.ts       # 路由定义
│   ├── stores/            # 状态管理
│   │   └── user.ts        # 用户状态
│   ├── types/             # TypeScript 类型定义
│   │   └── user.ts        # 用户类型
│   ├── views/             # 页面组件
│   │   ├── Login.vue      # 登录页
│   │   ├── Register.vue   # 注册页
│   │   ├── Home.vue       # 首页
│   │   └── Profile.vue    # 个人中心
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── index.html             # HTML 模板
├── vite.config.ts         # Vite 配置
├── tsconfig.json          # TypeScript 配置
└── package.json           # 项目依赖

```

## 快速开始

### 1. 安装依赖

```bash
npm install
# 或
yarn install
# 或
pnpm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产构建

```bash
npm run preview
```

## 功能特性

### ✅ 已实现功能

- **用户认证**
  - 用户登录
  - 用户注册
  - 自动登录（Token 持久化）
  - 退出登录

- **个人中心**
  - 查看个人信息
  - 编辑个人信息
  - 修改密码

- **路由守卫**
  - 登录状态检查
  - 自动跳转

- **HTTP 拦截器**
  - 自动添加 Token
  - 统一错误处理
  - 401 自动跳转登录

### 🎨 UI 特性

- 响应式布局
- 现代化设计
- 流畅的动画效果
- Element Plus 组件库
- 图标支持

## API 配置

### 开发环境

开发环境使用 Vite 代理，配置在 `vite.config.ts`：

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### 生产环境

生产环境需要配置 Nginx 反向代理或修改 `src/api/request.ts` 中的 `baseURL`。

## 环境变量

创建 `.env.development` 和 `.env.production` 文件：

```bash
# .env.development
VITE_API_BASE_URL=/api

# .env.production
VITE_API_BASE_URL=https://your-api-domain.com/api
```

## 页面说明

### 登录页 (`/login`)
- 用户名/邮箱登录
- 密码输入
- 跳转注册

### 注册页 (`/register`)
- 用户名
- 密码
- 邮箱
- 手机号（可选）
- 真实姓名（可选）

### 首页 (`/home`)
- 显示用户信息
- 快捷操作入口

### 个人中心 (`/profile`)
- 编辑个人信息
- 修改密码

## 开发规范

### 组件命名

- 页面组件：大驼峰命名，如 `Login.vue`
- 通用组件：大驼峰命名，如 `UserCard.vue`

### API 调用

```typescript
import { login } from '@/api/auth'

const handleLogin = async () => {
  try {
    const res = await login({ username, password })
    // 处理响应
  } catch (error) {
    // 错误已被拦截器处理
  }
}
```

### 状态管理

```typescript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 设置 Token
userStore.setToken(token)

// 获取用户信息
const userInfo = userStore.userInfo
```

## 常见问题

### 1. 跨域问题

开发环境已配置代理，生产环境需要后端配置 CORS 或使用 Nginx 反向代理。

### 2. Token 失效

Token 失效会自动跳转到登录页，无需手动处理。

### 3. 路由守卫

所有需要登录的页面都在路由配置中设置了 `meta.requiresAuth: true`。

## 部署

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/zoyo_front/dist;
    index index.html;
    
    # 前端路由
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 许可证

MIT License

## 相关链接

- [Vue 3 文档](https://cn.vuejs.org/)
- [Vite 文档](https://cn.vitejs.dev/)
- [Element Plus 文档](https://element-plus.org/zh-CN/)
- [Pinia 文档](https://pinia.vuejs.org/zh/)
- [Vue Router 文档](https://router.vuejs.org/zh/)
