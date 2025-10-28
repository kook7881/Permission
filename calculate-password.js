// Node.js 脚本：计算管理员密码
// 运行: node calculate-password.js

const crypto = require('crypto');

const SECRET_KEY = 'zoyo-auth-system-2024';
const rawPassword = 'admin123';

// 步骤1: 前端 MD5 加密
const frontendEncrypted = crypto.createHash('md5')
    .update(rawPassword + SECRET_KEY)
    .digest('hex');

console.log('='.repeat(80));
console.log('密码加密计算');
console.log('='.repeat(80));
console.log();
console.log('步骤1 - 前端MD5加密:');
console.log('  明文密码:', rawPassword);
console.log('  加密密钥:', SECRET_KEY);
console.log('  拼接字符串:', rawPassword + SECRET_KEY);
console.log('  MD5结果:', frontendEncrypted);
console.log();
console.log('步骤2 - 后端BCrypt加密:');
console.log('  需要对以下字符串进行 BCrypt 加密:');
console.log('  ' + frontendEncrypted);
console.log();
console.log('='.repeat(80));
console.log('使用在线工具生成 BCrypt:');
console.log('='.repeat(80));
console.log('1. 访问: https://bcrypt-generator.com/');
console.log('2. 输入:', frontendEncrypted);
console.log('3. Rounds: 10');
console.log('4. 点击 Generate');
console.log('5. 复制生成的 BCrypt 哈希值');
console.log('='.repeat(80));
