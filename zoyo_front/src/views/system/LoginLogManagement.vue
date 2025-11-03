<template>
  <div class="login-log-management">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="用户名">
          <el-input
            v-model="queryForm.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleQuery"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
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
      <el-button
        type="danger"
        :disabled="selectedIds.length === 0"
        v-permission="['system:loginlog:delete']"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button
        type="danger"
        plain
        v-permission="['system:loginlog:delete']"
        @click="handleClear"
      >
        <el-icon><Delete /></el-icon>
        清空日志
      </el-button>
      <el-button @click="handleExport">
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="loginIp" label="登录IP" width="150" />
        <el-table-column prop="loginLocation" label="登录地点" width="180" />
        <el-table-column prop="browser" label="浏览器" width="120" />
        <el-table-column prop="os" label="操作系统" width="120" />
        <el-table-column prop="deviceType" label="设备类型" width="100" align="center" />
        <el-table-column prop="loginTime" label="登录时间" width="180" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="提示消息" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleView(row)"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              v-permission="['system:loginlog:delete']"
              @click="handleDelete(row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="登录日志详情"
      width="600px"
    >
      <el-descriptions :column="1" border v-if="currentLog">
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentLog.userId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="登录IP">{{ currentLog.loginIp }}</el-descriptions-item>
        <el-descriptions-item label="登录地点">{{ currentLog.loginLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="浏览器">{{ currentLog.browser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{ currentLog.os || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备类型">{{ currentLog.deviceType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="登录时间">{{ currentLog.loginTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提示消息">{{ currentLog.message }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  queryLoginLogs,
  deleteLoginLog,
  batchDeleteLoginLogs,
  clearLoginLogs,
  type LoginLog
} from '@/api/loginLog'

// 查询表单
const queryForm = reactive({
  username: '',
  status: undefined as number | undefined,
  page: 1,
  size: 10,
  sortBy: 'loginTime',
  sortOrder: 'desc'
})

// 数据
const loading = ref(false)
const tableData = ref<LoginLog[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

// 详情对话框
const detailVisible = ref(false)
const currentLog = ref<LoginLog | null>(null)

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      page: queryForm.page - 1 // 后端从0开始
    }
    const res = await queryLoginLogs(params)
    tableData.value = res.data.content
    total.value = res.data.totalElements
  } catch (error: any) {
    ElMessage.error(error.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryForm.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryForm.username = ''
  queryForm.status = undefined
  handleQuery()
}

// 页码变化
const handlePageChange = () => {
  loadData()
}

// 每页条数变化
const handleSizeChange = () => {
  queryForm.page = 1
  loadData()
}

// 选择变化
const handleSelectionChange = (selection: LoginLog[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 查看详情
const handleView = (row: LoginLog) => {
  currentLog.value = row
  detailVisible.value = true
}

// 删除
const handleDelete = async (row: LoginLog) => {
  try {
    await ElMessageBox.confirm('确定要删除该登录日志吗？', '提示', {
      type: 'warning'
    })
    await deleteLoginLog(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条登录日志吗？`, '提示', {
      type: 'warning'
    })
    await batchDeleteLoginLogs(selectedIds.value)
    ElMessage.success('删除成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 清空日志
const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有登录日志吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定清空',
      cancelButtonText: '取消'
    })
    await clearLoginLogs()
    ElMessage.success('清空成功')
    loadData()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '清空失败')
    }
  }
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.login-log-management {
  padding: 20px;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
