# 智慧园林·树木智能修剪决策平台

## 在线演示（任何网络均可访问）

🔗 **公网链接**: https://2b49139c.r20.vip.cpolar.cn

> 说明：该链接通过 cpolar 内网穿透映射到本地运行的服务，电脑开机且隧道运行时可访问。免费版链接每次重启会变化。

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 修剪工人 | worker01 | 123456 |
| 客户 | client01 | 123456 |

## 技术栈

- **前端**：Vue 3 + Vite + Element Plus + Axios
- **后端**：Spring Boot（Java 17）+ MyBatis-Plus + JWT
- **数据库**：SQL Server

## 功能模块

- 用户登录与角色权限管理（管理员 / 工人 / 客户）
- 树木档案管理与生长状态记录
- 智能修剪决策建议
- 修剪工单派发与执行跟踪
- 图片上传与作业记录

## 本地运行

1. **数据库**：SQL Server，创建库 `smart_garden`，用户 `garden` / `Garden@123456`，导入初始化脚本。
2. **后端**（端口 9999，context-path `/api`）：
   ```
   cd backend
   mvn spring-boot:run
   ```
3. **前端**（端口 5173，Vite 代理 `/api` 到 9999）：
   ```
   cd frontend
   npm install
   npm run dev
   ```
4. 浏览器访问 http://localhost:5173

## 目录结构

```
backend/    Spring Boot 后端源码
frontend/   Vue 3 前端源码
```
