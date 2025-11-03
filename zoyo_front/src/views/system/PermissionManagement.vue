<template>
  <div class="permission-management">
    <!-- 操作按钮 -->
    <el-card class="toolbar-card">
      <el-button 
        type="primary" 
        v-permission="['system:permission:create']"
        @click="handleCreate"
      >
        <el-icon><Plus /></el-icon>
        新增权限
      </el-button>
      <el-button @click="handleExpandAll">
        <el-icon><Fold /></el-icon>
        {{ expandAll ? '折叠全部' : '展开全部' }}
      </el-button>
    </el-card>

    <!-- 权限树表格 -->
    <el-card class="table-card">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="permissionTree"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="expandAll"
        stripe
        border
      >
        <el-table-column prop="permissionName" label="权限名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限标识" min-width="180" />
        <el-table-column prop="permissionType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.permissionType === 1" type="primary">菜单</el-tag>
            <el-tag v-else type="success">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="routePath" label="路由路径" min-width="150" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="visible" label="可见" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.visible" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              link 
              type="primary" 
              v-permission="['system:permission:update']"
              @click="handleEdit(row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button 
              link 
              type="success" 
              v-permission="['system:permission:create']"
              @click="handleAddChild(row)"
            >
              <el-icon><Plus /></el-icon>
              新增子权限
            </el-button>
            <el-button 
              link 
              type="danger" 
              v-permission="['system:permission:delete']"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 权限表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="permissionFormRef"
        :model="permissionForm"
        :rules="permissionRules"
        label-width="100px"
      >
        <el-form-item label="父权限" prop="parentId">
          <el-tree-select
            v-model="permissionForm.parentId"
            :data="permissionTreeOptions"
            :props="{ label: 'permissionName', value: 'id', children: 'children' }"
            placeholder="请选择父权限（不选则为顶级权限）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="permissionForm.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionCode">
          <el-input v-model="permissionForm.permissionCode" placeholder="如：system:user:list" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-radio-group v-model="permissionForm.permissionType">
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="路由路径" prop="routePath" v-if="permissionForm.permissionType === 1">
          <el-input v-model="permissionForm.routePath" placeholder="如：/system/user" />
        </el-form-item>
        <el-form-item label="组件路径" prop="componentPath" v-if="permissionForm.permissionType === 1">
          <el-input v-model="permissionForm.componentPath" placeholder="如：system/user/index" />
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="permissionForm.permissionType === 1">
          <el-input v-model="permissionForm.icon" placeholder="如：User" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="permissionForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="是否可见" prop="visible" v-if="permissionForm.permissionType === 1">
          <el-radio-group v-model="permissionForm.visible">
            <el-radio :label="true">是</el-radio>
            <el-radio :label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="permissionForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="permissionForm.remark"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getPermissionTree,
  createPermission,
  updatePermission,
  deletePermission
} from '@/api/permission'

// 权限树
const permissionTree = ref<any[]>([])
const permissionTreeOptions = ref<any[]>([])
const loading = ref(false)
const expandAll = ref(true)
const tableRef = ref()

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const submitting = ref(false)

// 权限表单
const permissionFormRef = ref<FormInstance>()
const permissionForm = reactive({
  id: null as number | null,
  parentId: null as number | null,
  permissionName: '',
  permissionCode: '',
  permissionType: 1,
  routePath: '',
  componentPath: '',
  icon: '',
  sortOrder: 0,
  visible: true,
  status: 1,
  remark: ''
})

// 表单验证规则
const permissionRules: FormRules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ],
  permissionType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
}

// 加载权限树
const loadPermissionTree = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    permissionTree.value = res.data
    // 构建树选择器选项（添加顶级选项）
    permissionTreeOptions.value = [
      { id: 0, permissionName: '顶级权限', children: res.data }
    ]
  } catch (error) {
    ElMessage.error('加载权限树失败')
  } finally {
    loading.value = false
  }
}

// 展开/折叠全部
const handleExpandAll = () => {
  expandAll.value = !expandAll.value
  nextTick(() => {
    toggleRowExpansion(permissionTree.value, expandAll.value)
  })
}

// 递归展开/折叠所有行
const toggleRowExpansion = (data: any[], isExpand: boolean) => {
  data.forEach((item) => {
    if (tableRef.value) {
      tableRef.value.toggleRowExpansion(item, isExpand)
    }
    if (item.children && item.children.length > 0) {
      toggleRowExpansion(item.children, isExpand)
    }
  })
}

// 新增
const handleCreate = () => {
  isEdit.value = false
  dialogTitle.value = '新增权限'
  resetForm()
  dialogVisible.value = true
}

// 新增子权限
const handleAddChild = (row: any) => {
  isEdit.value = false
  dialogTitle.value = '新增子权限'
  resetForm()
  permissionForm.parentId = row.id
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑权限'
  Object.assign(permissionForm, {
    ...row,
    parentId: row.parentId === 0 ? null : row.parentId
  })
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
      type: 'warning'
    })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadPermissionTree()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!permissionFormRef.value) return

  await permissionFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        ...permissionForm,
        parentId: permissionForm.parentId || 0
      }

      if (isEdit.value && permissionForm.id) {
        await updatePermission(permissionForm.id, data)
        ElMessage.success('更新成功')
      } else {
        await createPermission(data)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadPermissionTree()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  permissionForm.id = null
  permissionForm.parentId = null
  permissionForm.permissionName = ''
  permissionForm.permissionCode = ''
  permissionForm.permissionType = 1
  permissionForm.routePath = ''
  permissionForm.componentPath = ''
  permissionForm.icon = ''
  permissionForm.sortOrder = 0
  permissionForm.visible = true
  permissionForm.status = 1
  permissionForm.remark = ''
  permissionFormRef.value?.clearValidate()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 初始化
onMounted(() => {
  loadPermissionTree()
})
</script>

<style scoped>
.permission-management {
  padding: 20px;
}

.toolbar-card,
.table-card {
  margin-bottom: 20px;
}
</style>
