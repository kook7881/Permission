<template>
  <div class="profile-page">
      <!-- 用户信息卡片 -->
      <div class="user-info-banner">
        <div class="user-avatar-section">
          <el-avatar :size="100" :src="profileForm.avatar" style="background: linear-gradient(135deg, #2a5298 0%, #1e88e5 100%)">
            <el-icon :size="50"><UserFilled /></el-icon>
          </el-avatar>
          <div class="user-basic-info">
            <h2>{{ profileForm.realName || profileForm.username }}</h2>
            <p>{{ profileForm.email }}</p>
            <div class="user-meta">
              <span v-if="profileForm.deptName">
                <el-icon><OfficeBuilding /></el-icon>
                {{ profileForm.deptName }}
              </span>
              <span v-if="profileForm.position">
                <el-icon><Briefcase /></el-icon>
                {{ profileForm.position }}
              </span>
              <span v-if="profileForm.roles && profileForm.roles.length > 0">
                <el-icon><UserFilled /></el-icon>
                {{ profileForm.roles.map(r => r.name).join('、') }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 编辑表单区域 -->
      <div class="form-section">
        <el-row :gutter="24">
          <!-- 个人信息编辑 -->
          <el-col :xs="24" :sm="24" :md="12">
            <el-card class="form-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <div class="icon-wrapper" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                      <el-icon :size="20"><Edit /></el-icon>
                    </div>
                    <span>编辑个人信息</span>
                  </div>
                </div>
              </template>

              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="90px"
                size="large"
              >
                <el-form-item label="用户名">
                  <el-input v-model="profileForm.username" disabled>
                    <template #prefix>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="邮箱">
                  <el-input v-model="profileForm.email" disabled>
                    <template #prefix>
                      <el-icon><Message /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="手机号" prop="phone">
                  <el-input
                    v-model="profileForm.phone"
                    placeholder="请输入手机号"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Phone /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="真实姓名" prop="realName">
                  <el-input
                    v-model="profileForm.realName"
                    placeholder="请输入真实姓名"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><UserFilled /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="部门">
                  <el-input v-model="profileForm.deptName" disabled>
                    <template #prefix>
                      <el-icon><OfficeBuilding /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="职位" prop="position">
                  <el-input
                    v-model="profileForm.position"
                    placeholder="请输入职位"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Briefcase /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio :label="0">未知</el-radio>
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="2">女</el-radio>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="角色">
                  <el-tag
                    v-for="role in profileForm.roles"
                    :key="role.id"
                    size="large"
                    style="margin-right: 8px"
                  >
                    {{ role.name }}
                  </el-tag>
                  <span v-if="!profileForm.roles || profileForm.roles.length === 0" style="color: #909399">
                    暂无角色
                  </span>
                </el-form-item>

                <el-form-item label="备注" prop="remark">
                  <el-input
                    v-model="profileForm.remark"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入备注信息"
                  />
                </el-form-item>

                <el-form-item>
                  <el-button type="primary" :loading="updateLoading" @click="handleUpdateProfile" class="submit-button">
                    <el-icon v-if="!updateLoading"><Check /></el-icon>
                    {{ updateLoading ? '保存中...' : '保存修改' }}
                  </el-button>
                  <el-button @click="loadUserInfo" class="reset-button">
                    <el-icon><Refresh /></el-icon>
                    重置
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>

          <!-- 修改密码 -->
          <el-col :xs="24" :sm="24" :md="12">
            <el-card class="form-card">
              <template #header>
                <div class="card-header">
                  <div class="header-left">
                    <div class="icon-wrapper" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                      <el-icon :size="20"><Lock /></el-icon>
                    </div>
                    <span>修改密码</span>
                  </div>
                </div>
              </template>

              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="90px"
                size="large"
              >
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入旧密码"
                    show-password
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="6-20个字符"
                    show-password
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-alert
                  title="密码修改后需要重新登录"
                  type="warning"
                  :closable="false"
                  style="margin-bottom: 20px"
                />

                <el-form-item>
                  <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword" class="submit-button">
                    <el-icon v-if="!passwordLoading"><Check /></el-icon>
                    {{ passwordLoading ? '修改中...' : '修改密码' }}
                  </el-button>
                  <el-button @click="resetPasswordForm" class="reset-button">
                    <el-icon><Refresh /></el-icon>
                    重置
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { changePassword, type PasswordChangeDTO, type UserDetailVO } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()

const userStore = useUserStore()

const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const updateLoading = ref(false)
const passwordLoading = ref(false)

const profileForm = reactive<Partial<UserDetailVO>>({
  username: '',
  email: '',
  phone: '',
  realName: '',
  deptName: '',
  position: '',
  gender: 0,
  avatar: '',
  roles: [],
  remark: ''
})

const passwordForm = reactive<PasswordChangeDTO>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateNewPassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const profileRules: FormRules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  try {
    // 从 store 或 localStorage 获取用户信息
    let userInfo = userStore.userInfo
    
    if (!userInfo) {
      const storedUserInfo = localStorage.getItem('userInfo')
      if (storedUserInfo) {
        userInfo = JSON.parse(storedUserInfo)
        userStore.setUserInfo(userInfo)
      }
    }
    
    if (!userInfo) {
      ElMessage.error('无法获取用户信息，请重新登录')
      router.push('/login')
      return
    }
    
    // 使用 store 中的用户信息填充表单
    // 注意：这里只能显示登录时返回的基本信息
    // 如果需要更详细的信息（如部门名称、角色等），需要后端提供相应的 API
    Object.assign(profileForm, {
      id: userInfo.id,
      username: userInfo.username,
      email: userInfo.email,
      phone: userInfo.phone || '',
      realName: userInfo.realName || '',
      gender: 0,
      avatar: '',
      roles: [],
      remark: ''
    })
  } catch (error: any) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      updateLoading.value = true
      try {
        // 目前个人中心只允许修改部分字段
        // 完整的用户编辑功能在用户管理页面
        ElMessage.info('个人信息修改功能需要联系管理员')
        
        // TODO: 如果需要用户自己修改信息，可以调用用户管理的更新接口
        // const { username, email, id, deptName, roles, ...data } = profileForm
        // await updateUser(id!, data)
        // ElMessage.success('个人信息更新成功')
        // await userStore.getUserInfo()
        // loadUserInfo()
      } catch (error) {
        console.error('更新失败:', error)
      } finally {
        updateLoading.value = false
      }
    }
  })
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true
      try {
        await changePassword(passwordForm)
        
        ElMessage.success('密码修改成功，请重新登录')
        
        // 清除登录信息并跳转到登录页
        setTimeout(() => {
          userStore.logout()
          router.push('/login')
        }, 1500)
      } catch (error) {
        ElMessage.error('修改密码失败')
        console.error('修改密码失败:', error)
      } finally {
        passwordLoading.value = false
      }
    }
  })
}

const resetPasswordForm = () => {
  passwordFormRef.value?.resetFields()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  max-width: 1400px;
  margin: 0 auto;
}

/* 用户信息横幅 */
.user-info-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 48px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.user-avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  color: white;
}

.user-basic-info h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.user-basic-info p {
  margin: 0 0 12px 0;
  font-size: 16px;
  opacity: 0.9;
}

.user-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  font-size: 14px;
  opacity: 0.85;
}

.user-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 表单区域 */
.form-section {
  margin-top: 24px;
}

.form-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  margin-bottom: 24px;
}

.form-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 12px 15px;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(30, 136, 229, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 2px 8px rgba(30, 136, 229, 0.25);
}

:deep(.el-input.is-disabled .el-input__wrapper) {
  background: #f5f7fa;
  box-shadow: none;
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 12px 15px;
}

/* 按钮样式 */
.submit-button {
  background: linear-gradient(135deg, #2a5298 0%, #1e88e5 100%);
  border: none;
  border-radius: 8px;
  padding: 12px 32px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 136, 229, 0.4);
}

.reset-button {
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 15px;
}

:deep(.el-alert) {
  border-radius: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .user-info-banner {
    padding: 24px;
  }

  .user-avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .user-basic-info h2 {
    font-size: 22px;
  }

  .main-content {
    padding: 16px;
  }

  .form-card {
    margin-bottom: 16px;
  }

  :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>
