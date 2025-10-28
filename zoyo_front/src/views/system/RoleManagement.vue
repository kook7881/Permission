<template>
  <div class="role-management">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.roleName"
            placeholder="请输入角色名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
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

    <!-- 操作按钮 -->
    <el-card class="toolbar-card">
      <el-button 
        type="primary" 
        v-permission="['system:role:create']"
        @click="handleCreate"
      >
        <el-icon><Plus /></el-icon>
        新增角色
      </el-button>
      <el-button
        type="danger"
        :disabled="selectedIds.length === 0"
        v-permission="['system:role:delete']"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="roleList"
        @selection-change="handleSelectionChange"
        stripe
        border
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleCode" label="角色编码" min-width="150" />
        <el-table-column prop="roleSort" label="排序" width="80" align="center" />
        <el-table-column prop="dataScope" label="数据范围" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.dataScope === 1" type="success">全部</el-tag>
            <el-tag v-else-if="row.dataScope === 2" type="warning">本部门</el-tag>
            <el-tag v-else type="info">本人</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="!hasPermission('system:role:update')" :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
            <el-switch
              v-else
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="isSystem" label="系统角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isSystem" type="danger">是</el-tag>
            <el-tag v-else type="success">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              link 
              type="primary" 
              v-permission="['system:role:update']"
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              link 
              type="warning" 
              v-permission="['system:role:query', 'system:role:permission']"
              @click="handleAssignPermission(row)"
            >
              <el-icon><Lock /></el-icon>
              {{ hasPermission('system:role:permission') ? '分配权限' : '查看权限' }}
            </el-button>
            <el-button
              link
              type="danger"
              :disabled="row.isSystem"
              v-permission="['system:role:delete']"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRoleList"
        @current-change="loadRoleList"
      />
    </el-card>

    <!-- 角色表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input
            v-model="roleForm.roleCode"
            placeholder="请输入角色编码"
            :disabled="isEdit && roleForm.isSystem"
          />
        </el-form-item>
        <el-form-item label="排序" prop="roleSort">
          <el-input-number v-model="roleForm.roleSort" :min="0" />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="roleForm.dataScope" placeholder="请选择数据范围">
            <el-option label="全部数据" :value="1" />
            <el-option label="本部门数据" :value="2" />
            <el-option label="本人数据" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="roleForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="hasPermission('system:role:permission') ? '分配权限' : '查看权限'"
      width="600px"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="{ label: 'permissionName', children: 'children' }"
        node-key="id"
        show-checkbox
        :disabled="!hasPermission('system:role:permission')"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">
          {{ hasPermission('system:role:permission') ? '取消' : '关闭' }}
        </el-button>
        <el-button 
          v-if="hasPermission('system:role:permission')"
          type="primary" 
          @click="handlePermissionSubmit" 
          :loading="submitting"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElTree } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/date'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  batchDeleteRoles,
  updateRoleStatus,
  getRolePermissions,
  assignRolePermissions
} from '@/api/role'
import { getPermissionTree } from '@/api/permission'

// 用户store
const userStore = useUserStore()

// 检查权限
const hasPermission = (permission: string) => {
  return userStore.hasPermission(permission)
}

// 搜索表单
const searchForm = reactive({
  roleName: '',
  status: null as number | null
})

// 角色列表
const roleList = ref<any[]>([])
const loading = ref(false)
const selectedIds = ref<number[]>([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)

// 角色表单
const roleFormRef = ref<FormInstance>()
const roleForm = reactive({
  id: null as number | null,
  roleName: '',
  roleCode: '',
  roleSort: 0,
  dataScope: 1,
  status: 1,
  isSystem: false,
  remark: ''
})

// 表单验证规则
const roleRules: FormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  dataScope: [
    { required: true, message: '请选择数据范围', trigger: 'change' }
  ]
}

// 权限分配
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref<InstanceType<typeof ElTree>>()
const permissionTree = ref<any[]>([])
const currentRoleId = ref<number | null>(null)

// 加载角色列表
const loadRoleList = async () => {
  loading.value = true
  try {
    const res = await getRoleList({
      roleName: searchForm.roleName || undefined,
      status: searchForm.status ?? undefined,
      page: pagination.page,
      size: pagination.size
    })
    roleList.value = res.data.content
    pagination.total = res.data.totalElements
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadRoleList()
}

// 重置
const handleReset = () => {
  searchForm.roleName = ''
  searchForm.status = null
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(roleForm, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  if (row.isSystem) {
    ElMessage.warning('系统内置角色不允许删除')
    return
  }

  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      type: 'warning'
    })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的角色吗？', '提示', {
      type: 'warning'
    })
    await batchDeleteRoles(selectedIds.value)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 状态变化
const handleStatusChange = async (row: any) => {
  try {
    await updateRoleStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!roleFormRef.value) return

  await roleFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value && roleForm.id) {
        await updateRole(roleForm.id, roleForm)
        ElMessage.success('更新成功')
      } else {
        await createRole(roleForm)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadRoleList()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// 分配权限
const handleAssignPermission = async (row: any) => {
  currentRoleId.value = row.id
  permissionDialogVisible.value = true

  // 加载权限树
  try {
    const treeRes = await getPermissionTree()
    permissionTree.value = treeRes.data

    // 加载角色已有权限
    const permRes = await getRolePermissions(row.id)
    const permissionIds = permRes.data || []
    
    // 设置选中的权限（只设置叶子节点，避免父节点重复）
    setTimeout(() => {
      if (permissionTreeRef.value) {
        // 清空之前的选中状态
        permissionTreeRef.value.setCheckedKeys([], false)
        
        // 获取所有叶子节点ID
        const leafNodeIds = getLeafNodeIds(permissionTree.value)
        
        // 只设置叶子节点为选中状态
        const leafPermissions = permissionIds.filter((id: number) => leafNodeIds.includes(id))
        permissionTreeRef.value.setCheckedKeys(leafPermissions, false)
      }
    }, 100)
  } catch (error) {
    ElMessage.error('加载权限数据失败')
  }
}

// 获取所有叶子节点ID
const getLeafNodeIds = (nodes: any[]): number[] => {
  const leafIds: number[] = []
  
  const traverse = (nodeList: any[]) => {
    nodeList.forEach(node => {
      if (!node.children || node.children.length === 0) {
        // 叶子节点
        leafIds.push(node.id)
      } else {
        // 有子节点，继续遍历
        traverse(node.children)
      }
    })
  }
  
  traverse(nodes)
  return leafIds
}

// 提交权限分配
const handlePermissionSubmit = async () => {
  if (!currentRoleId.value) return

  submitting.value = true
  try {
    const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() || []
    const allKeys = [...checkedKeys, ...halfCheckedKeys] as number[]

    await assignRolePermissions(currentRoleId.value, allKeys)
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '权限分配失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  roleForm.id = null
  roleForm.roleName = ''
  roleForm.roleCode = ''
  roleForm.roleSort = 0
  roleForm.dataScope = 1
  roleForm.status = 1
  roleForm.isSystem = false
  roleForm.remark = ''
  roleFormRef.value?.clearValidate()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 初始化
onMounted(() => {
  loadRoleList()
})
</script>

<style scoped>
.role-management {
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
