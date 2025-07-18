

# 智能自媒体内容创作与运营平台

## 技术栈
- 前端：基于 Element Plus 的 Vue 3 项目，使用 Quasar 框架和 SCSS 样式
- 后端：Java Spring Boot
- 数据库：MySQL
- 构建工具：Maven
- 部署：前端使用 Node.js 和 npm

## 功能特点
- 自媒体内容创作与管理
- 数据可视化仪表盘
- 用户认证与权限管理
- 主题与内容编辑功能
- 模板化内容生成流程
- 响应式设计适配不同设备
- 使用 Element Plus 组件库实现现代化 UI
- 支持内容版本控制与统计分析

## 项目结构
```
.
├── README.md
├── database-setup.sql
├── pom.xml
├── .idea
│   ├── compiler.xml
│   ├── encodings.xml
│   ├── jarRepositories.xml
│   ├── misc.xml
│   └── *.xml
└── src
    └── main
        ├── frontend
        │   └── dist
        │       ├── index.html
        │       ├── static
        │       │   ├── css
        │       │   └── js
        │       └── mock
        │           ├── dashboard-data.json
        │           └── topics.json
        └── java
            └── 包含 Spring Boot 项目源码
```

## 快速开始

### 前提条件
- Java 1.8+（后端）
- Node.js 14.x+ / npm 6.x+（前端）
- MySQL 5.7+

### 后端启动
1. 打开 IntelliJ IDEA
2. 导入项目
3. 配置数据库连接
4. 执行 database-setup.sql 初始化数据库
5. 运行 Spring Boot 项目

### 前端启动
1. 进入 frontend 目录
   ```bash
   cd src/main/frontend
   ```
2. 安装依赖
   ```bash
   npm install
   ```
3. 启动开发服务器
   ```bash
   npm run serve
   ```
4. 构建生产版本
   ```bash
   npm run build
   ```

## 配置说明
- 项目配置存储在 .idea 目录下的 XML 文件中
- 前端构建配置可通过 npm config 命令管理
- mock 数据位于 dist/mock 目录
- 主样式文件为 static/css/app.493ecb61.css
- 主要 HTML 结构在 dist/index.html

## 使用流程
1. 用户通过认证页面登录
2. 进入仪表盘查看统计数据
3. 选择内容主题
4. 使用编辑器创建/修改内容
5. 发布并管理自媒体内容
6. 查看运营效果与趋势分析

## 注意事项
- 项目使用 Vue 3 Composition API
- 所有 Element Plus 组件已按需加载
- mock 数据仅用于前端测试，实际使用需连接后端 API
- 生产构建前请确保后端 API 地址已正确配置
- 包含 Quasar 框架配置

## 许可证
该项目采用 Apache 2.0 许可证。详见 LICENSE 文件。