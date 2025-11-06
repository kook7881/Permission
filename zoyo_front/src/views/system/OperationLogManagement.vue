<template>
  <div class="operation-log-management">
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
        <el-form-item label="操作模块">
          <el-input
            v-model="queryForm.module"
            placeholder="请输入模块名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-select v-model="queryForm.businessType" placeholder="请选择类型" clearable style="width: 150px">
            <el-option label="其它" :value="0" />
            <el-option label="新增" :value="1" />
            <el-option label="修改" :value="2" />
            <el-option label="删除" :value="3" />
            <el-option label="查询" :value="4" />
            <el-option label="导出" :value="5" />
            <el-option label="导入" :value="6" />
            <el-option label="授权" :value="7" />
          </el-select>
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
        v-permission="['system:operlog:delete']"
        @click="handleBatchDelete"
      >
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
      <el-button
        type="danger"
        plain
        v-permission="['system:operlog:delete']"
        @click="handleClear"
      >
        <el-icon><Delete /></el-icon>
        清空日志
      </el-button>
      <el-button
        type="warning"
        plain
        v-permission="['system:operlog:delete']"
        @click="handleClean"
      >
        <el-icon><Document /></el-icon>
        清理历史
      </el-button>
      <el-button
        type="success"
        plain
        @click="handleExport"
      >
        <el-icon><Download /></el-icon>
        导出
      </el-button>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        :default-sort="{ prop: 'operationTime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="日志ID" width="80" />
        <el-table-column prop="module" label="操作模块" width="120" />
        <el-table-column prop="businessType" label="业务类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getBusinessTypeTagType(row.businessType)">
              {{ getBusinessTypeName(row.businessType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="requestIp" label="请求IP" width="140" />
        <el-table-column prop="requestLocation" label="操作地点" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executionTime" label="执行时长" width="110">
          <template #default="{ row }">
            {{ row.executionTime }}ms
          </template>
        </el-table-column>
        <el-table-column prop="operationTime" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.operationTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleView(row)"
            >
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button
              type="danger"
              link
              v-permission="['system:operlog:delete']"
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
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialog.visible"
      title="操作日志详情"
      width="60%"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border v-if="detailDialog.data">
        <el-descriptions-item label="日志ID">{{ detailDialog.data.id }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ detailDialog.data.module }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">
          <el-tag :type="getBusinessTypeTagType(detailDialog.data.businessType)">
            {{ getBusinessTypeName(detailDialog.data.businessType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ detailDialog.data.description }}</el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">{{ detailDialog.data.method }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ detailDialog.data.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ detailDialog.data.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailDialog.data.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ detailDialog.data.realName }}</el-descriptions-item>
        <el-descriptions-item label="请求IP">{{ detailDialog.data.requestIp }}</el-descriptions-item>
        <el-descriptions-item label="操作地点">{{ detailDialog.data.requestLocation }}</el-descriptions-item>
        <el-descriptions-item label="浏览器">{{ detailDialog.data.browser }}</el-descriptions-item>
        <el-descriptions-item label="操作系统">{{ detailDialog.data.os }}</el-descriptions-item>
        <el-descriptions-item label="设备类型">{{ detailDialog.data.deviceType }}</el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="detailDialog.data.status === 1 ? 'success' : 'danger'">
            {{ detailDialog.data.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行时长">{{ detailDialog.data.executionTime }}ms</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatDateTime(detailDialog.data.operationTime) }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <el-input
            v-model="detailDialog.data.requestParam"
            type="textarea"
            :rows="5"
            readonly
          />
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <el-input
            v-model="detailDialog.data.responseResult"
            type="textarea"
            :rows="5"
            readonly
          />
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="detailDialog.data.errorMsg">
          <el-input
            v-model="detailDialog.data.errorMsg"
            type="textarea"
            :rows="3"
            readonly
          />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 清理历史对话框 -->
    <el-dialog
      v-model="cleanDialog.visible"
      title="清理历史日志"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="cleanDialog.form" label-width="100px">
        <el-form-item label="保留天数">
          <el-input-number
            v-model="cleanDialog.form.days"
            :min="1"
            :max="365"
            controls-position="right"
          />
          <span style="margin-left: 10px; color: #999;">天</span>
        </el-form-item>
        <el-alert
          title="将删除指定天数之前的所有操作日志"
          type="warning"
          :closable="false"
          style="margin-top: 10px"
        />
      </el-form>
      <template #footer>
        <el-button @click="cleanDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmClean">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, View, Download, Document } from '@element-plus/icons-vue'
import {
  getOperationLogList,
  getOperationLogById,
  deleteOperationLog,
  batchDeleteOperationLogs,
  clearOperationLogs,
  cleanOperationLogs,
  type OperationLog
} from '@/api/operationLog'

// 查询表单
const queryForm = reactive({
  username: '',
  module: '',
  businessType: undefined as number | undefined,
  status: undefined as number | undefined
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref<OperationLog[]>([])
const loading = ref(false)
const selectedIds = ref<number[]>([])

// 详情对话框
const detailDialog = reactive({
  visible: false,
  data: null as OperationLog | null
})

// 清理对话框
const cleanDialog = reactive({
  visible: false,
  form: {
    days: 30
  }
})

// 格式化时间
const formatDateTime = (dateTime: string | null | undefined) => {
  if (!dateTime) return '-'
  
  // 如果是字符串，转换为Date对象
  const date = typeof dateTime === 'string' ? new Date(dateTime) : dateTime
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取业务类型名称
const getBusinessTypeName = (type: number) => {
  const types = ['其它', '新增', '修改', '删除', '查询', '导出', '导入', '授权']
  return types[type] || '其它'
}

// 获取业务类型标签类型
const getBusinessTypeTagType = (type: number) => {
  const typeMap: Record<number, string> = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: '',
    5: 'success',
    6: 'warning',
    7: 'warning'
  }
  return typeMap[type] || 'info'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      page: pagination.page - 1, // 后端从0开始
      size: pagination.size,
      sortBy: 'operationTime',
      sortOrder: 'desc'
    }
    const response = await getOperationLogList(params)
    if (response.code === 200) {
      tableData.value = response.data.content
      pagination.total = response.data.totalElements
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  queryForm.username = ''
  queryForm.module = ''
  queryForm.businessType = undefined
  queryForm.status = undefined
  handleQuery()
}

// 选择变化
const handleSelectionChange = (selection: OperationLog[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 查看详情
const handleView = async (row: OperationLog) => {
  try {
    const response = await getOperationLogById(row.id)
    if (response.code === 200) {
      detailDialog.data = response.data
      detailDialog.visible = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = async (row: OperationLog) => {
  try {
    await ElMessageBox.confirm('确定要删除这条操作日志吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await deleteOperationLog(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条操作日志吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await batchDeleteOperationLogs(selectedIds.value)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 清空日志
const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有操作日志吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    const response = await clearOperationLogs()
    if (response.code === 200) {
      ElMessage.success('清空成功')
      loadData()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '清空失败')
    }
  }
}

// 清理历史
const handleClean = () => {
  cleanDialog.visible = true
}

// 确认清理
const handleConfirmClean = async () => {
  try {
    const date = new Date()
    date.setDate(date.getDate() - cleanDialog.form.days)
    const beforeTime = date.toISOString()
    
    const response = await cleanOperationLogs(beforeTime)
    if (response.code === 200) {
      ElMessage.success('清理成功')
      cleanDialog.visible = false
      loadData()
    }
  } catch (error: any) {
    ElMessage.error(error.message || '清理失败')
  }
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 分页大小变化
const handleSizeChange = () => {
  loadData()
}

// 当前页变化
const handleCurrentChange = () => {
  loadData()
}

// 组件挂载
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.operation-log-management {
  padding: 20px;
}

.search-card,
.toolbar-card,
.table-card {
  margin-bottom: 20px;
}
</style>
