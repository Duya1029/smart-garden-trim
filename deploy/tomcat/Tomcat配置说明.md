# Tomcat 配置说明

> 智慧园林·树木智能修剪决策平台 —— 后端 Web 容器配置
> 适用范围：Spring Boot 3.2.5 + 内置 Tomcat 10.1（jakarta 命名空间）+ JDK 17

---

## 一、方案说明

本项目采用 **Spring Boot 内置 Tomcat** 运行，无需单独安装 Tomcat 服务器。所有容器参数（端口、地址、线程池、连接数、编码、上传大小）集中配置在
`backend/src/main/resources/application.yml` 的 `server` 节点下，生产环境可通过 `deploy/tomcat/application-prod.yml` 覆盖。

内置 Tomcat 的优势：

- 打包后仅需一个可执行 jar（`smart-garden-trim.jar`），`java -jar` 即可启动；
- 容器与业务同进程，部署链路短、无版本冲突；
- 参数可通过配置文件或启动参数动态调整，无需改 `server.xml`。

---

## 二、内置 Tomcat 核心参数

`application.yml` 中的 `server` 节点：

```yaml
server:
  port: 8080                                   # 服务端口，Nginx upstream 指向此端口
  servlet:
    context-path: /api                         # 统一接口前缀，与 Nginx 反代规则 /api 一致
    encoding:
      charset: UTF-8                           # 请求/响应编码
      enabled: true
      force: true
    session:
      timeout: 30m                             # 会话超时（本项目为无状态 JWT，仅保留默认值）
  tomcat:
    uri-encoding: UTF-8                        # URI 编码
    threads:
      max: 200                                 # 最大工作线程数（生产建议 400）
      min-spare: 20                            # 最小空闲线程数（生产建议 50）
    accept-count: 200                          # 线程耗尽后的等待队列长度
    max-connections: 8192                      # 最大连接数
    connection-timeout: 20000                  # 连接超时（毫秒）
    remoteip:                                  # 经过 Nginx 代理时还原真实客户端信息
      remote-ip-header: X-Forwarded-For
      protocol-header: X-Forwarded-Proto
  compression:
    enabled: true                              # 响应压缩
    mime-types: application/json,text/html,text/css,application/javascript
    min-response-size: 2048
```

| 参数 | 含义 | 调优建议 |
| --- | --- | --- |
| `server.port` | 服务端口 | 与 Nginx `upstream` 保持一致，默认 8080 |
| `server.servlet.context-path` | 接口统一前缀 | 固定为 `/api`，前端与 Nginx 均按此前缀请求 |
| `server.tomcat.threads.max` | 最大工作线程 | 单机推荐 200～400，按并发量与 CPU 核数调整 |
| `server.tomcat.threads.min-spare` | 最小空闲线程 | 取 `max` 的 10% 左右，减少突发流量时的线程创建开销 |
| `server.tomcat.accept-count` | 排队队列长度 | 高并发场景可调至 300～500 |
| `server.tomcat.max-connections` | 最大连接数 | NIO 模式默认 8192，可按需提升至 10000 |
| `server.tomcat.connection-timeout` | 建连超时 | 默认 20s，一般无需调整 |
| `spring.servlet.multipart.max-file-size` | 单文件上限 | 图片上传，默认 10MB |
| `spring.servlet.multipart.max-request-size` | 单请求上限 | 多图上传，默认 50MB |

> 说明：本项目使用 JWT 无状态鉴权，`session.timeout` 不影响登录态；登录有效期由 `garden.jwt.expire-hours` 控制。

---

## 三、生产环境启动步骤

### 1. 打包

```bash
cd backend
mvn clean package -DskipTests
# 产物：backend/target/smart-garden-trim.jar
```

### 2. 部署目录建议

```
/opt/smart-garden/
├── smart-garden-trim.jar     # 后端可执行 jar
├── config/
│   └── application-prod.yml  # 生产配置（直接使用 deploy/tomcat/application-prod.yml）
├── upload/                   # 图片上传目录（与 Nginx /upload/ 指向同一路径）
└── logs/                     # 日志目录
```

### 3. 启动命令

```bash
# 以 prod 配置启动（Spring Boot 会自动加载 jar 同级 config/ 目录下的 application-prod.yml）
java -jar /opt/smart-garden/smart-garden-trim.jar --spring.profiles.active=prod

# 后台运行并输出日志
nohup java -jar /opt/smart-garden/smart-garden-trim.jar --spring.profiles.active=prod \
      > /opt/smart-garden/logs/console.log 2>&1 &

# 常用 JVM 参数示例（生产推荐）
java -Xms512m -Xmx1024m -XX:+UseG1GC -Dfile.encoding=UTF-8 \
     -jar /opt/smart-garden/smart-garden-trim.jar --spring.profiles.active=prod
```

### 4. 启动参数也可直接覆盖容器配置

```bash
java -jar smart-garden-trim.jar --server.port=8080 \
     --server.tomcat.threads.max=400 \
     --server.tomcat.accept-count=300
```

### 5. 验证

```bash
curl -X POST http://127.0.0.1:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"123456"}'
```

能返回 `{"code":200,...,"token":"..."}` 即为启动成功。

---

## 四、可选：外置 Tomcat（war 部署）

如企业规范要求统一使用外置 Tomcat（Tomcat 10.1+，jakarta 命名空间），可按以下方式改造：

1. **pom.xml 调整**
   - `<packaging>war</packaging>`
   - 增加依赖：`spring-boot-starter-tomcat` 并设为 `<scope>provided</scope>`
2. **启动类调整**：`SmartGardenApplication extends SpringBootServletInitializer`，并覆写
   `configure(SpringApplicationBuilder builder)` 返回 `builder.sources(SmartGardenApplication.class)`
3. **context-path**：war 包名即访问路径。例如产物为 `smart-garden.war`，则接口地址变为
   `http://<域名>/smart-garden/api/...`，此时需同步修改 Nginx 的 `location /api/` 与
   `proxy_pass` 路径（如 `proxy_pass http://127.0.0.1:8080/smart-garden/api/;`），并保持前端 `VITE_API_BASE` 一致。
4. **外置 Tomcat 侧配置**：在 `conf/server.xml` 的 `<Connector>` 中设置端口、`URIEncoding="UTF-8"`、
   `maxThreads`、`acceptCount`、`maxConnections`、`connectionTimeout`、`maxPostSize`（上传大小），
   参数含义与上表一致。
5. **部署**：将 war 放入 `webapps/`，Tomcat 会自动解压并部署。

> 结论：本项目默认采用内置 Tomcat 交付，外置 war 方案仅在运维规范强制要求时选用；
> 两种方式下业务代码（Service/Controller/Mapper）完全不需要改动。

---

## 五、与 Nginx 的配合

| 配置项 | Tomcat（本项目） | Nginx |
| --- | --- | --- |
| 服务端口 | `server.port = 8080` | `upstream` 指向 `127.0.0.1:8080` |
| 接口前缀 | `context-path = /api` | `location /api/` 反向代理，`proxy_pass` 不带路径以保留前缀 |
| 图片访问 | `WebMvcConfig` 映射 `/upload/**` 到本地上传目录 | `location /upload/` 用 `alias` 直接托管同一目录 |
| 真实 IP | `server.tomcat.remoteip.remote-ip-header = X-Forwarded-For` | 反代时注入 `X-Real-IP` / `X-Forwarded-For` |

详见 [nginx.conf](../nginx/nginx.conf)。
