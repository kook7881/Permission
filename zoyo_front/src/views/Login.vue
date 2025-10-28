<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="brand-section">
          <div class="brand-icon">
            <el-icon :size="60"><Lock /></el-icon>
          </div>
          <h1 class="brand-title">Zoyo 认证系统</h1>
          <p class="brand-subtitle">安全 · 可靠 · 高效</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>双重加密保护</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>JWT Token 认证</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>企业级安全标准</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="form-wrapper">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>登录您的账户以继续</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            size="large"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="用户名或邮箱"
                prefix-icon="User"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                show-password
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="login-button"
                @click="handleLogin"
              >
                <span v-if="!loading">立即登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>

            <div class="form-footer">
              <span class="footer-text">还没有账号？</span>
              <el-button
                type="primary"
                link
                @click="$router.push('/register')"
              >
                立即注册
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { encryptPassword } from '@/utils/crypto'
import type { LoginRequest } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 加密密码后发送
        const encryptedData = {
          username: loginForm.username,
          password: encryptPassword(loginForm.password)
        }
        const res = await login(encryptedData)
        
        // 保存 token 和用户信息
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data.userInfo as any)
        // 同时保存到 localStorage 以便页面刷新后恢复
        localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
        
        // 加载用户权限和菜单
        try {
          const { getUserPermissions, getUserMenus } = await import('@/api/permission')
          const [permsRes, menusRes] = await Promise.all([
            getUserPermissions(),
            getUserMenus()
          ])
          userStore.setPermissions(permsRes.data)
          userStore.setMenus(menusRes.data)
        } catch (error) {
          console.error('加载权限失败:', error)
        }
        
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 20px;
}

.login-box {
  display: flex;
  width: 900px;
  max-width: 100%;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.brand-section {
  text-align: center;
}

.brand-icon {
  margin-bottom: 30px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px 0;
  letter-spacing: 1px;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 40px 0;
}

.feature-list {
  text-align: left;
  display: inline-block;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 15px;
}

.feature-item .el-icon {
  margin-right: 10px;
  font-size: 18px;
}

.login-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: 40px;
  text-align: center;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px 0;
}

.form-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.el-form-item {
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.25);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #2a5298 0%, #1e88e5 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(30, 136, 229, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.footer-text {
  color: #909399;
  font-size: 14px;
}

@media (max-width: 768px) {
  .login-box {
    flex-direction: column;
    width: 100%;
    max-width: 450px;
  }

  .login-left {
    padding: 40px 30px;
  }

  .brand-title {
    font-size: 24px;
  }

  .login-right {
    padding: 40px 30px;
  }
}
</style>
