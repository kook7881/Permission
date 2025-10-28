<template>
  <div class="user-management">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="用户名">
          <el-input
            v-model="queryForm.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="queryForm.email"
            placeholder="请输入邮箱"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryForm.phone"
            placeholder="请输入手机号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="queryForm.deptId"
            :data="departmentTree"
            :props="{ label: 'name', value: 'id' }"
            placeholder="请选择部门"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button
            type="primary"
            v-permission="['system:user:create']"
            @click="handleCreate"
          >
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            :disabled="selectedIds.length === 0"
            v-permission="['system:user:delete']"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            :disabled="selectedIds.length === 0"
            v-permission="['system:user:update']"
            @click="handleBatchEnable"
          >
            <el-icon><Check /></el-icon>
            批量启用
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            :disabled="selectedIds.length === 0"
            v-permission="['system:user:update']"
            @click="handleBatchDisable"
          >
            <el-icon><Close /></el-icon>
            批量禁用
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="info"
            @click="handleExport"
          >
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="info"
            @click="handleImport"
          >
            <el-icon><Upload /></el-icon>
            导入
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="userList"
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column 
          type="selection" 
          width="55" 
          align="center"
          :selectable="(row) => row.username !== 'admin'"
        />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column label="角色" width="150">
          <template #default="{ row }">
            <el-tag
              v-for="role in row.roles"
              :key="role.id"
              size="small"
              style="margin-right: 5px"
            >
              {{ role.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :disabled="row.username === 'admin'"
              v-permission="['system:user:update']"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <!-- 超级管理员显示保护提示 -->
            <template v-if="row.username === 'admin'">
              <el-tag type="info" size="small">
                <el-icon><Lock /></el-icon>
                系统保护
              </el-tag>
            </template>
            <!-- 普通用户显示操作按钮 -->
            <template v-else>
              <el-button
                type="primary"
                size="small"
                link
                v-permission="['system:user:update']"
                @click="handleEdit(row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                type="warning"
                size="small"
                link
                v-permission="['system:user:reset-password']"
                @click="handleResetPassword(row)"
              >
                <el-icon><Key /></el-icon>
                重置密码
              </el-button>
              <el-button
                type="success"
                size="small"
                link
                v-permission="['system:user:role']"
                @click="handleAssignRoles(row)"
              >
                <el-icon><User /></el-icon>
                分配角色
              </el-button>
              <el-button
                type="danger"
                size="small"
                link
                v-permission="['system:user:delete']"
                @click="handleDelete(row)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
            v-model="formData.deptId"
            :data="departmentTree"
            :props="{ label: 'name', value: 'id' }"
            placeholder="请选择部门"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="formData.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select
            v-model="formData.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
    >
      <el-form label-width="80px">
        <el-form-item label="选择角色">
          <el-select
            v-model="selectedRoleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
            collapse-tags
            collapse-tags-tooltip
          >
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入用户"
      width="500px"
    >
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx,.xls"
        drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>


<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  queryUsers,
  createUser,
  updateUser,
  deleteUser,
  batchDeleteUsers,
  updateUserStatus,
  batchUpdateUserStatus,
  resetPassword,
  assignUserRoles,
  getUserRoles,
  exportUsers,
  importUsers,
  type UserDetailVO,
  type UserCreateDTO,
  type UserUpdateDTO,
  type UserQueryDTO,
  type RoleDTO
} from '@/api/user'
import { getDepartmentTree, type DepartmentTreeVO } from '@/api/department'
import { getAllRoles } from '@/api/role'
import { encryptPassword } from '@/utils/crypto'

// 查询表单
const queryForm = reactive<UserQueryDTO>({
  username: '',
  email: '',
  phone: '',
  status: undefined,
  deptId: undefined,
  page: 1,
  size: 10,
  sortBy: 'id',
  sortDirection: 'asc'
})

// 数据
const loading = ref(false)
const userList = ref<UserDetailVO[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const departmentTree = ref<DepartmentTreeVO[]>([])
const roleList = ref<RoleDTO[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const formData = reactive<UserCreateDTO | UserUpdateDTO>({
  username: '',
  password: '',
  email: '',
  phone: '',
  realName: '',
  deptId: undefined,
  position: '',
  gender: 0,
  status: 1,
  roleIds: [],
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 角色对话框
const roleDialogVisible = ref(false)
const selectedRoleIds = ref<number[]>([])
const currentUserId = ref<number>()

// 导入对话框
const importDialogVisible = ref(false)
const uploadRef = ref()

// 初始化
onMounted(() => {
  loadDepartmentTree()
  loadRoleList()
  handleQuery()
})

// 加载部门树
const loadDepartmentTree = async () => {
  try {
    const res = await getDepartmentTree()
    departmentTree.value = res.data
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

// 加载角色列表
const loadRoleList = async () => {
  try {
    const res = await getAllRoles()
    roleList.value = res.data
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 查询
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await queryUsers(queryForm)
    userList.value = res.data.content
    total.value = res.data.totalElements
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  queryForm.username = ''
  queryForm.email = ''
  queryForm.phone = ''
  queryForm.status = undefined
  queryForm.deptId = undefined
  queryForm.page = 1
  handleQuery()
}

// 选择变化
const handleSelectionChange = (selection: UserDetailVO[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增
const handleCreate = () => {
  dialogTitle.value = '新增用户'
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑
const handleEdit = async (row: UserDetailVO) => {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  dialogVisible.value = true
  
  // 填充表单数据
  Object.assign(formData, {
    id: row.id,
    username: row.username,
    email: row.email,
    phone: row.phone,
    realName: row.realName,
    deptId: row.deptId,
    position: row.position,
    gender: row.gender,
    status: row.status,
    roleIds: row.roles?.map(r => r.id) || [],
    remark: row.remark
  })
}

// 删除
const handleDelete = async (row: UserDetailVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个用户吗？`, '提示', {
      type: 'warning'
    })
    await batchDeleteUsers(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 状态变化
const handleStatusChange = async (row: UserDetailVO) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    ElMessage.error('状态修改失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

// 批量启用
const handleBatchEnable = async () => {
  try {
    await batchUpdateUserStatus(selectedIds.value, 1)
    ElMessage.success('启用成功')
    handleQuery()
  } catch (error) {
    ElMessage.error('启用失败')
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  try {
    await batchUpdateUserStatus(selectedIds.value, 0)
    ElMessage.success('禁用成功')
    handleQuery()
  } catch (error) {
    ElMessage.error('禁用失败')
  }
}

// 重置密码
const handleResetPassword = async (row: UserDetailVO) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入新密码', '重置密码', {
      inputPattern: /^.{6,20}$/,
      inputErrorMessage: '密码长度在 6 到 20 个字符'
    })
    // 密码需要加密后发送
    const encryptedPassword = encryptPassword(value)
    await resetPassword(row.id, encryptedPassword)
    ElMessage.success('密码重置成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('密码重置失败')
    }
  }
}

// 分配角色
const handleAssignRoles = async (row: UserDetailVO) => {
  currentUserId.value = row.id
  roleDialogVisible.value = true
  
  // 加载用户当前角色
  try {
    const res = await getUserRoles(row.id)
    selectedRoleIds.value = res.data.map((r: RoleDTO) => r.id)
  } catch (error) {
    ElMessage.error('加载用户角色失败')
  }
}

// 提交角色分配
const handleRoleSubmit = async () => {
  if (!currentUserId.value) return
  
  submitLoading.value = true
  try {
    await assignUserRoles(currentUserId.value, selectedRoleIds.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('角色分配失败')
  } finally {
    submitLoading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (isEdit.value) {
        // 编辑时，如果密码为空，则不发送密码字段
        const updateData = { ...formData } as UserUpdateDTO
        if (!updateData.password || updateData.password.trim() === '') {
          delete updateData.password
        } else {
          // 如果修改密码，需要加密
          updateData.password = encryptPassword(updateData.password)
        }
        await updateUser(updateData.id!, updateData)
        ElMessage.success('更新成功')
      } else {
        // 创建用户时，密码需要加密
        const createData = { ...formData } as UserCreateDTO
        createData.password = encryptPassword(createData.password)
        await createUser(createData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      handleQuery()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    username: '',
    password: '',
    email: '',
    phone: '',
    realName: '',
    deptId: undefined,
    position: '',
    gender: 0,
    status: 1,
    roleIds: [],
    remark: ''
  })
}

// 导出
const handleExport = async () => {
  try {
    const res = await exportUsers(queryForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `用户列表_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 导入
const handleImport = () => {
  importDialogVisible.value = true
}

// 提交导入
const handleImportSubmit = async () => {
  const files = uploadRef.value?.uploadFiles
  if (!files || files.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  
  submitLoading.value = true
  try {
    await importUsers(files[0].raw)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    submitLoading.value = false
  }
}
</script>


<style scoped>
.user-management {
  padding: 20px;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
