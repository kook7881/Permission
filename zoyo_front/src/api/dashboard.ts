import request from './request'

/**
 * 获取监控大屏统计数据
 */
export const getDashboardStats = () => {
  return request({
    url: '/api/dashboard/stats',
    method: 'get'
  })
}
