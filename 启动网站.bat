@echo off
chcp 65001 >nul
title 智慧园林·树木智能修剪决策平台 - 一键启动
echo ==================================================
echo   智慧园林·树木智能修剪决策平台  正在启动...
echo ==================================================
echo.

:: ===== 1. 启动后端（Spring Boot，端口 9999）=====
echo [1/3] 启动后端服务...
start "后端-9999" /MIN cmd /k "cd /d D:\桌面\smart-garden-trim\backend && "D:\db\openjdk-17_windows-x64_bin\jdk-17\bin\java.exe" -jar target\smart-garden-trim.jar"

timeout /t 15 /nobreak >nul

:: ===== 2. 启动前端（Vite，端口 5173）=====
echo [2/3] 启动前端服务...
start "前端-5173" /MIN cmd /k "cd /d D:\桌面\smart-garden-trim\frontend && npm.cmd run dev"

timeout /t 6 /nobreak >nul

:: ===== 3. 打开浏览器 =====
echo [3/3] 打开浏览器...
start http://localhost:5173/

echo.
echo ==================================================
echo   启动完成！浏览器已打开 http://localhost:5173/
echo   演示账号：admin / 123456
echo   （请保持弹出的两个黑色窗口开启）
echo ==================================================
pause
