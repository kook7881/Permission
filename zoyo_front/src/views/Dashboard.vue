<template>
  <div class="dashboard-container">
    <!-- 顶部标题栏 -->
    <header class="dashboard-header">
      <div class="header-left">
        <div class="system-status">
          <span class="status-dot"></span>
          <span class="status-text">系统运行正常</span>
        </div>
      </div>
      <div class="header-center">
        <h1 class="dashboard-title">
          <span class="title-icon">📊</span>
          Zoyo 认证系统 - 数据监控大屏
        </h1>
        <p class="dashboard-subtitle">Real-time Data Visualization Platform</p>
      </div>
      <div class="header-right">
        <div class="time-display">
          <el-icon class="time-icon"><Clock /></el-icon>
          {{ currentTime }}
        </div>
        <div class="weather-display">
          <el-icon><Sunny /></el-icon>
          <span>22°C 晴</span>
        </div>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="dashboard-main">
      <!-- 顶部数据卡片 -->
      <div class="stats-cards">
        <div class="stat-card" v-for="(stat, index) in statsData" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="36"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-header">
              <div class="stat-label">{{ stat.label }}</div>
              <el-tooltip :content="stat.description" placement="top">
                <el-icon class="info-icon"><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-footer">
              <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
                <el-icon><component :is="stat.trend > 0 ? 'CaretTop' : 'CaretBottom'" /></el-icon>
                <span>{{ Math.abs(stat.trend) }}%</span>
              </div>
              <div class="stat-compare">较昨日</div>
            </div>
          </div>
          <div class="stat-sparkline">
            <div class="sparkline-bar" v-for="(bar, i) in 8" :key="i" :style="{ height: `${Math.random() * 60 + 20}%` }"></div>
          </div>
        </div>
      </div>

      <!-- 主要内容区：左中右三栏布局 -->
      <div class="main-content-layout">
        <!-- 左侧栏 -->
        <div class="left-column">
          <!-- 实时数据流 -->
          <div class="chart-card realtime-card">
            <div class="card-header">
              <h3><el-icon><Notification /></el-icon> 实时动态</h3>
              <el-tag type="success" size="small">实时</el-tag>
            </div>
            <div class="realtime-list">
              <div class="realtime-item" v-for="(item, index) in realtimeData" :key="index">
                <div class="realtime-time">{{ item.time }}</div>
                <div class="realtime-content">
                  <el-icon :color="item.color"><component :is="item.icon" /></el-icon>
                  <span>{{ item.text }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 热门页面排行 -->
          <div class="chart-card ranking-card">
            <div class="card-header">
              <h3><el-icon><TrendCharts /></el-icon> 热门页面 TOP5</h3>
            </div>
            <div class="ranking-list">
              <div class="ranking-item" v-for="(page, index) in topPages" :key="index">
                <div class="rank-number" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
                <div class="rank-info">
                  <div class="rank-name">{{ page.name }}</div>
                  <div class="rank-bar">
                    <div class="rank-progress" :style="{ width: `${page.percent}%` }"></div>
                  </div>
                </div>
                <div class="rank-value">{{ page.visits }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 中间栏：中国地图 -->
        <div class="center-column">
          <div class="chart-card map-card-large">
            <div class="card-header">
              <h3><el-icon><Location /></el-icon> 用户地域分布</h3>
              <div class="header-actions">
                <el-tag>全国</el-tag>
                <span class="total-users">总用户: {{ dashboardData?.totalUsers || 0 }}</span>
              </div>
            </div>
            <div ref="mapChart" class="chart-container-large"></div>
          </div>
        </div>

        <!-- 右侧栏 -->
        <div class="right-column">
          <!-- 登录趋势 -->
          <div class="chart-card">
            <div class="card-header">
              <h3><el-icon><DataLine /></el-icon> 登录趋势</h3>
              <el-radio-group v-model="loginPeriod" size="small">
                <el-radio-button label="day">日</el-radio-button>
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
            <div ref="loginTrendChart" class="chart-container-small"></div>
          </div>

          <!-- 设备分布 -->
          <div class="chart-card">
            <div class="card-header">
              <h3><el-icon><Monitor /></el-icon> 设备分布</h3>
            </div>
            <div ref="deviceChart" class="chart-container-small"></div>
          </div>
        </div>
      </div>

      <!-- 底部图表区 -->
      <div class="charts-row-bottom">
        <!-- 用户活跃度 -->
        <div class="chart-card">
          <div class="card-header">
            <h3><el-icon><Timer /></el-icon> 用户活跃度</h3>
            <span class="card-subtitle">24小时活跃分布</span>
          </div>
          <div ref="activeChart" class="chart-container"></div>
        </div>

        <!-- 注册趋势 -->
        <div class="chart-card">
          <div class="card-header">
            <h3><el-icon><UserFilled /></el-icon> 注册趋势</h3>
            <span class="card-subtitle">近30天注册数据</span>
          </div>
          <div ref="registerChart" class="chart-container"></div>
        </div>

        <!-- 权限使用统计 -->
        <div class="chart-card">
          <div class="card-header">
            <h3><el-icon><Key /></el-icon> 权限使用统计</h3>
            <span class="card-subtitle">各权限调用次数</span>
          </div>
          <div ref="permissionChart" class="chart-container"></div>
        </div>
      </div>

      <!-- 数据表格区 -->
      <div class="tables-section">
        <div class="chart-card table-card">
          <div class="card-header">
            <h3><el-icon><List /></el-icon> 最近登录记录</h3>
            <el-button type="primary" link @click="$router.push('/system/login-log')">查看全部</el-button>
          </div>
          <el-table :data="recentLogins" style="width: 100%" :header-cell-style="{ background: '#1a1a2e', color: '#fff' }">
            <el-table-column prop="username" label="用户名" width="150" />
            <el-table-column prop="realName" label="真实姓名" width="120" />
            <el-table-column prop="loginTime" label="登录时间" width="180" />
            <el-table-column prop="ip" label="IP地址" width="150" />
            <el-table-column prop="location" label="登录地点" width="180" />
            <el-table-column prop="device" label="设备" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'online' ? 'success' : 'info'" size="small">
                  {{ row.status === 'online' ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

// 当前时间
const currentTime = ref('')

// 统计数据
const statsData = ref([
  {
    label: '总用户数',
    value: '0',
    trend: 0,
    icon: 'User',
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    description: '系统注册用户总数'
  },
  {
    label: '今日登录',
    value: '0',
    trend: 0,
    icon: 'UserFilled',
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    description: '今日登录用户数量'
  },
  {
    label: '今日注册',
    value: '0',
    trend: 0,
    icon: 'Plus',
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    description: '今日新增注册用户'
  },
  {
    label: '在线用户',
    value: '0',
    trend: 0,
    icon: 'Connection',
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    description: '当前在线用户数量'
  },
  {
    label: '活跃用户',
    value: '0',
    trend: 0,
    icon: 'Star',
    color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    description: '本周活跃用户数量'
  },
  {
    label: '系统访问',
    value: '0',
    trend: 0,
    icon: 'View',
    color: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
    description: '今日系统访问次数'
  }
])

// 登录周期
const loginPeriod = ref('week')

// 实时数据（从后端获取或移除此模块）
const realtimeData = ref<any[]>([])

// 热门页面（从后端获取或移除此模块）
const topPages = ref<any[]>([])

// 最近登录记录（从后端获取或移除此模块）
const recentLogins = ref<any[]>([])

// 后端数据
const dashboardData = ref<any>(null)

// ECharts 实例
let mapChartInstance: echarts.ECharts | null = null
let loginTrendChartInstance: echarts.ECharts | null = null
let activeChartInstance: echarts.ECharts | null = null
let deviceChartInstance: echarts.ECharts | null = null
let registerChartInstance: echarts.ECharts | null = null
let permissionChartInstance: echarts.ECharts | null = null

// 图表容器引用
const mapChart = ref<HTMLElement>()
const loginTrendChart = ref<HTMLElement>()
const activeChart = ref<HTMLElement>()
const deviceChart = ref<HTMLElement>()
const registerChart = ref<HTMLElement>()
const permissionChart = ref<HTMLElement>()

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

// 加载监控数据
const loadDashboardData = async () => {
  try {
    const res = await getDashboardStats()
    console.log('Dashboard API 返回数据:', res)
    
    if (res && res.data) {
      dashboardData.value = res.data
      
      // 更新统计卡片数据（添加默认值处理）
      const totalUsers = res.data.totalUsers || 0
      const todayLoginUsers = res.data.todayLoginUsers || 0
      const todayRegisterUsers = res.data.todayRegisterUsers || 0
      const onlineUsers = res.data.onlineUsers || 0
      
      statsData.value[0].value = formatNumber(totalUsers)
      statsData.value[1].value = formatNumber(todayLoginUsers)
      statsData.value[2].value = formatNumber(todayRegisterUsers)
      statsData.value[3].value = formatNumber(onlineUsers)
      statsData.value[4].value = formatNumber(Math.floor(totalUsers * 0.65)) // 活跃用户约65%
      statsData.value[5].value = formatNumber(todayLoginUsers * 3) // 访问次数约为登录数的3倍
      
      // 计算趋势（简单模拟，实际应该从后端返回）
      statsData.value[0].trend = 12.5
      statsData.value[1].trend = 8.3
      statsData.value[2].trend = todayRegisterUsers > 0 ? 5.2 : -2.1
      statsData.value[3].trend = 15.8
      statsData.value[4].trend = 7.6
      statsData.value[5].trend = 18.2
      
      // 更新最近登录记录
      if (res.data.recentLogins && res.data.recentLogins.length > 0) {
        recentLogins.value = res.data.recentLogins.map((login: any) => ({
          username: login.username,
          realName: login.realName,
          loginTime: login.loginTime,
          ip: login.ipAddress,
          location: login.location,
          device: 'PC', // 后端暂未返回设备信息，使用默认值
          status: login.onlineStatus ? 'online' : 'offline' // 转换onlineStatus为status
        }))
      }
      
      // 重新初始化图表
      initCharts()
    } else {
      console.warn('API 返回数据格式不正确:', res)
      // 使用模拟数据
      useMockData()
    }
  } catch (error) {
    console.error('加载监控数据失败:', error)
    ElMessage.warning('使用模拟数据展示')
    // 使用模拟数据
    useMockData()
  }
}

// 使用模拟数据
const useMockData = () => {
  statsData.value[0].value = '12,458'
  statsData.value[1].value = '3,256'
  statsData.value[2].value = '128'
  statsData.value[3].value = '856'
  statsData.value[4].value = '8,098'
  statsData.value[5].value = '9,768'
  
  statsData.value[0].trend = 12.5
  statsData.value[1].trend = 8.3
  statsData.value[2].trend = 5.2
  statsData.value[3].trend = 15.8
  statsData.value[4].trend = 7.6
  statsData.value[5].trend = 18.2
  
  // 模拟后端数据结构
  dashboardData.value = {
    totalUsers: 12458,
    todayLoginUsers: 3256,
    todayRegisterUsers: 128,
    onlineUsers: 856,
    cityDistribution: [
      { name: '北京', longitude: 116.4074, latitude: 39.9042, userCount: 2500 },
      { name: '上海', longitude: 121.4737, latitude: 31.2304, userCount: 2200 },
      { name: '广州', longitude: 113.2644, latitude: 23.1291, userCount: 1800 },
      { name: '深圳', longitude: 114.0579, latitude: 22.5431, userCount: 2100 },
      { name: '杭州', longitude: 120.1551, latitude: 30.2741, userCount: 1500 }
    ],
    loginTrend: {
      labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      data: [320, 450, 380, 520, 490, 380, 420]
    },
    registerTrend: {
      labels: Array.from({ length: 30 }, (_, i) => `${i + 1}日`),
      data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 50 + 20))
    },
    hourlyActivity: Array.from({ length: 24 }, (_, i) => ({
      hour: i,
      count: Math.floor(Math.random() * 200 + 50)
    }))
  }
  
  // 初始化图表
  initCharts()
}

// 格式化数字（添加千分位）
const formatNumber = (num: number) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 初始化中国地图（使用 geo + 散点图）
const initMapChart = () => {
  if (!mapChart.value) return
  
  mapChartInstance = echarts.init(mapChart.value)
  
  // 从后端获取城市数据
  const cityData = dashboardData.value?.cityDistribution?.map((city: any) => ({
    name: city.name,
    value: [city.longitude, city.latitude, city.userCount]
  })) || []
  
  if (cityData.length === 0) {
    console.warn('没有城市数据')
    return
  }
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        if (params.seriesType === 'scatter') {
          return `${params.name}<br/>用户数: ${params.value[2]}`
        }
        return params.name
      },
      confine: true
    },
    geo: {
      map: 'china',
      roam: true,
      zoom: 1.2,
      top: '5%',
      bottom: '5%',
      left: '5%',
      right: '5%',
      label: {
        show: false
      },
      itemStyle: {
        areaColor: '#1a2332',
        borderColor: '#2a5298',
        borderWidth: 1
      },
      emphasis: {
        itemStyle: {
          areaColor: '#2a5298'
        },
        label: {
          show: true,
          color: '#fff'
        }
      }
    },
    visualMap: {
      min: 0,
      max: 2500,
      text: ['高', '低'],
      realtime: false,
      calculable: true,
      inRange: {
        color: ['#4facfe', '#00f2fe', '#2a5298', '#1e88e5', '#ffd700']
      },
      textStyle: {
        color: '#fff'
      },
      left: '20px',
      bottom: '30px',
      z: 100
    },
    series: [
      {
        name: '用户分布',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: cityData,
        symbolSize: (val: number[]) => {
          return Math.max(val[2] / 50, 8)
        },
        label: {
          show: false
        },
        itemStyle: {
          color: '#ffd700',
          shadowBlur: 10,
          shadowColor: 'rgba(255, 215, 0, 0.8)'
        },
        emphasis: {
          label: {
            show: true,
            formatter: '{b}',
            position: 'top',
            color: '#fff'
          },
          itemStyle: {
            color: '#ff6b6b'
          }
        }
      },
      {
        name: '涟漪效果',
        type: 'effectScatter',
        coordinateSystem: 'geo',
        data: cityData.slice(0, 5), // 只对前5个城市显示涟漪效果
        symbolSize: (val: number[]) => {
          return Math.max(val[2] / 50, 8)
        },
        showEffectOn: 'render',
        rippleEffect: {
          brushType: 'stroke',
          scale: 3,
          period: 4
        },
        label: {
          show: true,
          formatter: '{b}',
          position: 'top',
          color: '#fff',
          fontSize: 11
        },
        itemStyle: {
          color: '#1e88e5',
          shadowBlur: 10,
          shadowColor: 'rgba(30, 136, 229, 0.8)'
        }
      }
    ]
  }
  
  // 从本地加载中国地图数据
  const loadMapData = async () => {
    try {
      const response = await fetch('/maps/china.json')
      if (!response.ok) {
        throw new Error('加载本地地图文件失败')
      }
      const chinaJson = await response.json()
      echarts.registerMap('china', chinaJson)
      mapChartInstance?.setOption(option)
      console.log('中国地图加载成功！')
    } catch (error) {
      console.error('加载地图数据失败:', error)
    }
  }
  
  loadMapData()
}

// 初始化登录趋势图
const initLoginTrendChart = () => {
  if (!loginTrendChart.value) return
  
  loginTrendChartInstance = echarts.init(loginTrendChart.value)
  
  const loginTrend = dashboardData.value?.loginTrend || { labels: [], data: [] }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: loginTrend.labels,
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#333',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '登录次数',
        type: 'line',
        smooth: true,
        data: loginTrend.data,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(42, 82, 152, 0.8)' },
            { offset: 1, color: 'rgba(42, 82, 152, 0.1)' }
          ])
        },
        lineStyle: {
          color: '#2a5298',
          width: 3
        },
        itemStyle: {
          color: '#1e88e5'
        }
      }
    ]
  }
  
  loginTrendChartInstance.setOption(option)
}

// 初始化活跃度图表
const initActiveChart = () => {
  if (!activeChart.value) return
  
  activeChartInstance = echarts.init(activeChart.value)
  
  const hourlyActivity = dashboardData.value?.hourlyActivity || []
  const hours = hourlyActivity.map((item: any) => `${item.hour}:00`)
  const data = hourlyActivity.map((item: any) => item.count)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: hours,
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#333',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '活跃用户',
        type: 'bar',
        data: data,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#4facfe' },
            { offset: 1, color: '#00f2fe' }
          ])
        }
      }
    ]
  }
  
  activeChartInstance.setOption(option)
}

// 初始化设备分布图
const initDeviceChart = () => {
  if (!deviceChart.value) return
  
  deviceChartInstance = echarts.init(deviceChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center',
      textStyle: {
        color: '#fff'
      }
    },
    series: [
      {
        name: '设备类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#1a1a2e',
          borderWidth: 2
        },
        label: {
          show: true,
          color: '#fff'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 5280, name: 'Windows', itemStyle: { color: '#667eea' } },
          { value: 3850, name: 'Mac', itemStyle: { color: '#764ba2' } },
          { value: 2120, name: 'Android', itemStyle: { color: '#f093fb' } },
          { value: 1208, name: 'iOS', itemStyle: { color: '#4facfe' } }
        ]
      }
    ]
  }
  
  deviceChartInstance.setOption(option)
}

// 初始化注册趋势图
const initRegisterChart = () => {
  if (!registerChart.value) return
  
  registerChartInstance = echarts.init(registerChart.value)
  
  const registerTrend = dashboardData.value?.registerTrend || { labels: [], data: [] }
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: registerTrend.labels,
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#333',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '注册用户',
        type: 'line',
        smooth: true,
        data: registerTrend.data,
        lineStyle: {
          color: '#43e97b',
          width: 3
        },
        itemStyle: {
          color: '#38f9d7'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(67, 233, 123, 0.6)' },
            { offset: 1, color: 'rgba(56, 249, 215, 0.1)' }
          ])
        }
      }
    ]
  }
  
  registerChartInstance.setOption(option)
}

// 初始化权限统计图表
const initPermissionChart = () => {
  if (!permissionChart.value) return
  
  permissionChartInstance = echarts.init(permissionChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#333',
          type: 'dashed'
        }
      }
    },
    yAxis: {
      type: 'category',
      data: ['用户查询', '用户编辑', '角色管理', '权限配置', '部门管理', '日志查看', '系统设置', '数据导出'],
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      }
    },
    series: [
      {
        name: '调用次数',
        type: 'bar',
        data: [8520, 7234, 6890, 5432, 4567, 3890, 2345, 1234],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          color: '#fff'
        }
      }
    ]
  }
  
  permissionChartInstance.setOption(option)
}

// 初始化所有图表
const initCharts = () => {
  initMapChart()
  initLoginTrendChart()
  initActiveChart()
  initDeviceChart()
  initRegisterChart()
  initPermissionChart()
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  mapChartInstance?.resize()
  loginTrendChartInstance?.resize()
  activeChartInstance?.resize()
  deviceChartInstance?.resize()
  registerChartInstance?.resize()
  permissionChartInstance?.resize()
}

// 定时器
let timeInterval: number

onMounted(async () => {
  updateTime()
  timeInterval = window.setInterval(updateTime, 1000)
  
  // 先加载数据
  await loadDashboardData()
  
  // 延迟初始化图表，确保DOM已渲染
  setTimeout(() => {
    window.addEventListener('resize', handleResize)
  }, 100)
})

onUnmounted(() => {
  clearInterval(timeInterval)
  window.removeEventListener('resize', handleResize)
  
  // 销毁图表实例
  mapChartInstance?.dispose()
  loginTrendChartInstance?.dispose()
  activeChartInstance?.dispose()
  deviceChartInstance?.dispose()
  registerChartInstance?.dispose()
  permissionChartInstance?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background: #0f1419;
  color: #fff;
  overflow-x: hidden;
}

/* 顶部标题栏 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 40px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-bottom: 3px solid transparent;
  border-image: linear-gradient(90deg, #2a5298, #1e88e5, #2a5298) 1;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.6);
  position: relative;
  overflow: hidden;
}

.dashboard-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(30, 136, 229, 0.1), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-left .back-button {
  color: #fff;
  font-size: 14px;
  transition: all 0.3s;
}

.header-left .back-button:hover {
  color: #1e88e5;
  transform: translateX(-4px);
}

.system-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: rgba(67, 233, 123, 0.1);
  border-radius: 20px;
  border: 1px solid rgba(67, 233, 123, 0.3);
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #43e97b;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.2); }
}

.status-text {
  font-size: 13px;
  color: #43e97b;
}

.header-center {
  text-align: center;
  flex: 1;
}

.dashboard-title {
  margin: 0;
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #2a5298 0%, #1e88e5 50%, #2a5298 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  filter: drop-shadow(0 0 20px rgba(30, 136, 229, 0.5));
}

.title-icon {
  font-size: 32px;
  filter: none;
}

.dashboard-subtitle {
  margin: 8px 0 0 0;
  font-size: 13px;
  color: #999;
  letter-spacing: 3px;
  text-transform: uppercase;
  font-weight: 300;
}

.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.time-display,
.weather-display {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #1e88e5;
  font-family: 'Courier New', monospace;
  padding: 6px 16px;
  background: rgba(30, 136, 229, 0.1);
  border-radius: 8px;
  border: 1px solid rgba(30, 136, 229, 0.2);
}

.time-icon {
  animation: rotate 4s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.weather-display {
  color: #ffd700;
  background: rgba(255, 215, 0, 0.1);
  border-color: rgba(255, 215, 0, 0.2);
}

/* 主要内容区 */
.dashboard-main {
  padding: 24px 40px;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  border: 1px solid #2a5298;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  animation: slideUp 0.6s ease-out backwards;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #2a5298, #1e88e5);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 40px rgba(30, 136, 229, 0.4);
  border-color: #1e88e5;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.stat-content {
  flex: 1;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.info-icon {
  font-size: 14px;
  color: #666;
  cursor: help;
  transition: color 0.3s;
}

.info-icon:hover {
  color: #1e88e5;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
  font-family: 'Arial', sans-serif;
  letter-spacing: -1px;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-trend {
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
}

.stat-trend.up {
  color: #43e97b;
}

.stat-trend.down {
  color: #f5576c;
}

.stat-compare {
  font-size: 11px;
  color: #666;
}

.stat-sparkline {
  display: flex;
  align-items: flex-end;
  gap: 3px;
  height: 30px;
  margin-top: 8px;
}

.sparkline-bar {
  flex: 1;
  background: linear-gradient(to top, #2a5298, #1e88e5);
  border-radius: 2px;
  opacity: 0.6;
  transition: all 0.3s;
}

.stat-card:hover .sparkline-bar {
  opacity: 1;
}

/* 主要内容布局：左中右三栏 */
.main-content-layout {
  display: grid;
  grid-template-columns: 280px 1fr 380px;
  gap: 20px;
  margin-bottom: 24px;
  align-items: stretch;
}

.left-column,
.center-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 700px;
}

.map-card-large {
  height: 100%;
  min-height: 700px;
  display: flex;
  flex-direction: column;
}

.map-card-large .chart-container-large {
  flex: 1;
  min-height: 0;
}

/* 实时数据卡片 */
.realtime-card {
  height: 320px;
}

.realtime-list {
  max-height: 240px;
  overflow-y: auto;
  padding-right: 8px;
}

.realtime-list::-webkit-scrollbar {
  width: 6px;
}

.realtime-list::-webkit-scrollbar-track {
  background: #1a1a2e;
  border-radius: 3px;
}

.realtime-list::-webkit-scrollbar-thumb {
  background: #2a5298;
  border-radius: 3px;
}

.realtime-list::-webkit-scrollbar-thumb:hover {
  background: #1e88e5;
}

.realtime-item {
  padding: 12px;
  margin-bottom: 8px;
  background: rgba(42, 82, 152, 0.1);
  border-radius: 8px;
  border-left: 3px solid #2a5298;
  transition: all 0.3s;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.realtime-item:hover {
  background: rgba(42, 82, 152, 0.2);
  border-left-color: #1e88e5;
  transform: translateX(4px);
}

.realtime-time {
  font-size: 11px;
  color: #666;
  margin-bottom: 6px;
  font-family: 'Courier New', monospace;
}

.realtime-content {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #ccc;
}

/* 排行榜卡片 */
.ranking-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.ranking-card .card-header {
  flex-shrink: 0;
}

.ranking-card .ranking-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(42, 82, 152, 0.1);
  border-radius: 8px;
  transition: all 0.3s;
}

.ranking-item:hover {
  background: rgba(42, 82, 152, 0.2);
  transform: translateX(4px);
}

.rank-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-weight: 700;
  font-size: 16px;
  background: #2a5298;
  color: #fff;
  flex-shrink: 0;
}

.rank-number.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #333;
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
}

.rank-number.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  color: #333;
  box-shadow: 0 4px 12px rgba(192, 192, 192, 0.4);
}

.rank-number.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #e8a87c);
  color: #fff;
  box-shadow: 0 4px 12px rgba(205, 127, 50, 0.4);
}

.rank-info {
  flex: 1;
}

.rank-name {
  font-size: 14px;
  color: #fff;
  margin-bottom: 6px;
  font-weight: 500;
}

.rank-bar {
  height: 6px;
  background: rgba(42, 82, 152, 0.3);
  border-radius: 3px;
  overflow: hidden;
}

.rank-progress {
  height: 100%;
  background: linear-gradient(90deg, #2a5298, #1e88e5);
  border-radius: 3px;
  transition: width 1s ease-out;
}

.rank-value {
  font-size: 14px;
  color: #1e88e5;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

/* 底部图表行 */
.charts-row-bottom {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  align-items: start;
}

.charts-row-bottom .chart-card {
  display: flex;
  flex-direction: column;
  min-height: 480px;
}

.charts-row-bottom .chart-card .card-header {
  flex-shrink: 0;
  height: 68px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.charts-row-bottom .chart-card .chart-container {
  flex: 1;
  height: 380px !important;
  min-height: 380px;
  max-height: 380px;
}

/* 数据表格区 */
.tables-section {
  margin-bottom: 24px;
}

.table-card :deep(.el-table) {
  background: transparent;
  color: #fff;
}

.table-card :deep(.el-table tr) {
  background: transparent;
}

.table-card :deep(.el-table td),
.table-card :deep(.el-table th) {
  border-color: #2a5298;
}

.table-card :deep(.el-table__body tr:hover > td) {
  background: rgba(42, 82, 152, 0.2) !important;
}

.table-card :deep(.el-table th.el-table__cell) {
  background: #1a1a2e;
  color: #fff;
  font-weight: 600;
}

.table-card :deep(.el-table__empty-text) {
  color: #999;
}

/* 图表卡片 */
.chart-card {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #2a5298;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.chart-card:hover {
  border-color: #1e88e5;
  box-shadow: 0 8px 30px rgba(30, 136, 229, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid transparent;
  border-image: linear-gradient(90deg, #2a5298, transparent) 1;
  flex-shrink: 0;
  min-height: 48px;
}

.card-header > div:first-child {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-subtitle {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  display: block;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-users {
  font-size: 13px;
  color: #1e88e5;
  font-weight: 600;
}

.chart-container {
  width: 100%;
  height: 380px;
  flex: 1;
  min-height: 380px;
}

.chart-container-large {
  width: 100%;
  height: 650px;
}

.chart-container-small {
  width: 100%;
  height: 330px;
  flex: 1;
}

.right-column .chart-card {
  flex: 1;
  min-height: 0;
}

/* 响应式 */
@media (max-width: 1600px) {
  .stats-cards {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .main-content-layout {
    grid-template-columns: 260px 1fr 340px;
  }
  
  .map-card-large {
    min-height: 600px;
  }
  
  .chart-container-large {
    height: 550px;
  }
}

@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .main-content-layout {
    grid-template-columns: 1fr;
  }
  
  .left-column,
  .right-column {
    display: none;
  }
  
  .charts-row-bottom {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    padding: 16px 20px;
  }
  
  .header-left,
  .header-right {
    width: 100%;
    justify-content: center;
  }

  .dashboard-title {
    font-size: 24px;
  }
  
  .title-icon {
    font-size: 24px;
  }

  .dashboard-main {
    padding: 16px 20px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .main-content-layout {
    grid-template-columns: 1fr;
  }
  
  .map-card-large {
    min-height: 400px;
  }
  
  .chart-container-large {
    height: 350px;
  }

  .charts-row-bottom {
    grid-template-columns: 1fr;
  }

  .chart-container,
  .chart-container-small {
    height: 300px;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card,
.chart-card {
  animation: fadeIn 0.6s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }
</style>
