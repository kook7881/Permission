# Zoyo 认证系统 - API 接口文档

## 📋 目录

- [接口说明](#接口说明)
- [认证接口](#认证接口)
- [用户接口](#用户接口)
- [错误码说明](#错误码说明)
- [请求示例](#请求示例)

---

## 📖 接口说明

### 基础信息

- **Base URL**: `http://localhost:8080/api` (开发环境)
- **Base URL**: `https://your-domain.com/api` (生产环境)
- **Content-Type**: `application/json`
- **字符编码**: `UTF-8`

### 认证方式

除了认证接口外，其他接口都需要在请求头中携带 JWT Token：

```
Authorization: Bearer {token}
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1698765432000
}
```

---

## 🔐 认证接口

### 1. 用户注册

**接口地址**: `POST /auth/register`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名，3-50个字符，只能包含字母、数字、下划线 |
| password | string | 是 | 密码（前端已加密），32位MD5哈希值 |
| email | string | 是 | 邮箱地址 |
| phone | string | 否 | 手机号，11位数字 |
| realName | string | 否 | 真实姓名 |

**请求示例**:

```json
{
  "username": "testuser",
  "password": "903264fe406a7f70493f6fcee70b1c99",
  "email": "test@example.com",
  "phone": "13800138000",
  "realName": "测试用户"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "注册成功",
  "data": null,
  "timestamp": 1698765432000
}
```

**错误响应**:

```json
{
  "code": 1001,
  "message": "用户名已存在",
  "data": null,
  "timestamp": 1698765432000
}
```

---

### 2. 用户登录

**接口地址**: `POST /auth/login`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名或邮箱 |
| password | string | 是 | 密码（前端已加密），32位MD5哈希值 |

**请求示例**:

```json
{
  "username": "testuser",
  "password": "903264fe406a7f70493f6fcee70b1c99"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "phone": "13800138000",
      "realName": "测试用户",
      "status": 1
    }
  },
  "timestamp": 1698765432000
}
```

---

### 3. 用户登出

**接口地址**: `POST /auth/logout`

**请求头**:

```
Authorization: Bearer {token}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1698765432000
}
```

---

### 4. 健康检查

**接口地址**: `GET /auth/health`

**响应示例**:

```json
{
  "code": 200,
  "message": "服务正常",
  "data": "OK",
  "timestamp": 1698765432000
}
```

---

## 👤 用户接口

### 1. 获取当前用户信息

**接口地址**: `GET /user/current`

**请求头**:

```
Authorization: Bearer {token}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "realName": "测试用户",
    "status": 1,
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "enabled": true,
    "lastLoginTime": "2025-10-27T16:30:00",
    "lastLoginIp": "192.168.1.100",
    "createTime": "2025-10-27T10:00:00",
    "updateTime": "2025-10-27T16:30:00",
    "remark": null
  },
  "timestamp": 1698765432000
}
```

---

### 2. 根据ID获取用户信息

**接口地址**: `GET /user/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 用户ID |

**请求头**:

```
Authorization: Bearer {token}
```

**响应示例**: 同上

---

### 3. 更新用户信息

**接口地址**: `PUT /user/update`

**请求头**:

```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| phone | string | 否 | 手机号 |
| realName | string | 否 | 真实姓名 |
| remark | string | 否 | 备注信息 |

**请求示例**:

```json
{
  "phone": "13900139000",
  "realName": "新名字",
  "remark": "这是备注"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13900139000",
    "realName": "新名字",
    "remark": "这是备注",
    ...
  },
  "timestamp": 1698765432000
}
```

---

### 4. 修改密码

**接口地址**: `POST /user/password`

**请求头**:

```
Authorization: Bearer {token}
```

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 旧密码（前端已加密），32位MD5哈希值 |
| newPassword | string | 是 | 新密码（前端已加密），32位MD5哈希值 |

**请求示例**:

```json
{
  "oldPassword": "903264fe406a7f70493f6fcee70b1c99",
  "newPassword": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1698765432000
}
```

---

### 5. 删除用户

**接口地址**: `DELETE /user/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 用户ID |

**请求头**:

```
Authorization: Bearer {token}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1698765432000
}
```

---

## ⚠️ 错误码说明

### 通用错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未授权，请先登录 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| 1001 | 用户名已存在 |
| 1002 | 邮箱已存在 |
| 1003 | 用户不存在 |
| 1004 | 密码错误 |
| 1005 | 账号已被禁用 |
| 1006 | 账号已被锁定 |
| 2001 | Token无效 |
| 2002 | Token已过期 |

---

## 📝 请求示例

### cURL 示例

#### 注册

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "903264fe406a7f70493f6fcee70b1c99",
    "email": "test@example.com"
  }'
```

#### 登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "903264fe406a7f70493f6fcee70b1c99"
  }'
```

#### 获取当前用户信息

```bash
curl -X GET http://localhost:8080/api/user/current \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

#### 更新用户信息

```bash
curl -X PUT http://localhost:8080/api/user/update \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "13900139000",
    "realName": "新名字"
  }'
```

#### 修改密码

```bash
curl -X POST http://localhost:8080/api/user/password \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "903264fe406a7f70493f6fcee70b1c99",
    "newPassword": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"
  }'
```

---

### JavaScript/Axios 示例

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// 注册
async function register() {
  const response = await api.post('/auth/register', {
    username: 'testuser',
    password: '903264fe406a7f70493f6fcee70b1c99',
    email: 'test@example.com'
  })
  return response.data
}

// 登录
async function login() {
  const response = await api.post('/auth/login', {
    username: 'testuser',
    password: '903264fe406a7f70493f6fcee70b1c99'
  })
  const { token } = response.data.data
  // 保存 token
  localStorage.setItem('token', token)
  return response.data
}

// 获取当前用户信息
async function getCurrentUser() {
  const token = localStorage.getItem('token')
  const response = await api.get('/user/current', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  return response.data
}
```

---

### Python/Requests 示例

```python
import requests
import json

BASE_URL = 'http://localhost:8080/api'

# 注册
def register():
    url = f'{BASE_URL}/auth/register'
    data = {
        'username': 'testuser',
        'password': '903264fe406a7f70493f6fcee70b1c99',
        'email': 'test@example.com'
    }
    response = requests.post(url, json=data)
    return response.json()

# 登录
def login():
    url = f'{BASE_URL}/auth/login'
    data = {
        'username': 'testuser',
        'password': '903264fe406a7f70493f6fcee70b1c99'
    }
    response = requests.post(url, json=data)
    result = response.json()
    token = result['data']['token']
    return token

# 获取当前用户信息
def get_current_user(token):
    url = f'{BASE_URL}/user/current'
    headers = {
        'Authorization': f'Bearer {token}'
    }
    response = requests.get(url, headers=headers)
    return response.json()
```

---

## 🔒 安全说明

### 密码加密

前端使用 MD5 + 盐值对密码进行加密：

```javascript
import CryptoJS from 'crypto-js'

const SECRET_KEY = 'zoyo-auth-system-2024'

function encryptPassword(password) {
  return CryptoJS.MD5(password + SECRET_KEY).toString()
}

// 示例
const encrypted = encryptPassword('123456')
// 结果: 903264fe406a7f70493f6fcee70b1c99
```

后端再使用 BCrypt 进行二次加密存储。

### Token 管理

- Token 有效期：24小时
- Token 存储：localStorage
- Token 刷新：需要重新登录

### HTTPS

生产环境必须使用 HTTPS 协议，确保数据传输安全。

---

## 📞 技术支持

如有问题，请查看：
- [项目总览](项目总览.md)
- [启动说明](启动说明.md)
- [部署指南](部署指南.md)

---

**最后更新**: 2025-10-27
