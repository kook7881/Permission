/**
 * 日期格式化工具
 */

/**
 * 格式化日期时间为中国格式
 * @param date 日期字符串或Date对象
 * @param format 格式化模板，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期字符串
 */
export function formatDateTime(date: string | Date | null | undefined, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
    if (!date) return '-'

    const d = typeof date === 'string' ? new Date(date) : date

    // 检查日期是否有效
    if (isNaN(d.getTime())) return '-'

    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')

    return format
        .replace('YYYY', String(year))
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds)
}

/**
 * 格式化日期（不含时间）
 * @param date 日期字符串或Date对象
 * @returns 格式化后的日期字符串 YYYY-MM-DD
 */
export function formatDate(date: string | Date | null | undefined): string {
    return formatDateTime(date, 'YYYY-MM-DD')
}

/**
 * 格式化时间（不含日期）
 * @param date 日期字符串或Date对象
 * @returns 格式化后的时间字符串 HH:mm:ss
 */
export function formatTime(date: string | Date | null | undefined): string {
    return formatDateTime(date, 'HH:mm:ss')
}

/**
 * 格式化为相对时间（如：刚刚、5分钟前、2小时前等）
 * @param date 日期字符串或Date对象
 * @returns 相对时间字符串
 */
export function formatRelativeTime(date: string | Date | null | undefined): string {
    if (!date) return '-'

    const d = typeof date === 'string' ? new Date(date) : date
    if (isNaN(d.getTime())) return '-'

    const now = new Date()
    const diff = now.getTime() - d.getTime()
    const seconds = Math.floor(diff / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)
    const days = Math.floor(hours / 24)
    const months = Math.floor(days / 30)
    const years = Math.floor(days / 365)

    if (seconds < 60) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (hours < 24) return `${hours}小时前`
    if (days < 30) return `${days}天前`
    if (months < 12) return `${months}个月前`
    return `${years}年前`
}
