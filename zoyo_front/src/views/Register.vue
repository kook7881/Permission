<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-left">
        <div class="brand-section">
          <div class="brand-icon">
            <el-icon :size="60"><UserFilled /></el-icon>
          </div>
          <h1 class="brand-title">加入 Zoyo</h1>
          <p class="brand-subtitle">开启您的安全之旅</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>免费注册使用</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>数据安全加密</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>快速便捷登录</span>
            </div>
          </div>
        </div>
      </div>

      <div class="register-right">
        <div class="form-wrapper">
          <div class="form-header">
            <h2>创建新账户</h2>
            <p>填写以下信息完成注册</p>
          </div>

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            size="large"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="用户名（3-50个字符）"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="邮箱地址"
                clearable
              >
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="密码（6-20个字符）"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="确认密码"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-collapse accordion style="margin-bottom: 20px; border: none">
              <el-collapse-item name="1">
                <template #title>
                  <span style="color: #909399; font-size: 14px">
                    <el-icon style="margin-right: 5px"><More /></el-icon>
                    更多信息（可选）
                  </span>
                </template>
                <el-form-item prop="phone">
                  <el-input
                    v-model="registerForm.phone"
                    placeholder="手机号（可选）"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Phone /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item prop="realName">
                  <el-input
                    v-model="registerForm.realName"
                    placeholder="真实姓名（可选）"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><UserFilled /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
              </el-collapse-item>
            </el-collapse>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="register-button"
                @click="handleRegister"
              >
                <span v-if="!loading">立即注册</span>
                <span v-else>注册中...</span>
              </el-button>
            </el-form-item>

            <div class="form-footer">
              <span class="footer-text">已有账号？</span>
              <el-button
                type="primary"
                link
                @click="$router.push('/login')"
              >
                立即登录
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
import { register } from '@/api/auth'
import { encryptPassword } from '@/utils/crypto'
import type { RegisterRequest } from '@/types/user'

const router = useRouter()

const registerFormRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = reactive<RegisterRequest & { confirmPassword: string }>({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  realName: ''
})

const validatePassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { confirmPassword, ...data } = registerForm
        // 加密密码后发送
        const encryptedData = {
          ...data,
          password: encryptPassword(data.password)
        }
        // 清理空字符串字段
        const cleanData = {
          username: encryptedData.username,
          password: encryptedData.password,
          email: encryptedData.email,
          ...(encryptedData.phone && encryptedData.phone.trim() !== '' && { phone: encryptedData.phone }),
          ...(encryptedData.realName && encryptedData.realName.trim() !== '' && { realName: encryptedData.realName })
        }
        await register(cleanData)
        
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  padding: 20px;
}

.register-box {
  display: flex;
  width: 900px;
  max-width: 100%;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-left {
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

.register-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow-y: auto;
  max-height: 90vh;
}

.form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: 30px;
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
  margin-bottom: 20px;
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

:deep(.el-collapse) {
  border: none;
  background: transparent;
}

:deep(.el-collapse-item__header) {
  background: transparent;
  border: none;
  padding: 10px 0;
  height: auto;
  line-height: 1.5;
}

:deep(.el-collapse-item__wrap) {
  border: none;
  background: transparent;
}

:deep(.el-collapse-item__content) {
  padding: 10px 0 0 0;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #2a5298 0%, #1e88e5 100%);
  border: none;
  transition: all 0.3s;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(30, 136, 229, 0.4);
}

.register-button:active {
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
  .register-box {
    flex-direction: column;
    width: 100%;
    max-width: 450px;
  }

  .register-left {
    padding: 40px 30px;
  }

  .brand-title {
    font-size: 24px;
  }

  .register-right {
    padding: 40px 30px;
    max-height: none;
  }
}
</style>
