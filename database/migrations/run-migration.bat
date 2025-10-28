@echo off
chcp 65001 >nul
echo ========================================
echo 数据库迁移脚本执行工具
echo ========================================
echo.

set /p DB_PASSWORD=请输入MySQL root密码: 

echo.
echo 正在执行迁移脚本...
echo.

mysql -uroot -p%DB_PASSWORD% < 001_fix_login_log_user_id.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✅ 迁移执行成功！
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ❌ 迁移执行失败，请检查错误信息
    echo ========================================
)

echo.
pause
