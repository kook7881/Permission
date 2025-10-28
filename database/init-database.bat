@echo off
chcp 65001 >nul
echo ========================================
echo Zoyo 认证系统 - 数据库初始化
echo ========================================
echo.

REM 数据库配置
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=123456

echo 📋 数据库配置信息：
echo    主机: %MYSQL_HOST%
echo    端口: %MYSQL_PORT%
echo    用户: %MYSQL_USER%
echo.

echo ========================================
echo 第一步：创建数据库
echo ========================================
echo 说明：大部分表由JPA自动创建，这里只创建数据库
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < schema.sql

if %errorlevel% equ 0 (
    echo ✅ 数据库创建成功
) else (
    echo ❌ 数据库创建失败
    echo.
    echo 请检查：
    echo 1. MySQL服务是否启动
    echo 2. 数据库连接信息是否正确
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo 第二步：初始化数据
echo ========================================
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < init-data.sql

if %errorlevel% equ 0 (
    echo ✅ 数据初始化成功
) else (
    echo ❌ 数据初始化失败
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo 第三步：导入测试数据（可选）
echo ========================================
set /p IMPORT_TEST_DATA="是否导入测试数据？(Y/N): "
if /i "%IMPORT_TEST_DATA%"=="Y" (
    mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% < test-data.sql
    if %errorlevel% equ 0 (
        echo ✅ 测试数据导入成功
    ) else (
        echo ❌ 测试数据导入失败
    )
) else (
    echo ⏭️  跳过测试数据导入
)

echo.
echo ========================================
echo 🎉 数据库初始化完成！
echo ========================================
echo.
echo 📋 登录信息：
echo    用户名: admin
echo    密码: admin123
echo    地址: http://localhost:3000
echo.
echo ⚠️  重要提示：首次登录后请立即修改密码！
echo.
pause
