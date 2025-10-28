<template>
  <div class="department-management">
    <!-- 操作栏 -->
    <el-card class="toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button
            type="primary"
            v-permission="['system:dept:create']"
            @click="handleCreate"
          >
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button @click="handleExpandAll">
            <el-icon><Expand /></el-icon>
            展开全部
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button @click="handleCollapseAll">
            <el-icon><Fold /></el-icon>
            折叠全部
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 部门树表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="departmentList"
        row-key="id"
        :tree-props="{ children: 'children' }"
        :default-expand-all="expandAll"
        border
      >
        <el-table-column prop="name" label="部门名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="leaderName" label="负责人" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              v-permission="['system:dept:create']"
              @click="handleCreateChild(row)"
            >
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              v-permission="['system:dept:update']"
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              v-permission="['system:dept:delete']"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
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
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            :data="departmentTreeOptions"
            :props="{ label: 'name', value: 'id' }"
            placeholder="请选择上级部门"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="sort">
          <el-input-number
            v-model="formData.sort"
            :min="0"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="负责人" prop="leaderId">
          <el-select
            v-model="formData.leaderId"
            placeholder="请选择负责人"
            clearable
            filterable
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.realName || user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
  </div>
</template>


<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getDepartmentTree,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  checkDepartmentHasUsers,
  checkDepartmentHasChildren,
  type DepartmentDTO,
  type DepartmentTreeVO
} from '@/api/department'
import { queryUsers, type UserDetailVO } from '@/api/user'

// 数据
const loading = ref(false)
const departmentList = ref<DepartmentTreeVO[]>([])
const expandAll = ref(true)
const userList = ref<UserDetailVO[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 表单数据
const formData = reactive<DepartmentDTO>({
  name: '',
  parentId: 0,
  code: '',
  sort: 0,
  leaderId: undefined,
  phone: '',
  email: '',
  status: 1,
  remark: ''
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { max: 100, message: '部门名称长度不能超过100个字符', trigger: 'blur' }
  ],
  parentId: [
    { required: true, message: '请选择上级部门', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 部门树选项（用于选择上级部门）
const departmentTreeOptions = computed(() => {
  const options: DepartmentTreeVO[] = [
    {
      id: 0,
      name: '顶级部门',
      parentId: -1,
      sort: 0,
      status: 1,
      children: departmentList.value
    }
  ]
  return options
})

// 初始化
onMounted(() => {
  loadDepartmentTree()
  loadUserList()
})

// 加载部门树
const loadDepartmentTree = async () => {
  loading.value = true
  try {
    const res = await getDepartmentTree()
    departmentList.value = res.data
  } catch (error) {
    ElMessage.error('加载部门树失败')
  } finally {
    loading.value = false
  }
}

// 加载用户列表
const loadUserList = async () => {
  try {
    const res = await queryUsers({ page: 0, size: 1000 })
    userList.value = res.data.content
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

// 展开全部
const handleExpandAll = () => {
  expandAll.value = true
}

// 折叠全部
const handleCollapseAll = () => {
  expandAll.value = false
}

// 新增
const handleCreate = () => {
  dialogTitle.value = '新增部门'
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 新增子部门
const handleCreateChild = (row: DepartmentTreeVO) => {
  dialogTitle.value = '新增子部门'
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
  formData.parentId = row.id
}

// 编辑
const handleEdit = (row: DepartmentTreeVO) => {
  dialogTitle.value = '编辑部门'
  isEdit.value = true
  dialogVisible.value = true
  
  // 填充表单数据
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    parentId: row.parentId,
    code: row.code,
    sort: row.sort,
    leaderId: row.leaderId,
    phone: row.phone,
    email: row.email,
    status: row.status,
    remark: row.remark
  })
}

// 删除
const handleDelete = async (row: DepartmentTreeVO) => {
  try {
    // 检查是否有子部门
    const hasChildrenRes = await checkDepartmentHasChildren(row.id)
    if (hasChildrenRes.data) {
      ElMessage.warning('该部门下存在子部门，无法删除')
      return
    }
    
    // 检查是否有用户
    const hasUsersRes = await checkDepartmentHasUsers(row.id)
    if (hasUsersRes.data) {
      ElMessage.warning('该部门下存在用户，无法删除')
      return
    }
    
    await ElMessageBox.confirm(`确定要删除部门 "${row.name}" 吗？`, '提示', {
      type: 'warning'
    })
    
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    loadDepartmentTree()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
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
        await updateDepartment(formData.id!, formData)
        ElMessage.success('更新成功')
      } else {
        await createDepartment(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadDepartmentTree()
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
    name: '',
    parentId: 0,
    code: '',
    sort: 0,
    leaderId: undefined,
    phone: '',
    email: '',
    status: 1,
    remark: ''
  })
}
</script>

<style scoped>
.department-management {
  padding: 20px;
}

.toolbar-card,
.table-card {
  margin-bottom: 20px;
}
</style>
