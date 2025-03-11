# 牛津词汇应用架构设计

## 系统架构图

```mermaid
graph TB
    subgraph "前端 Frontend"
        A[Vue.js 应用]
        B[Vite 构建工具]
        subgraph "前端组件"
            C1[视图层 /views]
            C2[组件层 /components]
            C3[状态管理 /store]
            C4[API接口层 /api]
            C5[资源文件 /assets]
        end
    end

    subgraph "后端 Backend"
        D[Spring Boot 应用]
        subgraph "后端组件"
            E1[Controller 控制器]
            E2[Service 服务层]
            E3[Repository 数据访问层]
            E4[Entity 实体层]
        end
        F[(MySQL 数据库)]
    end

    subgraph "部署架构"
        G[Nginx Web服务器]
        H[Tomcat 应用服务器]
    end

    A --> C1
    C1 --> C2
    C2 --> C3
    C3 --> C4
    C4 --> G
    G --> H
    H --> D
    D --> E1
    E1 --> E2
    E2 --> E3
    E3 --> E4
    E4 --> F

    subgraph "部署工具"
        I[deploy.sh]
        I1[前端部署流程]
        I2[后端部署流程]
    end

    I --> I1
    I --> I2
    I1 --> G
    I2 --> H
```

## 架构说明

### 1. 前端架构
- 使用 Vue.js 作为前端框架
- 采用 Vite 作为构建工具
- 主要目录结构：
  * `/views`: 页面视图组件
  * `/components`: 可复用组件
  * `/store`: 状态管理
  * `/api`: 后端接口调用
  * `/assets`: 静态资源

### 2. 后端架构
- 基于 Spring Boot 框架
- 采用标准的多层架构：
  * Controller 层：处理 HTTP 请求
  * Service 层：业务逻辑处理
  * Repository 层：数据访问
  * Entity 层：数据模型

### 3. 部署架构
- Nginx：作为前端静态资源服务器和反向代理
- Tomcat：运行 Spring Boot 后端应用
- MySQL：数据存储

### 4. 部署工具
- `deploy.sh` 脚本支持：
  * 前端独立部署
  * 后端独立部署
  * 前后端同时部署

### 5. 网络架构
- 前端访问地址：`http://124.223.76.88/`
- 后端 API 地址：`http://124.223.76.88/oxford-vocabulary/api/`
- Nginx 配置：
  * 静态资源：`/var/www/html/`
  * API 转发：`/oxford-vocabulary/api/` 