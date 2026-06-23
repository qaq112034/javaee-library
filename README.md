# 图书管理系统

> JavaEE 期末课程设计 | Spring Boot 3.x + Vue 3 + MyBatis-Plus + MySQL + Redis 的前后端分离图书管理系统

## 项目结构

```
JEEteam/
├── library-server/              # 后端 Spring Boot 项目
│   ├── pom.xml
│   └── src/main/java/com/library/
│       ├── LibraryApplication.java
│       ├── common/              # 公共组件（Result、异常处理）
│       ├── config/              # 配置类（Security、Redis、MP）
│       ├── security/            # JWT + Spring Security
│       └── module/              # 业务模块
│           ├── auth/            # 认证登录
│           ├── system/          # 用户/角色/权限
│           ├── book/            # 图书管理
│           ├── reader/          # 读者管理
│           ├── borrow/          # 借阅管理
│           ├── notice/          # 公告管理
│           └── statistics/      # 数据统计
├── library-web/                 # 前端 Vue 3 项目
│   ├── package.json
│   └── src/
│       ├── api/                 # API 接口封装
│       ├── router/              # 路由配置
│       ├── stores/              # Pinia 状态管理
│       └── views/               # 页面组件
└── sql/                        # 文档
    └──  init.sql             # 数据库初始化

```

## 快速启动

### 1. 初始化数据库
```bash
mysql -u root -p < sql/init.sql
# 修改 src/main/resources/application.yml 中的数据库密码和 Redis 连接
```

### 2. 配置并启动后端
```bash
cd library-server
mvn spring-boot:run
```
访问 API 文档：http://localhost:8080/doc.html

### 3. 启动前端
```bash
cd library-web
npm install
npm run dev
```
访问系统：http://localhost:5173

### 4. 默认账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| libadmin | admin123 | 图书管理员 |

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.0 |
| 安全框架 | Spring Security + JWT | 6.x + 0.12.3 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 7.0+ |
| 前端框架 | Vue 3 + Element Plus | 3.4 + 2.5 |
| 构建工具 | Vite | 5.0 |
| 图表 | ECharts | 5.5 |
