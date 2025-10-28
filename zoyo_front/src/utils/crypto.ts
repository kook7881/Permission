import CryptoJS from 'crypto-js'

/**
 * 密码加密工具
 */

// 加密密钥（实际项目中应该从环境变量或配置中获取）
const SECRET_KEY = 'zoyo-auth-system-2024'

/**
 * MD5 加密
 * @param text 明文
 * @returns MD5 哈希值
 */
export const md5 = (text: string): string => {
    return CryptoJS.MD5(text).toString()
}

/**
 * 密码加密（MD5 + 盐值）
 * @param password 明文密码
 * @returns 加密后的密码
 */
export const encryptPassword = (password: string): string => {
    // 使用 MD5(password + salt) 的方式
    return md5(password + SECRET_KEY)
}

/**
 * AES 加密
 * @param text 明文
 * @param key 密钥
 * @returns 加密后的文本
 */
export const aesEncrypt = (text: string, key: string = SECRET_KEY): string => {
    return CryptoJS.AES.encrypt(text, key).toString()
}

/**
 * AES 解密
 * @param ciphertext 密文
 * @param key 密钥
 * @returns 解密后的文本
 */
export const aesDecrypt = (ciphertext: string, key: string = SECRET_KEY): string => {
    const bytes = CryptoJS.AES.decrypt(ciphertext, key)
    return bytes.toString(CryptoJS.enc.Utf8)
}
